package com.ubtedu.ukit.user.vo;

import com.google.gson.annotations.SerializedName;
import com.ubtedu.alpha1x.utils.GsonUtil;

import java.io.Serializable;

/**
 * @author Bright. Create on 2018/5/18.
 */
public class OldUserInfo implements Serializable {
    private static final long serialVersionUID = 2836303993293728541L;

    @SerializedName("userId")
    public String userId;

    @SerializedName("userAccount")
    public String userAccount;

    @SerializedName("userName")
    public String userName;


    @Override
    public String toString() {
        return GsonUtil.get().toJson(this);
    }
}
