package com.ubtedu.ukit.user.vo;

import androidx.annotation.IntDef;

import com.google.gson.annotations.SerializedName;
import com.ubtedu.ukit.common.locale.UBTLocale;

import java.io.Serializable;

/**
 * @author qinicy
 * @data 2018/7/7
 */
public class LoginAccountInfo implements Serializable {
    public final static int LOGIN_TYPE_PHONE = CommonLoginParam.LOGIN_TYPE_PHONE;
    public final static int LOGIN_TYPE_EMAIL = CommonLoginParam.LOGIN_TYPE_EMAIL;
    public final static int LOGIN_TYPE_EDU = CommonLoginParam.LOGIN_TYPE_EDU;

    @LoginAccountType
    @SerializedName("type")
    private int type = LOGIN_TYPE_PHONE;

    @SerializedName("account")
    public String account;
    @SerializedName("locale")
    public UBTLocale locale;

    /**
     * @return 带有国家地区码的电话号码
     */
    public String getLocalePhoneNumber() {
        return locale != null ? locale.dial_code + account : account;
    }

    public String getAPIAccount() {
        if (isPhoneAccount()) {
            return getLocalePhoneNumber();
        } else {
            return account;
        }

    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }


    public boolean isEduAccount(){
        return getType() == LOGIN_TYPE_EDU;
    }
    public boolean isPhoneAccount(){
        return getType() == LOGIN_TYPE_PHONE;
    }
    public boolean isEmailAccount(){
        return getType() == LOGIN_TYPE_EMAIL;
    }
    @IntDef({LOGIN_TYPE_PHONE, LOGIN_TYPE_EMAIL, LOGIN_TYPE_EDU})
    public @interface LoginAccountType {
    }
}
