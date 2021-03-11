package com.ubtrobot.smartprojector.tuyagw

import android.Manifest
import com.tuya.smart.home.sdk.TuyaHomeSdk
import com.tuya.smart.sdk.api.ITuyaActivatorGetToken
import eu.chainfire.libsuperuser.Shell
import timber.log.Timber

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

    private external fun permitJoin(permit: Boolean)

    fun initial() {
        Thread {
            val exitCode1 = Shell.Pool.SU.run("pm grant com.ubtrobot.smartprojector ${Manifest.permission.WRITE_EXTERNAL_STORAGE}")
            val exitCode2 = Shell.Pool.SU.run("pm grant com.ubtrobot.smartprojector ${Manifest.permission.READ_EXTERNAL_STORAGE}")
            if (exitCode1 == 0 && exitCode2 == 0) {
                initialZigbeeGW()
            } else {
                Timber.e("网关读写权限授予失败")
            }
        }.start()
    }

    fun activeGateway(homeId: Long, resultCall: (success: Int) -> Unit) {
        TuyaHomeSdk.getActivatorInstance().getActivatorToken(homeId, object :
                ITuyaActivatorGetToken {
            override fun onSuccess(token: String?) {
                if (token != null) {
                    val result = activeGW(token)
                    resultCall(result)
                    if (result == 0) {
                        Timber.d("网关激活成功")
                    } else {
                        Timber.d("网关激活失败")
                    }
                }
            }

            override fun onFailure(errorCode: String?, errorMsg: String?) {
                Timber.d("get token failre: $errorMsg")
            }
        })
    }

    fun unactiveGateway(resultCall: (success: Int) -> Unit) {
        val result = unactiveGW()
        resultCall(result)
        if (result == 0) {
            Timber.d("网关删除成功")
        } else {
            Timber.d("网关删除失败")
        }
    }

}