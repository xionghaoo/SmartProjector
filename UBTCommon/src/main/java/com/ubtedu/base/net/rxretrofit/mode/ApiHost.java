package com.ubtedu.base.net.rxretrofit.mode;

import com.ubtedu.base.common.FrameworkConfig;

/**
 * @Description: 主机信息
 * @author: qinicy
 * @date: 17/5/210:37
 */
public class ApiHost {

    private static String host = FrameworkConfig.API_HOST;

    public static String getHost() {
        return host;
    }

    public static void setHost(String url) {
        setHostHttps(url);
    }

    public static void setHostHttp(String url) {
        if (url.startsWith("https://") || url.startsWith("http://")) {
            host = url;
            host = host.replaceAll("https://", "http://");
        } else {
            host = "http://" + url;
        }
    }

    public static void setHostHttps(String url) {
        if (url.startsWith("https://") || url.startsWith("http://")) {
            host = url;
            host = host.replaceAll("http://", "https://");
        } else {
            host = "https://" + url;
        }
    }

    public static String getHttp() {
        if (host.startsWith("https://") || host.startsWith("http://")) {
            host = host.replaceAll("https://", "http://");
        } else {
            host = "http://" + host;
        }
        return host;
    }

    public static String getHttps() {
        if (host.startsWith("https://") || host.startsWith("http://")) {
            host = host.replaceAll("http://", "https://");
        } else {
            host = "https://" + host;
        }
        return host;
    }

}
