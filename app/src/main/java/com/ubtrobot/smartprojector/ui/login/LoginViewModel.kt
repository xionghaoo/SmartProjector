package com.ubtrobot.smartprojector.ui.login

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import com.ubtrobot.smartprojector.repo.Repository

class LoginViewModel @ViewModelInject internal constructor(
    private val repo: Repository
) : ViewModel() {
    fun saveLoginInfo(username: String) {
        repo.prefs.loginUsername = username
    }

    fun saveSerialNumber(sn: String) {
        repo.prefs.serialNumber = sn
    }
}