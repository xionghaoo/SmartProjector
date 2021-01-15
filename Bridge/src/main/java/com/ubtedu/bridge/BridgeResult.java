/**
 * ©2018, UBTECH Robotics, Inc. All rights reserved (C)
 **/
package com.ubtedu.bridge;

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.SerializedName;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import static com.ubtedu.bridge.BridgeFieldKeys.KEY_CALLBACK;
import static com.ubtedu.bridge.BridgeFieldKeys.KEY_CODE;
import static com.ubtedu.bridge.BridgeFieldKeys.KEY_COMPLETE;
import static com.ubtedu.bridge.BridgeFieldKeys.KEY_DATA;
import static com.ubtedu.bridge.BridgeFieldKeys.KEY_ID;
import static com.ubtedu.bridge.BridgeFieldKeys.KEY_MSG;
import static com.ubtedu.bridge.BridgeFieldKeys.KEY_RESULT_TYPE;

/**
 * Bridge result定义如下：
 *  <br>
 *  {
 *  "id": 11102,
 *  "code": 0,
 *  "callback": "callbackName",
 *  "msg": "success",
 *  "data": "数据类型参照以下说明",
 *  "complete": 0
 *  }
 *  <ul>
 *  <li>id:请求ID，异步调用需要根据请求ID处理相应回调结果。必须包含</li>
 *  <li>code:方法执行结果，{0:成功,901:方法未定义，902:参数不匹配，903:返回类型错误，1000:未知错误，-1:失败}。必选</li>
 *  <li>data:call接口方法的异步回调结果数据。data可接受的类型是number,string,object,array,强烈建议不要使用json字符串代替object。可选</li>
 *  <li>complete:异步调用是否结束，{0:未结束，1:已结束}，主要是处理一些有进度的回调使用。必选</li>
 *  <li>msg:额外的结果信息描述。可选</li>
 *  <li>callback:call中的参数callback。可选</li>
 *  </ul>
 * @Author qinicy
 * @Date 2018/10/8
 **/
public class BridgeResult {

    @SerializedName(KEY_ID)
    public int id = -1;
    @SerializedName(KEY_CODE)
    public int code = BridgeResultCode.SUCCESS;
    @SerializedName(KEY_CALLBACK)
    public String callback;
    @SerializedName(KEY_MSG)
    public String msg;
    @SerializedName(KEY_DATA)
    public Object data;
    @SerializedName(KEY_COMPLETE)
    public int complete = BridgeBoolean.TRUE();
    /**
     * 是否是异步返回结果
     */
    @SerializedName(KEY_RESULT_TYPE)
    public boolean isAsyncResult;


    public BridgeResult id(int id) {
        this.id = id;
        return this;
    }

    public BridgeResult code(int code) {
        this.code = code;
        return this;
    }

    public BridgeResult callback(String callback) {
        this.callback = callback;
        return this;
    }

    public BridgeResult msg(String msg) {
        this.msg = msg;
        return this;
    }

    public BridgeResult data(Object data) {
        this.data = data;
        return this;
    }

    public BridgeResult complete(boolean isComplete) {
        this.complete = BridgeBoolean.wrap(isComplete);
        return this;
    }

    public BridgeResult asyncResult(boolean isAsyncResult) {
        this.isAsyncResult = isAsyncResult;
        return this;
    }

    public static BridgeResult SUCCESS() {
        return newBuilder().code(BridgeResultCode.SUCCESS)
                .isComplete(true)
                .msg("success")
                .build();
    }

    public static BridgeResult FAIL() {
        return newBuilder().code(BridgeResultCode.FAIL)
                .isComplete(true)
                .msg("fail")
                .build();
    }

    public static BridgeResult ILLEGAL_ARGUMENTS() {
        return newBuilder().code(BridgeResultCode.ILLEGAL_ARGUMENTS)
                .isComplete(true)
                .msg("ILLEGAL_ARGUMENTS")
                .build();
    }

    public static BridgeResult FUN_UNDEFINED() {
        return newBuilder().code(BridgeResultCode.FUN_UNDEFINED)
                .isComplete(true)
                .msg("FUN_UNDEFINED")
                .build();
    }

    public static BridgeResult UNKNOWN() {
        return newBuilder().code(BridgeResultCode.UNKNOWN)
                .isComplete(true)
                .msg("UNKNOWN")
                .build();
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    public String toJson() {
        if (data == null) {
            data = "";
        }
        if (msg == null) {
            msg = "God bless you never bug";
        }
        GsonBuilder builder = new GsonBuilder();
        builder.addSerializationExclusionStrategy(new ExclusionStrategy() {
            @Override
            public boolean shouldSkipField(FieldAttributes f) {
                SerializedName serializedName = f.getAnnotation(SerializedName.class);
                String name = "";
                if (serializedName != null) {
                    name = serializedName.value();
                }
                //忽略isAsyncResult字段
                return (BridgeFieldKeys.KEY_RESULT_TYPE.equals(name)) &&
                        f.getDeclaringClass().getName().equals(BridgeResult.class.getName());
            }

            @Override
            public boolean shouldSkipClass(Class<?> clazz) {
                return false;
            }
        });
        Gson gson = builder.create();
        String json = gson.toJson(this);
        if (data instanceof JSONObject || data instanceof JSONArray) {
            try {
                JSONObject jsonObject = new JSONObject(json);
                jsonObject.put(BridgeFieldKeys.KEY_DATA, data);
                json = jsonObject.toString();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return json;
    }

    @Override
    public String toString() {
        return toJson();
    }

    public static class Builder {
        private int id = -1;
        private int code = BridgeResultCode.SUCCESS;
        private String callback;
        private String msg;
        private Object data;
        private int complete = BridgeBoolean.TRUE();

        public Builder() {
        }

        public Builder(BridgeResult ret) {
            this.id = ret.id;
            this.code = ret.code;
            this.callback = ret.callback;
            this.msg = ret.msg;
            this.data = ret.data;
            this.complete = ret.complete;

        }

        public Builder id(int id) {
            this.id = id;
            return this;
        }

        public Builder code(int code) {
            this.code = code;
            return this;
        }

        public Builder msg(String msg) {
            this.msg = msg;
            return this;
        }

        public Builder callback(String callback) {
            this.callback = callback;
            return this;
        }

        public Builder data(Object data) {
            this.data = data;
            return this;
        }

        public Builder isComplete(boolean complete) {
            this.complete = BridgeBoolean.wrap(complete);
            return this;
        }

        public BridgeResult build() {
            BridgeResult result = new BridgeResult();
            result.id = id;
            result.code = code;
            result.msg = msg;
            result.data = data;
            result.complete = complete;
            result.callback = callback;
            return result;
        }

    }
}
