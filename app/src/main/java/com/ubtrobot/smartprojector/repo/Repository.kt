package com.ubtrobot.smartprojector.repo

import android.content.Context
import com.ubtrobot.smartprojector.remoteRequestStrategy
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class Repository @Inject constructor(
        @ApplicationContext val context: Context,
        private val apiService: ApiService
) {
    fun test() = remoteRequestStrategy {
        apiService.test()
    }
}