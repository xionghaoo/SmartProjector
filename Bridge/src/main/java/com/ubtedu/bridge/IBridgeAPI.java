/**
 * ©2018, UBTECH Robotics, Inc. All rights reserved (C)
 **/
package com.ubtedu.bridge;

import java.lang.reflect.Method;
import java.util.Map;

/**
 * Android APIs定义接口，需要实现{@link #isJavascriptAPI()}和{@link #getBridgeAPIs()}，
 * 这两个接口不对外暴露
 *
 * @Author qinicy
 * @Date 2018/10/9
 **/
public interface IBridgeAPI {
    /**
     * 判断该class定义的API是否是JavascriptAPI，如果是JavascriptAPI，所定义的API需要添加
     * {@link android.webkit.JavascriptInterface}注解
     *
     * @return true ==>is JavascriptAPI，false: isn't JavascriptAPI
     */
    boolean isJavascriptAPI();

    /**
     * 获取I所有定义的API map，
     *
     * @return Map<方法名 , 方法>
     */
    Map<String, Method> getBridgeAPIs();

    /**
     * 该API的命名空间。Bridge框架只能注册一个无命名空间的API类，如果重复注册，后注册的将会取代前面
     * 注册的。调用具有命名空间的API时，需要添加命名空间前缀，如namespace.api
     * @return 返回命名空间名称
     */
    String getNamespace();
}
