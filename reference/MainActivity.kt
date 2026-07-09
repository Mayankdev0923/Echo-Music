package com.example.meshgradient

import android.graphics.Color
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

/**
 * Drop MeshGradientView behind your real UI.
 * Use activity_main.xml below; no extra setup needed.
 */
class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Optional: swap palette for a warmer monochrome at runtime
        val gradient = findViewById<MeshGradientView>(R.id.meshGradient)
        gradient.setPalette(
            Color.parseColor("#FAFAFA"),
            Color.parseColor("#D0D0D0"),
            Color.parseColor("#909090"),
            Color.parseColor("#505050"),
            Color.parseColor("#101010"),
        )
    }
}
