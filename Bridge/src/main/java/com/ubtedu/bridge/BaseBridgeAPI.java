/**
 * ©2018, UBTECH Robotics, Inc. All rights reserved (C)
 **/
package com.ubtedu.bridge;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * Bridge API的定义基类
 * @Author qinicy
 * @Date 2018/10/8
 **/
public abstract class BaseBridgeAPI implements IBridgeAPI {

    @Override
    public boolean isJavascriptAPI() {
        return false;
    }

    @Override
    public Map<String, Method> getBridgeAPIs() {
        Class clazz = this.getClass();
        Method[] methods = clazz.getMethods();
        if (methods != null) {
            Map<String, Method> methodMap = new HashMap<>();

            for (int i = 0; i < methods.length; i++) {
                Method method = methods[i];
                String packageName = method.getDeclaringClass().getPackage().getName();
                if (!Object.class.getPackage().getName().equals(packageName) &&
                        !BaseBridgeAPI.class.getSimpleName().equals(packageName)) {
                    methodMap.put(method.getName(), method);
                }
            }
            return methodMap;
        }
        return null;
    }
}
