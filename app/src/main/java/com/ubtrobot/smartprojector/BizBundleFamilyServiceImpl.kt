package com.ubtrobot.smartprojector

import com.tuya.smart.commonbiz.bizbundle.family.api.AbsBizBundleFamilyService
import timber.log.Timber

class BizBundleFamilyServiceImpl : AbsBizBundleFamilyService() {

    private var homeId: Long = 0

    override fun getCurrentHomeId(): Long {
        return homeId
    }

    override fun setCurrentHomeId(id: Long) {
        homeId = id
    }

    override fun shiftCurrentFamily(p0: Long, p1: String?) {

    }
}