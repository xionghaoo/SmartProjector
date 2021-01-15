/**
 * ©2018, UBTECH Robotics, Inc. All rights reserved (C)
 **/
package com.ubtedu.bridge;

/**
 * Bridge框架定义的错误码
 * @Author qinicy
 * @Date 2018/10/8
 **/
public class BridgeResultCode {
    /**
     * 成功
     */
    public static final int SUCCESS = 0;
    /**
     * 方法未定义
     */
    public static final int FUN_UNDEFINED = 901;
    /**
     * 参数不符合
     */
    public static final int ILLEGAL_ARGUMENTS = 902;
    /**
     * 成功或失败，未知
     */
    public static final int UNKNOWN = 1000;
    /**
     * 失败
     */
    public static final int FAIL = -1;
}
