package com.ubtedu.ukit.user.vo;

import com.google.gson.annotations.SerializedName;
import com.ubtedu.ukit.common.annotation.CommonType;

import java.io.Serializable;

/**
 * @author Bright. Create on 2018/4/27.
 */
public class UserApiParam implements Serializable {

    public static final int LOGIN_TYPE_PHONE = 0;
    public static final int LOGIN_TYPE_EMAIL = 1;
    @SerializedName("account")
    public String account;

    @SerializedName("accountType")
    public int accountType;

    @SerializedName("appId")
    public int appId;

    @SerializedName("password")
    public String password;

    @SerializedName("oldPassword")
    public String oldPassword;

    @SerializedName("newPassword")
    public String newPassword;

    @SerializedName("captcha")
    public String captcha;

    @SerializedName("token")
    public String token;

    @SerializedName("userId")
    public int userId;

    @SerializedName("purpose")
    @CommonType.CaptchaPurpose
    public int purpose;

    @SerializedName("zhFlag")
    public int zhFlag = 0;

    @CommonType.LangCode
    @SerializedName("lanCode")
    public String lanCode;

    @SerializedName("authorization")
    public String authorization;
}
