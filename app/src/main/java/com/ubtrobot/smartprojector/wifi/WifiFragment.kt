package com.ubtrobot.smartprojector.wifi

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.net.wifi.WifiManager
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.tuya.smart.home.sdk.TuyaHomeSdk
import com.tuya.smart.home.sdk.builder.ActivatorBuilder
import com.tuya.smart.sdk.api.ITuyaActivator
import com.tuya.smart.sdk.api.ITuyaActivatorCreateToken
import com.tuya.smart.sdk.api.ITuyaSmartActivatorListener
import com.tuya.smart.sdk.bean.DeviceBean
import com.tuya.smart.sdk.enums.ActivatorModelEnum
import com.ubtrobot.smartprojector.R
import com.ubtrobot.smartprojector.utils.PromptDialog
import com.ubtrobot.smartprojector.utils.ToastUtil
import kotlinx.android.synthetic.main.fragment_wifi.*
import timber.log.Timber


/**
 * Wifi扫描
 */
class WifiFragment : Fragment() {

    private lateinit var wifiManager: WifiManager
    private var wifiScanReceiver: BroadcastReceiver? = null
    private lateinit var adapter: AccessPointAdapter
    private var homeWifiSSID: String? = null
    private var tuyaDeviceToken: String? = null
    private var homeWifiPwd: String? = null
    private var activator: ITuyaActivator? = null

    override fun onDetach() {
        activator?.onDestroy()
        super.onDetach()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_wifi, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        rc_ssid_list.layoutManager = LinearLayoutManager(requireContext())
        adapter = AccessPointAdapter(emptyList())
        rc_ssid_list.adapter = adapter

        requireContext().apply {
            wifiManager = applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
            // 获取本机WIFI
            val connManager = applicationContext.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val networkInfo = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI)
            if (networkInfo?.isConnected == true) {
                val connInfo = wifiManager.connectionInfo
                homeWifiSSID = connInfo.ssid.substring(1, connInfo.ssid.length - 1)
                tv_connected_wifi.text = "已连接wifi: ${homeWifiSSID}"
            } else {
                tv_connected_wifi.text = "Wifi未连接"
            }
        }

        btn_config_home_wifi.setOnClickListener {
            PromptDialog.Builder(requireContext())
                .setView(R.layout.dialog_config_home_wifi)
                .configView { v ->
                    v.findViewById<TextView>(R.id.tv_wifi_ssid).text = homeWifiSSID
                    v.findViewById<EditText>(R.id.edt_wifi_pwd).addTextChangedListener(
                            onTextChanged = { text, start, before, count ->
                                homeWifiPwd = text?.toString()
                            }
                    )
                }
                .build()
                .show()
        }

        getToken()

    }

    // 配网token
    private fun getToken() {
        TuyaHomeSdk.getActivatorInstance().getActivatorToken(object : ITuyaActivatorCreateToken {
            override fun onSuccess(token: String?) {
                tuyaDeviceToken = token
                Timber.d("get token success: $token")
                startFindTuyaDevices()
            }

            override fun onFailure(errorCode: String?, errorMsg: String?) {
                Timber.d("get token failre: $errorMsg")
            }
        })
    }

    private fun startFindTuyaDevices() {
        homeWifiPwd = "Ubtech@11F"
        Timber.d("SSID: ${homeWifiSSID}, wifiPWD: $homeWifiPwd, deviceToken: $tuyaDeviceToken")
        val builder = ActivatorBuilder()
            .setSsid(homeWifiSSID)
            .setContext(requireContext())
            .setPassword(homeWifiPwd)
            .setActivatorModel(ActivatorModelEnum.TY_EZ)
            .setTimeOut(30)
            .setToken(tuyaDeviceToken)
            .setListener(object : ITuyaSmartActivatorListener {
                override fun onError(errorCode: String?, errorMsg: String?) {
                    Timber.d("startFindTuyaDevices error：$errorCode, $errorMsg")
                }

                override fun onActiveSuccess(devResp: DeviceBean?) {
                    // 多个设备同时配网，将多次回调
                    Timber.d("发现设备：${devResp?.dpName}")
                }

                override fun onStep(step: String?, data: Any?) {
                    Timber.d("startFindTuyaDevices onStep")
                }
            })

        activator = TuyaHomeSdk.getActivatorInstance().newMultiActivator(builder)
        activator?.start()
    }

    fun startScan() {
        activity?.apply {
            wifiScanReceiver = object : BroadcastReceiver() {

                @RequiresApi(Build.VERSION_CODES.M)
                override fun onReceive(context: Context, intent: Intent) {
                    val success = intent.getBooleanExtra(WifiManager.EXTRA_RESULTS_UPDATED, false)
                    stopScan()
                    if (success) {
                        scanSuccess()
                    } else {
                        scanFailure()
                    }
                }
            }

            val intentFilter = IntentFilter()
            intentFilter.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION)
            registerReceiver(wifiScanReceiver, intentFilter)

            radar_view.visibility = View.VISIBLE
            radar_view.start()
            val success = wifiManager.startScan()
            Timber.d("wifi scan: $success")
            if (!success) {
                // scan failure handling
                scanFailure()
            }
        }
    }

    fun stopScan() {
        activity?.apply {
            if (wifiScanReceiver != null) {
                unregisterReceiver(wifiScanReceiver)
                wifiScanReceiver = null
                radar_view.stop()
                radar_view.visibility = View.GONE
            }
        }
    }

    private fun scanSuccess() {
        ToastUtil.showToast(requireContext(), "扫描完成")
        val tmp = HashSet<String>()
        wifiManager.scanResults.forEach { r ->
            tmp.add(r.SSID)
        }
        val results = tmp.map { item -> AccessPoint(item) }
//        Timber.d("scanSuccess: ${results.size}")
//        results.forEach { p ->
//            Timber.d("wifi ssid: ${p.ssid}")
//        }
        adapter.updateData(results)
    }

    private fun scanFailure() {
        // handle failure: new scan did NOT succeed
        // consider using old scan results: these are the OLD results!
        val results = wifiManager.scanResults
        Timber.d("scanFailure: ${results.size}")
    }

    companion object {
        fun newInstance() =
                WifiFragment().apply {
//                    arguments = Bundle().apply {
//                        putString(ARG_PARAM1, param1)
//                        putString(ARG_PARAM2, param2)
//                    }
                }
    }
}