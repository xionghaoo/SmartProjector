package com.ubtrobot.smartprojector.ui

import android.app.Notification
import android.content.Context
import com.google.android.exoplayer2.database.DatabaseProvider
import com.google.android.exoplayer2.database.ExoDatabaseProvider
import com.google.android.exoplayer2.offline.DefaultDownloadIndex
import com.google.android.exoplayer2.offline.Download
import com.google.android.exoplayer2.offline.DownloadManager
import com.google.android.exoplayer2.offline.DownloadService
import com.google.android.exoplayer2.scheduler.Scheduler
import com.google.android.exoplayer2.upstream.DataSource
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory
import com.google.android.exoplayer2.upstream.HttpDataSource
import com.google.android.exoplayer2.upstream.cache.Cache
import com.google.android.exoplayer2.upstream.cache.NoOpCacheEvictor
import com.google.android.exoplayer2.upstream.cache.SimpleCache
import com.ubtrobot.smartprojector.R
import java.io.File
import java.util.concurrent.Executors

class VideoDownloadService : DownloadService(
        FOREGROUND_NOTIFICATION_ID,
        DEFAULT_FOREGROUND_NOTIFICATION_UPDATE_INTERVAL,
        "download_channel",
        R.string.exo_download_notification_channel_name
) {
    companion object {
        private const val JOB_ID = 1
        private const val FOREGROUND_NOTIFICATION_ID = 1
        private const val DOWNLOAD_CONTENT_DIRECTORY = "downloads"

        private var databaseProvider: ExoDatabaseProvider? = null
        private var downloadCache: Cache? = null
        private var downloadManager: DownloadManager? = null
        private var httpDataSourceFactory: HttpDataSource.Factory? = null

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
                        Executors.newFixedThreadPool( /* nThreads= */6))
//                downloadTracker = DownloadTracker(context, getHttpDataSourceFactory(context), downloadManager)
            }
        }

        @Synchronized
        fun getHttpDataSourceFactory(context: Context): HttpDataSource.Factory {
            if (httpDataSourceFactory == null) {
//                context = context.applicationContext
//                val cronetEngineWrapper = CronetEngineWrapper(context)
//                httpDataSourceFactory = CronetDataSourceFactory(cronetEngineWrapper, Executors.newSingleThreadExecutor())
                httpDataSourceFactory = DefaultHttpDataSourceFactory()
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
        private fun getDownloadCache(context: Context): Cache {
            if (downloadCache == null) {
                val downloadContentDirectory: File = File(getDownloadDirectory(context), DOWNLOAD_CONTENT_DIRECTORY)
                downloadCache = SimpleCache(
                        downloadContentDirectory, NoOpCacheEvictor(), getDatabaseProvider(context))
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
    }



    override fun getDownloadManager(): DownloadManager {
        val databaseProvider = getDatabaseProvider(this)

        TODO("Not yet implemented")
    }

    override fun getScheduler(): Scheduler? {
        TODO("Not yet implemented")
    }

    override fun getForegroundNotification(downloads: MutableList<Download>): Notification {
        TODO("Not yet implemented")
    }
}