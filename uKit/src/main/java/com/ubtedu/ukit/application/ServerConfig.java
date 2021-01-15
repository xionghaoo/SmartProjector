/**
 * @2018 UBTECH Robotics Corp. All rights reserved (C)
 **/
package com.ubtedu.ukit.application;

import com.ubtedu.alpha1x.utils.LogUtil;
import com.ubtedu.alpha1x.utils.SharedPreferenceUtils;
import com.ubtedu.ukit.BuildConfig;
import com.ubtedu.ukit.common.flavor.Channel;
import com.ubtedu.ukit.common.flavor.Flavor;
import com.ubtedu.ukit.menu.settings.Settings;

/**
 * 正式服务器比较特殊：
 * 国内：
 * https://internal.ubtrobot.com/v1/ubt-ems-rest
 * <p>
 * 国外：
 * https://interface.ubtrobot.com/v1/ubt-ems-rest
 * <p>
 * 测试服务器和预发布服务器跟其它的一致。
 *
 * @Author qinicy
 * @Date 2019/4/19
 **/
public class ServerConfig {
    public final static String INTERNAL_URL = BuildConfig.RELEASE_INTERNAL_URL;
    public final static String OVERSEA_URL = BuildConfig.RELEASE_INTERFACE_URL;
    public static final String SP_SERVER_CONFIG = "server_config";
    private static final String AWS_API_SERVER_URL = BuildConfig.RELEASE_AWS_URL;
    private static String sAPIServer;
    private static String sOtaServer;
    private static String sOtaStorage;
    
    private final static ServerEnv DEFAULT_SERVER_ENV = ServerEnv.RELEASE;
//    private final static ServerEnv DEFAULT_SERVER_ENV = ServerEnv.PRERELEASE;
//    private final static ServerEnv DEFAULT_SERVER_ENV = ServerEnv.TEST;

    private static String sTutorialServer;


    static {
        init();
    }

    private static void init() {
        ServerEnv env = getServerEnvConfig();

        LogUtil.d("Server Env=================》:" + env.name());
        switch (env) {
            case TEST:
                sAPIServer = BuildConfig.API_SERVER_TEST;
                sTutorialServer = sAPIServer;
                sOtaServer = BuildConfig.OTA_SERVER_TEST;
                sOtaStorage = BuildConfig.OTA_STORAGE_TEST;
                break;
            case PRERELEASE:
                sAPIServer = BuildConfig.API_SERVER_PRE;
                sTutorialServer = sAPIServer;
                sOtaServer = BuildConfig.OTA_SERVER_PRE;
                sOtaStorage = BuildConfig.OTA_STORAGE_PRE;
                break;
            case RELEASE:
                //release环境，北美api服务器和教程服务器不是同一个
                sAPIServer = getReleaseServer();
                sTutorialServer = getCommonReleaseApiServer();
                sOtaServer = BuildConfig.OTA_SERVER_RELEASE;
                sOtaStorage = BuildConfig.OTA_STORAGE_RELEASE;
        }
    }

    /**
     * 根据条件获取服务器
     */
    private static String getReleaseServer() {
        if (Flavor.getChannel().getId() == Channel.NA.getId()) {
            return AWS_API_SERVER_URL;
        } else {
            return getCommonReleaseApiServer();
        }
    }

    /**
     * 获取非北美的服务器
     *
     * @return
     */
    private static String getCommonReleaseApiServer() {
        if (Settings.isRegionCN()) {
            return INTERNAL_URL;
        } else {
            return OVERSEA_URL;
        }
    }

    public static ServerEnv getServerEnvConfig() {
        String defaultName = isDebug() ? ServerEnv.TEST.name() : DEFAULT_SERVER_ENV.name();
        String envName = SharedPreferenceUtils.getInstance(UKitApplication.getInstance()).getStringValue(SP_SERVER_CONFIG, defaultName);
        try {
            return ServerEnv.valueOf(envName);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
        return DEFAULT_SERVER_ENV;
    }


    public static String getAPIServer() {
        init();
        return sAPIServer;
    }

    public static String getTutorialServer(){
        init();
        return sTutorialServer;
    }

    public static String getOtaServer() {
        return sOtaServer;
    }

    public static String getOtaStorage() {
        return sOtaStorage;
    }

    private static boolean isDebug() {
        return BuildConfig.DEBUG;
    }
}
