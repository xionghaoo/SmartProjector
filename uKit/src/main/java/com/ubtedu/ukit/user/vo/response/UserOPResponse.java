package com.ubtedu.ukit.user.vo.response;

import com.google.gson.annotations.SerializedName;
import com.ubtedu.ukit.user.vo.UserInfo;

/**
 * User operation response for login/password/register
 *
 * @author Bright. Create on 2018/4/18.
 */
public class UserOPResponse extends EmptyResponse {

    @SerializedName("token")
    public Token tokenObject;

    @SerializedName("user")
    public UserInfo userInfo;

}
