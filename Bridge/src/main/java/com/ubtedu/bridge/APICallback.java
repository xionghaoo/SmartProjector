/**
 * @2018 UBTECH Robotics Corp. All rights reserved (C)
 **/
package com.ubtedu.bridge;

/**
 * 如果是定义异步API，形参列表的最后一个参数需要是{@link APICallback}
 * @Author qinicy
 * @Date 2019/3/27
 **/
public abstract class APICallback<T> implements OnCallback{
    /**
     * 异步API执行完，通过该方法回调结果
     * @param data bridge result结构体{@link BridgeResult}中的data字段
     * @param isComplete 异步API是否已经完成，如果是一些进度回调还没有完成，isComplete需要取值false，
     *                   否则bridge将会移除callback，调用者再也接收不到回调。
     */
    public abstract void onCallback(T data,boolean isComplete);

    /**
     * 不再建议使用该方法，请用{@link #onCallback(T, boolean)}
     * @param result
     */
    @Deprecated
    @Override
    public void onCallback(BridgeResult result) {

    }
}
