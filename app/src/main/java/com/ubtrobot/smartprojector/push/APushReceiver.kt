package com.ubtrobot.smartprojector.push

import android.content.Context
import com.alibaba.sdk.android.push.MessageReceiver
import com.alibaba.sdk.android.push.notification.CPushMessage
import timber.log.Timber

class APushReceiver : MessageReceiver() {
    override fun onNotificationOpened(p0: Context?, p1: String?, p2: String?, p3: String?) {
        Timber.d("onNotificationOpened")
    }

    override fun onNotificationRemoved(p0: Context?, p1: String?) {
        Timber.d("onNotificationRemoved")
    }

    override fun onNotification(
        context: Context?,
        title: String?,
        summary: String?,
        extraMap: MutableMap<String, String>?
    ) {
        Timber.d("Receive notification, title: " + title + ", summary: " + summary + ", extraMap: " + extraMap);
    }

    override fun onMessage(p0: Context?, p1: CPushMessage?) {
        Timber.d("onMessage")
    }

    override fun onNotificationClickedWithNoAction(
        p0: Context?,
        p1: String?,
        p2: String?,
        p3: String?
    ) {
        Timber.d("onNotificationClickedWithNoAction")
    }

    override fun onNotificationReceivedInApp(
        p0: Context?,
        p1: String?,
        p2: String?,
        p3: MutableMap<String, String>?,
        p4: Int,
        p5: String?,
        p6: String?
    ) {
        Timber.d("onNotificationReceivedInApp")
    }
}