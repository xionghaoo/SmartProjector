package com.ubtrobot.smartprojector.repo.table

import android.graphics.drawable.Drawable
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey

@Entity(tableName = "ThirdApp")
class ThirdApp {
    @PrimaryKey
    var packageName: String = ""

    var name: String? = null
    @Ignore
    var icon: Drawable? = null

    var isLimited: Boolean = false
}