package com.ubtrobot.smartprojector.repo

import androidx.lifecycle.LiveData
import com.ubtrobot.smartprojector.core.vo.ApiResponse
import com.ubtrobot.smartprojector.repo.data.TestData
import retrofit2.Call
import retrofit2.http.GET

interface ApiService {
    @GET("/test")
    fun test() : LiveData<ApiResponse<TestData>>
}