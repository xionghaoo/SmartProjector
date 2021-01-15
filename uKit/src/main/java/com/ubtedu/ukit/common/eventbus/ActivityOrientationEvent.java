/**
 * @2018 UBTECH Robotics Corp. All rights reserved (C)
 **/
package com.ubtedu.ukit.common.eventbus;

/**
 * @Author qinicy
 * @Date 2019/1/10
 **/
public class ActivityOrientationEvent {
    public boolean lock;

    public ActivityOrientationEvent(boolean lock) {
        this.lock = lock;
    }
}
