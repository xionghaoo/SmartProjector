package com.ubtedu.ukit.application;


import com.google.gson.Gson;
import com.ubtedu.alpha1x.utils.MD5Util;
import com.ubtedu.alpha1x.utils.StringUtil;
import com.ubtedu.bridge.BaseBridgeAPI;
import com.ubtedu.ukit.project.vo.Blockly;

import org.junit.Test;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * @Author qinicy
 * @Date 2018/11/20
 **/
public class PrivacyInfoTest {
    private static final String SIGN_PART_SEPARATOR = " ";


    @Test
    public void tests() {

        long now = System.currentTimeMillis() / 1000;
        String randStr = StringUtil.getRandomChar(10);
        String versionNum = "v2";
        String str = now +"85eca10c5b654315a23ef28ed37cace3" + randStr + "8f3185fca893d1ec5c08df277aac0472";
        String sign = MD5Util.encodeByMD5(str)
                + SIGN_PART_SEPARATOR + now
                + SIGN_PART_SEPARATOR + randStr
                + SIGN_PART_SEPARATOR + versionNum;

        System.out.println(sign);

    }

    @Test
    public void rxTest() {

        Map<String, Object> map = new HashMap<>();
        map.put("id",1);
        map.put("name","motion1");
        Gson gson = new Gson();
        System.out.println(gson.toJson(map));

    }

    public Map<String, Method> getBridgeAPIs() {
        Class clazz = Blockly.class;
        System.out.println(clazz.getPackage().getName());

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