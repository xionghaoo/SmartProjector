package com.ubtrobot.smartprojector.ui.call

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import com.ubtrobot.smartprojector.repo.Repository

class AgoraCallViewModel @ViewModelInject internal constructor(
    private val repo: Repository
) : ViewModel() {
    fun prefs() = repo.prefs
}