package com.ubtedu.ukit.user.vo;

import com.google.gson.annotations.SerializedName;

/**
 * @Author qinicy
 * @Date 2019/11/14
 **/
public class UpdatePwdParam {
    @SerializedName("newPwd")
    public String newPwd;
    @SerializedName("oldPwd")
    public String oldPwd;
}
