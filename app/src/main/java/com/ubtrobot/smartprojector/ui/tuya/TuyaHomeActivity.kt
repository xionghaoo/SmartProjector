package com.ubtrobot.smartprojector.ui.tuya

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.ubtrobot.smartprojector.R
import com.ubtrobot.smartprojector.replaceFragment

class TuyaHomeActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tuya_home)

        replaceFragment(TuyaHomeFragment.newInstance(), R.id.fragment_container)
    }
}