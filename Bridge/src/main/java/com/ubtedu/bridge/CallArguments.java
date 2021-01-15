/**
 * ©2018, UBTECH Robotics, Inc. All rights reserved (C)
 **/
package com.ubtedu.bridge;

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.SerializedName;


/**
 * bridge arguments of call:
 <br />
 {
 "id": 11102,
 "func": "method name",
 "args": [],
 "callback": "callbackName"
 }

 <ul>
 <li>id:请求ID，异步调用需要根据请求ID处理相应回调结果。必须包含</li>
 <li>func:期望调用的接口方法名。必须包含</li>
 <li>args:JSON数组，期望调用的接口方法参数列表。无参数可省略该字段。强烈建议不要使用json字符串代替object。 可选</li>
 <li>callback:异步回调方法名。可选</li>
 </ul>
 * @Author qinicy
 * @Date 2018/10/9
 **/
public class CallArguments {

    @SerializedName(BridgeFieldKeys.KEY_ID)
    public int id = -1;

    @SerializedName(BridgeFieldKeys.KEY_FUNC)
    public String func;

    @SerializedName(BridgeFieldKeys.KEY_CALLBACK)
    public String callback;
    @SerializedName(BridgeFieldKeys.KEY_ARGS)
    public Object args;
    @SerializedName(BridgeFieldKeys.ASYNC)
    public boolean async;
    @SerializedName(BridgeFieldKeys.ON_CALLBACK)
    public OnCallback onCallback;

    public String funcArgsToJson(){
        if (args != null){
            return new Gson().toJson(args);
        }
        return "";
    }
    public String toJson() {
        GsonBuilder builder = new GsonBuilder();
        builder.addSerializationExclusionStrategy(new ExclusionStrategy() {
            @Override
            public boolean shouldSkipField(FieldAttributes f) {
                SerializedName serializedName = f.getAnnotation(SerializedName.class);
                String name = "";
                if (serializedName != null) {
                    name = serializedName.value();
                }
                //忽略async和onCallback两个字段
                return (BridgeFieldKeys.ASYNC.equals(name) || BridgeFieldKeys.ON_CALLBACK.equals(name)) &&
                        f.getDeclaringClass().getName().equals(CallArguments.class.getName());
            }

            @Override
            public boolean shouldSkipClass(Class<?> clazz) {
                return false;
            }
        });
        Gson gson = builder.create();

        return gson.toJson(this);
    }

    @Override
    public String toString() {
        return toJson();
    }
}
