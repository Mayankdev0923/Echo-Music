package iad1tya.echo.music.sources

import android.net.ConnectivityManager
import com.music.innertube.models.response.PlayerResponse
import iad1tya.echo.music.constants.AudioQuality
import iad1tya.echo.music.utils.YTPlayerUtils.PlaybackData

interface PlaybackSource {
    val name: String

    suspend fun resolve(
        videoId: String,
        playlistId: String?,
        audioQuality: AudioQuality,
        connectivityManager: ConnectivityManager,
        knownArtist: String?,
        knownTitle: String?,
        knownDurationMs: Long?,
        isDownload: Boolean,
    ): Result<PlaybackData>
}
