package com.ubtrobot.smartprojector.ui

import android.app.Notification
import android.content.Context
import android.os.Build
import androidx.core.net.toUri
import com.google.android.exoplayer2.DefaultRenderersFactory
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.database.DatabaseProvider
import com.google.android.exoplayer2.database.ExoDatabaseProvider
import com.google.android.exoplayer2.ext.cronet.CronetDataSourceFactory
import com.google.android.exoplayer2.ext.cronet.CronetEngineWrapper
import com.google.android.exoplayer2.offline.*
import com.google.android.exoplayer2.scheduler.PlatformScheduler
import com.google.android.exoplayer2.scheduler.Scheduler
import com.google.android.exoplayer2.ui.DownloadNotificationHelper
import com.google.android.exoplayer2.upstream.DataSource
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.upstream.HttpDataSource
import com.google.android.exoplayer2.upstream.cache.Cache
import com.google.android.exoplayer2.upstream.cache.CacheDataSource
import com.google.android.exoplayer2.upstream.cache.NoOpCacheEvictor
import com.google.android.exoplayer2.upstream.cache.SimpleCache
import com.ubtrobot.smartprojector.R
import java.io.File
import java.util.concurrent.Executors

class VideoDownloadService : DownloadService(
    FOREGROUND_NOTIFICATION_ID,
    DEFAULT_FOREGROUND_NOTIFICATION_UPDATE_INTERVAL,
    DOWNLOAD_NOTIFICATION_CHANNEL_ID,
    R.string.exo_download_notification_channel_name
) {
    companion object {
        private const val JOB_ID = 1
        private const val FOREGROUND_NOTIFICATION_ID = 1
        private const val DOWNLOAD_CONTENT_DIRECTORY = "downloads"
        private const val DOWNLOAD_NOTIFICATION_CHANNEL_ID = "download_channel"

        private var databaseProvider: ExoDatabaseProvider? = null
        private var downloadCache: Cache? = null
        private var downloadManager: DownloadManager? = null
        private var httpDataSourceFactory: HttpDataSource.Factory? = null
        private var dataSourceFactory: DataSource.Factory? = null

        private var downloadHelper: DownloadHelper? = null

        private var downloadNotificationHelper: DownloadNotificationHelper? = null

        fun start(context: Context) {
            val downloadVideoUri = VideoActivity.TEST_VIDEO.toUri()
            val item = MediaItem.fromUri(downloadVideoUri)
            downloadHelper = DownloadHelper.forMediaItem(
                context,
                item,
                DefaultRenderersFactory(context.applicationContext),
                httpDataSourceFactory
            )

            val downloadRequest = DownloadRequest.Builder(VideoActivity.TEST_VIDEO, downloadVideoUri).build()
            sendAddDownload(
                context,
                VideoDownloadService::class.java,
                downloadRequest,
                false
            )
        }

        @Synchronized
        private fun ensureDownloadManagerInitialized(context: Context) {
            if (downloadManager == null) {
                val downloadIndex = DefaultDownloadIndex(getDatabaseProvider(context))
//                upgradeActionFile(
//                        context, DOWNLOAD_ACTION_FILE, downloadIndex,  /* addNewDownloadsAsCompleted= */false)
//                upgradeActionFile(
//                        context, DOWNLOAD_TRACKER_ACTION_FILE,
//                        downloadIndex,  /* addNewDownloadsAsCompleted= */
//                        true)
                downloadManager = DownloadManager(
                    context,
                    getDatabaseProvider(context),
                    getDownloadCache(context),
                    getHttpDataSourceFactory(context),
                    Executors.newFixedThreadPool( /* nThreads= */6)
                )
//                downloadTracker = DownloadTracker(context, getHttpDataSourceFactory(context), downloadManager)

            }
        }

        @Synchronized
        fun getHttpDataSourceFactory(context: Context): HttpDataSource.Factory {
            if (httpDataSourceFactory == null) {
                val cronetEngineWrapper = CronetEngineWrapper(context.applicationContext)
                httpDataSourceFactory = CronetDataSourceFactory(
                    cronetEngineWrapper,
                    Executors.newSingleThreadExecutor()
                )
            }
            return httpDataSourceFactory!!
        }

        @Synchronized
        private fun getDatabaseProvider(context: Context) : DatabaseProvider {
            if (databaseProvider == null) {
                databaseProvider = ExoDatabaseProvider(context)
            }
            return databaseProvider!!
        }

        @Synchronized
        fun getDataSourceFactory(context: Context): DataSource.Factory {
            if (dataSourceFactory == null) {
                val upstreamFactory = DefaultDataSourceFactory(
                    context,
                    getHttpDataSourceFactory(context)
                )
                dataSourceFactory = buildReadOnlyCacheDataSource(
                    upstreamFactory, getDownloadCache(context)
                )
            }
            return dataSourceFactory!!
        }

        private fun buildReadOnlyCacheDataSource(
            upstreamFactory: DataSource.Factory, cache: Cache
        ): CacheDataSource.Factory {
            return CacheDataSource.Factory()
                .setCache(cache)
                .setUpstreamDataSourceFactory(upstreamFactory)
                .setCacheWriteDataSinkFactory(null)
                .setFlags(CacheDataSource.FLAG_IGNORE_CACHE_ON_ERROR)
        }

        @Synchronized
        private fun getDownloadCache(context: Context): Cache {
            if (downloadCache == null) {
                val downloadContentDirectory: File = File(
                    getDownloadDirectory(context),
                    DOWNLOAD_CONTENT_DIRECTORY
                )
                downloadCache = SimpleCache(
                    downloadContentDirectory, NoOpCacheEvictor(), getDatabaseProvider(context)
                )
            }
            return downloadCache!!
        }

        @Synchronized
        private fun getDownloadDirectory(context: Context): File? {
            var downloadDirectory = context.getExternalFilesDir( /* type= */null)
            if (downloadDirectory == null) {
                downloadDirectory = context.filesDir
            }
            return downloadDirectory
        }

        private fun getDownloadNotificationHelper(context: Context) : DownloadNotificationHelper {
            if (downloadNotificationHelper == null) {
                downloadNotificationHelper = DownloadNotificationHelper(
                    context,
                    DOWNLOAD_NOTIFICATION_CHANNEL_ID
                )
            }
            return downloadNotificationHelper!!
        }

        private fun getMyDownloadManager(context: Context) : DownloadManager? {
            ensureDownloadManagerInitialized(context)
            return downloadManager
        }
    }



    override fun getDownloadManager(): DownloadManager {
        return getMyDownloadManager(this)!!
    }

    override fun getScheduler(): Scheduler? {
        return if (Build.VERSION.SDK_INT >= 21) PlatformScheduler(this, JOB_ID) else null
    }

    override fun getForegroundNotification(downloads: MutableList<Download>): Notification {
        return getDownloadNotificationHelper(this)
            .buildProgressNotification(
                this,
                R.drawable.ic_launcher_foreground,
                null,
                null,
                downloads
            )
    }
}