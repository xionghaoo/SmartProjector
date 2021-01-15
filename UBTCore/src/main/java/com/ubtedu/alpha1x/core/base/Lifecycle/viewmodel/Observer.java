/**
 * @2018 UBTECH Robotics Corp. All rights reserved (C)
 **/
package com.ubtedu.alpha1x.core.base.Lifecycle.viewmodel;

/**
 * @Author qinicy
 * @Date 2019/4/3
 **/
public interface Observer<T> {
    /**
     * Called when the data is changed.
     * @param t  The new data
     */
    void onChanged(T t);
}