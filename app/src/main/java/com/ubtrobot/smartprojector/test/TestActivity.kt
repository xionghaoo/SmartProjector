package com.ubtrobot.smartprojector.test

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.ubtedu.ukit.project.host.ProjectHostFragment
import com.ubtedu.ukit.project.vo.Project
import com.ubtrobot.smartprojector.R
import com.ubtrobot.smartprojector.replaceFragment
import eu.chainfire.libsuperuser.Shell
import timber.log.Timber

class TestActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_test)

        Thread {
            val result = Shell.SU.run("cd /data/misc/wifi/")
            Timber.d("result: ${result?.first()}")
            val r = Shell.SU.run("cat wpa_supplicant.conf")
        }.start()
    }
}