package com.ubtedu.ukit.user.vo;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * @author Bright. Create on 2017/11/27.
 */
public class QQLogin implements Serializable {

    private static final long serialVersionUID = 2464281269937282659L;

    @SerializedName("ret")
    public int ret;

    @SerializedName("openid")
    public String openid;

    @SerializedName("access_token")
    public String accessToken;

    @SerializedName("pay_token")
    public String payToken;

    @SerializedName("expires_in")
    public int expiresIn;

    @SerializedName("pf")
    public String pf;

    @SerializedName("pfkey")
    public String pfkey;

    @SerializedName("msg")
    public String msg;

    @SerializedName("login_cost")
    public int login_cost;

    @SerializedName("query_authority_cost")
    public int queryAuthorityCost;

    @SerializedName("authority_cost")
    public int authorityCost;
}
