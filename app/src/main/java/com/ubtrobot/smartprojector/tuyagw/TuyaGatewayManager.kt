package com.ubtrobot.smartprojector.tuyagw

import android.Manifest
import com.tuya.smart.home.sdk.TuyaHomeSdk
import com.tuya.smart.sdk.api.ITuyaActivatorGetToken
import com.ubtrobot.smartprojector.BuildConfig
import eu.chainfire.libsuperuser.Shell
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber

/**
 * 涂鸦Zigbee网关
 */
class TuyaGatewayManager {
    companion object {
        init {
            System.loadLibrary("tuya_ext")
        }

        private var INSTANCE: TuyaGatewayManager? = null

        fun instance() : TuyaGatewayManager {
            if (INSTANCE == null) {
                INSTANCE = TuyaGatewayManager()
            }
            return INSTANCE!!
        }

    }

    private external fun initialZigbeeGW()

    // 绑定网关， 0 成功
    private external fun activeGW(token: String) : Int

    // 解绑网关： 0 成功
    private external fun unactiveGW() : Int

    // 允许子设备接入 1 开启， 0 关闭，默认是允许
    private external fun permitJoin(permit: Boolean)

//    public fun onlineCallback() {
//        Timber.d("online callback")
//    }

    fun initial() {
        Thread {
            val exitCode1 = Shell.Pool.SU.run("pm grant ${BuildConfig.APPLICATION_ID} ${Manifest.permission.WRITE_EXTERNAL_STORAGE}")
            val exitCode2 = Shell.Pool.SU.run("pm grant ${BuildConfig.APPLICATION_ID} ${Manifest.permission.READ_EXTERNAL_STORAGE}")
            if (exitCode1 == 0 && exitCode2 == 0) {
                initialZigbeeGW()
            } else {
                Timber.e("网关读写权限授予失败")
            }
        }.start()
    }

    /**
     * 网关注册
     */
    fun activeGateway(homeId: Long, resultCall: (success: Int) -> Unit) {
        TuyaHomeSdk.getActivatorInstance().getActivatorToken(homeId, object : ITuyaActivatorGetToken {
            override fun onSuccess(token: String?) {
                if (token != null) {
                    CoroutineScope(Dispatchers.IO).launch {
                        val result = activeGW(token)
                        withContext(Dispatchers.Main) {
                            resultCall(result)
//                            if (result == 0) {
//                                Timber.d("网关激活成功")
//                            } else {
//                                Timber.d("网关激活失败")
//                            }
                        }
                    }
                }
            }

            override fun onFailure(errorCode: String?, errorMsg: String?) {
                Timber.d("get token failre: $errorMsg")
            }
        })
    }

    /**
     * 网关注销
     */
    fun unactiveGateway(resultCall: (success: Int) -> Unit) {
        CoroutineScope(Dispatchers.IO).launch {
            val result = unactiveGW()
            withContext(Dispatchers.Main) {
                resultCall(result)
//                if (result == 0) {
//                    Timber.d("网关删除成功")
//                } else {
//                    Timber.d("网关删除失败")
//                }
            }
        }
    }

}