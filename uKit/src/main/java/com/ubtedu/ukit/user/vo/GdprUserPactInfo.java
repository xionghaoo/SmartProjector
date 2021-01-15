package com.ubtedu.ukit.user.vo;

import androidx.annotation.IntDef;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public class GdprUserPactInfo implements Serializable {
    private static final long serialVersionUID = -1407836889711046922L;
    /**
     * 使用条款和隐私声明
     */
    public final static int GDPR_TYPE_ALL = 0;
    /**
     * 使用条款
     */
    public final static int GDPR_TYPE_TERMS_OF_USE = 1;
    /**
     * 隐私声明
     */
    public final static int GDPR_TYPE_PRIVACY_POLICY = 2;

    @PactType
    @SerializedName("type")
    public int type;

    @SerializedName("productId")
    public String productId;

    @SerializedName("url")
    public String url;

    @SerializedName("lan")
    public String lang;

    @SerializedName("version")
    public String version;

    @SerializedName("summary")
    public String abstractText;
    @IntDef({GDPR_TYPE_ALL,GDPR_TYPE_TERMS_OF_USE, GDPR_TYPE_PRIVACY_POLICY})
    @Retention(RetentionPolicy.SOURCE)
    public @interface PactType {
    }

    @Override
    public String toString() {
        return "type:"+type+" productId:"+productId+" lang:"+lang+" url:"+url+" version:"+version;
    }
}
