package com.ubtedu.ukit.user.vo;

import com.google.gson.annotations.SerializedName;

/**
 * @Author qinicy
 * @Date 2019/11/14
 **/
public class CommonLoginParam {
    public final static int LOGIN_TYPE_EDU = 0;
    public final static int LOGIN_TYPE_PHONE = 1;
    public final static int LOGIN_TYPE_EMAIL = 2;
    /**
     * token过期时间，默认为7天
     */
    @SerializedName("expireTime")
    public long expireTime = 7L * 24 * 60 * 60 * 1000;
    @SerializedName("userName")
    public String userName;
    @SerializedName("userPwd")
    public String userPwd;
    /**
     * 0: 校园账号 1：手机号 2：邮箱
     */
    @SerializedName("userType")
    public int userType;
}
