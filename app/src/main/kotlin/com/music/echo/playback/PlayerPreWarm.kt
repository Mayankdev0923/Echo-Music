package iad1tya.echo.music.playback

import android.content.Context
import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PlayerPreWarm @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private var exoPlayer: ExoPlayer? = null

    fun warmUp() {
        if (exoPlayer != null) return
        try {
            exoPlayer = ExoPlayer.Builder(context)
                .setAudioAttributes(
                    androidx.media3.common.AudioAttributes.Builder()
                        .setContentType(C.AUDIO_CONTENT_TYPE_MUSIC)
                        .setUsage(C.USAGE_MEDIA)
                        .build(),
                    false
                )
                .setHandleAudioBecomingNoisy(false)
                .setWakeMode(C.WAKE_MODE_NONE)
                .setSkipSilenceEnabled(false)
                .build()
                .apply {
                    playWhenReady = false
                    repeatMode = Player.REPEAT_MODE_OFF
                }
        } catch (_: Exception) {
        }
    }

    fun release() {
        exoPlayer?.release()
        exoPlayer = null
    }
}
