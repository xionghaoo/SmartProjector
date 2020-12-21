package com.ubtrobot.smartprojector.repo

import javax.inject.Inject

class Repository @Inject constructor(
    private val apiService: ApiService
) {
    fun test() = apiService.test()
}