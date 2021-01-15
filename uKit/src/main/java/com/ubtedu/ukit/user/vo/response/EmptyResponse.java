package com.ubtedu.ukit.user.vo.response;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * @author Bright. Create on 2018/4/27.
 */
public class EmptyResponse implements Serializable {

    @SerializedName("code")
    public int code = 0;

    @SerializedName("message")
    public String message;

    @SerializedName("status")
    public String status;

    @Override
    public String toString() {
        return "{code: " + code + ", message: " + message + ", status: " + status + "}";
    }
}
