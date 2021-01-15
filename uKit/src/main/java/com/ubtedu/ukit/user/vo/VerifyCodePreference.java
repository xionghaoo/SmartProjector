/**
 * @2018 UBTECH Robotics Corp. All rights reserved (C)
 **/
package com.ubtedu.ukit.user.vo;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * @Author qinicy
 * @Date 2019/2/23
 **/
public class VerifyCodePreference implements Serializable {
    @SerializedName("account")
    public String account;
    @SerializedName("time")
    public long time;
}
