package com.ubtedu.deviceconnect.libs.utils;

import java.util.HashSet;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;

/**
 * @Author naOKi
 * @Date 2019/09/18
 **/
public abstract class URoListenerGroup<T, D> {

    private final HashSet<T> listeners;

    public URoListenerGroup() {
        this.listeners = new HashSet<>();
    }

    public void addListener(T listener) {
        synchronized (listeners) {
            if(listener == null) {
                return;
            }
            listeners.add(listener);
        }
    }

    public void removeListener(T listener) {
        synchronized (listeners) {
            if(listener == null) {
                return;
            }
            listeners.remove(listener);
        }
    }

    public void sendNotifyToListener(D data) {
        T[] array;
        synchronized (listeners) {
            array = (T[])listeners.toArray();
        }
        Observable.fromArray(array)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(new Consumer<T>() {
                @Override
                public void accept(T listener) throws Exception {
                    notifyListener(listener, data);
                }
            });
    }

    public abstract void notifyListener(T listener, D data);

}
