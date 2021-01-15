package com.ubtedu.deviceconnect.libs.ukit.smart.product.core;

import com.google.protobuf.ByteString;
import com.ubtedu.deviceconnect.libs.base.URoDataWrapper;
import com.ubtedu.deviceconnect.libs.base.component.URoComponent;
import com.ubtedu.deviceconnect.libs.base.component.URoSensorComponent;
import com.ubtedu.deviceconnect.libs.base.component.URoSpeaker;
import com.ubtedu.deviceconnect.libs.base.interfaces.URoConverter;
import com.ubtedu.deviceconnect.libs.base.interfaces.URoConverterDelegate;
import com.ubtedu.deviceconnect.libs.base.link.URoLinkType;
import com.ubtedu.deviceconnect.libs.base.mission.URoProductInitMission;
import com.ubtedu.deviceconnect.libs.base.model.URoColor;
import com.ubtedu.deviceconnect.libs.base.model.URoComponentInfo;
import com.ubtedu.deviceconnect.libs.base.model.URoComponentType;
import com.ubtedu.deviceconnect.libs.base.model.URoError;
import com.ubtedu.deviceconnect.libs.base.model.URoHumitureValue;
import com.ubtedu.deviceconnect.libs.base.model.URoLEDStripInfo;
import com.ubtedu.deviceconnect.libs.base.model.URoLinkModel;
import com.ubtedu.deviceconnect.libs.base.model.URoSpeakerInfo;
import com.ubtedu.deviceconnect.libs.base.model.URoTouchSensorValue;
import com.ubtedu.deviceconnect.libs.base.model.event.URoComponentErrorEvent;
import com.ubtedu.deviceconnect.libs.base.model.event.URoPushMessageEvent;
import com.ubtedu.deviceconnect.libs.base.model.event.URoPushMessageType;
import com.ubtedu.deviceconnect.libs.base.product.URoConnectStatus;
import com.ubtedu.deviceconnect.libs.base.product.URoInitStatus;
import com.ubtedu.deviceconnect.libs.base.product.URoProductResponseInterceptor;
import com.ubtedu.deviceconnect.libs.base.product.URoProductType;
import com.ubtedu.deviceconnect.libs.base.product.core.URoCoreProduct;
import com.ubtedu.deviceconnect.libs.base.product.core.protocol.URoProtocolHandler;
import com.ubtedu.deviceconnect.libs.base.product.core.queue.URoCommand;
import com.ubtedu.deviceconnect.libs.base.product.core.queue.URoCommandConstants;
import com.ubtedu.deviceconnect.libs.base.product.core.queue.URoRequest;
import com.ubtedu.deviceconnect.libs.base.product.core.queue.URoRequestQueueManager;
import com.ubtedu.deviceconnect.libs.base.product.core.queue.URoResponse;
import com.ubtedu.deviceconnect.libs.ukit.smart.mission.URoUkitSmartInitMission;
import com.ubtedu.deviceconnect.libs.ukit.smart.model.URoAsrResult;
import com.ubtedu.deviceconnect.libs.ukit.smart.model.URoAudioPlayResult;
import com.ubtedu.deviceconnect.libs.ukit.smart.model.URoAudioPlayState;
import com.ubtedu.deviceconnect.libs.ukit.smart.model.URoAudioRecord;
import com.ubtedu.deviceconnect.libs.ukit.smart.model.URoAudioRecordState;
import com.ubtedu.deviceconnect.libs.ukit.smart.model.URoNetworkState;
import com.ubtedu.deviceconnect.libs.ukit.smart.model.URoUkitSmartBatteryInfo;
import com.ubtedu.deviceconnect.libs.ukit.smart.model.URoUkitSmartComponentMap;
import com.ubtedu.deviceconnect.libs.ukit.smart.model.URoUkitSmartMainBoardInfo;
import com.ubtedu.deviceconnect.libs.ukit.smart.model.URoUkitSmartSerialNumberInfo;
import com.ubtedu.deviceconnect.libs.ukit.smart.model.URoUkitSmartSpeakerInfoData;
import com.ubtedu.deviceconnect.libs.ukit.smart.model.URoVoiceServiceState;
import com.ubtedu.deviceconnect.libs.ukit.smart.model.URoWiFiAuthMode;
import com.ubtedu.deviceconnect.libs.ukit.smart.model.URoWiFiStatusInfo;
import com.ubtedu.deviceconnect.libs.ukit.smart.product.URoUkitSmart;
import com.ubtedu.deviceconnect.libs.ukit.smart.product.core.protocol.URoUkitSmartProtocolHandler;
import com.ubtedu.deviceconnect.libs.ukit.smart.product.core.protocol.formatter.URoUkitSmartCommandFormatter;
import com.ubtedu.deviceconnect.libs.ukit.smart.product.core.queue.URoUkitSmartRequestQueueManager;
import com.ubtedu.deviceconnect.libs.ukit.smart.protobuffer.PbBatteryPowerPercent;
import com.ubtedu.deviceconnect.libs.ukit.smart.protobuffer.PbBatteryStatusGet;
import com.ubtedu.deviceconnect.libs.ukit.smart.protobuffer.PbBatteryStatusReport;
import com.ubtedu.deviceconnect.libs.ukit.smart.protobuffer.PbBatteryVoltage;
import com.ubtedu.deviceconnect.libs.ukit.smart.protobuffer.PbColorRgbGet;
import com.ubtedu.deviceconnect.libs.ukit.smart.protobuffer.PbColorRgbPush;
import com.ubtedu.deviceconnect.libs.ukit.smart.protobuffer.PbInfraredDistanceGet;
import com.ubtedu.deviceconnect.libs.ukit.smart.protobuffer.PbInfraredDistancePush;
import com.ubtedu.deviceconnect.libs.ukit.smart.protobuffer.PbIntelligentDevInfoGet;
import com.ubtedu.deviceconnect.libs.ukit.smart.protobuffer.PbIntelligentDevNetworkInfoReport;
import com.ubtedu.deviceconnect.libs.ukit.smart.protobuffer.PbIntelligentDevWifiInfoReport;
import com.ubtedu.deviceconnect.libs.ukit.smart.protobuffer.PbIntelligentDevscriptExecReport;
import com.ubtedu.deviceconnect.libs.ukit.smart.protobuffer.PbLedBeltLedsNumGet;
import com.ubtedu.deviceconnect.libs.ukit.smart.protobuffer.PbLightValueGet;
import com.ubtedu.deviceconnect.libs.ukit.smart.protobuffer.PbLightValuePush;
import com.ubtedu.deviceconnect.libs.ukit.smart.protobuffer.PbMcuSnGet;
import com.ubtedu.deviceconnect.libs.ukit.smart.protobuffer.PbMessageHeader;
import com.ubtedu.deviceconnect.libs.ukit.smart.protobuffer.PbSnGet;
import com.ubtedu.deviceconnect.libs.ukit.smart.protobuffer.PbSoundAdcValueGet;
import com.ubtedu.deviceconnect.libs.ukit.smart.protobuffer.PbSoundAdcValuePush;
import com.ubtedu.deviceconnect.libs.ukit.smart.protobuffer.PbSpeakerGet;
import com.ubtedu.deviceconnect.libs.ukit.smart.protobuffer.PbSwVer;
import com.ubtedu.deviceconnect.libs.ukit.smart.protobuffer.PbTemperatureHumidityGet;
import com.ubtedu.deviceconnect.libs.ukit.smart.protobuffer.PbTemperatureHumidityPush;
import com.ubtedu.deviceconnect.libs.ukit.smart.protobuffer.PbTouchTypeGet;
import com.ubtedu.deviceconnect.libs.ukit.smart.protobuffer.PbTouchTypePush;
import com.ubtedu.deviceconnect.libs.ukit.smart.protobuffer.PbUltrasonicDistanceGet;
import com.ubtedu.deviceconnect.libs.ukit.smart.protobuffer.PbUltrasonicDistancePush;
import com.ubtedu.deviceconnect.libs.ukit.smart.protobuffer.PbVoiceAsrInfoReport;
import com.ubtedu.deviceconnect.libs.ukit.smart.protobuffer.PbVoicePlayInfoReport;
import com.ubtedu.deviceconnect.libs.ukit.smart.protobuffer.PbVoiceRecordInfoReport;
import com.ubtedu.deviceconnect.libs.ukit.smart.protobuffer.PbVoiceTtsInfoReport;
import com.ubtedu.deviceconnect.libs.utils.URoLogUtils;

import org.greenrobot.eventbus.EventBus;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

/**
 * @Author naOKi
 * @Date 2019/08/12
 **/
public class URoCoreUkitSmart extends URoCoreProduct<URoRequest, URoResponse, URoUkitSmartProtocolHandler> {

    private static final long HEARTBEAT_INTERVAL = 5000L;
    private static final long HEARTBEAT_FAILURE_COUNT = 2;
    private static final long HEARTBEAT_TIMEOUT = (HEARTBEAT_INTERVAL * HEARTBEAT_FAILURE_COUNT) + 1000L;

    private URoUkitSmart product;

    public URoCoreUkitSmart(String productID, URoProductType productType, URoLinkModel link, URoConnectStatus connectStatus) {
        super(productID, productType, link, connectStatus);
    }

    @Override
    protected URoProtocolHandler createProtocolHandler() {
        return new URoUkitSmartProtocolHandler();
    }

    @Override
    protected URoRequestQueueManager createRequestQueueManager(URoLinkModel link, URoProtocolHandler protocolHandler) {
        return new URoUkitSmartRequestQueueManager(link, protocolHandler);
    }

    @Override
    protected URoProductInitMission createInitMission() {
        return new URoUkitSmartInitMission(asProduct());
    }

    @Override
    public URoUkitSmart asProduct() {
        synchronized (this) {
            if(product == null) {
                product = new URoUkitSmart(getLinkModel().linkName, getProductID());
            }
        }
        return product;
    }

    @Override
    protected Disposable resetDisconnectTimerInternal() {
        Disposable disposable = null;
        if(URoLinkType.COM.equals(getLinkModel().linkType)) {
            disposable = Observable.just(new URoDataWrapper<Object>(null)).delay(HEARTBEAT_TIMEOUT, TimeUnit.MILLISECONDS)
                .subscribeOn(Schedulers.newThread())
                .observeOn(Schedulers.newThread())
                .subscribe(new Consumer<URoDataWrapper<Object>>() {
                    @Override
                    public void accept(URoDataWrapper<Object> warpper) throws Exception {
                        URoLogUtils.e(" >>>>> Heartbeat timeout, disconnecting!");
                        disconnect();
                    }
                });
        }
        return disposable;
    }

    protected Disposable resetHeartBeatTimerInternal() {
        Disposable disposable = null;
        if(URoLinkType.COM.equals(getLinkModel().linkType)) {
            disposable = Observable.interval(0, HEARTBEAT_INTERVAL, TimeUnit.MILLISECONDS)
                .subscribeOn(Schedulers.newThread())
                .observeOn(Schedulers.newThread())
                .subscribe((aLong) -> {
                    if (!isQueueContain(URoCommandConstants.CMD_HEARTBEAT)) {
                        URoRequest request = new URoRequest(URoCommandConstants.CMD_HEARTBEAT);
                        addRequest(request, null, URoCommandConstants.CMD_HEARTBEAT, null);
                    }
                });
            addInterceptor(new URoProductResponseInterceptor() {
                @Override
                public URoResponse onInterceptResponse(URoCommand cmd, URoResponse response) {
                    if(response.isSuccess()) {
                        resetDisconnectTimer();
                    }
                    return null;
                }
            });
        }
        return disposable;
    }

    protected Disposable resetBatteryTimerInternal() {
        Disposable disposable = Observable.interval(60000, 60000, TimeUnit.MILLISECONDS)
            .subscribeOn(Schedulers.newThread())
            .observeOn(Schedulers.newThread())
            .subscribe((aLong) -> {
                URoRequest request;
                request = new URoRequest(URoCommandConstants.CMD_BAT_PWR_PCT);
                addRequest(request, null, URoCommandConstants.CMD_BAT_PWR_PCT, null);
                request = new URoRequest(URoCommandConstants.CMD_BAT_VOL);
                addRequest(request, null, URoCommandConstants.CMD_BAT_VOL, null);
            });
        return disposable;
    }

    @Override
    protected void onInitComplete(URoInitStatus initStatus) {
//        if(URoLinkType.COM.equals(getLinkModel().linkType)) {
//            URoRequest request;
//            request = new URoRequest(URoCommandConstants.CMD_INTELLIGENT_DEV_SCRIPT_STOP);
//            addRequest(request, null, URoCommandConstants.CMD_INTELLIGENT_DEV_SCRIPT_STOP, null);
//        }
    }

    @Override
    protected void onInitInterceptor() {
        addInterceptor(new URoProductResponseInterceptor() {
            @Override
            public URoResponse onInterceptResponse(URoCommand cmd, URoResponse response) {
                if (!URoCommandConstants.CMD_INTELLIGENT_DEV_INFO_GET.equals(cmd)
//                           && !URoCommandConstants.CMD_INTELLIGENT_DEV_VERSION_INFO_GET.equals(cmd)
                        && !URoCommandConstants.CMD_SW_VER.equals(cmd)
                        && !URoCommandConstants.CMD_MCU_SN_GET.equals(cmd)
                        && !URoCommandConstants.CMD_SRV_SW_VER.equals(cmd)
                        && !URoCommandConstants.CMD_MTR_SW_VER.equals(cmd)
                        && !URoCommandConstants.CMD_IR_SW_VER.equals(cmd)
                        && !URoCommandConstants.CMD_TH_SW_VER.equals(cmd)
                        && !URoCommandConstants.CMD_SND_SW_VER.equals(cmd)
                        && !URoCommandConstants.CMD_LED_SW_VER.equals(cmd)
                        && !URoCommandConstants.CMD_ULT_SW_VER.equals(cmd)
                        && !URoCommandConstants.CMD_TCH_SW_VER.equals(cmd)
                        && !URoCommandConstants.CMD_CLR_SW_VER.equals(cmd)
                        && !URoCommandConstants.CMD_LGT_SW_VER.equals(cmd)
                        && !URoCommandConstants.CMD_SPK_SW_VER.equals(cmd)
                        && !URoCommandConstants.CMD_SUK_SW_VER.equals(cmd)
                        && !URoCommandConstants.CMD_LED_BELT_SW_VER.equals(cmd)
                        && !URoCommandConstants.CMD_LCD_SW_VER.equals(cmd)
                        && !URoCommandConstants.CMD_VISIONMODULE_SW_VER.equals(cmd)
                        && !URoCommandConstants.CMD_LED_BELT_LEDS_NUM_GET.equals(cmd)
                ) {
                    return null;
                }
                URoUkitSmartMainBoardInfo mainBoardInfo = new URoUkitSmartMainBoardInfo(getMainBoardInfo());
                if(URoCommandConstants.CMD_INTELLIGENT_DEV_INFO_GET.equals(cmd)) {
                    if(response.isSuccess()) {
                        mainBoardInfo = new URoUkitSmartMainBoardInfo(null);
                        PbIntelligentDevInfoGet.IntelligentDevInfoGetResponse data = (PbIntelligentDevInfoGet.IntelligentDevInfoGetResponse) response.getData();
                        List<PbIntelligentDevInfoGet.IntelligentDevInfoGetResponse.dev_info_t> list = data.getDevInfoList();
                        for (PbIntelligentDevInfoGet.IntelligentDevInfoGetResponse.dev_info_t item : list) {
                            Integer[] idMapList = URoUkitSmartCommandFormatter.toIdList(item.getIdMap());
                            Integer[] idClashMapList = URoUkitSmartCommandFormatter.toIdList(item.getIdClashMap());
                            URoComponentType componentType = URoUkitSmartComponentMap.getTypeByValue(item.getDev());
                            for (Integer id : idMapList) {
                                mainBoardInfo.addAvailableId(componentType, id);
                            }
                            for (Integer id : idClashMapList) {
                                mainBoardInfo.addAbnormalId(componentType, id);
                            }
                        }
                        setMainBoardInfo(mainBoardInfo);
                    }
                } else if (URoCommandConstants.CMD_LED_BELT_LEDS_NUM_GET.equals(cmd)) {
                    if(response.isSuccess()) {
                        PbMessageHeader.Header header = (PbMessageHeader.Header) response.getExtraData();
                        if (header == null) {
                            return null;
                        }
                        int id = header.getId();
                        PbLedBeltLedsNumGet.LedBeltLedsNumGetResponse data = (PbLedBeltLedsNumGet.LedBeltLedsNumGetResponse) response.getData();
                        List<PbLedBeltLedsNumGet.LedBeltLedsNumGetResponse.dev_num_lump> list = data.getNumLumpList();
                        for (PbLedBeltLedsNumGet.LedBeltLedsNumGetResponse.dev_num_lump item : list) {
                            int port = item.getPort();
                            int ledNum = item.getToatl();
                            URoComponentInfo componentInfo = mainBoardInfo.getComponentInfo(URoComponentType.LED_BELT);
                            if (componentInfo != null) {
                                componentInfo.addSubComponent(id, new URoLEDStripInfo(port, ledNum));
                            }
                        }
                        setMainBoardInfo(mainBoardInfo);
                    }
                } else if (URoCommandConstants.CMD_SW_VER.equals(cmd)) {
                    if (response.isSuccess()) {
                        PbSwVer.SwVerResponse data = (PbSwVer.SwVerResponse) response.getData();
                        mainBoardInfo.boardVersion = formatString(data.getSwVer());
                        setMainBoardInfo(mainBoardInfo);
                    }
                } else if (URoCommandConstants.CMD_MCU_SN_GET.equals(cmd)) {
                    if (response.isSuccess()) {
                        PbMcuSnGet.McuSnGetResponse data = (PbMcuSnGet.McuSnGetResponse) response.getData();
                        mainBoardInfo.mcuVersion = data.getMcuVer();
                        setMainBoardInfo(mainBoardInfo);
                    }
                } else {
                    PbMessageHeader.Header header = (PbMessageHeader.Header) response.getExtraData();
                    if (header == null) {
                        return null;
                    }
                    int id = header.getId();
                    URoComponentType componentType = URoUkitSmartComponentMap.getTypeByValue(header.getDev());
                    if (response.isSuccess()) {
                        PbSwVer.SwVerResponse data = (PbSwVer.SwVerResponse) response.getData();
                        mainBoardInfo.addIdVersion(componentType, id, formatString(data.getSwVer()));
                    } else {
                        mainBoardInfo.addIdVersion(componentType, id, URoComponent.INVALID_VERSION);
                    }
                    setMainBoardInfo(mainBoardInfo);
                }
                return null;
            }
        });
        addInterceptor(new URoProductResponseInterceptor() {
            @Override
            public URoResponse onInterceptResponse(URoCommand cmd, URoResponse response) {
                if(!URoCommandConstants.CMD_SN_GET.equals(cmd)) {
                    return null;
                }
                if(!response.isSuccess()) {
                    return null;
                }
                PbSnGet.SnGetResponse serialNumberInfo = (PbSnGet.SnGetResponse)response.getData();
                setSerialNumberInfo(new URoUkitSmartSerialNumberInfo(serialNumberInfo));
                return null;
            }
        });
        addInterceptor(new URoProductResponseInterceptor() {
            @Override
            public URoResponse onInterceptResponse(URoCommand cmd, URoResponse response) {
                if(!URoCommandConstants.CMD_SPK_GET.equals(cmd)) {
                    return null;
                }
                if(!response.isSuccess()) {
                    return null;
                }
                PbSpeakerGet.SpeakerGetResponse source = (PbSpeakerGet.SpeakerGetResponse)response.getData();
                URoSpeakerInfo speakerInfo = new URoUkitSmartSpeakerInfoData(source);
                URoSpeaker speaker = findComponent(URoComponentType.SPEAKER, 1);
                if(speaker != null) {
                    speaker.setSpeakerInfo(speakerInfo);
                }
                return null;
            }
        });
        addInterceptor(new URoProductResponseInterceptor() {
            @Override
            public URoResponse onInterceptResponse(URoCommand cmd, URoResponse response) {
                if(!URoCommandConstants.CMD_BAT_STATUS_GET.equals(cmd)
                        && !URoCommandConstants.CMD_BAT_STATUS_REPORT.equals(cmd)
                        && !URoCommandConstants.CMD_BAT_VOL.equals(cmd)
                        && !URoCommandConstants.CMD_BAT_PWR_PCT.equals(cmd)) {
                    return null;
                }
                if(!response.isSuccess()) {
                    return null;
                }
                URoUkitSmartBatteryInfo batteryInfo = new URoUkitSmartBatteryInfo(getBatteryInfo());
                if(URoCommandConstants.CMD_BAT_STATUS_GET.equals(cmd)) {
                    PbBatteryStatusGet.BatteryStatusGetResponse data = (PbBatteryStatusGet.BatteryStatusGetResponse)response.getData();
                    batteryInfo.updateCharging(data.getAdapterStatus() == 1);
                    batteryInfo.updateFullCharge(data.getBatStatus() == 3);
                } else if(URoCommandConstants.CMD_BAT_STATUS_REPORT.equals(cmd)) {
                    PbBatteryStatusReport.BatteryStatusReportResponse data = (PbBatteryStatusReport.BatteryStatusReportResponse)response.getData();
                    batteryInfo.updateCharging(data.getAdapterStatus() == 1);
                    batteryInfo.updateFullCharge(data.getBatStatus() == 3);
                } else if(URoCommandConstants.CMD_BAT_VOL.equals(cmd)) {
                    PbBatteryVoltage.BatteryVoltageResponse data = (PbBatteryVoltage.BatteryVoltageResponse)response.getData();
                    batteryInfo.updateVoltage(Float.parseFloat(String.format(Locale.US, "%.3f", data.getVoltage() / 1000.0f)));
                } else if(URoCommandConstants.CMD_BAT_PWR_PCT.equals(cmd)) {
                    PbBatteryPowerPercent.BatteryPowerPercentResponse data = (PbBatteryPowerPercent.BatteryPowerPercentResponse)response.getData();
                    batteryInfo.updateBatteryRemaining(data.getPwrPercent());
                }
                setBatteryInfo(batteryInfo);
                return null;
            }
        });
        addInterceptor(new URoProductResponseInterceptor() {
            @Override
            public URoResponse onInterceptResponse(URoCommand cmd, URoResponse response) {
                if(response.isSuccess()) {
                    return null;
                }
                PbMessageHeader.Header header = (PbMessageHeader.Header)response.getExtraData();
                if(header == null) {
                    return null;
                }
                if(URoCommandConstants.CMD_SRV_ANGLE_SET.equals(cmd)) {
                    EventBus.getDefault().post(new URoComponentErrorEvent(getLinkModel(), URoComponentType.SERVOS, header.getId(), URoError.CTRL_ERROR));
                    return null;
                }
                if(URoCommandConstants.CMD_SRV_ANGLE_GET.equals(cmd)) {
                    EventBus.getDefault().post(new URoComponentErrorEvent(getLinkModel(), URoComponentType.SERVOS, header.getId(), URoError.CTRL_ERROR));
                    return null;
                }
                if(URoCommandConstants.CMD_SRV_PWM_SET.equals(cmd)) {
                    EventBus.getDefault().post(new URoComponentErrorEvent(getLinkModel(), URoComponentType.SERVOS, header.getId(), URoError.CTRL_ERROR));
                    return null;
                }
                if(URoCommandConstants.CMD_SRV_STOP.equals(cmd)) {
                    EventBus.getDefault().post(new URoComponentErrorEvent(getLinkModel(), URoComponentType.SERVOS, header.getId(), URoError.CTRL_ERROR));
                    return null;
                }
                if(URoCommandConstants.CMD_MTR_SPEED_SET.equals(cmd)) {
                    EventBus.getDefault().post(new URoComponentErrorEvent(getLinkModel(), URoComponentType.MOTOR, header.getId(), URoError.CTRL_ERROR));
                    return null;
                }
                if(URoCommandConstants.CMD_MTR_PWM_SET.equals(cmd)) {
                    EventBus.getDefault().post(new URoComponentErrorEvent(getLinkModel(), URoComponentType.MOTOR, header.getId(), URoError.CTRL_ERROR));
                    return null;
                }
                if(URoCommandConstants.CMD_MTR_STOP.equals(cmd)) {
                    EventBus.getDefault().post(new URoComponentErrorEvent(getLinkModel(), URoComponentType.MOTOR, header.getId(), URoError.CTRL_ERROR));
                    return null;
                }
                if(URoCommandConstants.CMD_LED_FIX_EXP_SET.equals(cmd)) {
                    EventBus.getDefault().post(new URoComponentErrorEvent(getLinkModel(), URoComponentType.LED, header.getId(), URoError.CTRL_ERROR));
                    return null;
                }
                if(URoCommandConstants.CMD_LED_EXP_SET.equals(cmd)) {
                    EventBus.getDefault().post(new URoComponentErrorEvent(getLinkModel(), URoComponentType.LED, header.getId(), URoError.CTRL_ERROR));
                    return null;
                }
                if(URoCommandConstants.CMD_ULT_LIGHT_SET.equals(cmd)) {
                    EventBus.getDefault().post(new URoComponentErrorEvent(getLinkModel(), URoComponentType.ULTRASOUNDSENSOR, header.getId(), URoError.CTRL_ERROR));
                    return null;
                }
                if(URoCommandConstants.CMD_IR_DIS_GET.equals(cmd)) {
                    EventBus.getDefault().post(new URoComponentErrorEvent(getLinkModel(), URoComponentType.INFRAREDSENSOR, header.getId(), URoError.CTRL_ERROR));
                    return null;
                }
                if(URoCommandConstants.CMD_TCH_TYPE_GET.equals(cmd)) {
                    EventBus.getDefault().post(new URoComponentErrorEvent(getLinkModel(), URoComponentType.TOUCHSENSOR, header.getId(), URoError.CTRL_ERROR));
                    return null;
                }
                if(URoCommandConstants.CMD_ULT_DIS_GET.equals(cmd)) {
                    EventBus.getDefault().post(new URoComponentErrorEvent(getLinkModel(), URoComponentType.ULTRASOUNDSENSOR, header.getId(), URoError.CTRL_ERROR));
                    return null;
                }
                if(URoCommandConstants.CMD_CLR_RGB_GET.equals(cmd)) {
                    EventBus.getDefault().post(new URoComponentErrorEvent(getLinkModel(), URoComponentType.COLORSENSOR, header.getId(), URoError.CTRL_ERROR));
                    return null;
                }
                if(URoCommandConstants.CMD_TH_GET.equals(cmd)) {
                    EventBus.getDefault().post(new URoComponentErrorEvent(getLinkModel(), URoComponentType.ENVIRONMENTSENSOR, header.getId(), URoError.CTRL_ERROR));
                    return null;
                }
                if(URoCommandConstants.CMD_LGT_VALUE_GET.equals(cmd)) {
                    EventBus.getDefault().post(new URoComponentErrorEvent(getLinkModel(), URoComponentType.BRIGHTNESSSENSOR, header.getId(), URoError.CTRL_ERROR));
                    return null;
                }
                if(URoCommandConstants.CMD_SND_ADC_VALUE_GET.equals(cmd)) {
                    EventBus.getDefault().post(new URoComponentErrorEvent(getLinkModel(), URoComponentType.SOUNDSENSOR, header.getId(), URoError.CTRL_ERROR));
                    return null;
                }
                return null;
            }
        });
        addInterceptor(new URoProductResponseInterceptor() {
            @Override
            public URoResponse onInterceptResponse(URoCommand cmd, URoResponse response) {
                if(!URoCommandConstants.CMD_IR_DIS_GET.equals(cmd)
                        && !URoCommandConstants.CMD_TCH_TYPE_GET.equals(cmd)
                        && !URoCommandConstants.CMD_ULT_DIS_GET.equals(cmd)
                        && !URoCommandConstants.CMD_CLR_RGB_GET.equals(cmd)
                        && !URoCommandConstants.CMD_TH_GET.equals(cmd)
                        && !URoCommandConstants.CMD_LGT_VALUE_GET.equals(cmd)
                        && !URoCommandConstants.CMD_SND_ADC_VALUE_GET.equals(cmd)
                        && !URoCommandConstants.CMD_IR_DIS_PUSH.equals(cmd)
                        && !URoCommandConstants.CMD_TCH_TYPE_PUSH.equals(cmd)
                        && !URoCommandConstants.CMD_ULT_DIS_PUSH.equals(cmd)
                        && !URoCommandConstants.CMD_CLR_RGB_PUSH.equals(cmd)
                        && !URoCommandConstants.CMD_TH_PUSH.equals(cmd)
                        && !URoCommandConstants.CMD_LGT_VALUE_PUSH.equals(cmd)
                        && !URoCommandConstants.CMD_SND_ADC_VALUE_PUSH.equals(cmd)) {
                    return null;
                }
                if(!response.isSuccess()) {
                    return null;
                }
                PbMessageHeader.Header header = (PbMessageHeader.Header)response.getExtraData();
                if(header == null) {
                    return null;
                }
                URoComponentType componentType = URoUkitSmartComponentMap.getTypeByValue(header.getDev());
                if(componentType == null) {
                    return null;
                }
                URoComponent component = findComponent(componentType, header.getId());
                if(component == null) {
                    return null;
                }
                if(!(component instanceof URoSensorComponent)) {
                    return null;
                }
                URoSensorComponent sensorComponent = (URoSensorComponent<?>)component;
                URoConverter converter = null;
                if(URoCommandConstants.CMD_IR_DIS_GET.equals(cmd)) {
                    PbInfraredDistanceGet.InfraredDistanceGetResponse data = (PbInfraredDistanceGet.InfraredDistanceGetResponse)response.getData();
                    converter = URoConverter.newInstance(data, new URoConverterDelegate<PbInfraredDistanceGet.InfraredDistanceGetResponse, Integer>() {
                        @Override
                        public Integer convert(PbInfraredDistanceGet.InfraredDistanceGetResponse source) throws Throwable {
                            return source.getDistance();
                        }
                    });
                } else if(URoCommandConstants.CMD_IR_DIS_PUSH.equals(cmd)) {
                    PbInfraredDistancePush.InfraredDistancePushResponse data = (PbInfraredDistancePush.InfraredDistancePushResponse)response.getData();
                    converter = URoConverter.newInstance(data, new URoConverterDelegate<PbInfraredDistancePush.InfraredDistancePushResponse, Integer>() {
                        @Override
                        public Integer convert(PbInfraredDistancePush.InfraredDistancePushResponse source) throws Throwable {
                            return source.getDistance();
                        }
                    });
                } else if(URoCommandConstants.CMD_TCH_TYPE_GET.equals(cmd)) {
                    PbTouchTypeGet.TouchTypeGetResponse data = (PbTouchTypeGet.TouchTypeGetResponse)response.getData();
                    converter = URoConverter.newInstance(data, new URoConverterDelegate<PbTouchTypeGet.TouchTypeGetResponse, URoTouchSensorValue>() {
                        @Override
                        public URoTouchSensorValue convert(PbTouchTypeGet.TouchTypeGetResponse source) throws Throwable {
                            return new URoTouchSensorValue(source.getType());
                        }
                    });
                } else if(URoCommandConstants.CMD_TCH_TYPE_PUSH.equals(cmd)) {
                    PbTouchTypePush.TouchTypePushResponse data = (PbTouchTypePush.TouchTypePushResponse)response.getData();
                    converter = URoConverter.newInstance(data, new URoConverterDelegate<PbTouchTypePush.TouchTypePushResponse, URoTouchSensorValue>() {
                        @Override
                        public URoTouchSensorValue convert(PbTouchTypePush.TouchTypePushResponse source) throws Throwable {
                            return new URoTouchSensorValue(source.getType());
                        }
                    });
                } else if(URoCommandConstants.CMD_ULT_DIS_GET.equals(cmd)) {
                    PbUltrasonicDistanceGet.UltrasonicDistanceGetResponse data = (PbUltrasonicDistanceGet.UltrasonicDistanceGetResponse)response.getData();
                    converter = URoConverter.newInstance(data, new URoConverterDelegate<PbUltrasonicDistanceGet.UltrasonicDistanceGetResponse, Integer>() {
                        @Override
                        public Integer convert(PbUltrasonicDistanceGet.UltrasonicDistanceGetResponse source) throws Throwable {
                            return source.getDistance();
                        }
                    });
                } else if(URoCommandConstants.CMD_ULT_DIS_PUSH.equals(cmd)) {
                    PbUltrasonicDistancePush.UltrasonicDistancePushResponse data = (PbUltrasonicDistancePush.UltrasonicDistancePushResponse)response.getData();
                    converter = URoConverter.newInstance(data, new URoConverterDelegate<PbUltrasonicDistancePush.UltrasonicDistancePushResponse, Integer>() {
                        @Override
                        public Integer convert(PbUltrasonicDistancePush.UltrasonicDistancePushResponse source) throws Throwable {
                            return source.getDistance();
                        }
                    });
                } else if(URoCommandConstants.CMD_CLR_RGB_GET.equals(cmd)) {
                    PbColorRgbGet.ColorRgbGetResponse data = (PbColorRgbGet.ColorRgbGetResponse)response.getData();
                    converter = URoConverter.newInstance(data, new URoConverterDelegate<PbColorRgbGet.ColorRgbGetResponse, URoColor>() {
                        @Override
                        public URoColor convert(PbColorRgbGet.ColorRgbGetResponse source) throws Throwable {
                            return new URoColor((source.getRgbc() & 0xFFFFFF00) >> 8);
                        }
                    });
                } else if(URoCommandConstants.CMD_CLR_RGB_PUSH.equals(cmd)) {
                    PbColorRgbPush.ColorRgbPushResponse data = (PbColorRgbPush.ColorRgbPushResponse)response.getData();
                    converter = URoConverter.newInstance(data, new URoConverterDelegate<PbColorRgbPush.ColorRgbPushResponse, URoColor>() {
                        @Override
                        public URoColor convert(PbColorRgbPush.ColorRgbPushResponse source) throws Throwable {
                            return new URoColor((source.getRgbc() & 0xFFFFFF00) >> 8);
                        }
                    });
                } else if(URoCommandConstants.CMD_TH_GET.equals(cmd)) {
                    PbTemperatureHumidityGet.TemperatureHumidityGetResponse data = (PbTemperatureHumidityGet.TemperatureHumidityGetResponse)response.getData();
                    converter = URoConverter.newInstance(data, new URoConverterDelegate<PbTemperatureHumidityGet.TemperatureHumidityGetResponse, URoHumitureValue>() {
                        @Override
                        public URoHumitureValue convert(PbTemperatureHumidityGet.TemperatureHumidityGetResponse source) throws Throwable {
                            return new URoHumitureValue(source.getHumidity(), source.getTemperature());
                        }
                    });
                } else if(URoCommandConstants.CMD_TH_PUSH.equals(cmd)) {
                    PbTemperatureHumidityPush.TemperatureHumidityPushResponse data = (PbTemperatureHumidityPush.TemperatureHumidityPushResponse)response.getData();
                    converter = URoConverter.newInstance(data, new URoConverterDelegate<PbTemperatureHumidityPush.TemperatureHumidityPushResponse, URoHumitureValue>() {
                        @Override
                        public URoHumitureValue convert(PbTemperatureHumidityPush.TemperatureHumidityPushResponse source) throws Throwable {
                            return new URoHumitureValue(source.getHumidity(), source.getTemperature());
                        }
                    });
                } else if(URoCommandConstants.CMD_LGT_VALUE_GET.equals(cmd)) {
                    PbLightValueGet.LightValueGetResponse data = (PbLightValueGet.LightValueGetResponse)response.getData();
                    converter = URoConverter.newInstance(data, new URoConverterDelegate<PbLightValueGet.LightValueGetResponse, Integer>() {
                        @Override
                        public Integer convert(PbLightValueGet.LightValueGetResponse source) throws Throwable {
                            return source.getValue();
                        }
                    });
                } else if(URoCommandConstants.CMD_LGT_VALUE_PUSH.equals(cmd)) {
                    PbLightValuePush.LightValuePushResponse data = (PbLightValuePush.LightValuePushResponse)response.getData();
                    converter = URoConverter.newInstance(data, new URoConverterDelegate<PbLightValuePush.LightValuePushResponse, Integer>() {
                        @Override
                        public Integer convert(PbLightValuePush.LightValuePushResponse source) throws Throwable {
                            return source.getValue();
                        }
                    });
                } else if(URoCommandConstants.CMD_SND_ADC_VALUE_GET.equals(cmd)) {
                    PbSoundAdcValueGet.SoundAdcValueGetResponse data = (PbSoundAdcValueGet.SoundAdcValueGetResponse)response.getData();
                    converter = URoConverter.newInstance(data, new URoConverterDelegate<PbSoundAdcValueGet.SoundAdcValueGetResponse, Integer>() {
                        @Override
                        public Integer convert(PbSoundAdcValueGet.SoundAdcValueGetResponse source) throws Throwable {
                            return source.getAdcValue();
                        }
                    });
                } else if(URoCommandConstants.CMD_SND_ADC_VALUE_PUSH.equals(cmd)) {
                    PbSoundAdcValuePush.SoundAdcValuePushResponse data = (PbSoundAdcValuePush.SoundAdcValuePushResponse)response.getData();
                    converter = URoConverter.newInstance(data, new URoConverterDelegate<PbSoundAdcValuePush.SoundAdcValuePushResponse, Integer>() {
                        @Override
                        public Integer convert(PbSoundAdcValuePush.SoundAdcValuePushResponse source) throws Throwable {
                            return source.getAdcValue();
                        }
                    });
                }
                sensorComponent.setSensorValueByConverter(converter);
                return null;
            }
        });
        addInterceptor(new URoProductResponseInterceptor() {
            @Override
            public URoResponse onInterceptResponse(URoCommand cmd, URoResponse response) {
                if(!URoCommandConstants.CMD_INTELLIGENT_DEV_SCRIPT_EXEC_REPORT.equals(cmd)) {
                    return null;
                }
                if(!response.isSuccess()) {
                    return null;
                }
                PbIntelligentDevscriptExecReport.IntelligentDevscriptExecReportResponse data = (PbIntelligentDevscriptExecReport.IntelligentDevscriptExecReportResponse)response.getData();
                String msg = data.getMsg();
                int subType = data.getType();
                URoPushMessageType type = URoPushMessageType.SCRIPT_REPORT;
                EventBus.getDefault().post(new URoPushMessageEvent<>(getLinkModel(), type, subType, msg));
                return null;
            }
        });
        addInterceptor(new URoProductResponseInterceptor() {
            @Override
            public URoResponse onInterceptResponse(URoCommand cmd, URoResponse response) {
                if(!URoCommandConstants.CMD_INTELLIGENT_DEV_WIFI_INFO_REPORT.equals(cmd)) {
                    return null;
                }
                if(!response.isSuccess()) {
                    return null;
                }
                PbIntelligentDevWifiInfoReport.IntelligentDevWifiInfoReportResponse data = (PbIntelligentDevWifiInfoReport.IntelligentDevWifiInfoReportResponse)response.getData();
                URoPushMessageType type = URoPushMessageType.WIFI_STATUS_CHANGE;
                URoWiFiStatusInfo.URoWiFiState state = URoWiFiStatusInfo.URoWiFiState.findByCode(data.getWifiState());
                URoWiFiStatusInfo.URoWiFiDisconnectReason disconnectReason = URoWiFiStatusInfo.URoWiFiDisconnectReason.findByCode(data.getWifiDisconnectReason());
                String ssid = data.getSsid();
                int rssi = data.getRssi();
                URoWiFiAuthMode authMode = URoWiFiAuthMode.findByCode(data.getAuthmode());
                EventBus.getDefault().post(new URoPushMessageEvent<>(getLinkModel(), type, new URoWiFiStatusInfo(state, disconnectReason, ssid, rssi, authMode)));
                return null;
            }
        });
        addInterceptor(new URoProductResponseInterceptor() {
            @Override
            public URoResponse onInterceptResponse(URoCommand cmd, URoResponse response) {
                if(!URoCommandConstants.CMD_INTELLIGENT_DEV_NETWORK_INFO_REPORT.equals(cmd)) {
                    return null;
                }
                if(!response.isSuccess()) {
                    return null;
                }
                PbIntelligentDevNetworkInfoReport.IntelligentDevNetworkInfoReportResponse data = (PbIntelligentDevNetworkInfoReport.IntelligentDevNetworkInfoReportResponse)response.getData();
                URoPushMessageType type = URoPushMessageType.NETWORK_STATUS_CHANGE;
                URoNetworkState state = URoNetworkState.findByCode(data.getNetworkState());
                EventBus.getDefault().post(new URoPushMessageEvent<>(getLinkModel(), type, state));
                return null;
            }
        });
        addInterceptor(new URoProductResponseInterceptor() {
            @Override
            public URoResponse onInterceptResponse(URoCommand cmd, URoResponse response) {
                if(!URoCommandConstants.CMD_VR_TTS_INFO_REPORT.equals(cmd)) {
                    return null;
                }
                if(!response.isSuccess()) {
                    return null;
                }
                PbVoiceTtsInfoReport.VoiceTtsInfoReportResponse data = (PbVoiceTtsInfoReport.VoiceTtsInfoReportResponse)response.getData();
                URoPushMessageType type = URoPushMessageType.TTS_REPORT;
                URoVoiceServiceState state = URoVoiceServiceState.findByCode(data.getState());
                EventBus.getDefault().post(new URoPushMessageEvent<>(getLinkModel(), type, state));
                return null;
            }
        });
        addInterceptor(new URoProductResponseInterceptor() {
            @Override
            public URoResponse onInterceptResponse(URoCommand cmd, URoResponse response) {
                if(!URoCommandConstants.CMD_VR_ASR_INFO_REPORT.equals(cmd)) {
                    return null;
                }
                if(!response.isSuccess()) {
                    return null;
                }
                PbVoiceAsrInfoReport.VoiceAsrInfoReportResponse data = (PbVoiceAsrInfoReport.VoiceAsrInfoReportResponse)response.getData();
                URoPushMessageType type = URoPushMessageType.ASR_REPORT;
                URoVoiceServiceState state = URoVoiceServiceState.findByCode(data.getState());
                boolean isMatch = data.getMatchState() == 1;
                String keyWord = data.getKeyWord();
                EventBus.getDefault().post(new URoPushMessageEvent<>(getLinkModel(), type, new URoAsrResult(state, isMatch, keyWord)));
                return null;
            }
        });
        addInterceptor(new URoProductResponseInterceptor() {
            @Override
            public URoResponse onInterceptResponse(URoCommand cmd, URoResponse response) {
                if(!URoCommandConstants.CMD_VR_RECORD_INFO_REPORT.equals(cmd)) {
                    return null;
                }
                if(!response.isSuccess()) {
                    return null;
                }
                PbVoiceRecordInfoReport.VoiceRecordInfoReportResponse data = (PbVoiceRecordInfoReport.VoiceRecordInfoReportResponse)response.getData();
                URoPushMessageType type = URoPushMessageType.AUDIO_RECORD_REPORT;
                URoAudioRecordState state = URoAudioRecordState.findByCode(data.getFileState());
                String name = data.getFileName();
                long duration = data.getFileDuration();
                long date = data.getFileDate();
                int sessionId = data.getSessionId();
                URoAudioRecord record = new URoAudioRecord(name, duration, date, state);
                record.setSessionId(sessionId);
                EventBus.getDefault().post(new URoPushMessageEvent<>(getLinkModel(), type, record));
                return null;
            }
        });
        addInterceptor(new URoProductResponseInterceptor() {
            @Override
            public URoResponse onInterceptResponse(URoCommand cmd, URoResponse response) {
                if(!URoCommandConstants.CMD_VR_PLAY_INFO_REPORT.equals(cmd)) {
                    return null;
                }
                if(!response.isSuccess()) {
                    return null;
                }
                PbVoicePlayInfoReport.VoicePlayInfoReportResponse data = (PbVoicePlayInfoReport.VoicePlayInfoReportResponse)response.getData();
                URoPushMessageType type = URoPushMessageType.AUDIO_PLAY_REPORT;
                URoAudioPlayState state = URoAudioPlayState.findByCode(data.getState());
                int sessionId = data.getSessionId();
                URoAudioPlayResult result = new URoAudioPlayResult(state);
                result.setSessionId(sessionId);
                EventBus.getDefault().post(new URoPushMessageEvent<>(getLinkModel(), type, result));
                return null;
            }
        });
        addInterceptor(new URoProductResponseInterceptor() {
            @Override
            public URoResponse onInterceptResponse(URoCommand cmd, URoResponse response) {
                PbMessageHeader.Header header = (PbMessageHeader.Header) response.getExtraData();
                if (header != null && !PbMessageHeader.Header.Attr.REQACK.equals(header.getAttr())) {
                    //非REQUEST应答的消息肯定都属于推送消息类的，设置返回数据包为推送消息标识
                    response.setPush(true);
                }
                return response;
            }
        });
        addInterceptor(new URoProductResponseInterceptor() {
            @Override
            public URoResponse onInterceptResponse(URoCommand cmd, URoResponse response) {
                if(!URoCommandConstants.CMD_RESET_STATE_SET.equals(cmd)) {
                    return null;
                }
                if(!response.isSuccess()) {
                    PbMessageHeader.Header header = (PbMessageHeader.Header) response.getExtraData();
                    if(header.getAck() == 4) {
                        response.setSuccess(true);
                        return response;
                    }
                }
                return null;
            }
        });
    }

    protected String formatByteString(ByteString bytes) {
        StringBuilder sb = new StringBuilder();
        byte[] byteArray = bytes.toByteArray();
        for(int i = 0; i < byteArray.length; i++) {
            sb.append(String.format("%02X", byteArray[i] & 0xFF));
        }
        return sb.toString();
    }

    protected String formatString(ByteString bytes) {
        byte[] data = bytes.toByteArray();
        int length = data.length;
        for(int i = length - 1; i >= 0; i--) {
            if(data[i] != 0) {
                break;
            }
            length--;
        }
        return new String(Arrays.copyOfRange(data, 0, length));
    }

}
