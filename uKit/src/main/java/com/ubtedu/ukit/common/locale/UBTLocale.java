package com.ubtedu.ukit.common.locale;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class UBTLocale implements Serializable {
    @SerializedName("latin")
    public String latin;
    @SerializedName("code")
    public String code;
    @SerializedName("dial_code")
    public String dial_code;
    @SerializedName("chineseName")
    public String chineseName;
    @SerializedName("name")
    public String name;

    public static UBTLocale newChinaMainlandLocale() {
        UBTLocale mainland = new UBTLocale();
        mainland.chineseName = "中国";
        mainland.name = "China";
        mainland.dial_code = "86";
        mainland.code = "CN";
        return mainland;
    }

    public static UBTLocale newKoreaLocale() {
        UBTLocale locale = new UBTLocale();
        locale.chineseName = "韩国";
        locale.name = "Korea, Republic of";
        locale.dial_code = "82";
        locale.code = "KR";
        return locale;
    }

    public static UBTLocale newPolandLocale() {
        UBTLocale locale = new UBTLocale();
        locale.chineseName = "波兰";
        locale.name = "Poland";
        locale.dial_code = "48";
        locale.code = "PL";
        return locale;
    }

    public static UBTLocale newUSLocale() {
        UBTLocale locale = new UBTLocale();
        locale.chineseName = "美国";
        locale.name = "United States";
        locale.dial_code = "1";
        locale.code = "US";
        return locale;
    }

    public static UBTLocale newVietnamLocale() {
        UBTLocale locale = new UBTLocale();
        locale.chineseName = "越南";
        locale.name = "Viet Nam";
        locale.dial_code = "84";
        locale.code = "VN";
        return locale;
    }

    public static UBTLocale newBangladeshLocale() {
        UBTLocale locale = new UBTLocale();
        locale.chineseName = "孟加拉国";
        locale.name = "Bangladesh";
        locale.dial_code = "880";
        locale.code = "BD";
        return locale;
    }
}
