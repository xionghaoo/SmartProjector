package com.ubtrobot.smartprojector.test

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.ubtedu.ukit.project.host.ProjectHostFragment
import com.ubtedu.ukit.project.vo.Project
import com.ubtrobot.smartprojector.R
import com.ubtrobot.smartprojector.replaceFragment

class TestActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_test)
    }
}