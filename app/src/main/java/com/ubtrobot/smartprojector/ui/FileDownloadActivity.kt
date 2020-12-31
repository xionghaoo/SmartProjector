package com.ubtrobot.smartprojector.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import com.liulishuo.filedownloader.BaseDownloadTask
import com.liulishuo.filedownloader.FileDownloadListener
import com.liulishuo.filedownloader.FileDownloader
import com.ubtrobot.smartprojector.R
import com.ubtrobot.smartprojector.utils.ToastUtil
import kotlinx.android.synthetic.main.activity_file_download.*
import timber.log.Timber
import java.io.File
import kotlin.math.roundToInt

class FileDownloadActivity : AppCompatActivity() {

    private var downloadTaskId: Int = -1
    private var targetPath: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_file_download)

        btn_pause.setOnClickListener {
            FileDownloader.getImpl().pause(downloadTaskId)
        }

        btn_resume.setOnClickListener {
            startDownload()
        }

        btn_cancel.setOnClickListener {
            FileDownloader.getImpl().clear(downloadTaskId, targetPath)
            tv_pb_num.text = ""
            pb_download.progress = 0
        }

        btn_download.setOnClickListener {
            startDownload()
        }
    }

    private fun startDownload() {
        val path = File(Environment.getExternalStorageDirectory(), "${Environment.DIRECTORY_DOWNLOADS}/test.apk")
        Timber.d("path: ${path.absoluteFile}")
        targetPath = path.absolutePath
        downloadTaskId = FileDownloader.getImpl().create("http://cdn.llsapp.com/android/LLS-v4.0-595-20160908-143200.apk")
            .setPath(targetPath)
            .setCallbackProgressTimes(300)
            .setMinIntervalUpdateSpeed(400)
            .setListener(object : FileDownloadListener() {
                override fun pending(
                    task: BaseDownloadTask?,
                    soFarBytes: Int,
                    totalBytes: Int
                ) {
                    Timber.d("pending")
                }

                override fun progress(
                    task: BaseDownloadTask?,
                    soFarBytes: Int,
                    totalBytes: Int
                ) {
                    pb_download.progress = (soFarBytes.toFloat() / totalBytes * 100f).toInt()
                    tv_pb_num.text = "${pb_download.progress}%"
                    Timber.d("progress: ${pb_download.progress}")
                }

                override fun completed(task: BaseDownloadTask?) {
                    ToastUtil.showToast(this@FileDownloadActivity, "下载完成")
                    pb_download.progress = 100
                    tv_pb_num.text = "100%"
                    Timber.d("completed")
                }

                override fun paused(task: BaseDownloadTask?, soFarBytes: Int, totalBytes: Int) {
                    Timber.d("paused")
                }

                override fun error(task: BaseDownloadTask?, e: Throwable?) {
                    Timber.d("error: ${e?.localizedMessage}")
                }

                override fun warn(task: BaseDownloadTask?) {
                    Timber.d("warn")
                }
            })
            .start()

    }
}