package com.ubtedu.deviceconnect.libs.base.component;

import com.ubtedu.deviceconnect.libs.base.URoCompletionCallback;
import com.ubtedu.deviceconnect.libs.base.invocation.URoInvocation;
import com.ubtedu.deviceconnect.libs.base.invocation.constants.URoInvocationNames;
import com.ubtedu.deviceconnect.libs.base.invocation.constants.URoInvocationParamKeys;
import com.ubtedu.deviceconnect.libs.base.model.URoComponentType;
import com.ubtedu.deviceconnect.libs.base.model.URoSpeakerInfo;
import com.ubtedu.deviceconnect.libs.base.product.URoProduct;

/**
 * @Author naOKi
 * @Date 2019/09/09
 **/
public class URoSpeaker extends URoComponent {

    private URoSpeakerInfo speakerInfo;

    public URoSpeaker(int componentId, String componentName, String version, URoProduct product) {
        super(componentId, componentName, version, product);
        componentType = URoComponentType.SPEAKER;
        changeIdEnable = false;
    }

    public boolean readInfo(URoCompletionCallback<URoSpeakerInfo> callback) {
        URoInvocation invocation = new URoInvocation(URoInvocationNames.INVOCATION_SPEAKER_INFO);
        invocation.setParameter(URoInvocationParamKeys.Component.PARAM_KEY_ID, componentId);
        invocation.setCompletionCallback(callback);
        return invocation.sendToTarget(product);
    }

    public String getMacValue() {
        return speakerInfo != null ? speakerInfo.getMac() : null;
    }

    public String getNameValue() {
        return speakerInfo != null ? speakerInfo.getName() : null;
    }

    public void setSpeakerInfo(URoSpeakerInfo speakerInfo) {
        this.speakerInfo = speakerInfo;
    }

}
