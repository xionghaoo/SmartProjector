package com.ubtedu.ukit.menu.settings;

import androidx.annotation.IntDef;

import com.ubtedu.ukit.bluetooth.UkitDeviceCompact;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * @Author qinicy
 * @Date 2019-12-25
 **/
public class TargetDevice {
    public static final int NONE = 0;
    public static final int UKIT1 = UkitDeviceCompact.UKIT_LEGACY_DEVICE;
    public static final int UKIT2 = UkitDeviceCompact.UKIT_SMART_DEVICE;

    @IntDef({NONE, UKIT1, UKIT2})
    @Retention(RetentionPolicy.SOURCE)
    public @interface Values{

    }
}
