package com.ubtedu.alpha1x.utils;

import java.util.Iterator;
import java.util.List;

/**
 * Created by qinicy on 2017/7/19.
 */

public class ListUtil {
    public static <T> void iterate(List<T> listeners, IteratorCallback<T> callback) {
        if (listeners != null && callback != null) {
            Iterator<T> iterator = listeners.iterator();
            while (iterator.hasNext()) {
                callback.onNext(iterator.next());
            }
        }
    }

    public static  <T> void add(List<T> listeners, T listener) {
        if (listeners != null && listener != null && !listeners.contains(listener)) {
            listeners.add(listener);
        }
    }

    public static  <T> void remove(List<T> listeners, T listener) {
        if (listeners != null && listener != null & listeners.contains(listener)) {
            listeners.remove(listener);
            // fixme: 下面的方法删除listener失效，待查
//            Iterator<T> iterator = listeners.iterator();
//            while (iterator.hasNext()) {
//                if (iterator.next() == listener) {
//                    iterator.remove();
//                }
//            }
        }
    }
}
