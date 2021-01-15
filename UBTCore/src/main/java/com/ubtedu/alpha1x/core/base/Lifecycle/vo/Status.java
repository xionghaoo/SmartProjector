/**
 * ©2018, UBTECH Robotics，Inc. All rights reserved (C)
 **/
package com.ubtedu.alpha1x.core.base.Lifecycle.vo;
/**
 * Status of a resource that is provided to the UI.
 * These are usually created by the Repository classes where they return
 * `LiveData<Resource<T>>` to pass back the latest data to the UI with its fetch status.
 *@Author qinicy
 *@Date 2018/9/4
 **/
public enum  Status {
    SUCCESS,
    ERROR,
    LOADING
}
