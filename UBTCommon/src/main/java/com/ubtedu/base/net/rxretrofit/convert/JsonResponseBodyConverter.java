package com.ubtedu.base.net.rxretrofit.convert;

import java.io.IOException;

import okhttp3.ResponseBody;
import retrofit2.Converter;

/**
 * @Description: ResponseBody to T
 * @author: qinicy
 * @date: 17/5/7 18:04
 */
final class JsonResponseBodyConverter<T> implements Converter<ResponseBody, T> {

    JsonResponseBodyConverter() {
    }

    @Override
    public T convert(ResponseBody value) throws IOException {
        return (T) value.string();
    }
}
