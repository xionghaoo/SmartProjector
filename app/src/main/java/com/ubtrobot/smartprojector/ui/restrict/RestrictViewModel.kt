package com.ubtrobot.smartprojector.ui.restrict

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import com.ubtrobot.smartprojector.repo.Repository
import com.ubtrobot.smartprojector.repo.table.ThirdApp

class RestrictViewModel @ViewModelInject constructor(
    private val repo: Repository
) : ViewModel() {

    fun loadThirdApps() = repo.loadThirdApps()

    fun saveThirdApps(items: List<ThirdApp>) = repo.saveThirdApps(items)
}