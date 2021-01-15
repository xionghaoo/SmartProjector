package com.ubtedu.deviceconnect.libs.base.invocation.constants;

public interface URoInvocationParamKeys {
    interface Component {
        String PARAM_KEY_SRC_ID = "src_id";
        String PARAM_KEY_NEW_ID = "new_id";

        String PARAM_KEY_ID = "id";
        String PARAM_KEY_ID_ARRAY = "id_array";

        String PARAM_KEY_TYPE = "type";

        String PARAM_KEY_MOTION = "motion";

        String PARAM_KEY_TIME = "time";
        String PARAM_KEY_PADDING_TIME = "padding_time";

        String PARAM_KEY_COLOR = "color";

        String PARAM_KEY_ENABLE = "enable";
    }

    interface Servos {
        String PARAM_KEY_TURN_ID_ANGLE_PAIR = "id_angle_pair";
        String PARAM_KEY_ROTATE_ID_SPEED_PAIR = "id_speed_pair";
        String PARAM_KEY_ROTATE_SPEED = "speed";
        String PARAM_KEY_READBACK_POWEROFF = "poweroff";
        String PARAM_KEY_OFFSET = "offset";
    }

    interface Motor {
        String PARAM_KEY_ROTATE_COMMAND = "rotate_command";
        String PARAM_KEY_ROTATE_PWM = "rotate_pwm";
    }

    interface Sensor {
        String PARAM_KEY_EFFECT_ID = "effect_id";
        String PARAM_KEY_TIMES = "times";
        String PARAM_KEY_SOUND_CALIBRATE_TYPE = "sound_calibrate_type";
        String PARAM_KEY_CALIBRATE_VALUE = "calibrate_value";
    }

    interface File {
        String PARAM_KEY_SOURCE = "source";
        String PARAM_KEY_SOURCE_DATA = "source_data";
        String PARAM_KEY_TARGET = "target";
    }

    interface Upgrade {
        String PARAM_KEY_URL = "url";
        String PARAM_KEY_MD5 = "md5";
        String PARAM_KEY_SIZE = "size";
    }

}
