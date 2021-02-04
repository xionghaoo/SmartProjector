package com.ubtrobot.smartprojector.ui.tuya

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.ubtrobot.smartprojector.R
import com.ubtrobot.smartprojector.replaceFragment
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber

@AndroidEntryPoint
class NewDeviceActivity : AppCompatActivity() {

    companion object {
        fun start(context: Context?, homeId: Long) {
            val intent = Intent(context, NewDeviceActivity::class.java)
            intent.putExtra("homeId", homeId)
            context?.startActivity(intent)
        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_device)

        val homeId = intent.getLongExtra("homeId", -1)
        replaceFragment(AddDeviceFragment.newInstance(homeId), R.id.fragment_container)
    }
}