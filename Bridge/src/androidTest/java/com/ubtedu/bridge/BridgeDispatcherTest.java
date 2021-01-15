package com.ubtedu.bridge;

import androidx.test.runner.AndroidJUnit4;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @Author qinicy
 * @Date 2019/3/25
 **/
@RunWith(AndroidJUnit4.class)
public class BridgeDispatcherTest {
    private BridgeDispatcher mDispatcher;
    @Before
    public void init() {
        mDispatcher = new BridgeDispatcher(new IBridgeHandler() {
            @Override
            public void registerFrameworkInterFace(IBridgeInterface bridgeInterface) {

            }

            @Override
            public void call(String funName, Object[] args, OnCallback callback) {

            }

            @Override
            public void sendCallbackResult(BridgeResult result) {

            }

            @Override
            public void onCallback(BridgeResult result) {

            }
        });
        TestAPI api = new TestAPI();
        mDispatcher.addBridgeAPI(api, api.getNamespace());
    }

    @Test
    public void testJsonObject() {
        /**
         {
         "id": 11102,
         "func": "testJsonObject",
         "args": [1,{"name":"qinicy","age":28,"info":{"phone":"17688990990","email":"im@qinicy.me"}}]
         }
         */
        String json = "    {\n" +
                "         \"id\": 11102,\n" +
                "         \"func\": \"testJsonObject\",\n" +
                "         \"args\": [1,{\"name\":\"qinicy\",\"age\":28,\"info\":{\"phone\":\"17688990990\",\"email\":\"im@qinicy.me\"}}]\n" +
                "         }";
        String result = mDispatcher.call(json);
        System.out.println("bridge test testJsonObject:" + result);
    }
    @Test
    public void testJsonArray() {
        /**
         {
         "id": 11102,
         "func": "testJsonArray",
         "args": [1,[{"name":"qinicy","age":28,"info":{"phone":"17688990990","email":"im@qinicy.me"}},{"name":"qinicy","age":28,"info":{"phone":"17688990990","email":"im@qinicy.me"}}]]
         }
         */
        String json = "    {\n" +
                "         \"id\": 11102,\n" +
                "         \"func\": \"testJsonArray\",\n" +
                "         \"args\": [1,[{\"name\":\"qinicy\",\"age\":28,\"info\":{\"phone\":\"17688990990\",\"email\":\"im@qinicy.me\"}},{\"name\":\"qinicy\",\"age\":28,\"info\":{\"phone\":\"17688990990\",\"email\":\"im@qinicy.me\"}}]]\n" +
                "         }";
        String result = mDispatcher.call(json);
        System.out.println("bridge test testJsonArray:" + result);
    }
    @Test
    public void testReturnType() {
        /**
         {
         "id": 11102,
         "func": "testReturnType",
         "args": [3,4]
         }
         */
        String json = "      {\n" +
                "         \"id\": 11102,\n" +
                "         \"func\": \"testReturnType\",\n" +
                "         \"args\": [3,4]\n" +
                "         }";
        String result = mDispatcher.call(json);
        System.out.println("bridge test testReturnType:" + result);
    }
    @Test
    public void testThrowException() {
        /**
         {
         "id": 11102,
         "func": "testThrowException",
         "args": []
         }
         */
        String json = "      {\n" +
                "         \"id\": 11102,\n" +
                "         \"func\": \"testThrowException\",\n" +
                "         \"args\": [1]\n" +
                "         }";
        String result = mDispatcher.call(json);
        System.out.println("bridge test testThrowException:" + result);
    }
    @Test
    public void testNoReturn() {
        /**
         {
         "id": 11102,
         "func": "testNoReturn",
         "args": []
         }
         */
        String json = "      {\n" +
                "         \"id\": 11102,\n" +
                "         \"func\": \"testNoReturn\",\n" +
                "         \"args\": []\n" +
                "         }";
        String result = mDispatcher.call(json);
        System.out.println("bridge test testNoReturn:" + result);
    }
    @Test
    public void testNoArgs() {
        /**
         {
         "id": 11102,
         "func": "testNoArgs",
         "args": []
         }
         */
        String json = "      {\n" +
                "         \"id\": 11102,\n" +
                "         \"func\": \"testNoArgs\"\n" +
                "         }";
        String result = mDispatcher.call(json);
        System.out.println("bridge test testNoArgs:" + result);
    }
    @Test
    public void testAsync(){
        /**
         {
         "id": 11102,
         "func": "testAsync",
         "args": []
         }
         */
        String json = "      {\n" +
                "         \"id\": 11102,\n" +
                "         \"func\": \"testAsync\"\n" +
                "         }";
        String result = mDispatcher.call(json);
        System.out.println("bridge test testAsync:" + result);
    }
}