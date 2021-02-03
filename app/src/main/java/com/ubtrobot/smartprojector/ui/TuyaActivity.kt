package com.ubtrobot.smartprojector.ui

import android.Manifest
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.wifi.WifiManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.annotation.RequiresApi
import com.tuya.smart.android.user.api.ILoginCallback
import com.tuya.smart.android.user.api.IRegisterCallback
import com.tuya.smart.android.user.api.IValidateCallback
import com.tuya.smart.android.user.bean.User
import com.tuya.smart.home.sdk.TuyaHomeSdk
import com.tuya.smart.home.sdk.bean.HomeBean
import com.tuya.smart.home.sdk.builder.TuyaGwSubDevActivatorBuilder
import com.tuya.smart.home.sdk.callback.ITuyaGetHomeListCallback
import com.tuya.smart.home.sdk.callback.ITuyaHomeResultCallback
import com.tuya.smart.sdk.api.IDevListener
import com.tuya.smart.sdk.api.ITuyaDevice
import com.tuya.smart.sdk.api.ITuyaSmartActivatorListener
import com.tuya.smart.sdk.bean.DeviceBean
import com.ubtrobot.smartprojector.R
import com.ubtrobot.smartprojector.replaceFragment
import com.ubtrobot.smartprojector.utils.ToastUtil
import com.ubtrobot.smartprojector.wifi.AccessPoint
import com.ubtrobot.smartprojector.wifi.WifiFragment
import kotlinx.android.synthetic.main.activity_tuya.*
import pub.devrel.easypermissions.AfterPermissionGranted
import pub.devrel.easypermissions.EasyPermissions
import timber.log.Timber

/**
 * 涂鸦测试页面
 */
class TuyaActivity : AppCompatActivity() {

    companion object {
        private const val RC_LOCATION_PERMISSION = 2
    }

    private lateinit var wifiFragment: WifiFragment

    private var homeId: Long? = null
    private var gwDeviceId: String? = null
    private var sosDeviceId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tuya)

        btn_phone_validate_code.setOnClickListener {
            TuyaHomeSdk.getUserInstance().getValidateCode(
                "86",
                "18617039316",
                object : IValidateCallback {
                    override fun onSuccess() {
                        Timber.d("验证成功")
                        ToastUtil.showToast(this@TuyaActivity, "验证码已发送")
                    }

                    override fun onError(code: String?, error: String?) {
                        Timber.d("验证失败：$code, $error")
                        ToastUtil.showToast(this@TuyaActivity, "验证失败：$code, $error")
                    }
                }
            )
        }

        btn_register.setOnClickListener {
            val account = edt_account.text.toString()
            val password = edt_password.text.toString()
            val validateCode = edt_validate_code.text.toString()
            TuyaHomeSdk.getUserInstance().registerAccountWithPhone(
                "86",
                account,
                password,
                validateCode,
                object : IRegisterCallback {
                    override fun onSuccess(user: User?) {
                        Timber.d("注册成功")
                        ToastUtil.showToast(this@TuyaActivity, "注册成功")
                    }

                    override fun onError(code: String?, error: String?) {
                        Timber.d("注册失败")
                        ToastUtil.showToast(this@TuyaActivity, "注册失败: $error")
                    }
                }
            )
        }

        edt_account.setText("18617039316")
        edt_password.setText("123456")
        btn_login.setOnClickListener {
            val account = edt_account.text.toString()
            val password = edt_password.text.toString()
            TuyaHomeSdk.getUserInstance().loginWithPhonePassword(
                "86",
                account,
                password,
                object : ILoginCallback {
                    override fun onSuccess(user: User?) {
                        ToastUtil.showToast(this@TuyaActivity, "登录成功")
                    }

                    override fun onError(code: String?, error: String?) {
                        ToastUtil.showToast(this@TuyaActivity, "登陆失败")
                    }
                }
            )
        }

        wifiFragment = WifiFragment.newInstance()
        replaceFragment(wifiFragment, R.id.fragment_container)

        btn_wifi_scan.setOnClickListener {
            wifiScanTask()
        }

        btn_wifi_scan_stop.setOnClickListener {
            wifiFragment.stopScan()
        }

        btn_home_query.setOnClickListener {
            tuyaHomeQuery()
        }

        btn_home_create.setOnClickListener {
            tuyaHomeCreate()
        }

        btn_config_gw.setOnClickListener {
            wifiFragment.getToken(homeId)
        }

        btn_home_device_query.setOnClickListener {
            tuyaHomeDevicesQuery()
        }

        btn_config_sub_dev.setOnClickListener {
            tuyaSubDeviceConfig()
        }

        btn_dev_listener.setOnClickListener {
            deviceListener()
        }
    }

    override fun onResume() {
        super.onResume()

    }

    override fun onDestroy() {
        wifiFragment.stopScan()
        super.onDestroy()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)
    }

    @AfterPermissionGranted(RC_LOCATION_PERMISSION)
    private fun wifiScanTask() {
        if (hasFineLocationPermission()) {
            wifiFragment.startScan()
        } else {
            EasyPermissions.requestPermissions(
                    this,
                    "App需要位置权限进行Wifi扫描，请授予",
                    RC_LOCATION_PERMISSION,
                    Manifest.permission.ACCESS_FINE_LOCATION
            )
        }
    }

    private fun hasFineLocationPermission(): Boolean {
        return EasyPermissions.hasPermissions(this, Manifest.permission.ACCESS_FINE_LOCATION)
    }

    private fun tuyaHomeQuery() {
        TuyaHomeSdk.getHomeManagerInstance().queryHomeList(object : ITuyaGetHomeListCallback {
            override fun onSuccess(homeBeans: MutableList<HomeBean>?) {
                Timber.d("家庭数量: ${homeBeans?.size}")
                if (homeBeans?.isNotEmpty() == true) {
                    val home = homeBeans.first()
                    homeId = home.homeId
                    Timber.d("家庭：${home.homeId}, ${home.name}, ${home.deviceList.size}")
                }
            }

            override fun onError(errorCode: String?, error: String?) {
                Timber.d("家庭查询失败: $errorCode, $error")
            }
        })
    }

    private fun tuyaHomeCreate() {
        Timber.d("create home: x_home")
        TuyaHomeSdk.getHomeManagerInstance().createHome(
            "x_home",
            0.0,
            0.0,
            null,
            List(1) { index -> "classroom" },
            object : ITuyaHomeResultCallback {
                override fun onSuccess(bean: HomeBean?) {
                    homeId = bean?.homeId
                    Timber.d("创建家庭成功， ${bean?.homeId}")
                }

                override fun onError(errorCode: String?, errorMsg: String?) {
                    Timber.d("创建家庭失败: $errorCode, $errorMsg")
                }
            }
        )
    }

    private fun tuyaHomeDevicesQuery() {
        if (homeId == null) return
        TuyaHomeSdk.newHomeInstance(homeId!!).getHomeDetail(object : ITuyaHomeResultCallback {
            override fun onSuccess(bean: HomeBean?) {
                val deviceList = bean?.deviceList
                Timber.d("家庭设备查询成功： ${deviceList?.size}")
                deviceList?.forEach { d ->
                    // gwType: If device is virtual, the filed value is "v" , else is "s"
                    Timber.d("设备：${d.dpName}, ${d.devId}, gwType: ${d.gwType}, pid: ${d.productId}, isZigBeeWifi: ${d.isZigBeeWifi}, isOnline: ${d.isOnline}")
                    d.dps.forEach { p ->
                        Timber.d("功能点: ${p.key}, ${p.value}")
                    }
                    if (d.isZigBeeWifi) {
                        gwDeviceId = d.devId
                    } else {
                        sosDeviceId = d.devId
                    }
                }
            }

            override fun onError(errorCode: String?, errorMsg: String?) {
                Timber.d("家庭设备查询失败: $errorCode, $errorMsg")
            }
        })
    }

    private fun tuyaSubDeviceConfig() {
        val builder = TuyaGwSubDevActivatorBuilder()
            .setDevId(gwDeviceId)
            .setTimeOut(100)
            .setListener(object : ITuyaSmartActivatorListener {
                override fun onError(errorCode: String?, errorMsg: String?) {
                    Timber.d("tuyaSubDeviceConfig error：$errorCode, $errorMsg")
                }

                override fun onActiveSuccess(devResp: DeviceBean?) {
                    // 子设备发现回调
                    Timber.d("tuyaSubDeviceConfig onActiveSuccess 发现设备：${devResp?.dpName}, ${devResp?.devId}")
                }

                override fun onStep(step: String?, data: Any?) {
                    // 网关设备发现回调
                    Timber.d("tuyaSubDeviceConfig onStep: $step, ${data}")
                    if (step == "device_find") {
                        // 发现设备 6c94af80222594a3dfv4r3
                        val deviceId = data as? String
                        Timber.d("发现设备： $deviceId")
                    } else if (step == "device_bind_success") {
                        val dev = data as? DeviceBean
                        dev?.apply {
                            Timber.d("激活设备成功: $dpName, $devId")
                        }
                    }
                }
            })

        val activator = TuyaHomeSdk.getActivatorInstance().newGwSubDevActivator(builder)
        activator.start()
    }

    private fun deviceListener() {
        if (sosDeviceId == null) return
        Timber.d("开始设备监听")
        val device = TuyaHomeSdk.newDeviceInstance(sosDeviceId)
        device.registerDevListener(object : IDevListener {
            override fun onDpUpdate(devId: String?, dpStr: String?) {
                Timber.d("onDpUpdate")
            }

            override fun onRemoved(devId: String?) {
                Timber.d("onRemoved")
            }

            override fun onStatusChanged(devId: String?, online: Boolean) {
                Timber.d("onStatusChanged")
            }

            override fun onNetworkStatusChanged(devId: String?, status: Boolean) {
                Timber.d("onNetworkStatusChanged")
            }

            override fun onDevInfoUpdate(devId: String?) {
                Timber.d("onDevInfoUpdate")
            }
        })
    }
}