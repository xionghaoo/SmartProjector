/**
 * ©2018, UBTECH Robotics, Inc. All rights reserved (C)
 **/
package com.ubtedu.alpha1x.core.base.Lifecycle.livedata;

import androidx.lifecycle.LiveData;

/**
 * A LiveData class that has `null` value.
 * @Author qinicy
 * @Date 2018/9/4
 **/
public class AbsentLiveData<T> extends LiveData<T> {
    {
        postValue(null);
    }

    public static <T> LiveData<T> create() {
        return new AbsentLiveData<>();
    }
}
