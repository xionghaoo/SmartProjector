/**
 * @2018 UBTECH Robotics Corp. All rights reserved (C)
 **/
package com.ubtedu.ukit.user.register;

import com.ubtedu.ukit.user.vo.LoginAccountInfo;

/**
 * @Author qinicy
 * @Date 2019/2/22
 **/
public class RegisterAccountInfo extends LoginAccountInfo {
    public static final int INTENT_TYPE_REGISTER = 0;
    public static final int INTENT_TYPE_RESET_PASSWORD = 1;
    public int intentType = INTENT_TYPE_REGISTER;
    public String nickName;
    public String verifyCode;
    public int userId;
    public String verifyToken;

    public RegisterAccountInfo() {
    }

    public RegisterAccountInfo(LoginAccountInfo accountInfo) {
        if (accountInfo != null) {
            this.account = accountInfo.account;
            this.locale = accountInfo.locale;
            setType(accountInfo.getType());
        }
    }

    public boolean isRegister() {
        return intentType == INTENT_TYPE_REGISTER;
    }
}
