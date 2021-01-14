package com.ubtrobot.smartprojector.repo.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.ubtrobot.smartprojector.repo.table.ThirdApp

@Dao
abstract class ThirdAppDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun insert(item: ThirdApp)

    @Query("SELECT * FROM ThirdApp")
    abstract fun findAll() : LiveData<List<ThirdApp>>

    @Query("DELETE FROM ThirdApp")
    abstract suspend fun clear()
}