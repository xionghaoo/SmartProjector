package com.ubtrobot.smartprojector.ui.settings

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import com.ubtrobot.smartprojector.repo.Repository

class SettingsViewModel @ViewModelInject internal constructor(
    private val repo: Repository
) : ViewModel() {
    fun prefs() = repo.prefs
}