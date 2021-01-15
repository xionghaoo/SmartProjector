package com.ubtedu.base.net.rxretrofit.func;

import com.google.gson.Gson;

import java.io.IOException;
import java.lang.reflect.Type;

import io.reactivex.functions.Function;
import okhttp3.ResponseBody;

/**
 * @Description: ResponseBody转T
 * @author: qinicy
 * @date: 17/5/5 14:39
 */
public class ApiFunc<T> implements Function<ResponseBody, T> {
    protected Type type;

    public ApiFunc(Type clazz) {
        this.type = clazz;
    }

    @Override
    public T apply(ResponseBody responseBody) {
        Gson gson = new Gson();
        String json = null;
        try {
            json = responseBody.string();
            return gson.fromJson(json, type);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            responseBody.close();
        }
        return (T) json;
    }
}
