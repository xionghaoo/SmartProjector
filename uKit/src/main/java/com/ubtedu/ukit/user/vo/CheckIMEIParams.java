package com.ubtedu.ukit.user.vo;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class CheckIMEIParams implements Serializable {
    @SerializedName("account")
    public String account;
    @SerializedName("captcha")
    public String captcha;
}
