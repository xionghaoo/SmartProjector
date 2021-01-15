/**
 * @2018 UBTECH Robotics Corp. All rights reserved (C)
 **/
package com.ubtedu.ukit.common.utils;

import java.util.UUID;

/**
 * @Author qinicy
 * @Date 2018/11/14
 **/
public class UuidUtil {
    public static String createUUID() {
        return UUID.randomUUID().toString().replace("-", "");
    }
}
