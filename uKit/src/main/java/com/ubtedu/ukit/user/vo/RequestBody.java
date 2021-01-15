package com.ubtedu.ukit.user.vo;

import com.google.gson.annotations.SerializedName;
import com.ubtedu.alpha1x.utils.GsonUtil;

import java.io.Serializable;

/**
 * @author qinicy
 * @data 2017/10/17
 */

public class RequestBody implements Serializable {
    private static final long serialVersionUID = -1406836889711030922L;
    @SerializedName("userId")
    public String userId;
    @SerializedName("version")
    public String version;
    @SerializedName("mcode")
    public String mcode;
    @SerializedName("mDeviceToken")
    public String deviceToken;

    public okhttp3.RequestBody body(){
        return okhttp3.RequestBody.create(okhttp3.MediaType.parse("Content-Type=text/json; charset=utf-8"),toJson());
    }
    public String toJson() {
        return GsonUtil.get().toJson(this);
    }
}
