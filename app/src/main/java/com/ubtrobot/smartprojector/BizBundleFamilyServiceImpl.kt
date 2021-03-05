package com.ubtrobot.smartprojector

import com.tuya.smart.commonbiz.bizbundle.family.api.AbsBizBundleFamilyService
import timber.log.Timber

class BizBundleFamilyServiceImpl : AbsBizBundleFamilyService() {

    private var homeId: Long = 0

    override fun getCurrentHomeId(): Long {
        Timber.d("getCurrentHomeId: $homeId")
        return homeId
    }

    override fun setCurrentHomeId(p0: Long) {
        Timber.d("setCurrentHomeId: $p0")
        homeId = p0
    }

    override fun shiftCurrentFamily(p0: Long, p1: String?) {

    }
}