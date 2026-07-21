package iad1tya.echo.music.playback

import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.common.util.UnstableApi
import androidx.media3.datasource.DataSpec
import androidx.media3.datasource.cache.CacheDataSource
import androidx.media3.datasource.cache.CacheWriter
import androidx.media3.exoplayer.offline.Downloader
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.cancel
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.io.IOException
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicLong

@UnstableApi
class SegmentedDownloader(
    private val mediaItem: MediaItem,
    private val cacheDataSourceFactory: CacheDataSource.Factory,
    private val parallelChunks: Int
) : Downloader {

    private val isCanceled = AtomicBoolean(false)
    private val downloadJob = Job()
    private val scope = CoroutineScope(Dispatchers.IO + downloadJob)
    private val cacheWriters = mutableListOf<CacheWriter>()

    override fun download(progressListener: Downloader.ProgressListener?) {
        val uri = mediaItem.localConfiguration?.uri ?: return
        val dataSource = cacheDataSourceFactory.createDataSource()
        
        var contentLength = C.LENGTH_UNSET.toLong()
        
        // 1. Fetch content length
        try {
            val dataSpec = DataSpec.Builder().setUri(uri).build()
            contentLength = dataSource.open(dataSpec)
            dataSource.close()
        } catch (e: Exception) {
            e.printStackTrace()
            // Fallback to progressive if length can't be fetched
        }

        val downloadedBytes = AtomicLong(0)
        
        val writerProgressListener = CacheWriter.ProgressListener { requestLength, bytesCached, newBytesCached ->
            if (contentLength != C.LENGTH_UNSET.toLong()) {
                val total = downloadedBytes.addAndGet(newBytesCached)
                progressListener?.onProgress(contentLength, total, 100f * total / contentLength)
            } else {
                progressListener?.onProgress(requestLength, bytesCached, 100f * bytesCached / requestLength)
            }
        }

        if (contentLength == C.LENGTH_UNSET.toLong() || contentLength <= 0 || parallelChunks <= 1) {
            // Progressive fallback
            val dataSpec = DataSpec.Builder().setUri(uri).setKey(mediaItem.mediaId).build()
            val cacheWriter = CacheWriter(
                cacheDataSourceFactory.createDataSource(),
                dataSpec,
                null,
                writerProgressListener
            )
            cacheWriters.add(cacheWriter)
            try {
                cacheWriter.cache()
            } catch (e: Exception) {
                if (!isCanceled.get()) throw e
            }
            return
        }

        // 2. Divide into chunks
        val chunkSize = contentLength / parallelChunks
        
        runBlocking {
            val deferreds = (0 until parallelChunks).map { i ->
                scope.async {
                    if (isCanceled.get()) return@async
                    
                    val start = i * chunkSize
                    val end = if (i == parallelChunks - 1) contentLength else (i + 1) * chunkSize
                    val length = end - start
                    
                    val dataSpec = DataSpec.Builder()
                        .setUri(uri)
                        .setKey(mediaItem.mediaId)
                        .setPosition(start)
                        .setLength(length)
                        .build()

                    val chunkDataSource = cacheDataSourceFactory.createDataSource()
                    val cacheWriter = CacheWriter(
                        chunkDataSource,
                        dataSpec,
                        null,
                        writerProgressListener
                    )
                    synchronized(cacheWriters) {
                        cacheWriters.add(cacheWriter)
                    }

                    try {
                        cacheWriter.cache()
                    } catch (e: Exception) {
                        if (!isCanceled.get()) {
                            throw e
                        }
                    }
                }
            }
            
            deferreds.awaitAll()
        }
    }

    override fun cancel() {
        isCanceled.set(true)
        downloadJob.cancel()
        synchronized(cacheWriters) {
            cacheWriters.forEach { it.cancel() }
        }
    }

    override fun remove() {
        val uri = mediaItem.localConfiguration?.uri ?: return
        val dataSource = cacheDataSourceFactory.createDataSource()
        val dataSpec = DataSpec.Builder().setUri(uri).setKey(mediaItem.mediaId).build()
        val cache = cacheDataSourceFactory.cache
        cache?.let {
            val cacheKey = cacheDataSourceFactory.cacheKeyFactory.buildCacheKey(dataSpec)
            cache.removeResource(cacheKey)
        }
    }
}

@UnstableApi
class SegmentedDownloaderFactory(
    private val cacheDataSourceFactory: CacheDataSource.Factory,
    private val parallelChunksProvider: () -> Int
) : androidx.media3.exoplayer.offline.DownloaderFactory {
    override fun createDownloader(request: androidx.media3.exoplayer.offline.DownloadRequest): Downloader {
        val mediaItem = MediaItem.Builder()
            .setUri(request.uri)
            .setMediaId(request.id)
            .build()
        return SegmentedDownloader(mediaItem, cacheDataSourceFactory, parallelChunksProvider())
    }
}
