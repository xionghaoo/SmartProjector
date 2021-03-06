package com.ubtrobot.smartprojector.ui.settings

import android.Manifest
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.view.View
import android.widget.ProgressBar
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ubtrobot.smartprojector.BuildConfig
import com.ubtrobot.smartprojector.GlideApp
import com.ubtrobot.smartprojector.R
import com.ubtrobot.smartprojector.bluetooth.BluetoothDelegate
import com.ubtrobot.smartprojector.bluetooth.BluetoothDeviceItem
import com.ubtrobot.smartprojector.bluetooth.BluetoothDeviceAdapter
import com.ubtrobot.smartprojector.core.ListDividerDecoration
import com.ubtrobot.smartprojector.databinding.ActivitySettingsBinding
import com.ubtrobot.smartprojector.replaceFragment
import com.ubtrobot.smartprojector.ui.settings.eyesprotect.EyesProtectSettingsFragment
import com.ubtrobot.smartprojector.ui.settings.eyesprotect.EyesProtectSettingsSelectFragment
import com.ubtrobot.smartprojector.ui.settings.programdevices.ProgramDevicesSettingsFragment
import com.ubtrobot.smartprojector.utils.SystemUtil
import dagger.hilt.android.AndroidEntryPoint
import eu.chainfire.libsuperuser.Shell
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import pub.devrel.easypermissions.AfterPermissionGranted
import pub.devrel.easypermissions.EasyPermissions
import timber.log.Timber
import kotlin.math.roundToInt

@AndroidEntryPoint
class SettingsActivity : AppCompatActivity(),
    SettingsFragment.OnFragmentActionListener
{

    companion object {
        private const val REQUEST_CODE_FINE_PERMISSION = 1
    }

    private lateinit var bluetoothDelegate: BluetoothDelegate
    private var btDevices = ArrayList<BluetoothDeviceItem>()
    private var adapter: BluetoothDeviceAdapter? = null
    private var btDevicesDialog: AlertDialog? = null
    private var currentSelectedDeviceItem: BluetoothDeviceItem? = null
    private var progressBar: ProgressBar? = null

    private lateinit var binding: ActivitySettingsBinding

    private lateinit var eyesProtectSettingsFragment: EyesProtectSettingsFragment
    private lateinit var settingsMenuAdapter: SettingsMenuAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        SystemUtil.toFullScreenMode(this)
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        GlideApp.with(this)
                .load(R.mipmap.ic_settings_bg)
                .into(binding.ivBackground)

        eyesProtectSettingsFragment = EyesProtectSettingsFragment.newInstance()

        binding.rcSettingsMenu.layoutManager = LinearLayoutManager(this)
        binding.rcSettingsMenu.addItemDecoration(ListDividerDecoration(
                lineHeight = resources.getDimension(R.dimen._1dp).roundToInt(),
                lineColor = resources.getColor(R.color.color_menu_split),
                padding = resources.getDimension(R.dimen._40dp),
                ignoreLastChildNum = 0
        ))
        settingsMenuAdapter = SettingsMenuAdapter(listOf(
                "屏幕亮度", "系统音量", "网络设置", "护眼功能", "姿势检测", "编程设备管理"
        )) { position ->
            when (position) {
                0 -> replaceFragment(GeneralSettingsFragment.newInstance(GeneralSettingsFragment.TYPE_LIGHT_ADJUST), R.id.fragment_container)
                1 -> replaceFragment(GeneralSettingsFragment.newInstance(GeneralSettingsFragment.TYPE_VOLUME_ADJUST), R.id.fragment_container)
                2 -> replaceFragment(GeneralSettingsFragment.newInstance(GeneralSettingsFragment.TYPE_NETWORK), R.id.fragment_container)
                3 -> replaceFragment(eyesProtectSettingsFragment, R.id.fragment_container)
                4 -> replaceFragment(PostureDetectFragment.newInstance(), R.id.fragment_container)
                5 -> replaceFragment(ProgramDevicesSettingsFragment.newInstance(), R.id.fragment_container)
            }
        }
        binding.rcSettingsMenu.adapter = settingsMenuAdapter

        binding.toolbar.setTitle("设置")
                .configBackButton(this)

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
        grantWriteSettingsPermission()
    }

    override fun onStart() {
        super.onStart()
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

    private fun showFirstSettingsPage() {
        replaceFragment(GeneralSettingsFragment.newInstance(GeneralSettingsFragment.TYPE_LIGHT_ADJUST), R.id.fragment_container)
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

    private fun grantWriteSettingsPermission() {
        if (Shell.SU.available()) {
            CoroutineScope(Dispatchers.Default).launch {
                Shell.Pool.SU.run("pm grant ${BuildConfig.APPLICATION_ID} ${Manifest.permission.WRITE_SETTINGS}")
                Shell.Pool.SU.run("pm grant ${BuildConfig.APPLICATION_ID} ${Settings.ACTION_MANAGE_WRITE_SETTINGS}")
                withContext(Dispatchers.Main) {
                    settingsPageTask()
                }
            }
        } else {
            settingsPageTask()
        }
    }

    private fun settingsPageTask() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (Settings.System.canWrite(this)) {
                showFirstSettingsPage()
            } else {
                AlertDialog.Builder(this)
                    .setMessage("App需要在设置里面打开读写系统设置的权限")
                    .setCancelable(false)
                    .setPositiveButton("去设置") { _, _ ->
                        SystemUtil.openSettingsWrite(this)
                    }
                    .setNegativeButton("取消") { _, _ -> finish() }
                    .show()
            }
        } else {
            showFirstSettingsPage()
        }
    }

    private fun hasFineLocationPermission() : Boolean {
        return EasyPermissions.hasPermissions(this, Manifest.permission.ACCESS_FINE_LOCATION)
    }
}