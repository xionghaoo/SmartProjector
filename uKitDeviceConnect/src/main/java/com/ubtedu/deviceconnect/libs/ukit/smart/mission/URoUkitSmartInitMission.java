package com.ubtedu.deviceconnect.libs.ukit.smart.mission;

import androidx.annotation.NonNull;

import com.ubtedu.deviceconnect.libs.base.URoCompletionResult;
import com.ubtedu.deviceconnect.libs.base.mission.URoProductInitMission;
import com.ubtedu.deviceconnect.libs.base.product.URoProduct;
import com.ubtedu.deviceconnect.libs.ukit.legacy.mission.URoUkitLegacyBoardInfoMission;

/**
 * @Author naOKi
 * @Date 2019/09/18
 **/
public class URoUkitSmartInitMission extends URoUkitSmartBoardInfoMission {

    public URoUkitSmartInitMission(@NonNull URoProduct product) {
        super(product);
    }

}
