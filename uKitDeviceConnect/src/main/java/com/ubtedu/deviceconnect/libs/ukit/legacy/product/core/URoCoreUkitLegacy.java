package com.ubtedu.deviceconnect.libs.ukit.legacy.product.core;

import com.ubtedu.deviceconnect.libs.base.component.URoComponent;
import com.ubtedu.deviceconnect.libs.base.component.URoSensorComponent;
import com.ubtedu.deviceconnect.libs.base.component.URoSpeaker;
import com.ubtedu.deviceconnect.libs.base.interfaces.URoConverter;
import com.ubtedu.deviceconnect.libs.base.interfaces.URoConverterDelegate;
import com.ubtedu.deviceconnect.libs.base.mission.URoProductInitMission;
import com.ubtedu.deviceconnect.libs.base.model.URoBatteryInfo;
import com.ubtedu.deviceconnect.libs.base.model.URoColor;
import com.ubtedu.deviceconnect.libs.base.model.URoComponentType;
import com.ubtedu.deviceconnect.libs.base.model.URoError;
import com.ubtedu.deviceconnect.libs.base.model.URoHumitureValue;
import com.ubtedu.deviceconnect.libs.base.model.URoLinkModel;
import com.ubtedu.deviceconnect.libs.base.model.URoMainBoardInfo;
import com.ubtedu.deviceconnect.libs.base.model.URoSerialNumberInfo;
import com.ubtedu.deviceconnect.libs.base.model.URoSpeakerInfo;
import com.ubtedu.deviceconnect.libs.base.model.URoTouchSensorValue;
import com.ubtedu.deviceconnect.libs.base.model.event.URoComponentErrorEvent;
import com.ubtedu.deviceconnect.libs.base.product.URoConnectStatus;
import com.ubtedu.deviceconnect.libs.base.product.URoProductResponseInterceptor;
import com.ubtedu.deviceconnect.libs.base.product.URoProductType;
import com.ubtedu.deviceconnect.libs.base.product.core.URoCoreProduct;
import com.ubtedu.deviceconnect.libs.base.product.core.protocol.URoProtocolHandler;
import com.ubtedu.deviceconnect.libs.base.product.core.queue.URoCommand;
import com.ubtedu.deviceconnect.libs.base.product.core.queue.URoCommandConstants;
import com.ubtedu.deviceconnect.libs.base.product.core.queue.URoRequest;
import com.ubtedu.deviceconnect.libs.base.product.core.queue.URoRequestQueueManager;
import com.ubtedu.deviceconnect.libs.base.product.core.queue.URoResponse;
import com.ubtedu.deviceconnect.libs.ukit.legacy.mission.URoUkitLegacyInitMission;
import com.ubtedu.deviceconnect.libs.ukit.legacy.model.URoUkitLegacyComponentMap;
import com.ubtedu.deviceconnect.libs.ukit.legacy.product.URoUkitLegacy;
import com.ubtedu.deviceconnect.libs.ukit.legacy.product.core.protocol.URoUkitLegacyProtocolHandler;
import com.ubtedu.deviceconnect.libs.ukit.legacy.product.core.queue.URoUkitLegacyRequestQueueManager;
import com.ubtedu.deviceconnect.libs.utils.URoLogUtils;

import org.greenrobot.eventbus.EventBus;

import java.util.Arrays;
import java.util.HashSet;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

import static com.ubtedu.deviceconnect.libs.base.product.core.queue.URoUkitIdHelper.bytesToIds;

/**
 * @Author naOKi
 * @Date 2019/08/12
 **/
public class URoCoreUkitLegacy extends URoCoreProduct<URoRequest, URoResponse, URoUkitLegacyProtocolHandler> {

    private URoUkitLegacy product;

    public URoCoreUkitLegacy(String productID, URoProductType productType, URoLinkModel link, URoConnectStatus connectStatus) {
        super(productID, productType, link, connectStatus);
    }

    @Override
    protected URoProtocolHandler createProtocolHandler() {
        return new URoUkitLegacyProtocolHandler();
    }

    @Override
    protected URoRequestQueueManager createRequestQueueManager(URoLinkModel link, URoProtocolHandler protocolHandler) {
        return new URoUkitLegacyRequestQueueManager(link, protocolHandler);
    }

    @Override
    protected URoProductInitMission createInitMission() {
        return new URoUkitLegacyInitMission(product);
    }

    @Override
    public URoUkitLegacy asProduct() {
        synchronized (this) {
            if(product == null) {
                product = new URoUkitLegacy(getLinkModel().linkName, getProductID());
            }
        }
        return product;
    }

    protected Disposable resetHeartBeatTimerInternal() {
        Disposable disposable = Observable.interval(1000, 1000, TimeUnit.MILLISECONDS)
                .subscribeOn(Schedulers.newThread())
                .observeOn(Schedulers.newThread())
                .subscribe((aLong) -> {
                    if(!isQueueContain(URoCommandConstants.CMD_BOARD_HEARTBEAT) && (System.currentTimeMillis() - getLastSendTime()) > 2000) {
                        URoRequest request = new URoRequest(URoCommandConstants.CMD_BOARD_HEARTBEAT);
                        addRequest(request, null, URoCommandConstants.CMD_BOARD_HEARTBEAT, null);
                    }
                });
        return disposable;
    }

    protected Disposable resetBatteryTimerInternal() {
        Disposable disposable = Observable.interval(60000, 60000, TimeUnit.MILLISECONDS)
                .subscribeOn(Schedulers.newThread())
                .observeOn(Schedulers.newThread())
                .subscribe((aLong) -> {
                    URoRequest request = new URoRequest(URoCommandConstants.CMD_BOARD_BATTERY);
                    addRequest(request, null, URoCommandConstants.CMD_BOARD_BATTERY, null);
                });
        return disposable;
    }

    @Override
    protected void onInitInterceptor() {
        addInterceptor(new URoProductResponseInterceptor() {
            @Override
            public URoResponse onInterceptResponse(URoCommand cmd, URoResponse response) {
                if(!URoCommandConstants.CMD_BOARD_INFO.equals(cmd)) {
                    return null;
                }
                if(!response.isSuccess()) {
                    return null;
                }
                URoMainBoardInfo mainBoardInfo = (URoMainBoardInfo)response.getData();
                setMainBoardInfo(mainBoardInfo);
                return null;
            }
        });
        addInterceptor(new URoProductResponseInterceptor() {
            @Override
            public URoResponse onInterceptResponse(URoCommand cmd, URoResponse response) {
                if(!URoCommandConstants.CMD_BOARD_SERIAL_NUMBER.equals(cmd)) {
                    return null;
                }
                if(!response.isSuccess()) {
                    return null;
                }
                URoSerialNumberInfo serialNumberInfo = (URoSerialNumberInfo)response.getData();
                setSerialNumberInfo(serialNumberInfo);
                return null;
            }
        });
        addInterceptor(new URoProductResponseInterceptor() {
            @Override
            public URoResponse onInterceptResponse(URoCommand cmd, URoResponse response) {
                if(!URoCommandConstants.CMD_BOARD_BATTERY.equals(cmd)) {
                    return null;
                }
                if(!response.isSuccess()) {
                    return null;
                }
                URoBatteryInfo batteryInfo = (URoBatteryInfo)response.getData();
                setBatteryInfo(batteryInfo);
                return null;
            }
        });
        addInterceptor(new URoProductResponseInterceptor() {
            @Override
            public URoResponse onInterceptResponse(URoCommand cmd, URoResponse response) {
                if(!URoCommandConstants.CMD_SENSOR_SPEAKER_INFO.equals(cmd)) {
                    return null;
                }
                if(!response.isSuccess()) {
                    return null;
                }
                URoSpeakerInfo speakerInfo = (URoSpeakerInfo)response.getData();
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
                if(!URoCommandConstants.CMD_BOARD_SELF_CHECK.equals(cmd)) {
                    return null;
                }
                byte[] bizData = response.getBizData();
                if (bizData.length == 29 && (bizData[0] == 0x02 || bizData[0] == 0x04)) {
                    //舵机或电机有问题
                    HashSet<Integer> errorIds = new HashSet<>();
                    errorIds.addAll(bytesToIds(bizData[1], bizData[2], bizData[3], bizData[4])); //堵转保护
                    errorIds.addAll(bytesToIds(bizData[5], bizData[6], bizData[7], bizData[8])); //电流保护
                    errorIds.addAll(bytesToIds(bizData[9], bizData[10], bizData[11], bizData[12])); //温度保护
                    errorIds.addAll(bytesToIds(bizData[13], bizData[14], bizData[15], bizData[16])); //过压保护
                    errorIds.addAll(bytesToIds(bizData[17], bizData[18], bizData[19], bizData[20])); //欠压保护
                    errorIds.addAll(bytesToIds(bizData[21], bizData[22], bizData[23], bizData[24])); //其它类型异常
                    errorIds.addAll(bytesToIds(bizData[25], bizData[26], bizData[27], bizData[28])); //熔丝位或加密位错误保护
                    if (!errorIds.isEmpty()) {
                        URoComponentType componentType;
                        if (bizData[0] == 0x02) {
                            componentType = URoComponentType.SERVOS;
                        } else {
                            componentType = URoComponentType.MOTOR;
                        }
                        for (Integer errorId : errorIds) {
                            URoLogUtils.d("%s-%d异常", componentType.name(), errorId);
                            EventBus.getDefault().post(new URoComponentErrorEvent(getLinkModel(), componentType, errorId, URoError.CTRL_ERROR));
                        }
                    }
                }
                if (bizData.length == 2 && (bizData[0] == 0x03 || bizData[0] == 0x05)) {
                    //舵机或电机版本不一致
                    int errorId = (int) bizData[1];
                    URoComponentType componentType;
                    if (bizData[0] == 0x03) {
                        componentType = URoComponentType.SERVOS;
                    } else {
                        componentType = URoComponentType.MOTOR;
                    }
                    URoLogUtils.d("%s-%d异常", componentType.name(), errorId);
                    EventBus.getDefault().post(new URoComponentErrorEvent(getLinkModel(), componentType, errorId, URoError.CTRL_ERROR));
                }
                return null;
            }
        });
        addInterceptor(new URoProductResponseInterceptor() {
            @Override
            public URoResponse onInterceptResponse(URoCommand cmd, URoResponse response) {
                if(!URoCommandConstants.CMD_LED_EMOTION.equals(cmd)) {
                    return null;
                }
                byte[] bizData = response.getBizData();
                URoComponentType componentType = URoUkitLegacyComponentMap.getTypeByValue(bizData[0]);
                if (bizData.length == 3 && URoComponentType.LED.equals(componentType) && bizData[2] == 0x01) {
                    //灯光有问题
                    HashSet<Integer> errorIds = new HashSet<>(bytesToIds(bizData[1]));
                    if (!errorIds.isEmpty()) {
                        for (Integer errorId : errorIds) {
                            URoLogUtils.d("%s-%d异常", componentType.name(), errorId);
                            EventBus.getDefault().post(new URoComponentErrorEvent(getLinkModel(), componentType, errorId, URoError.CTRL_ERROR));
                        }
                    }
                }
                return null;
            }
        });
        addInterceptor(new URoProductResponseInterceptor() {
            @Override
            public URoResponse onInterceptResponse(URoCommand cmd, URoResponse response) {
                if(!URoCommandConstants.CMD_LED_FACE.equals(cmd)) {
                    return null;
                }
                byte[] bizData = response.getBizData();
                URoComponentType componentType = URoUkitLegacyComponentMap.getTypeByValue(bizData[0]);
                if (bizData.length == 3 && (URoComponentType.LED.equals(componentType) || URoComponentType.ULTRASOUNDSENSOR.equals(componentType)) && bizData[2] == 0x01) {
                    //灯光或超声有问题
                    HashSet<Integer> errorIds = new HashSet<>(bytesToIds(bizData[1]));
                    if (!errorIds.isEmpty()) {
                        for (Integer errorId : errorIds) {
                            URoLogUtils.d("%s-%d异常", componentType.name(), errorId);
                            EventBus.getDefault().post(new URoComponentErrorEvent(getLinkModel(), componentType, errorId, URoError.CTRL_ERROR));
                        }
                    }
                }
                return null;
            }
        });
        addInterceptor(new URoProductResponseInterceptor() {
            @Override
            public URoResponse onInterceptResponse(URoCommand cmd, URoResponse response) {
                if(!URoCommandConstants.CMD_SENSOR_VALUE.equals(cmd)) {
                    return null;
                }
                byte[] bizData = response.getBizData();
                if (bizData.length > 1) {
                    for (int i = 3; i < bizData.length; ) {
                        byte type = bizData[i++];
                        URoComponentType componentType = URoUkitLegacyComponentMap.getTypeByValue(type);
                        int dataLength = URoUkitLegacyComponentMap.getSensorDataLength(componentType);
                        byte errId = bizData[i++];
                        byte ids = bizData[i++];
                        i += bytesToIds(ids).size() * dataLength;
                        HashSet<Integer> errorIds = new HashSet<>(bytesToIds(errId));
                        if (!errorIds.isEmpty()) {
                            for (Integer errorId : errorIds) {
                                URoLogUtils.d("%s-%d异常", componentType.name(), errorId);
                                EventBus.getDefault().post(new URoComponentErrorEvent(getLinkModel(), componentType, errorId, URoError.READ_ERROR));
                            }
                        }
                    }
                }
                return null;
            }
        });
        addInterceptor(new URoProductResponseInterceptor() {
            @Override
            public URoResponse onInterceptResponse(URoCommand cmd, URoResponse response) {
                if(!URoCommandConstants.CMD_SENSOR_VALUE.equals(cmd)) {
                    return null;
                }
                byte[] bizData = response.getBizData();
                if(bizData.length == 1) {
                    return null;
                }
                for(int i = 3; i < bizData.length; ) {
                    byte type = bizData[i++];
                    URoComponentType componentType = URoUkitLegacyComponentMap.getTypeByValue(type);
                    byte errId = bizData[i++];
                    byte ids = bizData[i++];
                    for(int j = 0; j < 8; j++) {
                        if((errId & (1 << j)) != 0) {
                            int id = j + 1;
                            URoComponent component = findComponent(componentType, id);
                            if(component instanceof URoSensorComponent) {
                                ((URoSensorComponent<?>)component).setSensorValue(null);
                            }
                            errId &= (~(1 << j)) & 0xFF;
                            if(errId == 0) {
                                break;
                            }
                        }
                    }
                    int dataLength = URoUkitLegacyComponentMap.getSensorDataLength(componentType);
                    for(int j = 0; j < 8; j++) {
                        if((ids & (1 << j)) != 0) {
                            int id = j + 1;
                            URoComponent component = findComponent(componentType, id);
                            URoSensorComponent sensorComponent = null;
                            if(component instanceof URoSensorComponent) {
                                sensorComponent = (URoSensorComponent<?>)component;
                            }
                            if(sensorComponent != null) {
                                byte[] value = Arrays.copyOfRange(bizData, i, i + dataLength);
                                URoConverter converter = null;
                                switch (componentType) {
                                case INFRAREDSENSOR:
                                    converter = URoConverter.newInstance(value, new URoConverterDelegate<byte[], Integer>() {
                                        @Override
                                        public Integer convert(byte[] source) {
                                            int value = (((source[0] & 0xFF) << 8) | (source[1] & 0xFF)) & 0xFFFF;
                                            float realValue = value - 850;
                                            int level;
                                            if (realValue < 0) {
                                                level = 0;
                                            } else if (realValue < 70) {
                                                level = (int) ((realValue - 15) / 13.5);
                                            } else if (realValue < 1210) {
                                                level = (int) ((realValue + 1134) / 288.0);
                                            } else if (realValue < 1565) {
                                                level = (int) ((realValue + 206) / 177);
                                            } else if (realValue < 1821) {
                                                level = (int) ((realValue - 1033) / 53.75);
                                            } else if (realValue < 2200) {
                                                level = (int) ((realValue - 1462) / 22.75);
                                            } else {
                                                level = 20;
                                            }
                                            return Math.min(level, 20);
                                        }
                                    });
                                    break;
                                case TOUCHSENSOR:
                                    converter = URoConverter.newInstance(value, new URoConverterDelegate<byte[], URoTouchSensorValue>() {
                                        @Override
                                        public URoTouchSensorValue convert(byte[] source) {
                                            int value = source[0] & 0xFF;
                                            return new URoTouchSensorValue(value);
                                        }
                                    });
                                    break;
                                case BRIGHTNESSSENSOR:
                                    converter = URoConverter.newInstance(value, new URoConverterDelegate<byte[], Integer>() {
                                        @Override
                                        public Integer convert(byte[] source) {
                                            int value = (((source[0] & 0xFF) << 8) | (source[1] & 0xFF)) & 0xFFFF;
                                            return Math.min(4000, Math.max(0, value));
                                        }
                                    });
                                    break;
                                case SOUNDSENSOR:
                                    converter = URoConverter.newInstance(value, new URoConverterDelegate<byte[], Integer>() {
                                        @Override
                                        public Integer convert(byte[] source) {
                                            int value = (((source[0] & 0xFF) << 8) | (source[1] & 0xFF)) & 0xFFFF;
                                            return (Math.min(4095, Math.max(2048, value)) - 2048) / 2;
                                        }
                                    });
                                    break;
                                case COLORSENSOR:
                                    converter = URoConverter.newInstance(value, new URoConverterDelegate<byte[], URoColor>() {
                                        @Override
                                        public URoColor convert(byte[] source) {
                                            return new URoColor(((source[0] & 0xFF) << 16) | ((source[1] & 0xFF) << 8) | (source[2] & 0xFF));
                                        }
                                    });
                                    break;
                                case ENVIRONMENTSENSOR:
                                    converter = URoConverter.newInstance(value, new URoConverterDelegate<byte[], URoHumitureValue>() {
                                        @Override
                                        public URoHumitureValue convert(byte[] source) {
                                            int value = (((source[0] & 0xFF) << 24) | ((source[1] & 0xFF) << 16)) & 0xFFFF0000;
                                            float temperature = ((value / 0x10000) + 5) / 10;
                                            int humidity = (((source[2] & 0xFF) << 8) | (source[3] & 0xFF)) & 0xFFFF;
                                            return new URoHumitureValue(humidity, temperature);
                                        }
                                    });
                                    break;
                                case ULTRASOUNDSENSOR:
                                    converter = URoConverter.newInstance(value, new URoConverterDelegate<byte[], Integer>() {
                                        @Override
                                        public Integer convert(byte[] source) {
                                            int value = (((((source[0] & 0xFF) << 8) | (source[1] & 0xFF)) & 0xFFFF) + 5) / 10;
                                            return Math.min(400, Math.max(3, value));
                                        }
                                    });
                                    break;
                                default:
                                    continue;
                                }
                                sensorComponent.setSensorValueByConverter(converter);
                            }
                            i += dataLength;
                            ids &= (~(1 << j)) & 0xFF;
                            if(ids == 0) {
                                break;
                            }
                        }
                    }
                }
                return null;
            }
        });
    }

}
