package com.ubtrobot.smartprojector.ui

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.ViewModel
import com.ubtrobot.smartprojector.repo.Repository
import com.ubtrobot.smartprojector.repo.data.TestData
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainViewModel @ViewModelInject internal constructor(
    private val repo: Repository
) : ViewModel() {
//    fun apiTest() : LiveData<TestData> {
//        val data = MediatorLiveData<TestData>()
//        repo.test().enqueue(object : Callback<TestData> {
//            override fun onResponse(call: Call<TestData>, response: Response<TestData>) {
//                if (response.isSuccessful) {
//                    data.value = response.body()
//                } else {
//                    data.value = TestData(null)
//                }
//            }
//
//            override fun onFailure(call: Call<TestData>, t: Throwable) {
//                data.value = TestData(null)
//            }
//        })
//        return data
//    }

    fun apiTest() {}
}