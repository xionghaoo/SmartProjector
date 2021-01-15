package com.ubtedu.base.net.rxretrofit.convert;

import android.text.TextUtils;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.TypeAdapter;
import com.ubtedu.base.net.rxretrofit.mode.ApiResult;

import java.io.IOException;
import java.lang.reflect.Type;

import okhttp3.ResponseBody;
import retrofit2.Converter;

/**
 * @Description: ResponseBody to T
 * @author: qinicy
 * @date: 17/5/7 18:05
 */
final class GsonResponseBodyConverter<T> implements Converter<ResponseBody, T> {
    private final Gson gson;
    private final TypeAdapter<T> adapter;
    private final Type mType;

    GsonResponseBodyConverter(Gson gson, TypeAdapter<T> adapter, Type type) {
        this.gson = gson;
        this.adapter = adapter;
        this.mType = type;
    }

    @Override
    public T convert(ResponseBody value) throws IOException {
        if (adapter != null && gson != null) {
            String json = value.string();

            if (TextUtils.isEmpty(json)) {
                json = "";
            }
            T response = null;
            try {
                response = adapter.fromJson(json);
            } catch (JsonSyntaxException e) {
                e.printStackTrace();
            }

            if (response != null) {
                if (response instanceof ApiResult){
                    ApiResult wrapper = (ApiResult) response;
                    wrapper.sourceRawString = json;
                    try {
                        T raw = adapter.fromJson("{\"sourceRawData\":" + json + "}");
                        if (raw != null && raw instanceof ApiResult) {
                            ApiResult result = (ApiResult) raw;
                            wrapper.sourceRawData = result.sourceRawData;
                        }
                    } catch (JsonSyntaxException e) {
//                        e.printStackTrace();
                    }
                }
                return response;
            }

        }
        ApiResult apiResult = new ApiResult();
        apiResult.code = -1;
        return (T) apiResult;

    }
}