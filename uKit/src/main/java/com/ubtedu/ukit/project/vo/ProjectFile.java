/**
 * ©2018, UBTECH Robotics, Inc. All rights reserved (C)
 **/
package com.ubtedu.ukit.project.vo;

import com.google.gson.annotations.SerializedName;
import com.ubtedu.ukit.common.utils.UuidUtil;
import com.ubtedu.ukit.common.vo.SerializablePOJO;

/**
 * @Author qinicy
 * @Date 2018/11/5
 **/
public class ProjectFile extends SerializablePOJO {
    @SerializedName("id")
    public String id = UuidUtil.createUUID();
    @SerializedName("name")
    public String name;
    @SerializedName("createTime")
    public long createTime = System.currentTimeMillis();
    @SerializedName("modifyTime")
    public long modifyTime;
}
