package com.ubtedu.ukit.user.vo.response;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * 用户信息
 *
 * @author Bright. Create on 2017/5/8.
 */
public class Token implements Serializable {

    /**
     * 到期时间时间戳
     */
    @SerializedName("expireAt")
    public long expireAt;

    @SerializedName("token")
    public String token;
}
