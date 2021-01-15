/**
 * ©2018, UBTECH Robotics, Inc. All rights reserved (C)
 **/
package com.ubtedu.bridge;

/**
 * @Author qinicy
 * @Date 2018/10/9
 **/
public interface ICall {
    /**
     * android ==> other,bridge android的call方法，android通过该方法调用外部模块API
     * @param funName 方法名，如果目标API有命名空间，需要带上命名空间
     * @param args 参数数组，类型和顺序需要和目标API一致
     * @param callback 目标API执行完成之后的回调
     */
    void call(String funName,Object[] args,OnCallback callback);
}
