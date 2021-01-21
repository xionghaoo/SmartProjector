package com.ubtrobot.smartprojector.ui.video

import android.app.Notification
import android.content.Context
import android.net.Uri
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
import com.google.android.exoplayer2.util.Log
import com.ubtrobot.smartprojector.R
import timber.log.Timber
import java.io.File
import java.io.IOException
import java.util.concurrent.Executors

/**
 * 视频缓存下载服务
 */
class VideoCacheDownloadService : DownloadService(
    FOREGROUND_NOTIFICATION_ID,
    DEFAULT_FOREGROUND_NOTIFICATION_UPDATE_INTERVAL,
    DOWNLOAD_NOTIFICATION_CHANNEL_ID,
    R.string.exo_download_notification_channel_name
) {
    companion object {
        private const val JOB_ID = 1
        private const val FOREGROUND_NOTIFICATION_ID = 1
        private const val DOWNLOAD_NOTIFICATION_CHANNEL_ID = "download_channel"

        private var downloadListener: DownloadManager.Listener? = null

        fun start(context: Context, downloadVideoUrl: String) {
//            VideoDownloadHelper.setOfflineDownloadHelper(context, downloadVideoUrl.toUri())
            val downloadRequest = DownloadRequest.Builder(
                downloadVideoUrl,
                downloadVideoUrl.toUri()
            ).build()
            sendAddDownload(
                context,
                VideoCacheDownloadService::class.java,
                downloadRequest,
                false
            )
        }

        fun setDownloadListener(listener: DownloadManager.Listener) {
            downloadListener = listener
        }

        /**
         * 删除缓存
         */
        fun removeDownload(context: Context, cacheId: String) {
            sendRemoveDownload(
                context,
                VideoCacheDownloadService::class.java,
                cacheId,
                false
            )
        }

//        fun clearCache(context: Context) {
//            Thread {
//                VideoDownloadHelper.getDownloadCache(context).removeResource(VideoActivity.TEST_VIDEO)
//                Timber.d("clear cache")
//            }.start()
//        }
    }

    override fun getDownloadManager(): DownloadManager {
        val downloadManager = VideoDownloadHelper.getCacheDownloadManager(this)
        downloadListener?.also {
            downloadManager?.addListener(it)
        }
        return downloadManager!!
    }

    override fun getScheduler(): Scheduler? {
        return if (Build.VERSION.SDK_INT >= 21) PlatformScheduler(this, JOB_ID) else null
    }

    override fun getForegroundNotification(downloads: MutableList<Download>): Notification {
        return VideoDownloadHelper.getDownloadNotificationHelper(this)
            .buildProgressNotification(
                this,
                R.drawable.ic_launcher_foreground,
                null,
                null,
                downloads
            )
    }
}