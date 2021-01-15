/**
 * @2018 UBTECH Robotics Corp. All rights reserved (C)
 **/
package com.ubtedu.ukit.menu.region;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.Locale;

/**
 * @Author qinicy
 * @Date 2019/3/5
 **/
public class RegionInfo implements Serializable {
    public final static String REGION_CN = "CN";
    public final static String REGION_NA = "NA";
    public final static String REGION_GL = "GL";
    public final static String REGION_PL = "PL";
    public final static String REGION_KR = "KR";
    public final static String REGION_VN = "VN";
    public final static String REGION_BD = "BD";
    //修改后
    public final static String LANGUAGE_CN = "zh_CN";
    public final static String LANGUAGE_NA = "en_NA";
    public final static String LANGUAGE_GL = "en_GL";
    public final static String LANGUAGE_PL = "pl_PL";
    public final static String LANGUAGE_KR = "ko_KR";
    public final static String LANGUAGE_VN = "vi_VN";
    public final static String LANGUAGE_BD = "bn_BD";
    public final static String TAG_CN = "CN";
    public final static String TAG_NA = "NA";
    public final static String TAG_GL = "GL";
    public final static String TAG_PL = "PL";
    public final static String TAG_KR = "KR";
    public final static String TAG_VN = "VN";
    public final static String TAG_BD = "BD";
    //app切换语言用到
    @SerializedName("name")
    public String name;
    @SerializedName("Locale")
    public Locale locale;
    //app UI显示
    @SerializedName("displayName")
    public String displayName;
    @SerializedName("backgroundResourceId")
    public int backgroundResourceId;
    //Unity用到
    @SerializedName("unityLanguage")
    public String unityLanguage;
    @SerializedName("unityTag")
    public String unityTag;
}
