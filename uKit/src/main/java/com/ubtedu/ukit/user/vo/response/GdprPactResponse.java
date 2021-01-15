package com.ubtedu.ukit.user.vo.response;

import com.google.gson.annotations.SerializedName;
import com.ubtedu.ukit.user.vo.GdprUserPactInfo;

/**
 *
 * @author Bright. Create on 2018/4/18.
 */
public class GdprPactResponse extends EmptyResponse {

    @SerializedName("token")
    public Token tokenObject;

    @SerializedName("user")
    public GdprUserPactInfo userInfo;

}
