/**
 * @2018 UBTECH Robotics Corp. All rights reserved (C)
 **/
package com.ubtedu.bridge;


import java.util.IllformedLocaleException;
import java.util.List;

/**
 * @Author qinicy
 * @Date 2019/3/25
 **/
public class TestAPI extends BaseBridgeAPI {
    @Override
    public String getNamespace() {
        return null;
    }

    public BridgeResult testJsonObject(Number number, BridgeObject o) {
        System.out.println("TestAPI:" + number + " name:" + o.opt("name") + " email:" + o.optJSONObject("info").optString("email"));
        return BridgeResult.SUCCESS();
    }

    public List testJsonArray(Number number, BridgeArray array) {
        System.out.println("TestAPI:testJsonArray");
        return array.asList();
    }

    public int testReturnType(Number a, Number b) throws Exception {
        System.out.println("TestAPI:testReturnType");
        return a.intValue() + b.intValue();
    }

    public void testNoReturn() throws Exception {

    }

    public String testNoArgs() throws Exception {

        return "testNoArgs";
    }

    public int testThrowException(Number x) throws Exception {

        throw new IllformedLocaleException("RuntimeException 5555 error....");
    }

    public void testAsync(APICallback callback) throws Exception {

        callback.onCallback("testAsync", true);
    }


}