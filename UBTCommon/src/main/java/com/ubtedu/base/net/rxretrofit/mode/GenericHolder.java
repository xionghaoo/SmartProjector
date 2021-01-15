package com.ubtedu.base.net.rxretrofit.mode;

import java.lang.reflect.Type;

/**
 * 泛型Holder，保存T的信息，以供gson转换使用
 * Created by qinicy on 2017/5/7.
 */

public class GenericHolder<T> {
    public Type type;
    public Class<T> clazz;
}
