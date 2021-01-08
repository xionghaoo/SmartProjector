package com.ubtrobot.smartprojector.core.vo

import retrofit2.Response
import java.io.IOException
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import java.util.concurrent.TimeoutException

class ApiResponse<T> {

    var code: Int = 0
    var body: T? = null
    var error: String? = null

    constructor(response: Response<T>) {
//        response = r
        code = response.code()
        if (code == 200) {
            body = response.body()
            error = null
        } else if (code >= 500) {
            error = "服务器访问错误"
        } else {
            var msg: String? = null
            if (response.errorBody() != null) {
                try {
                    msg = response.errorBody()!!.string()
                } catch (e: IOException) {
                    e.printStackTrace()
//                    Log.d("ApiResponse", "error while parsing response")
                }
            }
            if (msg == null || msg.trim().isEmpty()) {
                msg = response.message()
            }
            error = msg
//            body = null
        }
    }

    constructor(t: Throwable?) {
//        Log.d("ApiResponse", "error: $t")
        code = -1
        body = null
//        if (t is UnknownHostException) {
//            error = "网络未连接"
//        } else if ((t is TimeoutException) || (t is SocketTimeoutException)) {
//            error = "网络连接超时"
//        } else {
//            error = t?.message ?: "未知网络错误"
//        }
        error = if (t is UnknownHostException) {
            "网络未连接"
        } else if ((t is TimeoutException) || (t is SocketTimeoutException)) {
            "网络连接超时"
        } else {
            t?.message ?: "未知网络错误"
        }
    }

    public fun isSuccessful() : Boolean {
        return code == 200
    }
}