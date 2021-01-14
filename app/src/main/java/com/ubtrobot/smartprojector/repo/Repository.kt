package com.ubtrobot.smartprojector.repo

import android.content.Context
import androidx.lifecycle.LiveData
import com.ubtrobot.smartprojector.remoteRequestStrategy
import com.ubtrobot.smartprojector.repo.table.ThirdApp
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class Repository @Inject constructor(
        @ApplicationContext val context: Context,
        private val apiService: ApiService,
        val prefs: SharedPreferenceStorage,
        private val cacheDb: CacheDb
) {
    fun test() = remoteRequestStrategy {
        apiService.test()
    }

    fun loadThirdApps() : LiveData<List<ThirdApp>> {
        return cacheDb.thirdAppDao().findAll()
    }

    fun saveThirdApps(items: List<ThirdApp>) {
        CoroutineScope(Dispatchers.Default).launch {
            items.forEach { item ->
                cacheDb.thirdAppDao().insert(item)
            }
        }
    }
}