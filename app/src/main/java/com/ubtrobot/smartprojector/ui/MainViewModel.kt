package com.ubtrobot.smartprojector.ui

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.ViewModel
import com.ubtrobot.smartprojector.repo.Repository
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainViewModel @ViewModelInject internal constructor(
    private val repo: Repository
) : ViewModel() {

    fun prefs() = repo.prefs

    fun getRTMToken(uid: String) = repo.getRTMToken(uid)

    fun getRTCToken(roomId: String, uid: String) = repo.getRTCToken(roomId, uid)
}