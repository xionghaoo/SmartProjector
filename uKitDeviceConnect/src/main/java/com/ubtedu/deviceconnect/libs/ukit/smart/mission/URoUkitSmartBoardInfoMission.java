package com.ubtedu.deviceconnect.libs.ukit.smart.mission;

import androidx.annotation.NonNull;

import com.ubtedu.deviceconnect.libs.base.URoCompletionResult;
import com.ubtedu.deviceconnect.libs.base.URoDataWrapper;
import com.ubtedu.deviceconnect.libs.base.link.URoLinkType;
import com.ubtedu.deviceconnect.libs.base.mission.URoProductInitMission;
import com.ubtedu.deviceconnect.libs.base.model.URoComponentID;
import com.ubtedu.deviceconnect.libs.base.model.URoComponentType;
import com.ubtedu.deviceconnect.libs.base.product.URoProduct;
import com.ubtedu.deviceconnect.libs.base.product.core.queue.URoCommand;
import com.ubtedu.deviceconnect.libs.base.product.core.queue.URoCommandConstants;
import com.ubtedu.deviceconnect.libs.base.product.core.queue.URoRequest;
import com.ubtedu.deviceconnect.libs.ukit.smart.model.URoUkitSmartComponentMap;
import com.ubtedu.deviceconnect.libs.ukit.smart.product.core.protocol.formatter.URoUkitSmartCommandFormatter;
import com.ubtedu.deviceconnect.libs.ukit.smart.protobuffer.PbIntelligentDevInfoGet;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

/**
 * @Author naOKi
 * @Date 2019/09/17
 **/
public class URoUkitSmartBoardInfoMission extends URoProductInitMission {

    private Disposable disposable;

    private ArrayList<PbIntelligentDevInfoGet.IntelligentDevInfoGetResponse.dev_info_t> devInfoList = new ArrayList<>();
    private HashSet<URoComponentID> componentIdList = new HashSet<>();
    private static final String COMPONENT_VERSION = "component_version";
    private URoProduct product;
    private final boolean isComConnect;

    public URoUkitSmartBoardInfoMission(@NonNull URoProduct product) {
        super(product);
        this.product = product;
        if(product.getLinkModel() != null) {
            isComConnect = URoLinkType.COM.equals(product.getLinkModel().linkType);
        } else {
            isComConnect = false;
        }
    }

    @Override
    protected void onMissionStart() throws Throwable {
        disposable = Observable.just(new URoDataWrapper<Object>(null)).delay(100, TimeUnit.MILLISECONDS)
            .subscribeOn(Schedulers.newThread())
            .observeOn(Schedulers.newThread())
            .subscribe(new Consumer<URoDataWrapper<Object>>() {
                @Override
                public void accept(URoDataWrapper<Object> warpper) throws Exception {
                    URoRequest request = new URoRequest(URoCommandConstants.CMD_RESET_STATE_SET);
                    performNext(URoCommandConstants.CMD_RESET_STATE_SET, request);
                    resetTimeout(5000);
                    request = new URoRequest(URoCommandConstants.CMD_INTELLIGENT_DEV_CHANGE_SWITCH_SET);
                    request.setParameter("status", false);
                    sendRequestDirectly(request);
                    if(isComConnect) {
                        request = new URoRequest(URoCommandConstants.CMD_INTELLIGENT_DEV_SCRIPT_STOP);
                        sendRequestDirectly(request);
                        request = new URoRequest(URoCommandConstants.CMD_VR_FILE_PLAY_STOP);
                        sendRequestDirectly(request);
                        request = new URoRequest(URoCommandConstants.CMD_INTELLIGENT_DEV_COMBO_OFF);
                        request.setParameter("dev", 0xFF);
                        sendRequestDirectly(request);
                    }
                }
            });
    }

    @Override
    protected void onMissionRelease() {
        super.onMissionRelease();
        if (disposable != null && !disposable.isDisposed()) {
            disposable.dispose();
            disposable = null;
        }
    }

    @Override
    protected boolean checkPreviousResult(boolean isSuccess, Object identity, @NonNull URoCompletionResult result) {
        if (COMPONENT_VERSION.equals(identity) || URoCommandConstants.CMD_SN_GET.equals(identity) || URoCommandConstants.CMD_MCU_SN_GET.equals(identity)) {
            return true;
        } else {
            return super.checkPreviousResult(isSuccess, identity, result);
        }
    }

    @Override
    protected boolean checkPerformResult(@NonNull URoCompletionResult result) {
        return true;
    }

    @Override
    protected void onPreviousResult(Object identity, URoCompletionResult result) throws Throwable {
        if (!URoCommandConstants.CMD_SN_GET.equals(identity) && !URoCommandConstants.CMD_MCU_SN_GET.equals(identity) && !COMPONENT_VERSION.equals(identity) && !result.isSuccess()) {
            notifyError(result.getError());
            return;
        }
        if (URoCommandConstants.CMD_RESET_STATE_SET.equals(identity)){
            URoRequest request = new URoRequest(URoCommandConstants.CMD_INTELLIGENT_DEV_INFO_GET);
            performNext(URoCommandConstants.CMD_INTELLIGENT_DEV_INFO_GET, request);
            resetTimeout(10000);
        }else if (URoCommandConstants.CMD_INTELLIGENT_DEV_INFO_GET.equals(identity)) {
            URoRequest request = new URoRequest(URoCommandConstants.CMD_SW_VER);
            performNext(URoCommandConstants.CMD_SW_VER, request);
            resetTimeout(5000);
            PbIntelligentDevInfoGet.IntelligentDevInfoGetResponse data = (PbIntelligentDevInfoGet.IntelligentDevInfoGetResponse)result.getData();
            devInfoList.clear();
            devInfoList.addAll(data.getDevInfoList());
        } else if (URoCommandConstants.CMD_SW_VER.equals(identity) || COMPONENT_VERSION.equals(identity)) {
            boolean sendRequest = false;
            for (PbIntelligentDevInfoGet.IntelligentDevInfoGetResponse.dev_info_t item : devInfoList) {
                Integer[] idMapList = URoUkitSmartCommandFormatter.toIdList(item.getIdMap());
                URoComponentType componentType = URoUkitSmartComponentMap.getTypeByValue(item.getDev());
                if(componentType == null) {
                    continue;
                }
                URoCommand cmd = null;
                if(URoComponentType.SERVOS.equals(componentType)) {
                    cmd = URoCommandConstants.CMD_SRV_SW_VER;
                } else if(URoComponentType.MOTOR.equals(componentType)) {
                    cmd = URoCommandConstants.CMD_MTR_SW_VER;
                } else if(URoComponentType.INFRAREDSENSOR.equals(componentType)) {
                    cmd = URoCommandConstants.CMD_IR_SW_VER;
                } else if(URoComponentType.ENVIRONMENTSENSOR.equals(componentType)) {
                    cmd = URoCommandConstants.CMD_TH_SW_VER;
                } else if(URoComponentType.SOUNDSENSOR.equals(componentType)) {
                    cmd = URoCommandConstants.CMD_SND_SW_VER;
                } else if(URoComponentType.LED.equals(componentType)) {
                    cmd = URoCommandConstants.CMD_LED_SW_VER;
                } else if(URoComponentType.ULTRASOUNDSENSOR.equals(componentType)) {
                    cmd = URoCommandConstants.CMD_ULT_SW_VER;
                } else if(URoComponentType.TOUCHSENSOR.equals(componentType)) {
                    cmd = URoCommandConstants.CMD_TCH_SW_VER;
                } else if(URoComponentType.COLORSENSOR.equals(componentType)) {
                    cmd = URoCommandConstants.CMD_CLR_SW_VER;
                } else if(URoComponentType.BRIGHTNESSSENSOR.equals(componentType)) {
                    cmd = URoCommandConstants.CMD_LGT_SW_VER;
                } else if(URoComponentType.SPEAKER.equals(componentType)) {
                    cmd = URoCommandConstants.CMD_SPK_SW_VER;
                } else if(URoComponentType.SUCKER.equals(componentType)) {
                    cmd = URoCommandConstants.CMD_SUK_SW_VER;
                } else if(URoComponentType.LED_BELT.equals(componentType)) {
                    cmd = URoCommandConstants.CMD_LED_BELT_SW_VER;
                } else if(URoComponentType.LCD.equals(componentType)) {
                    cmd = URoCommandConstants.CMD_LCD_SW_VER;
                } else if(URoComponentType.VISION.equals(componentType)) {
                    cmd = URoCommandConstants.CMD_VISIONMODULE_SW_VER;
                }
                for(Integer id : idMapList) {
                    URoComponentID componentID = new URoComponentID(componentType, id);
                    if(componentIdList.contains(componentID)) {
                        continue;
                    }
                    if (URoComponentType.LED_BELT.equals(componentType)) {
                        URoRequest request = new URoRequest(URoCommandConstants.CMD_LED_BELT_LEDS_NUM_GET);
                        request.setParameter("id", id);
                        sendRequestDirectly(request);
                    }
                    if (URoComponentType.VISION.equals(componentType)) {
                        URoRequest request = new URoRequest(URoCommandConstants.CMD_VISIONMODULE_WIFI_CONNECT_SET);
                        request.setParameter("status", true);
                        sendRequestDirectly(request);
                    }
                    sendRequest = true;
                    componentIdList.add(componentID);
                    URoRequest versionRequest = new URoRequest(cmd);
                    versionRequest.setParameter("id", id);
                    performNext(COMPONENT_VERSION, versionRequest);
                    resetTimeout(5000);
                    break;
                }
                if(sendRequest) {
                    break;
                }
            }
            if (!sendRequest) {
                URoRequest request = new URoRequest(URoCommandConstants.CMD_BAT_VOL);
                performNext(URoCommandConstants.CMD_BAT_VOL, request);
                resetTimeout(5000);
            }
        } else if (URoCommandConstants.CMD_BAT_VOL.equals(identity)) {
            URoRequest request = new URoRequest(URoCommandConstants.CMD_BAT_PWR_PCT);
            performNext(URoCommandConstants.CMD_BAT_PWR_PCT, request);
            resetTimeout(5000);
        } else if (URoCommandConstants.CMD_BAT_PWR_PCT.equals(identity)) {
            URoRequest request = new URoRequest(URoCommandConstants.CMD_BAT_STATUS_GET);
            performNext(URoCommandConstants.CMD_BAT_STATUS_GET, request);
            resetTimeout(5000);
        } else if (URoCommandConstants.CMD_BAT_STATUS_GET.equals(identity)) {
            URoRequest request = new URoRequest(URoCommandConstants.CMD_SN_GET);
            performNext(URoCommandConstants.CMD_SN_GET, request);
            resetTimeout(5000);
        } else if (URoCommandConstants.CMD_SN_GET.equals(identity)) {
            URoRequest request = new URoRequest(URoCommandConstants.CMD_MCU_SN_GET);
            performNext(URoCommandConstants.CMD_MCU_SN_GET, request);
            resetTimeout(5000);
        } else if (URoCommandConstants.CMD_MCU_SN_GET.equals(identity)) {
            notifyComplete(null);
        }
    }

}
