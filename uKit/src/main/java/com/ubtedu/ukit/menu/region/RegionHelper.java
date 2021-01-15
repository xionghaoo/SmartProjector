/**
 * @2018 UBTECH Robotics Corp. All rights reserved (C)
 **/
package com.ubtedu.ukit.menu.region;

import com.ubtedu.alpha1x.utils.AppUtil;
import com.ubtedu.alpha1x.utils.LogUtil;
import com.ubtedu.ukit.common.locale.APILanguages;
import com.ubtedu.ukit.common.locale.UBTLocale;
import com.ubtedu.ukit.menu.settings.Settings;

import java.util.Locale;

/**
 * @Author qinicy
 * @Date 2019/3/5
 **/
public class RegionHelper {

    private final static String[] NA_COUNTRIES = new String[]{"us", "ca"};
    private final static String POLAND = "pl";
    private final static String KOREA = "kr";
    private final static String VIETNAM = "vn";
    private final static String BANGLADESH = "bd";

    public static boolean isChinaMainland() {
        return AppUtil.isSimpleChinese(AppUtil.getSystemLanguage());
    }

    public static boolean isNA() {
        Locale locale = AppUtil.getSystemLanguage();
        LogUtil.d(locale.toString());
        //是否北美地区
        String country = locale.getCountry();
        boolean isNA = false;
        if (country != null) {
            for (String region : NA_COUNTRIES) {
                isNA = country.toLowerCase().equalsIgnoreCase(region);
                if (isNA) {
                    break;
                }
            }
        }
        String language = locale.getLanguage();
        return language.toLowerCase().contains("en") && isNA;
    }

    public static boolean isPL() {
        Locale locale = AppUtil.getSystemLanguage();
        LogUtil.d(locale.toString());
        //是否波兰
        String country = locale.getCountry();
        return POLAND.equalsIgnoreCase(country);
    }

    public static boolean isVN() {
        Locale locale = AppUtil.getSystemLanguage();
        LogUtil.d(locale.toString());
        //是否是越南
        String country = locale.getCountry();
        return VIETNAM.equalsIgnoreCase(country);
    }

    public static boolean isKR() {
        Locale locale = AppUtil.getSystemLanguage();
        LogUtil.d(locale.toString());
        //是否韩国
        String country = locale.getCountry();
        return KOREA.equalsIgnoreCase(country);
    }

    public static boolean isBD() {
        Locale locale = AppUtil.getSystemLanguage();
        LogUtil.d(locale.toString());
        //是否是孟加拉国
        String country = locale.getCountry();
        return BANGLADESH.equalsIgnoreCase(country);
    }

    /**
     * 获取当前地区的服务器接口language参数
     * https://docs.qq.com/sheet/DQVhGQnJhQ0VycFZR?tab=BB08J2&coord=C5%24C5%240%240%240%240
     */
    public static String getAPIRegionLanguage() {
        String region = Settings.getRegion().name;
        switch (region) {
            case RegionInfo.REGION_GL:
            case RegionInfo.REGION_NA:
                return APILanguages.EN;
            case RegionInfo.REGION_CN:
                return APILanguages.ZH_HANS;
            case RegionInfo.REGION_PL:
                return APILanguages.PL;
            case RegionInfo.REGION_KR:
                return APILanguages.KO;
            case RegionInfo.REGION_VN:
                return APILanguages.VN;
            case RegionInfo.REGION_BD:
                return APILanguages.BN;
        }

        return APILanguages.ZH_HANS;
    }

    public static UBTLocale getDefaultRegionAccountLocale() {
        String region = Settings.getRegion().name;
        switch (region) {
            case RegionInfo.REGION_GL:
            case RegionInfo.REGION_NA:
                return UBTLocale.newUSLocale();
            case RegionInfo.REGION_CN:
                return UBTLocale.newChinaMainlandLocale();
            case RegionInfo.REGION_PL:
                return UBTLocale.newPolandLocale();
            case RegionInfo.REGION_KR:
                return UBTLocale.newKoreaLocale();
            case RegionInfo.REGION_VN:
                return UBTLocale.newVietnamLocale();
            case RegionInfo.REGION_BD:
                return UBTLocale.newBangladeshLocale();

        }

        return UBTLocale.newUSLocale();

    }
}
