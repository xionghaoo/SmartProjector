package com.ubtedu.base.net.rxretrofit.mode;

/**
 * 遵循FormUrlEncoded的key-value的实体类，需要重写toFormUrlEncoded方法，把
 * 属性和值转成FormUrlEncoded的key-value对。
 * 如：
 *
 * Created by qinicy on 2017/5/9.
 */

public interface IFormUrlEncoded {
    String toFormUrlEncodedParams();
}
