package com.ubtedu.base.net.rxretrofit.func;

import android.text.TextUtils;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.ubtedu.base.net.rxretrofit.mode.ApiResult;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.Type;

import io.reactivex.functions.Function;
import okhttp3.ResponseBody;

/**
 * @Description: ResponseBody转ApiResult<T>
 * @author: qinicy
 * @date: 17/5/417:55
 */
public class ApiResultFunc<T> implements Function<ResponseBody, ApiResult<T>> {
    protected Class<T> clazz;
    protected Type type;

    public ApiResultFunc(Class<T> clazz) {
        this.clazz = clazz;
    }

    public ApiResultFunc(Type clazz) {
        this.type = clazz;
    }

    private ApiResult parseApiResult(String json, ApiResult apiResult) throws JSONException {
        if (TextUtils.isEmpty(json)) return null;
        JSONObject jsonObject = new JSONObject(json);
        if (jsonObject.has("code")) {
            apiResult.code = jsonObject.getInt("code");
        }
        if (jsonObject.has("data")) {
            apiResult.data = jsonObject.getString("data");
        }
        if (jsonObject.has("msg")) {
            apiResult.msg = jsonObject.getString("msg");
        }

        return apiResult;
    }

    @Override
    public ApiResult<T> apply(ResponseBody responseBody) {
        Gson gson = new Gson();
        ApiResult<T> apiResult = new ApiResult<T>();
        apiResult.code = -1;
        try {
            String json = responseBody.string();
            if (type.equals(new TypeToken<String>() {
            }.getType())) {
                apiResult.data = (T) json;
                apiResult.code = 0;
            } else {
                ApiResult result = parseApiResult(json, apiResult);
                if (result != null) {
                    apiResult = result;
                    if (apiResult.data != null) {
                        T data = gson.fromJson(apiResult.data.toString(), type);
                        apiResult.data = data;
                    } else {
                        apiResult.msg = "ApiResult's data is null";
                    }
                } else {
                    apiResult.msg = "json is null";
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
            apiResult.msg = e.getMessage();
        } catch (IOException e) {
            e.printStackTrace();
            apiResult.msg = e.getMessage();
        } finally {
            responseBody.close();
        }
        return apiResult;
    }
}
