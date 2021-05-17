package com.ubtrobot.smartprojector.ui.video

import android.os.Parcelable
import androidx.annotation.Keep
import kotlinx.parcelize.Parcelize

@Keep
@Parcelize
data class VideoItem(
        val title: String,
        val url: String,
        var icon: String? = null,
) : Parcelable