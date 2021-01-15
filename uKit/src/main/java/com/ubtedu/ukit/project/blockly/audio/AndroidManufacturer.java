package com.ubtedu.ukit.project.blockly.audio;

import android.os.Build;
import android.text.TextUtils;

/**
 * @author shenghai.hu@ubtrobot.com
 * @ClassName TopWindowService
 * @date 2017/01/09
 * @modifier
 * @modifyTime 2017/01/09
 * @Description: Android各大厂商
 */
public class AndroidManufacturer {

    public static final String VIVO = "vivo";
    public static final String XIAO_MI = "Xiaomi";
    public static final String SAMSUNG = "samsung";
    public static final String ASUS = "asus";

    public static boolean match(String manufacturer){
        if(TextUtils.isEmpty(manufacturer)){
            return false;
        }
        String deviceManu = Build.MANUFACTURER.toLowerCase();
        String matchManu = manufacturer.toLowerCase();
        if(deviceManu.equals(matchManu)){
            return true;
        }else{
            return false;
        }
    }

}
