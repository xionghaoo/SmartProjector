package com.ubtedu.ukit.user.vo.response;

import com.google.gson.annotations.SerializedName;

/**
 * @author Bright. Create on 2018/4/27.
 */
public class TokenResponse extends EmptyResponse {

    @SerializedName("token")
    public String token;

    @SerializedName("expireAt")
    public int expireAt;

    @SerializedName("userId")
    public int userId;

}
