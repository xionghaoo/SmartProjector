/**
 * @2018 UBTECH Robotics Corp. All rights reserved (C)
 **/
package com.ubtedu.ukit.common.ota;


import io.reactivex.ObservableEmitter;

/**
 * @Author qinicy
 * @Date 2018/12/18
 **/
public class OtaEmitter<T> {
    private int mSize;
    private ObservableEmitter<T> mSourceEmitter;

    public OtaEmitter(int size, ObservableEmitter<T> source) {
        this.mSize = size;
        this.mSourceEmitter = source;
    }

    public void onNext(T t) {
        if (!mSourceEmitter.isDisposed()){
            mSize--;
            mSourceEmitter.onNext(t);
            if (mSize <= 0) {
                mSourceEmitter.onComplete();
            }
        }
    }
    public void onComplete(){
        if (!mSourceEmitter.isDisposed()){
            mSourceEmitter.onComplete();
        }
    }
}
