package com.ubtedu.deviceconnect.libs.base.interfaces;

import androidx.annotation.NonNull;

/**
 * @Author naOKi
 * @Date 2019/09/25
 **/
public class URoConverter<S, T> {

    private @NonNull S source;
    private @NonNull URoConverterDelegate<S, T> delegate;

    private URoConverter(@NonNull S source, @NonNull URoConverterDelegate<S, T> delegate) {
        this.source = source;
        this.delegate = delegate;
    }

    public T getResult() {
        try {
            return delegate.convert(source);
        } catch (Throwable e) {
            return null;
        }
    }

    public static <S, T> URoConverter<S, T> newInstance(@NonNull S source, @NonNull URoConverterDelegate<S, T> delegate) {
        return new URoConverter<>(source, delegate);
    }

}
