package com.ubtrobot.smartprojector.repo

import androidx.room.Database
import androidx.room.RoomDatabase
import com.ubtrobot.smartprojector.repo.dao.ThirdAppDao
import com.ubtrobot.smartprojector.repo.table.ThirdApp

@Database(
    entities = [ThirdApp::class],
    version = 1,
    exportSchema = false
)
abstract class CacheDb : RoomDatabase() {
    abstract fun thirdAppDao() : ThirdAppDao
}