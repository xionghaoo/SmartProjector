package com.ubtedu.ukit.user.vo;

import com.google.gson.annotations.SerializedName;
import com.ubtedu.ukit.BuildConfig;
import com.ubtedu.ukit.common.vo.SerializablePOJO;

/**
 * Created by ubt on 2017/11/2.
 */

public class IdeaFeedBackBody extends SerializablePOJO {
    @SerializedName("content")
    public String content;

    @SerializedName("feedbackType")
    public String feedbackType;

//    @SerializedName("softVersion")
//    public String softVersion = BuildConfig.VERSION_NAME;

    @SerializedName("userEmail")
    public String userEmail;

    @SerializedName("userId")
    public String userId;

}
