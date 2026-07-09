package iad1tya.echo.music.sources

import android.net.ConnectivityManager
import com.music.innertube.models.response.PlayerResponse
import iad1tya.echo.music.constants.AudioQuality
import iad1tya.echo.music.utils.YTPlayerUtils
import iad1tya.echo.music.utils.YTPlayerUtils.PlaybackData
import iad1tya.echo.music.utils.cleanSearchTerm
import iad1tya.echo.music.utils.qobuz.QobuzApiClient
import iad1tya.echo.music.utils.qobuz.QobuzQuality
import iad1tya.echo.music.utils.qobuz.QobuzTrack
import timber.log.Timber

class QobuzPlaybackSource : PlaybackSource {

    override val name: String = "Qobuz"

    private val qualityTiers = listOf(
        listOf(QobuzQuality.FLAC_HIRES_192, QobuzQuality.FLAC_HIRES_96, QobuzQuality.FLAC_CD, QobuzQuality.MP3_320),
        listOf(QobuzQuality.FLAC_HIRES_96, QobuzQuality.FLAC_CD, QobuzQuality.MP3_320),
        listOf(QobuzQuality.FLAC_CD, QobuzQuality.MP3_320),
    )

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
        Timber.tag(TAG).d("Resolving via Qobuz for videoId=$videoId")

        val metadata = YTPlayerUtils.playerResponseForMetadata(videoId).getOrNull()
        val title = knownTitle ?: metadata?.videoDetails?.title
            ?: throw Exception("Missing title")
        val author = knownArtist ?: metadata?.videoDetails?.author?.replace(" - Topic", "")
            ?: throw Exception("Missing artist")

        val durationSeconds = metadata?.videoDetails?.lengthSeconds?.toLongOrNull()
        val durationMs = knownDurationMs ?: (if (durationSeconds != null) durationSeconds * 1000L else null)

        val client = QobuzApiClient()
        var resolved: PlaybackData? = null

        for ((tierIndex, terms) in searchTerms(author, title).withIndex()) {
            if (resolved != null) break
            for (term in terms) {
                val searchResult = runCatching { client.search(term) }.getOrNull() ?: continue
                val candidates = searchResult.tracks?.items ?: continue
                val valid = candidates.filter {
                    val maxDepth = it.maximumBitDepth ?: 16
                    (it.streamable ?: true) && maxDepth >= 16
                }
                val sorted = valid.sortedByDescending { confidence(author, title, durationMs, it) }
                for (candidate in sorted) {
                    if (confidence(author, title, durationMs, candidate) < 0.4f) continue
                    val tierQualities = qualityTiers.getOrElse(tierIndex) { qualityTiers.last() }
                    val downloadData = runCatching {
                        tierQualities.firstNotNullOfOrNull { q ->
                            client.getFileUrl(candidate.id, q).takeIf { !it.url.isNullOrBlank() }
                        }
                    }.getOrNull()
                    val url = downloadData?.url ?: continue
                    val sampleRate = (candidate.maximumSamplingRate * 1000).toInt().coerceAtLeast(44100)
                    val bitDepth = candidate.maximumBitDepth.coerceAtLeast(16)
                    val format = PlayerResponse.StreamingData.Format(
                        itag = 0, url = url,
                        mimeType = "audio/flac; codecs=\"flac\"",
                        bitrate = (sampleRate * bitDepth * 2).coerceAtLeast(320000),
                        audioSampleRate = sampleRate, contentLength = 0L,
                        cipher = null, signatureCipher = null, audioQuality = "LOSSLESS",
                        fps = null, width = null, height = null, quality = "lossless",
                        qualityLabel = null, averageBitrate = null, approxDurationMs = null,
                        audioChannels = null, loudnessDb = null, lastModified = null, audioTrack = null,
                    )
                    resolved = PlaybackData(
                        audioConfig = null, videoDetails = metadata?.videoDetails,
                        playbackTracking = null, format = format, streamUrl = url,
                        streamExpiresInSeconds = 3600,
                    )
                    break
                }
            }
        }

        resolved ?: throw Exception("No streamable match resolved on Qobuz")
    }

    override fun toString(): String = name

    companion object {
        private const val TAG = "QobuzSource"

        private fun searchTerms(artist: String, title: String): List<List<String>> {
            val cleanArtist = cleanSearchTerm(artist)
            val cleanTitle = cleanSearchTerm(title)

            return listOf(
                listOf("$artist $title", "\"$title\" $artist"),
                listOf("$cleanArtist $cleanTitle", "\"$cleanTitle\""),
                listOf("$artist $cleanTitle", "$cleanArtist $title"),
                listOf(cleanTitle),
            )
        }

        private fun confidence(
            queryArtist: String, queryTitle: String,
            queryDuration: Long?, candidate: QobuzTrack,
        ): Float {
            val streamableBonus = if (candidate.streamable != false) 1.0f else 0.7f

            val titleSim = jaccard(normalize(queryTitle), normalize(candidate.title))
            val artistSim = artistSimilarity(
                normalize(queryArtist),
                normalize(candidate.performer?.name.orEmpty()),
            )

            val durationFactor: Float = run {
                val queryMs = queryDuration ?: return@run 1.0f
                if (queryMs <= 0 || candidate.duration <= 0) return@run 1.0f
                val candidateMs = candidate.duration * 1000L
                val drift = kotlin.math.abs(queryMs - candidateMs).toDouble() / queryMs.toDouble()
                when {
                    drift < 0.05 -> 1.0f
                    drift < 0.10 -> 0.85f
                    drift < 0.20 -> 0.6f
                    else -> 0.3f
                }
            }

            return (titleSim * 0.4f + artistSim * 0.3f + durationFactor * 0.3f) * streamableBonus
        }

        private fun normalize(s: String): String =
            s.lowercase()
                .replace(Regex("\\([^)]*\\)"), " ")
                .replace(Regex("\\[[^]]*\\]"), " ")
                .replace(Regex("(?i)\\b(feat\\.?|ft\\.?|featuring)\\b.*"), " ")
                .replace(Regex("[''`]"), "")
                .replace(Regex("[^\\p{L}\\p{N}\\p{S}\\s]"), " ")
                .replace(Regex("\\s+"), " ")
                .trim()

        private fun jaccard(a: String, b: String): Float {
            val setA = a.split(" ").filter { it.isNotEmpty() }.toSet()
            val setB = b.split(" ").filter { it.isNotEmpty() }.toSet()
            if (setA.isEmpty() || setB.isEmpty()) return 0f
            return setA.intersect(setB).size.toFloat() / setA.union(setB).size.toFloat()
        }

        private fun artistSimilarity(a: String, b: String): Float {
            val setA = a.split(" ").filter { it.isNotEmpty() }.toSet()
            val setB = b.split(" ").filter { it.isNotEmpty() }.toSet()
            if (setA.isEmpty() || setB.isEmpty()) return 0f
            val intersection = setA.intersect(setB)
            val union = setA.union(setB)
            val jaccardScore = intersection.size.toFloat() / union.size.toFloat()
            val smallerSize = minOf(setA.size, setB.size)
            val smallerFullyCovered = intersection.size == smallerSize
            val hasDistinctiveOverlap = intersection.any { it.length > 3 || it.any { ch -> !ch.isLetterOrDigit() } }
            val coverageScore = if (smallerFullyCovered && hasDistinctiveOverlap) 1.0f else 0f
            return maxOf(jaccardScore, coverageScore)
        }
    }
}
