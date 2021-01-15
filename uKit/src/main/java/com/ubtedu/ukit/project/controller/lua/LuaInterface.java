package com.ubtedu.ukit.project.controller.lua;

import android.graphics.Color;
import android.text.TextUtils;

import com.ubtedu.bridge.BridgeBoolean;
import com.ubtedu.bridge.BridgeObject;
import com.ubtedu.bridge.BridgeResult;
import com.ubtedu.bridge.OnCallback;
import com.ubtedu.deviceconnect.libs.base.component.URoEnvironmentSensor;
import com.ubtedu.deviceconnect.libs.base.model.URoComponentType;
import com.ubtedu.deviceconnect.libs.utils.URoNumberConversionUtil;
import com.ubtedu.ukit.R;
import com.ubtedu.ukit.application.UKitApplication;
import com.ubtedu.ukit.bluetooth.BluetoothHelper;
import com.ubtedu.ukit.bluetooth.processor.PeripheralErrorCollector;
import com.ubtedu.ukit.project.Workspace;
import com.ubtedu.ukit.project.bridge.BluetoothCommunicator;
import com.ubtedu.ukit.project.bridge.MediaAudioPlayer;
import com.ubtedu.ukit.project.bridge.api.ToastHelper;
import com.ubtedu.ukit.project.controller.manager.ControllerManager;
import com.ubtedu.ukit.project.controller.utils.RcLogUtils;

import org.json.JSONObject;
import org.keplerproject.luajava.JavaFunction;
import org.keplerproject.luajava.LuaException;
import org.keplerproject.luajava.LuaState;

public class LuaInterface {

    private LuaInterface() {
    }

    private static LuaInterface mInstance = null;

    private static final int INVALID_VALUE = -9999;

    private static final int MAX_RECORD_TIME = 15000;

    private static final int MAX_EMOJI_TIME = 90000;
    private static final int MAX_SCENELIGHT_TIME = 90000;
    private static final int MAX_LED_TIME = 10000;


    public static LuaInterface getInstance() {
        synchronized (LuaInterface.class) {
            if (mInstance == null) {
                mInstance = new LuaInterface();
            }
            return mInstance;
        }
    }

    public static RunningFlagJavaFunction initFunctions(LuaState luaState, ILuaRunProgramAgainInterface runProgramAgainInterface) throws Exception {

        JavaFunction isLowPower = new JavaFunction(luaState) {
            @Override
            public int execute() throws LuaException {
                try {
                    L.pushBoolean(BluetoothHelper.isLowBattery());
                } catch (Exception e) {
                    RcLogUtils.e(e);
                    L.pushBoolean(false);
                }
                return 1;
            }
        };
        isLowPower.register("isLowPower");

        BooleanJavaFunction checkInfraredDistance = new BooleanJavaFunction(luaState) {
            @Override
            public int execute() throws LuaException {
                try {
                    int id = L.toInteger(2);
                    String op = L.toString(3);
                    int distance = L.toInteger(4);
                    PeripheralErrorCollector.getInstance().appendUsedPeripheral(URoComponentType.INFRAREDSENSOR, id);
                    int sensorValue = BluetoothHelper.getSensorValue(URoComponentType.INFRAREDSENSOR, id);
                    if (sensorValue == INVALID_VALUE) {
                        L.pushBoolean(false);
                    } else {
                        L.pushBoolean(compare(sensorValue, op, distance));
                    }
                } catch (Exception e) {
                    RcLogUtils.e(e);
                    L.pushBoolean(false);
                }
                return 1;
            }
        };
        checkInfraredDistance.register("checkInfraredDistance");

        BooleanJavaFunction checkUltrasonicDistance = new BooleanJavaFunction(luaState) {
            @Override
            public int execute() throws LuaException {
                try {
                    int id = L.toInteger(2);
                    String op = L.toString(3);
                    int distance = L.toInteger(4);
                    PeripheralErrorCollector.getInstance().appendUsedPeripheral(URoComponentType.ULTRASOUNDSENSOR, id);
                    int sensorValue = BluetoothHelper.getSensorValue(URoComponentType.ULTRASOUNDSENSOR, id);
                    if (sensorValue == INVALID_VALUE) {
                        L.pushBoolean(false);
                    } else {
                        L.pushBoolean(compare(sensorValue, op, distance));
                    }
                } catch (Exception e) {
                    RcLogUtils.e(e);
                    L.pushBoolean(false);
                }
                return 1;
            }
        };
        checkUltrasonicDistance.register("checkUltrasonicDistance");

        JavaFunction checkDeviceTiltStatus = new JavaFunction(luaState) {
            @Override
            public int execute() throws LuaException {
                try {
                    String type = L.toString(2);
                    L.pushBoolean(TextUtils.equals(type, ControllerManager.getDeviceDirection()));
                } catch (Exception e) {
                    RcLogUtils.e(e);
                    L.pushBoolean(false);
                }
                return 1;
            }
        };
        checkDeviceTiltStatus.register("checkDeviceTiltStatus");

        JavaFunction checkColorValue = new JavaFunction(luaState) {

            @Override
            public int execute() throws LuaException {
                try {
                    int id = L.toInteger(2);
                    String color = L.toString(3);
                    PeripheralErrorCollector.getInstance().appendUsedPeripheral(URoComponentType.COLORSENSOR, id);
                    int sensorValue = BluetoothHelper.getSensorValue(URoComponentType.COLORSENSOR, id);
                    if (sensorValue == INVALID_VALUE) {
                        L.pushBoolean(false);
                    } else {
                        int colorValue = Integer.valueOf(color.substring(1), 16);
                        float[] hsb = new float[3];
                        Color.colorToHSV(sensorValue, hsb);
                        int hue = (int) hsb[0];
                        int saturation = (int) (hsb[1] * 100);//拿过来是算法里面比较值都是乘以100，所以这里要将原始值乘以100
                        int brightness = (int) (hsb[2] * 100);//拿过来是算法里面比较值都是乘以100，所以这里要将原始值乘以100
                        boolean result = false;
                        if (colorValue != 0x000000 && (saturation > 15 && brightness > 25)) {
                            if (colorValue == 0xFF0000 && ((hue >= 0 && hue <= 11) || (hue >= 340 && hue <= 360))) {
                                result = true;
                            } else if (colorValue == 0xFF8000 && ((hue >= 18 && hue <= 39))) {
                                result = true;
                            } else if (colorValue == 0xFFFF00 && ((hue >= 41 && hue <= 62))) {
                                result = true;
                            } else if (colorValue == 0x00FF00 && ((hue >= 67 && hue <= 169))) {
                                result = true;
                            } else if (colorValue == 0x800080 && ((hue >= 256 && hue <= 337))) {
                                result = true;
                            } else if (colorValue == 0x0000FF && ((hue > 194 && hue <= 255))) {
                                result = true;
                            } else if (colorValue == 0x00FFFF && ((hue >= 170 && hue <= 193))) {
                                result = true;
                            }
                        } else if (colorValue == 0xFFFFFF && hue < 20 && saturation <= 7 && brightness >= 80) {
                            result = true;
                        } else if (colorValue == 0x000000 && (brightness <= 25)) {
                            result = true;
                        } else if (colorValue == 0x808080 && hue > 80 && saturation <= 15 && (brightness >= 30)) {
                            result = true;
                        }
                        L.pushBoolean(result);
                    }
                } catch (Exception e) {
                    RcLogUtils.e(e);
                    L.pushBoolean(false);
                }
                return 1;
            }
        };
        checkColorValue.register("checkColorValue");

        JavaFunction checkTouchStatus = new JavaFunction(luaState) {
            @Override
            public int execute() throws LuaException {
                try {
                    int id = L.toInteger(2);
                    int status = L.toInteger(3);
                    PeripheralErrorCollector.getInstance().appendUsedPeripheral(URoComponentType.TOUCHSENSOR, id);
                    int sensorValue = BluetoothHelper.getSensorValue(URoComponentType.TOUCHSENSOR, id);
                    if (sensorValue == INVALID_VALUE) {
                        L.pushBoolean(false);
                    } else {
                        L.pushBoolean(sensorValue == status);
                    }
                } catch (Exception e) {
                    RcLogUtils.e(e);
                    L.pushBoolean(false);
                }
                return 1;
            }
        };
        checkTouchStatus.register("checkTouchStatus");

        BooleanJavaFunction checkSoundIntensity = new BooleanJavaFunction(luaState) {
            @Override
            public int execute() throws LuaException {
                try {
                    int id = L.toInteger(2);
                    String op = L.toString(3);
                    int intensity = L.toInteger(4);
                    PeripheralErrorCollector.getInstance().appendUsedPeripheral(URoComponentType.SOUNDSENSOR, id);
                    int sensorValue = BluetoothHelper.getSensorValue(URoComponentType.SOUNDSENSOR, id);
                    if (sensorValue == INVALID_VALUE) {
                        L.pushBoolean(false);
                    } else {
                        L.pushBoolean(compare(sensorValue, op, intensity));
                    }
                } catch (Exception e) {
                    RcLogUtils.e(e);
                    L.pushBoolean(false);
                }
                return 1;
            }
        };
        checkSoundIntensity.register("checkSoundIntensity");

        BooleanJavaFunction checkLightnessIntensity = new BooleanJavaFunction(luaState) {
            @Override
            public int execute() throws LuaException {
                try {
                    int id = L.toInteger(2);
                    String op = L.toString(3);
                    int intensity = L.toInteger(4);
                    PeripheralErrorCollector.getInstance().appendUsedPeripheral(URoComponentType.BRIGHTNESSSENSOR, id);
                    int sensorValue = BluetoothHelper.getSensorValue(URoComponentType.BRIGHTNESSSENSOR, id);
                    if (sensorValue == INVALID_VALUE) {
                        L.pushBoolean(false);
                    } else {
                        L.pushBoolean(compare(sensorValue, op, intensity));
                    }
                } catch (Exception e) {
                    RcLogUtils.e(e);
                    L.pushBoolean(false);
                }
                return 1;
            }
        };
        checkLightnessIntensity.register("checkLightnessIntensity");

        JavaFunction getInfraredDistance = new JavaFunction(luaState) {
            @Override
            public int execute() throws LuaException {
                try {
                    int id = L.toInteger(2);
                    PeripheralErrorCollector.getInstance().appendUsedPeripheral(URoComponentType.INFRAREDSENSOR, id);
                    int sensorValue = BluetoothHelper.getSensorValue(URoComponentType.INFRAREDSENSOR, id);
                    if (sensorValue == INVALID_VALUE) {
                        L.pushInteger(0);
                    } else {
                        L.pushInteger(sensorValue);
                    }
                } catch (Exception e) {
                    RcLogUtils.e(e);
                    L.pushInteger(0);
                }
                return 1;
            }
        };
        getInfraredDistance.register("getInfraredDistance");

        JavaFunction getServoAngle = new JavaFunction(luaState) {
            @Override
            public int execute() throws LuaException {
                try {
                    int id = L.toInteger(2);
                    //todo 舵机角度
                    L.pushInteger(0);
                } catch (Exception e) {
                    RcLogUtils.e(e);
                    L.pushInteger(0);
                }
                return 1;
            }
        };
        getServoAngle.register("getServoAngle");

        JavaFunction getUltrasonicDistance = new JavaFunction(luaState) {
            @Override
            public int execute() throws LuaException {
                try {
                    int id = L.toInteger(2);
                    PeripheralErrorCollector.getInstance().appendUsedPeripheral(URoComponentType.ULTRASOUNDSENSOR, id);
                    int sensorValue = BluetoothHelper.getSensorValue(URoComponentType.ULTRASOUNDSENSOR, id);
                    if (sensorValue == INVALID_VALUE) {
                        L.pushInteger(0);
                    } else {
                        L.pushInteger(sensorValue);
                    }
                } catch (Exception e) {
                    RcLogUtils.e(e);
                    L.pushInteger(0);
                }
                return 1;
            }
        };
        getUltrasonicDistance.register("getUltrasonicDistance");

        JavaFunction getColorValue = new JavaFunction(luaState) {
            @Override
            public int execute() throws LuaException {
                try {
                    int id = L.toInteger(2);
                    String type = L.toString(3);
                    PeripheralErrorCollector.getInstance().appendUsedPeripheral(URoComponentType.COLORSENSOR, id);
                    int sensorValue = BluetoothHelper.getSensorValue(URoComponentType.COLORSENSOR, id);
                    if (sensorValue == INVALID_VALUE) {
                        L.pushInteger(0);
                    } else {
                        switch (type) {
                            case "RGB":
                                L.pushInteger(sensorValue);
                                break;
                            case "R":
                                L.pushInteger((sensorValue >> 16) & 0xFF);
                                break;
                            case "G":
                                L.pushInteger((sensorValue >> 8) & 0xFF);
                                break;
                            case "B":
                                L.pushInteger(sensorValue & 0xFF);
                                break;
                            default:
                                L.pushInteger(0);
                                break;
                        }

                    }
                } catch (Exception e) {
                    RcLogUtils.e(e);
                    L.pushInteger(0);
                }
                return 1;
            }
        };
        getColorValue.register("getColorValue");

        JavaFunction getSoundIntensity = new JavaFunction(luaState) {
            @Override
            public int execute() throws LuaException {
                try {
                    int id = L.toInteger(2);
                    PeripheralErrorCollector.getInstance().appendUsedPeripheral(URoComponentType.SOUNDSENSOR, id);
                    int sensorValue = BluetoothHelper.getSensorValue(URoComponentType.SOUNDSENSOR, id);
                    if (sensorValue == INVALID_VALUE) {
                        L.pushInteger(0);
                    } else {
                        L.pushInteger(sensorValue);
                    }
                } catch (Exception e) {
                    RcLogUtils.e(e);
                    L.pushInteger(0);
                }
                return 1;
            }
        };
        getSoundIntensity.register("getSoundIntensity");

        JavaFunction getLightnessIntensity = new JavaFunction(luaState) {
            @Override
            public int execute() throws LuaException {
                try {
                    int id = L.toInteger(2);
                    PeripheralErrorCollector.getInstance().appendUsedPeripheral(URoComponentType.BRIGHTNESSSENSOR, id);
                    int sensorValue = BluetoothHelper.getSensorValue(URoComponentType.BRIGHTNESSSENSOR, id);
                    if (sensorValue == INVALID_VALUE) {
                        L.pushInteger(0);
                    } else {
                        L.pushInteger(sensorValue);
                    }
                } catch (Exception e) {
                    RcLogUtils.e(e);
                    L.pushInteger(0);
                }
                return 1;
            }
        };
        getLightnessIntensity.register("getLightnessIntensity");

        JavaFunction getHumitureSensor = new JavaFunction(luaState) {
            @Override
            public int execute() throws LuaException {
                try {
                    int id = L.toInteger(2);
                    String type = L.toString(3);
                    PeripheralErrorCollector.getInstance().appendUsedPeripheral(URoComponentType.ENVIRONMENTSENSOR, id);
                    URoEnvironmentSensor sensor = (URoEnvironmentSensor) BluetoothHelper.getSensor(URoComponentType.ENVIRONMENTSENSOR, id);
                    int sensorValue = INVALID_VALUE;
                    if (sensor != null && sensor.getSensorValue() != null) {
                        switch (type) {
                            case "HUMIDNESS":
                                sensorValue = sensor.getSensorValue().getHumidity();
                                break;
                            case "CENTIGRADE":
                                sensorValue = Math.round(sensor.getSensorValue().getTemperature());
                                break;
                            case "FAHRENHEIT":
                                sensorValue = 32 + (int) (sensor.getSensorValue().getTemperature() * 1.8f);
                                break;
                            default:
                                sensorValue = INVALID_VALUE;
                                break;
                        }
                    }
                    if (sensorValue == INVALID_VALUE) {
                        L.pushInteger(0);
                    } else {
                        L.pushInteger(sensorValue);
                    }
                } catch (Exception e) {
                    RcLogUtils.e(e);
                    L.pushInteger(0);
                }
                return 1;
            }
        };
        getHumitureSensor.register("getHumitureSensor");

        JavaFunction setServo = new JavaFunction(luaState) {
            @Override
            public int execute() throws LuaException {
                try {
                    String args = L.toString(2);
                    RcLogUtils.e("setServo: %s", args);
                    BluetoothCommunicator.getInstance().setServo(args, null);
                } catch (Exception e) {
                    RcLogUtils.e(e);
                }
                return 0;
            }
        };
        setServo.register("setServo");

        JavaFunction setServobySpeed = new JavaFunction(luaState) {
            @Override
            public int execute() throws LuaException {
                try {
                    String args = L.toString(2);
                    RcLogUtils.e("setServobySpeed: %s", args);
                    BluetoothCommunicator.getInstance().setServobySpeed(args, null);
                } catch (Exception e) {
                    RcLogUtils.e(e);
                }
                return 0;
            }
        };
        setServobySpeed.register("setServobySpeed");

        JavaFunction setServoBySpeedPercent = new JavaFunction(luaState) {
            @Override
            public int execute() throws LuaException {
                try {
                    String args = L.toString(2);
                    RcLogUtils.e("setServoBySpeedPercent: %s", args);
                    BluetoothCommunicator.getInstance().setServoBySpeedPercent(args, null);
                } catch (Exception e) {
                    RcLogUtils.e(e);
                }
                return 0;
            }
        };
        setServoBySpeedPercent.register("setServoBySpeedPercent");

        JavaFunction setMotorSpeed = new JavaFunction(luaState) {
            @Override
            public int execute() throws LuaException {
                try {
                    String args = L.toString(2);
                    RcLogUtils.e("setMotorSpeed: %s", args);
                    BluetoothCommunicator.getInstance().setMotorSpeed(args, null);
                } catch (Exception e) {
                    RcLogUtils.e(e);
                }
                return 0;
            }
        };
        setMotorSpeed.register("setMotorSpeed");

        JavaFunction stopMotor = new JavaFunction(luaState) {
            @Override
            public int execute() throws LuaException {
                try {
                    String args = L.toString(2);
                    RcLogUtils.e("stopMotor: %s", args);
                    BluetoothCommunicator.getInstance().stopMotor(args, null);
                } catch (Exception e) {
                    RcLogUtils.e(e);
                }
                return 0;
            }
        };
        stopMotor.register("stopMotor");

        JavaFunction sleep = new JavaFunction(luaState) {
            @Override
            public int execute() throws LuaException {
                try {
                    int time = L.toInteger(2);
                    RcLogUtils.e("sleep: %d", time);
                    Thread.sleep(time);
                } catch (Exception e) {
                    RcLogUtils.e(e);
                }
                return 0;
            }
        };
        sleep.register("sleep");

        @Deprecated
        JavaFunction setUltrasonicLED = new JavaFunction(luaState) {
            @Override
            public int execute() throws LuaException {
                try {
                    String args = L.toString(2);
                    RcLogUtils.e("setUltrasonicLED: %s", args);
                    BluetoothCommunicator.getInstance().setUltrasonicLED(args, null);
                } catch (Exception e) {
                    RcLogUtils.e(e);
                }
                return 0;
            }
        };
        setUltrasonicLED.register("setUltrasonicLED");

        JavaFunction setGroupUltrasonicLED = new JavaFunction(luaState) {
            @Override
            public int execute() throws LuaException {
                try {
                    String args = L.toString(2);
                    RcLogUtils.e("setGroupUltrasonicLED: %s", args);
                    BluetoothCommunicator.getInstance().setGroupUltrasonicLED(args, null);
                } catch (Exception e) {
                    RcLogUtils.e(e);
                }
                return 0;
            }
        };
        setGroupUltrasonicLED.register("setGroupUltrasonicLED");

        @Deprecated
        JavaFunction turnOffLeds = new JavaFunction(luaState) {
            @Override
            public int execute() throws LuaException {
                try {
                    RcLogUtils.e("turnOffLeds");
                    BluetoothCommunicator.getInstance().turnOffLeds(null);
                } catch (Exception e) {
                    RcLogUtils.e(e);
                }
                return 0;
            }
        };
        turnOffLeds.register("turnOffLeds");

        JavaFunction setEmoji = new JavaFunction(luaState) {
            @Override
            public int execute() throws LuaException {
                try {
                    String args = L.toString(2);
                    RcLogUtils.e("setEmoji: %s", args);
                    JSONObject object = new JSONObject(args);
                    boolean isDelay = BridgeBoolean.isTrue(object.optInt("isdelay"));
                    if (isDelay) {
                        LuaBridgeCallback callback = new LuaBridgeCallback();
                        BluetoothCommunicator.getInstance().setEmoji(args, callback);
                        if (!callback.isDone()) {
                            callback.waitForExit(MAX_EMOJI_TIME);
                        }
                    } else {
                        BluetoothCommunicator.getInstance().setEmoji(args, null);
                    }
                } catch (Exception e) {
                    RcLogUtils.e(e);
                }
                return 0;
            }
        };
        setEmoji.register("setEmoji");

        JavaFunction setScenelight = new JavaFunction(luaState) {
            @Override
            public int execute() throws LuaException {
                try {
                    String args = L.toString(2);
                    RcLogUtils.e("setScenelight: %s", args);
                    JSONObject object = new JSONObject(args);
                    boolean isDelay = BridgeBoolean.isTrue(object.optInt("isdelay"));
                    if (isDelay) {
                        LuaBridgeCallback callback = new LuaBridgeCallback();
                        BluetoothCommunicator.getInstance().setScenelight(args, callback);
                        if (!callback.isDone()) {
                            callback.waitForExit(MAX_SCENELIGHT_TIME);
                        }
                    } else {
                        BluetoothCommunicator.getInstance().setScenelight(args, null);
                    }
                } catch (Exception e) {
                    RcLogUtils.e(e);
                }
                return 0;
            }
        };
        setScenelight.register("setScenelight");

        JavaFunction setLEDs = new JavaFunction(luaState) {
            @Override
            public int execute() throws LuaException {
                try {
                    String args = L.toString(2);
                    RcLogUtils.e("setLEDs: %s", args);
                    JSONObject object = new JSONObject(args);
                    boolean isDelay = BridgeBoolean.isTrue(object.optInt("isdelay"));
                    if (isDelay) {
                        LuaBridgeCallback callback = new LuaBridgeCallback();
                        BluetoothCommunicator.getInstance().setLEDs(args, callback);
                        if (!callback.isDone()) {
                            callback.waitForExit(MAX_LED_TIME);
                        }
                    } else {
                        BluetoothCommunicator.getInstance().setLEDs(args, null);
                    }
                } catch (Exception e) {
                    RcLogUtils.e(e);
                }
                return 0;
            }
        };
        setLEDs.register("setLEDs");

        JavaFunction playAudio = new JavaFunction(luaState) {
            @Override
            public int execute() throws LuaException {
                try {
                    String args = L.toString(2);
                    RcLogUtils.e("playAudio: %s", args);
                    BridgeObject object = new BridgeObject(args);
                    boolean isDelay = BridgeBoolean.isTrue(object.optInt("isdelay"));
                    if (isDelay) {
                        LuaBridgeCallback callback = new LuaBridgeCallback();
                        MediaAudioPlayer.getInstance().playAudio(object, callback);
                        if (!callback.isDone()) {
                            callback.waitForExit(MAX_RECORD_TIME);
                        }
                    } else {
                        MediaAudioPlayer.getInstance().playAudio(object, null);
                    }
                } catch (Exception e) {
                    RcLogUtils.e(e);
                }
                return 0;
            }
        };
        playAudio.register("playAudio");

        JavaFunction stopAudio = new JavaFunction(luaState) {
            @Override
            public int execute() throws LuaException {
                try {
                    RcLogUtils.e("stopAudio");
                    MediaAudioPlayer.getInstance().stopAudio();
                } catch (Exception e) {
                    RcLogUtils.e(e);
                }
                return 0;
            }
        };
        stopAudio.register("stopAudio");

        JavaFunction showDisplayValue = new JavaFunction(luaState) {
            @Override
            public int execute() throws LuaException {
                try {
                    String id = L.toString(2);
                    int value = L.toInteger(3);
                    RcLogUtils.e("showDisplayValue %s %d", id, value);
                    ControllerManager.updateReaderValueView(id, value);
                } catch (Exception e) {
                    RcLogUtils.e(e);
                }
                return 0;
            }
        };
        showDisplayValue.register("showDisplayValue");

        JavaFunction showDisplayColor = new JavaFunction(luaState) {
            @Override
            public int execute() throws LuaException {
                try {
                    String id = L.toString(2);
                    String value = L.toString(3);
                    RcLogUtils.e("showDisplayColor %s %s", id, value);
                    int color = URoNumberConversionUtil.hex2IntegerE(value.substring(1), 16) & 0xFFFFFF;
                    ControllerManager.updateReaderColorView(id, color);
                } catch (Exception e) {
                    RcLogUtils.e(e);
                }
                return 0;
            }
        };
        showDisplayColor.register("showDisplayColor");

        JavaFunction getSliderValue = new JavaFunction(luaState) {
            @Override
            public int execute() throws LuaException {
                try {
                    String id = L.toString(2);
                    RcLogUtils.e("getSliderValue %s", id);
                    L.pushInteger(ControllerManager.getSliderValue(id));
                } catch (Exception e) {
                    RcLogUtils.e(e);
                    L.pushInteger(0);
                }
                return 1;
            }
        };
        getSliderValue.register("getSliderValue");

        JavaFunction checkSwitchStatus = new JavaFunction(luaState) {
            @Override
            public int execute() throws LuaException {
                try {
                    String id = L.toString(2);
                    String status = L.toString(3);
                    RcLogUtils.e("checkSwitchStatus %s %s", id, status);
                    L.pushBoolean(TextUtils.equals(ControllerManager.getSwitchStatus(id) ? "on" : "off", status));
                } catch (Exception e) {
                    RcLogUtils.e(e);
                    L.pushBoolean(false);
                }
                return 1;
            }
        };
        checkSwitchStatus.register("checkSwitchStatus");

        JavaFunction checkButtonStatus = new JavaFunction(luaState) {
            @Override
            public int execute() throws LuaException {
                try {
                    String id = L.toString(2);
                    String status = L.toString(3);
                    RcLogUtils.e("checkButtonStatus %s %s", id, status);
                    L.pushBoolean(TextUtils.equals(ControllerManager.getButtonStatus(id) ? "down" : "up", status));
                } catch (Exception e) {
                    RcLogUtils.e(e);
                    L.pushBoolean(false);
                }
                return 1;
            }
        };
        checkButtonStatus.register("checkButtonStatus");

        JavaFunction execMotionFile = new JavaFunction(luaState) {
            @Override
            public int execute() throws LuaException {
                try {
                    String id = L.toString(2);
                    RcLogUtils.e("execMotionFile: %s", id);
                    BluetoothCommunicator.getInstance().execMotion(id, null);
                } catch (Exception e) {
                    RcLogUtils.e(e);
                }
                return 0;
            }
        };
        execMotionFile.register("execMotionFile");

        RestartProgramJavaFunction restartProgram = new RestartProgramJavaFunction(luaState, runProgramAgainInterface) {
            @Override
            public int execute() throws LuaException {
                try {
                    RcLogUtils.e("restartProgram");
                    runProgramAgain();
                } catch (Exception e) {
                    RcLogUtils.e(e);
                }
                return 0;
            }
        };
        restartProgram.register("restartProgram");

        JavaFunction getMotionTime = new JavaFunction(luaState) {
            @Override
            public int execute() throws LuaException {
                try {
                    String id = L.toString(2);
                    RcLogUtils.e("getMotionTime: %s", id);
                    L.pushInteger((int) Workspace.getInstance().getProject().getMotion(id).totaltime);
                } catch (Exception e) {
                    RcLogUtils.e(e);
                    L.pushInteger(0);
                }
                return 1;
            }
        };
        getMotionTime.register("getMotionTime");

        RunningFlagJavaFunction isRunning = new RunningFlagJavaFunction(luaState);
        isRunning.register("isRunning");

        DivisorJavaFunction divisor = new DivisorJavaFunction(luaState, isRunning) {
            @Override
            public int execute() throws LuaException {
                try {
                    int num1 = L.toInteger(2);
                    int num2 = L.toInteger(3);
                    RcLogUtils.e("divisor: %d, %d", num1, num2);
                    if (num2 == 0) {
                        termination();
                        return 0;
                    }
                    L.pushInteger(num1 / num2);
                } catch (Exception e) {
                    RcLogUtils.e(e);
                    L.pushInteger(0);
                }
                return 1;
            }
        };
        divisor.register("divisor");

        return isRunning;
    }

    public static abstract class DivisorJavaFunction extends JavaFunction {
        private RunningFlagJavaFunction runningFlagJavaFunction;

        public DivisorJavaFunction(LuaState L, RunningFlagJavaFunction runningFlagJavaFunction) {
            super(L);
            this.runningFlagJavaFunction = runningFlagJavaFunction;
        }

        public void termination() {
            if (runningFlagJavaFunction != null) {
                runningFlagJavaFunction.setRunningState(false);
            }
            BluetoothHelper.getBtHandler().post(new Runnable() {
                @Override
                public void run() {
                    String msg = UKitApplication.getInstance().getString(R.string.project_controller_divisor_zero_msg);
                    ToastHelper.toastShort(msg);
                    ControllerManager.terminateExecution();
                    PeripheralErrorCollector.getInstance().updateCollectorState(PeripheralErrorCollector.CollectorState.STOP, null);
                }
            });
        }
    }

    public static abstract class RestartProgramJavaFunction extends JavaFunction {
        private ILuaRunProgramAgainInterface runProgramAgainInterface;

        public RestartProgramJavaFunction(LuaState L, ILuaRunProgramAgainInterface runProgramAgainInterface) {
            super(L);
            this.runProgramAgainInterface = runProgramAgainInterface;
        }

        public void runProgramAgain() throws Exception {
            if (runProgramAgainInterface != null) {
                runProgramAgainInterface.onRunProgramAgain();
            }
        }
    }

    public static abstract class BooleanJavaFunction extends JavaFunction {
        public BooleanJavaFunction(LuaState L) {
            super(L);
        }

        public boolean compare(int var1, String op, int var2) {
            switch (op) {
                case "LTE":
                    return var1 <= var2;
                case "GTE":
                    return var1 >= var2;
                case "LT":
                    return var1 < var2;
                case "GT":
                    return var1 > var2;
                case "EQ":
                    return var1 == var2;
                case "NEQ":
                    return var1 != var2;
                default:
                    return false;
            }
        }
    }

    public static class RunningFlagJavaFunction extends JavaFunction {
        private boolean isRunning = false;

        public RunningFlagJavaFunction(LuaState L) {
            super(L);
        }

        public void setRunningState(boolean isRunning) {
            synchronized (this) {
                this.isRunning = isRunning;
            }
        }

        @Override
        public int execute() throws LuaException {
            synchronized (this) {
                L.pushBoolean(isRunning);
            }
            return 1;
        }
    }

    private static class LuaBridgeCallback implements OnCallback {
        private final Object lock = new Object();
        private boolean done = false;

        public final void waitForExit(long millis) {
            synchronized (lock) {
                try {
                    lock.wait(millis);
                } catch (Exception e) {
                    // ignore
                }
            }
        }

        public final void notifyExit() {
            synchronized (lock) {
                try {
                    lock.notifyAll();
                } catch (Exception e) {
                    // ignore
                }
            }
        }

        @Override
        public final void onCallback(BridgeResult result) {
            done = true;
            onBridgeCallback(result);
            notifyExit();
        }

        public void onBridgeCallback(BridgeResult result) {
        }

        ;

        public boolean isDone() {
            return done;
        }
    }

    protected interface ILuaRunProgramAgainInterface {
        void onRunProgramAgain() throws Exception;
    }

}
