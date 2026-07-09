package iad1tya.echo.music.sources

import android.net.ConnectivityManager
import iad1tya.echo.music.constants.AudioQuality
import iad1tya.echo.music.utils.PlaybackLogLevel
import iad1tya.echo.music.utils.PlaybackLogManager
import iad1tya.echo.music.utils.YTPlayerUtils
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.withTimeoutOrNull
import timber.log.Timber

object PlaybackSourceRegistry {

    private val TAG = "PlaybackSourceRegistry"

    private val altSources: List<PlaybackSource> = listOf(
        SaavnPlaybackSource(),
        QobuzPlaybackSource(),
    )

    suspend fun resolveWithFallback(
        videoId: String,
        playlistId: String?,
        audioQuality: AudioQuality,
        connectivityManager: ConnectivityManager,
        context: android.content.Context?,
        knownArtist: String?,
        knownTitle: String?,
        knownDurationMs: Long?,
        isDownload: Boolean,
        ytResolver: suspend () -> Result<YTPlayerUtils.PlaybackData>,
    ): Result<YTPlayerUtils.PlaybackData> {

        val eligibleSources = altSources.filter { it.isEligible(audioQuality) }

        if (eligibleSources.isEmpty()) {
            Timber.tag(TAG).d("No alternative sources eligible for $audioQuality, using YouTube only")
            return ytResolver()
        }

        val primaryTimeoutMs = when (audioQuality) {
            AudioQuality.LOSSLESS -> 12_000L
            AudioQuality.SAAVN -> 15_000L
            else -> 0L
        }

        return resolveParallel(
            videoId = videoId,
            playlistId = playlistId,
            audioQuality = audioQuality,
            connectivityManager = connectivityManager,
            knownArtist = knownArtist,
            knownTitle = knownTitle,
            knownDurationMs = knownDurationMs,
            isDownload = isDownload,
            eligibleSources = eligibleSources,
            primaryTimeoutMs = primaryTimeoutMs,
            ytResolver = ytResolver,
        )
    }

    private suspend fun resolveParallel(
        videoId: String,
        playlistId: String?,
        audioQuality: AudioQuality,
        connectivityManager: ConnectivityManager,
        knownArtist: String?,
        knownTitle: String?,
        knownDurationMs: Long?,
        isDownload: Boolean,
        eligibleSources: List<PlaybackSource>,
        primaryTimeoutMs: Long,
        ytResolver: suspend () -> Result<YTPlayerUtils.PlaybackData>,
    ): Result<YTPlayerUtils.PlaybackData> {

        Timber.tag(TAG).d("resolveParallel: quality=$audioQuality, altSources=${eligibleSources.map { it.name }}, primaryTimeoutMs=$primaryTimeoutMs")

        if (primaryTimeoutMs > 0L && eligibleSources.isNotEmpty()) {
            val fastResult = withTimeoutOrNull(primaryTimeoutMs) {
                coroutineScope {
                    val deferred = eligibleSources.map { source ->
                        async {
                            source.resolve(
                                videoId = videoId,
                                playlistId = playlistId,
                                audioQuality = audioQuality,
                                connectivityManager = connectivityManager,
                                knownArtist = knownArtist,
                                knownTitle = knownTitle,
                                knownDurationMs = knownDurationMs,
                                isDownload = isDownload,
                            )
                        }
                    }

                    var firstSuccess: Result<YTPlayerUtils.PlaybackData>? = null
                    for (d in deferred) {
                        val result = d.await()
                        if (result.isSuccess) {
                            firstSuccess = result
                            break
                        }
                    }
                    firstSuccess
                }
            }

            if (fastResult != null && fastResult.isSuccess) {
                Timber.tag(TAG).d("Fast path succeeded: source=${fastResult.getOrNull()?.let { getSourceName(it) }}")
                PlaybackLogManager.log(PlaybackLogLevel.INFO, "Fast path resolved", getSourceName(fastResult.getOrNull()))
                return fastResult
            }

            Timber.tag(TAG).d("Fast path timed out or all alt sources failed, falling back to YouTube")
        }

        val ytResult = ytResolver()
        if (ytResult.isSuccess) {
            Timber.tag(TAG).d("YouTube resolver succeeded")
            return ytResult
        }

        if (primaryTimeoutMs <= 0L && eligibleSources.isNotEmpty()) {
            Timber.tag(TAG).d("YouTube failed, trying alt sources in parallel")
            return coroutineScope {
                val deferred = eligibleSources.map { source ->
                    async {
                        source.resolve(
                            videoId = videoId,
                            playlistId = playlistId,
                            audioQuality = audioQuality,
                            connectivityManager = connectivityManager,
                            knownArtist = knownArtist,
                            knownTitle = knownTitle,
                            knownDurationMs = knownDurationMs,
                            isDownload = isDownload,
                        )
                    }
                }

                var lastFailure: Result<YTPlayerUtils.PlaybackData>? = null
                for (result in deferred.map { it.await() }) {
                    if (result.isSuccess) return@coroutineScope result
                    lastFailure = result
                }
                lastFailure ?: Result.failure(Exception("All sources failed"))
            }
        }

        return ytResult
    }

    private fun getSourceName(data: YTPlayerUtils.PlaybackData?): String {
        if (data == null) return "unknown"
        return when {
            data.isSaavnStream -> "Saavn"
            data.streamUrl.contains("qobuz", ignoreCase = true) || data.format.mimeType.contains("flac") -> "Qobuz"
            else -> "YouTube"
        }
    }

    private fun PlaybackSource.isEligible(quality: AudioQuality): Boolean {
        return when (this) {
            is SaavnPlaybackSource -> quality != AudioQuality.OPUS
            is QobuzPlaybackSource -> quality == AudioQuality.LOSSLESS
            else -> false
        }
    }
}
