package com.ubtrobot.smartprojector.ui.tuya

import android.Manifest
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.ubtrobot.smartprojector.R
import com.ubtrobot.smartprojector.replaceFragment
import com.ubtrobot.smartprojector.utils.SystemUtil
import dagger.hilt.android.AndroidEntryPoint
import pub.devrel.easypermissions.AfterPermissionGranted
import pub.devrel.easypermissions.EasyPermissions
import timber.log.Timber

@AndroidEntryPoint
class NewDeviceActivity : AppCompatActivity() {

    companion object {
        private const val RC_LOCATION_PERMISSION = 2

        fun start(context: Context?, homeId: Long) {
            val intent = Intent(context, NewDeviceActivity::class.java)
            intent.putExtra("homeId", homeId)
            context?.startActivity(intent)
        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        SystemUtil.statusBarTransparent(window)
        setContentView(R.layout.activity_new_device)

        addNewDeviceFragmentTask()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)
    }

    @AfterPermissionGranted(RC_LOCATION_PERMISSION)
    private fun addNewDeviceFragmentTask() {
        if (hasFineLocationPermission()) {
            val homeId = intent.getLongExtra("homeId", -1)
            replaceFragment(AddDeviceFragment.newInstance(homeId), R.id.fragment_container)
        } else {
            EasyPermissions.requestPermissions(
                this,
                "App需要位置权限获取Wifi信息，请授予",
                RC_LOCATION_PERMISSION,
                Manifest.permission.ACCESS_FINE_LOCATION
            )
        }
    }

    private fun hasFineLocationPermission(): Boolean {
        return EasyPermissions.hasPermissions(this, Manifest.permission.ACCESS_FINE_LOCATION)
    }
}