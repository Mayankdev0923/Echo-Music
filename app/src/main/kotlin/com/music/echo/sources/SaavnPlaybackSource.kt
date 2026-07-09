package iad1tya.echo.music.sources

import android.net.ConnectivityManager
import android.util.Log
import com.music.innertube.models.Thumbnail
import com.music.innertube.models.Thumbnails
import com.music.innertube.models.response.PlayerResponse
import com.music.innertube.utils.YouTubeUrlParser
import iad1tya.echo.music.constants.AudioQuality
import iad1tya.echo.music.utils.YTPlayerUtils
import iad1tya.echo.music.utils.YTPlayerUtils.PlaybackData
import timber.log.Timber

class SaavnPlaybackSource : PlaybackSource {

    override val name: String = "Saavn"

    override suspend fun resolve(
        videoId: String,
        playlistId: String?,
        audioQuality: AudioQuality,
        connectivityManager: ConnectivityManager,
        knownArtist: String?,
        knownTitle: String?,
        knownDurationMs: Long?,
        isDownload: Boolean,
    ): Result<PlaybackData> = runCatching {
        Timber.tag(TAG).d("Resolving via Saavn for videoId=$videoId")

        val metadata = YTPlayerUtils.playerResponseForMetadata(videoId).getOrNull()
        val title = knownTitle ?: metadata?.videoDetails?.title.orEmpty()
        val artist = knownArtist ?: metadata?.videoDetails?.author?.replace(" - Topic", "").orEmpty()

        if (title.isBlank()) throw Exception("Title is blank")

        val query = "$title $artist"
            .replace("&", " ")
            .replace(",", " ")
            .replace(Regex("(?i)\\s*-\\s*topic\\b"), "")
            .replace(Regex("\\s+"), " ")
            .trim()

        Timber.tag(TAG).d("Saavn search query: \"$query\"")

        val songs = com.music.jiosaavn.SaavnService.searchSongs(query).getOrNull()
            ?: throw Exception("Saavn: no results for \"$query\"")

        val ytDuration = knownDurationMs?.let { it / 1000L }
            ?: metadata?.videoDetails?.lengthSeconds?.toLongOrNull()
            ?: 0L

        val scored = songs.map { candidate ->
            var score = 0
            score += wordOverlap(title, candidate.name, maxPts = 50)
            val saavnDuration = candidate.duration?.toLong() ?: 0L
            if (ytDuration > 0 && saavnDuration > 0) {
                val diff = kotlin.math.abs(ytDuration - saavnDuration)
                score += when {
                    diff <= 5 -> 30
                    diff <= 15 -> 15
                    else -> 0
                }
            }
            val saavnArtists = candidate.artists.primary.joinToString(" ") { it.name }
            score += wordOverlap(artist, saavnArtists, maxPts = 20)
            if (candidate.explicitContent) score += 5
            score += com.music.jiosaavn.SaavnMatcher.variantPenalty(title, candidate.name)
            candidate to score
        }

        val MIN_CONFIDENCE = 40
        val bestSong = scored.maxByOrNull { it.second }
            ?.takeIf { it.second >= MIN_CONFIDENCE }
            ?.first
            ?: throw Exception("Saavn: best score below threshold $MIN_CONFIDENCE")

        Timber.tag(TAG).d("Saavn best match: id=${bestSong.id}, name=${bestSong.name}")

        val streamUrl = com.music.jiosaavn.SaavnService.getBestStreamUrl(bestSong.id, "320kbps")
            ?: throw Exception("Saavn: no stream URL for songId=${bestSong.id}")

        val mimeType = when {
            streamUrl.contains(".flac", ignoreCase = true) -> "audio/flac; codecs=\"flac\""
            streamUrl.contains(".mp4", ignoreCase = true) || streamUrl.contains(".m4a", ignoreCase = true) -> "audio/mp4; codecs=\"mp4a.40.2\""
            else -> "audio/mp4; codecs=\"mp4a.40.2\""
        }

        val format = PlayerResponse.StreamingData.Format(
            itag = 0, url = streamUrl, mimeType = mimeType,
            bitrate = 320_000, width = null, height = null, contentLength = null,
            quality = "320kbps", fps = null, qualityLabel = null, averageBitrate = null,
            audioQuality = "320kbps", approxDurationMs = null, audioSampleRate = null,
            audioChannels = null, loudnessDb = null, lastModified = null,
            signatureCipher = null, cipher = null, audioTrack = null,
        )

        val saavnImage = bestSong.image.lastOrNull()?.url ?: bestSong.image.firstOrNull()?.url
        val updatedVideoDetails = metadata?.videoDetails?.copy(
            thumbnail = Thumbnails(
                thumbnails = listOf(Thumbnail(url = saavnImage ?: "", width = 500, height = 500))
            )
        ) ?: PlayerResponse.VideoDetails(
            videoId = videoId, title = title, author = artist, lengthSeconds = ytDuration.toString(),
            channelId = "", musicVideoType = null, viewCount = null,
            thumbnail = Thumbnails(listOf(Thumbnail(url = saavnImage ?: "", width = 500, height = 500))),
        )

        PlaybackData(
            audioConfig = metadata?.playerConfig?.audioConfig,
            videoDetails = updatedVideoDetails,
            playbackTracking = metadata?.playbackTracking,
            format = format,
            streamUrl = streamUrl,
            streamExpiresInSeconds = 3600,
            isSaavnStream = true,
        )
    }

    override fun toString(): String = name

    companion object {
        private const val TAG = "SaavnSource"

        private fun wordOverlap(a: String, b: String, maxPts: Int): Int {
            fun normalize(s: String): Set<String> =
                s.lowercase()
                    .replace(Regex("[^a-z0-9\\s]"), " ")
                    .split(Regex("\\s+"))
                    .filter { it.length > 1 }
                    .toSet()
            val setA = normalize(a)
            val setB = normalize(b)
            if (setA.isEmpty() || setB.isEmpty()) return 0
            val common = setA.intersect(setB).size
            val ratio = common.toDouble() / maxOf(setA.size, setB.size)
            return (ratio * maxPts).toInt()
        }
    }
}
