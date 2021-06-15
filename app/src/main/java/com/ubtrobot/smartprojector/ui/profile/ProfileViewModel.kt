package com.ubtrobot.smartprojector.ui.profile

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import com.ubtrobot.smartprojector.repo.Repository

class ProfileViewModel @ViewModelInject internal constructor(
    private val repo: Repository
) : ViewModel() {
    fun prefs() = repo.prefs
}