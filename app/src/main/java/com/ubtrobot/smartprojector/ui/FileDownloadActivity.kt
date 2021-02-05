package com.ubtrobot.smartprojector.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import com.liulishuo.filedownloader.BaseDownloadTask
import com.liulishuo.filedownloader.FileDownloadListener
import com.liulishuo.filedownloader.FileDownloader
import com.ubtrobot.smartprojector.MockData
import com.ubtrobot.smartprojector.R
import com.ubtrobot.smartprojector.databinding.ActivityFileDownloadBinding
import com.ubtrobot.smartprojector.utils.ToastUtil
import timber.log.Timber
import java.io.File
import kotlin.math.roundToInt

/**
 * 文件下载测试
 */
class FileDownloadActivity : AppCompatActivity() {

    private var downloadTaskId: Int = -1
    private var targetPath: String? = null

    private var startTime: Long = 0L
    private var progressTime: Long = 0L

    private lateinit var binding: ActivityFileDownloadBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFileDownloadBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnPause.setOnClickListener {
            FileDownloader.getImpl().pause(downloadTaskId)
        }

        binding.btnResume.setOnClickListener {
            startDownload()
        }

        binding.btnCancel.setOnClickListener {
            FileDownloader.getImpl().clear(downloadTaskId, targetPath)
            binding.tvPbNum.text = ""
            binding.pbDownload.progress = 0
        }

        binding.btnDownload.setOnClickListener {
            startDownload()
        }
    }

    private fun startDownload() {
        val testApkFile = "http://cdn.llsapp.com/android/LLS-v4.0-595-20160908-143200.apk"
        val path = File(Environment.getExternalStorageDirectory(), "${Environment.DIRECTORY_DOWNLOADS}/v.mp4")
        Timber.d("path: ${path.absoluteFile}")
        targetPath = path.absolutePath
        downloadTaskId = FileDownloader.getImpl().create(MockData.video1)
            .setPath(targetPath)
            .setCallbackProgressTimes(300)
            .setMinIntervalUpdateSpeed(400)
            .setListener(object : FileDownloadListener() {
                override fun pending(
                    task: BaseDownloadTask?,
                    soFarBytes: Int,
                    totalBytes: Int
                ) {
                    startTime = System.currentTimeMillis()
                    // 开始下载
                    Timber.d("pending")
                }

                override fun progress(
                    task: BaseDownloadTask?,
                    soFarBytes: Int,
                    totalBytes: Int
                ) {
                    progressTime = System.currentTimeMillis()
                    // 下载速度
                    val speed = soFarBytes / ((progressTime - startTime) / 1000)
                    val speedTxt = if (speed > 1024) {
                        "${speed / 1024}kb/s"
                    } else {
                        "${speed}b/s"
                    }
                    binding.tvSpeed.text = speedTxt
                    // 下载进度
                    binding.pbDownload.progress = (soFarBytes.toFloat() / totalBytes * 100f).toInt()
                    binding.tvPbNum.text = "${binding.pbDownload.progress}%"
                    Timber.d("progress: ${binding.pbDownload.progress}")
                }

                override fun completed(task: BaseDownloadTask?) {
                    // 下载完成
                    ToastUtil.showToast(this@FileDownloadActivity, "下载完成")
                    binding.pbDownload.progress = 100
                    binding.tvPbNum.text = "100%"
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