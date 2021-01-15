package com.ubtedu.base.net.rxretrofit.convert;

import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.Charset;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okio.Buffer;
import retrofit2.Converter;

/**
 * @Description: T to RequestBody
 * @author: qinicy
 * @date: 17/5/7 18:05
 */
final class GsonRequestBodyConverter<T> implements Converter<T, RequestBody> {
    private static final MediaType MEDIA_TYPE = MediaType.parse("init/json; charset=UTF-8");
    //    private static final MediaType MEDIA_TYPE = MediaType.parse("application/x-www-form-urlencoded;charset=utf-8");
    private static final Charset UTF_8 = Charset.forName("UTF-8");

    private final Gson gson;
    private final TypeAdapter<T> adapter;

    GsonRequestBodyConverter(Gson gson, TypeAdapter<T> adapter) {
        this.gson = gson;
        this.adapter = adapter;
    }

    @Override
    public RequestBody convert(T value) throws IOException {
        // 不使用IFormUrlEncoded，当前API均为application/json形式. 2018-4-17
//        if (value instanceof IFormUrlEncoded){
//            return RequestBody.create(okhttp3.MediaType.parse("application/x-www-form-urlencoded; charset=utf-8"),((IFormUrlEncoded) value).toFormUrlEncodedParams());
//        }
        Buffer buffer = new Buffer();
        Writer writer = new OutputStreamWriter(buffer.outputStream(), UTF_8);
        JsonWriter jsonWriter = gson.newJsonWriter(writer);
        adapter.write(jsonWriter, value);
        jsonWriter.close();
        return RequestBody.create(MEDIA_TYPE, buffer.readByteString());
    }


}