/**
 * @2018 UBTECH Robotics Corp. All rights reserved (C)
 **/
package com.ubtedu.bridge;

/**
 * Bridge定义的常见异常
 * @Author qinicy
 * @Date 2019/3/27
 **/
public class BridgeException extends Exception {
    private int mExceptionCode;

    public BridgeException(int code, String message) {
        super(message);
        this.mExceptionCode = code;
    }

    public int getExceptionCode() {
        return mExceptionCode;
    }

    public BridgeResult getExceptionResult() {
        switch (mExceptionCode) {
            case BridgeResultCode.ILLEGAL_ARGUMENTS:
                return BridgeResult.ILLEGAL_ARGUMENTS();

            case BridgeResultCode.FAIL:
                return BridgeResult.FAIL().msg(getMessage());
        }
        return BridgeResult.UNKNOWN();
    }

    public static BridgeException ILLEGAL_ARGUMENTS() {
        return new BridgeException(BridgeResultCode.ILLEGAL_ARGUMENTS, "ILLEGAL_ARGUMENTS");
    }


    public static BridgeException UNKNOWN() {
        return new BridgeException(BridgeResultCode.UNKNOWN, "UNKNOWN");
    }

    public static BridgeException FAIL(String errorMsg) {
        return new BridgeException(BridgeResultCode.FAIL, errorMsg);
    }
}
