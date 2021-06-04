package com.ubtrobot.smartprojector.repo

import androidx.lifecycle.LiveData
import com.ubtrobot.smartprojector.core.vo.ApiResponse
import com.ubtrobot.smartprojector.repo.data.PlainData
import com.ubtrobot.smartprojector.repo.data.TestData
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

interface ApiService {
    @GET("/test_mock")
    fun test() : LiveData<ApiResponse<TestData>>

    @GET("/user-service-rest/v2/live/auth/rtc/{roomId}/{uid}")
    fun getRTCToken(
        @Path("roomId") roomId: String,
        @Path("uid") uid: String
    ) : LiveData<ApiResponse<PlainData>>

    @GET("/user-service-rest/v2/live/auth/rtm/{uid}")
    fun getRTMToken(
        @Path("uid") uid: String
    ) : LiveData<ApiResponse<PlainData>>
}