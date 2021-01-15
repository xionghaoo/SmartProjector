package com.ubtedu.ukit.user.vo;

import com.google.gson.annotations.SerializedName;
import com.ubtedu.ukit.common.vo.SerializablePOJO;

/**
 * Created by qinicy on 2017/7/13.
 */

public class UpdateUserInfo extends SerializablePOJO {

    @SerializedName("countryCode")
    private String countryCode;

    @SerializedName("countryName")
    private String countryName;

    @SerializedName("nickName")
    private String nickName;

    @SerializedName("userImage")
    private String userImage;

    // 与account相同
    @SerializedName("userName")
    private String userName;


    public UpdateUserInfo(UserInfo user) {
        countryCode = user.getCountryCode();
        countryName = user.getCountryName();
        nickName = user.getNickName();
        userImage = user.getUserImage();
        userName = user.getUserName();
    }
}
