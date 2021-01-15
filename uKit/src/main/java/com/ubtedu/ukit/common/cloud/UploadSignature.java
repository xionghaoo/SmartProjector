/**
 * @2018 UBTECH Robotics Corp. All rights reserved (C)
 **/
package com.ubtedu.ukit.common.cloud;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 *
 *
 * @Author qinicy
 * @Date 2018/11/19
 **/
public class UploadSignature implements Serializable {
    @SerializedName("accessKeyId")
    public String accessKeyId;
    @SerializedName("securityToken")
    public String securityToken;
    @SerializedName("accessKeySecret")
    public String accessKeySecret;
    @SerializedName("expiration")
    public String expiration;

    @SerializedName("bucketName")
    public String bucket;
    /**
     * Ali云独有
     */
    @SerializedName("endPoint")
    public String endPoint;
    @SerializedName("domain")
    public String domain;
    /**
     * AWS独有
     */
    @SerializedName("region")
    public String region;
}
