package com.ubtrobot.smartprojector.ui.settings

import android.Manifest
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ubtrobot.smartprojector.R
import com.ubtrobot.smartprojector.bluetooth.BluetoothDelegate
import com.ubtrobot.smartprojector.bluetooth.BluetoothDeviceItem
import com.ubtrobot.smartprojector.bluetooth.BluetoothDeviceAdapter
import com.ubtrobot.smartprojector.replaceFragment
import dagger.hilt.android.AndroidEntryPoint
import pub.devrel.easypermissions.AfterPermissionGranted
import pub.devrel.easypermissions.EasyPermissions

@AndroidEntryPoint
class SettingsActivity : AppCompatActivity(), SettingsFragment.OnFragmentActionListener {

    companion object {
        private const val REQUEST_CODE_FINE_PERMISSION = 1
    }

    private lateinit var bluetoothDelegate: BluetoothDelegate
    private var btDevices = ArrayList<BluetoothDeviceItem>()
    private var adapter: BluetoothDeviceAdapter? = null
    private var btDevicesDialog: AlertDialog? = null
    private var currentSelectedDeviceItem: BluetoothDeviceItem? = null
    private var progressBar: ProgressBar? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        replaceFragment(SettingsFragment.newInstance(), R.id.fragment_container)

        bluetoothDelegate = BluetoothDelegate(
                activity = this,
                pairedDeviceAdd = { name, address ->
                    btDevices.add(BluetoothDeviceItem(name, address))
                    adapter?.notifyDataSetChanged()
                },
                newDeviceAdd = { name, address ->
                    btDevices.add(BluetoothDeviceItem(name, address))
                    adapter?.notifyDataSetChanged()
                },
                discoveryFinished = {
                    progressBar?.visibility = View.GONE
                }
        )
        bluetoothDelegate.onCreate()
        adapter = BluetoothDeviceAdapter(btDevices) { position, item ->
            currentSelectedDeviceItem = item
            btDevicesDialog?.dismiss()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        bluetoothDelegate.onActivityResult(requestCode, resultCode, data)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)
    }

    override fun onDestroy() {
        super.onDestroy()
        bluetoothDelegate.onDestroy()
    }

    override fun showBluetoothDialog() {
        showDeviceList()
    }

    @AfterPermissionGranted(REQUEST_CODE_FINE_PERMISSION)
    private fun showDeviceList() {
        if (hasFineLocationPermission()) {
            val v = layoutInflater.inflate(R.layout.dialog_bluetooth_devices, null)
            progressBar = v.findViewById(R.id.progress_bar)
            val listView = v.findViewById<RecyclerView>(R.id.dialog_recycler_view)
            listView.layoutManager = LinearLayoutManager(this)
            listView.adapter = adapter
            btDevicesDialog = AlertDialog.Builder(this)
                    .setView(v)
                    .create()

            btDevicesDialog?.show()
            progressBar?.visibility = View.VISIBLE

            btDevices.clear()
            bluetoothDelegate.queryPairedDevices()
        } else {
            EasyPermissions.requestPermissions(
                    this,
                    "打开蓝牙需要定位权限，请授予",
                    REQUEST_CODE_FINE_PERMISSION,
                    Manifest.permission.ACCESS_FINE_LOCATION
            )
        }
    }

    private fun hasFineLocationPermission() : Boolean {
        return EasyPermissions.hasPermissions(this, Manifest.permission.ACCESS_FINE_LOCATION)
    }
}