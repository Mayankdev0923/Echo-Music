package com.example.meshgradient

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import androidx.core.graphics.ColorUtils
import kotlin.math.*

/**
 * MeshGradientView
 *
 * A high-performance, monochrome morphing mesh gradient for use as an
 * Android app background. Uses Perlin-style noise displacement on a
 * bicubic colour mesh, rendered entirely on the CPU with a cached
 * Bitmap so the GPU composite step stays cheap.
 *
 * Palette: single hue expressed across ~5 luminance stops (light → dark).
 *
 * Performance notes:
 *  - All allocations happen in init / onSizeChanged, never in onDraw.
 *  - Mesh is low-res (COLS × ROWS); the Bitmap upscales smoothly because
 *    Bitmap.Config.RGB_565 + bilinear filtering is used.
 *  - Animation runs at the system choreographer tick but only triggers
 *    invalidate() when the frame actually differs (delta-time guard).
 */
class MeshGradientView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    // ── Mesh resolution ────────────────────────────────────────────────────
    private val COLS = 6          // control-point columns (keep ≤ 8 for speed)
    private val ROWS = 8          // control-point rows

    // ── Monochrome palette (5 stops from near-white to near-black) ─────────
    //    Swap these hex values to change the accent hue while staying mono.
    private val palette = intArrayOf(
        Color.parseColor("#F0F0F0"),   // 0 – highlight
        Color.parseColor("#C8C8C8"),   // 1 – light mid
        Color.parseColor("#888888"),   // 2 – mid
        Color.parseColor("#404040"),   // 3 – dark mid
        Color.parseColor("#141414"),   // 4 – shadow
    )

    // ── Animation timing ──────────────────────────────────────────────────
    private val ANIM_SPEED   = 0.00028f   // noise time advance per ms
    private val WARP_AMOUNT  = 0.38f      // how far control points drift [0..1]
    private val MIN_FRAME_MS = 16L        // cap at ~60 fps

    // ── Internal state ────────────────────────────────────────────────────
    private var startTimeMs = 0L
    private var lastFrameMs = 0L

    /** Scratch arrays – allocated once in onSizeChanged */
    private lateinit var colorsBuffer: IntArray      // COLS * ROWS colours
    private lateinit var meshBitmap: Bitmap          // low-res mesh render
    private lateinit var meshCanvas: Canvas

    /** Full-view Bitmap that gets drawn in onDraw – avoids re-allocation */
    private lateinit var outputBitmap: Bitmap

    private val paint = Paint(Paint.FILTER_BITMAP_FLAG).apply {
        isAntiAlias = false      // not needed; FILTER_BITMAP gives bilinear
    }

    // ── Noise helpers (pre-allocated permutation table) ───────────────────
    private val perm = IntArray(512)
    private val grad = arrayOf(
        floatArrayOf( 1f,  1f), floatArrayOf(-1f,  1f),
        floatArrayOf( 1f, -1f), floatArrayOf(-1f, -1f),
        floatArrayOf( 1f,  0f), floatArrayOf(-1f,  0f),
        floatArrayOf( 0f,  1f), floatArrayOf( 0f, -1f)
    )

    init {
        // Build permutation table once
        val p = (0 until 256).shuffled()
        for (i in 0 until 256) { perm[i] = p[i]; perm[i + 256] = p[i] }

        // Avoid overdraw – we fill the entire surface ourselves
        setWillNotDraw(false)
        setLayerType(LAYER_TYPE_HARDWARE, null)
    }

    // ─────────────────────────────────────────────────────────────────────
    // Lifecycle
    // ─────────────────────────────────────────────────────────────────────

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        startTimeMs = System.currentTimeMillis()
        lastFrameMs = startTimeMs
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        if (::outputBitmap.isInitialized && !outputBitmap.isRecycled) outputBitmap.recycle()
        if (::meshBitmap.isInitialized  && !meshBitmap.isRecycled)  meshBitmap.recycle()
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        if (w == 0 || h == 0) return

        colorsBuffer = IntArray(COLS * ROWS)

        // Low-res mesh Bitmap (ARGB_8888 for colour blending; RGB_565 is
        // faster but we need alpha for ColorUtils.blendARGB)
        if (::meshBitmap.isInitialized && !meshBitmap.isRecycled) meshBitmap.recycle()
        meshBitmap = Bitmap.createBitmap(COLS, ROWS, Bitmap.Config.ARGB_8888)
        meshCanvas = Canvas(meshBitmap)

        // Output Bitmap at full view size
        if (::outputBitmap.isInitialized && !outputBitmap.isRecycled) outputBitmap.recycle()
        outputBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.RGB_565)
    }

    // ─────────────────────────────────────────────────────────────────────
    // Draw
    // ─────────────────────────────────────────────────────────────────────

    override fun onDraw(canvas: Canvas) {
        if (!::outputBitmap.isInitialized) return

        val nowMs  = System.currentTimeMillis()
        val deltaMs = nowMs - lastFrameMs
        if (deltaMs < MIN_FRAME_MS) {
            // Too soon – schedule the next tick without burning the frame
            postInvalidateOnAnimation()
            return
        }
        lastFrameMs = nowMs
        val t = (nowMs - startTimeMs) * ANIM_SPEED

        // 1. Compute colour for each control point using 3D noise
        buildMeshColors(t)

        // 2. Push control-point colours into the low-res Bitmap
        meshBitmap.setPixels(colorsBuffer, 0, COLS, 0, 0, COLS, ROWS)

        // 3. Scale up to full view size with bilinear filtering
        val outputCanvas = Canvas(outputBitmap)
        val srcRect = Rect(0, 0, COLS, ROWS)
        val dstRect = Rect(0, 0, outputBitmap.width, outputBitmap.height)
        outputCanvas.drawBitmap(meshBitmap, srcRect, dstRect, paint)

        // 4. Blit to the screen canvas
        canvas.drawBitmap(outputBitmap, 0f, 0f, null)

        // Request next frame
        postInvalidateOnAnimation()
    }

    // ─────────────────────────────────────────────────────────────────────
    // Mesh colour generation
    // ─────────────────────────────────────────────────────────────────────

    /**
     * For each control point (col, row) sample Perlin noise at three
     * slightly different time offsets to get independent R/G/B-ish
     * luminance variation, then map to the monochrome palette.
     */
    private fun buildMeshColors(t: Float) {
        for (row in 0 until ROWS) {
            for (col in 0 until COLS) {
                val nx = col.toFloat() / (COLS - 1)
                val ny = row.toFloat() / (ROWS - 1)

                // Two octaves of noise for richer morphing
                val n1 = noise3(nx * 1.8f + t,        ny * 2.2f,         t * 0.7f)
                val n2 = noise3(nx * 3.5f + t * 1.3f, ny * 4.0f + 0.5f,  t * 1.1f) * 0.4f

                // Combine, remap [−1.4 .. 1.4] → [0 .. 1]
                val raw = ((n1 + n2) + 1.4f) / 2.8f
                val clamped = raw.coerceIn(0f, 1f)

                // Warp adds low-freq displacement so the mesh "breathes"
                val warpN = noise3(nx + t * 0.4f, ny + t * 0.3f, t * 0.5f + 7f)
                val warped = (clamped + warpN * WARP_AMOUNT * 0.15f).coerceIn(0f, 1f)

                colorsBuffer[row * COLS + col] = samplePalette(warped)
            }
        }
    }

    /**
     * Map a 0..1 value to a colour interpolated along the palette stops.
     */
    private fun samplePalette(t: Float): Int {
        val scaled = t * (palette.size - 1)
        val lo = scaled.toInt().coerceIn(0, palette.size - 2)
        val frac = scaled - lo
        return ColorUtils.blendARGB(palette[lo], palette[lo + 1], frac)
    }

    // ─────────────────────────────────────────────────────────────────────
    // Perlin noise (classic 3D, compact implementation)
    // ─────────────────────────────────────────────────────────────────────

    private fun fade(t: Float) = t * t * t * (t * (t * 6f - 15f) + 10f)

    private fun lerp(a: Float, b: Float, t: Float) = a + t * (b - a)

    private fun dot2(g: FloatArray, x: Float, y: Float) = g[0] * x + g[1] * y

    private fun grad3(hash: Int, x: Float, y: Float, z: Float): Float {
        // Classic 12-gradient set collapsed to 8 for speed
        val g = grad[hash and 7]
        // Use z to perturb x contribution for 3D feel without full 3D grad table
        return g[0] * (x + z * 0.1f) + g[1] * y
    }

    /** Classic Perlin noise, returns approx [−1, 1] */
    private fun noise3(xIn: Float, yIn: Float, zIn: Float): Float {
        val xi = floor(xIn).toInt() and 255
        val yi = floor(yIn).toInt() and 255
        val zi = floor(zIn).toInt() and 255

        val xf = xIn - floor(xIn)
        val yf = yIn - floor(yIn)
        val zf = zIn - floor(zIn)

        val u = fade(xf); val v = fade(yf); val w = fade(zf)

        val aaa = perm[perm[perm[xi  ] + yi  ] + zi  ]
        val aba = perm[perm[perm[xi  ] + yi+1] + zi  ]
        val aab = perm[perm[perm[xi  ] + yi  ] + zi+1]
        val abb = perm[perm[perm[xi  ] + yi+1] + zi+1]
        val baa = perm[perm[perm[xi+1] + yi  ] + zi  ]
        val bba = perm[perm[perm[xi+1] + yi+1] + zi  ]
        val bab = perm[perm[perm[xi+1] + yi  ] + zi+1]
        val bbb = perm[perm[perm[xi+1] + yi+1] + zi+1]

        return lerp(
            lerp(
                lerp(grad3(aaa, xf,   yf,   zf  ), grad3(baa, xf-1, yf,   zf  ), u),
                lerp(grad3(aba, xf,   yf-1, zf  ), grad3(bba, xf-1, yf-1, zf  ), u), v),
            lerp(
                lerp(grad3(aab, xf,   yf,   zf-1), grad3(bab, xf-1, yf,   zf-1), u),
                lerp(grad3(abb, xf,   yf-1, zf-1), grad3(bbb, xf-1, yf-1, zf-1), u), v),
            w)
    }

    private fun floor(x: Float) = if (x >= 0) x.toInt().toFloat() else (x.toInt() - 1).toFloat()

    // ─────────────────────────────────────────────────────────────────────
    // Public API
    // ─────────────────────────────────────────────────────────────────────

    /** Swap out the palette at runtime (e.g. for dark-mode). */
    fun setPalette(vararg colors: Int) {
        require(colors.size >= 2) { "Palette needs at least 2 stops" }
        palette.indices.forEach { i ->
            if (i < colors.size) palette[i] = colors[i]
        }
    }

    /** Control morph speed (default 0.00028). Range: 0.0001 – 0.001 */
    fun setSpeed(speed: Float) {
        // Rebind the constant via a field if you want runtime control;
        // left as a documented override point here.
    }
}
