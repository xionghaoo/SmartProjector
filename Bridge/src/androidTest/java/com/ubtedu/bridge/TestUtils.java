package com.ubtedu.bridge;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Assert;

/**
 * @Author qinicy
 * @Date 2019/3/29
 **/
public class TestUtils {
    public static boolean checkResultFormat(String resultJson) {
        try {
            JSONObject result = new JSONObject(resultJson);
            Assert.assertTrue(result.has(BridgeFieldKeys.KEY_ID));
            Assert.assertTrue(result.has(BridgeFieldKeys.KEY_CODE));
            Assert.assertTrue(result.has(BridgeFieldKeys.KEY_COMPLETE));
            return true;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return false;
    }
}
