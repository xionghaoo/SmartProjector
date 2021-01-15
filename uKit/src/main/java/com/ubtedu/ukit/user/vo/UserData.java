/**
 * @2018 UBTECH Robotics Corp. All rights reserved (C)
 **/
package com.ubtedu.ukit.user.vo;

import com.google.gson.annotations.SerializedName;
import com.ubtedu.ukit.common.vo.SerializablePOJO;
import com.ubtedu.ukit.project.vo.Project;

import java.util.List;

/**
 * @Author qinicy
 * @Date 2018/11/19
 **/
public class UserData extends SerializablePOJO {
    @SerializedName("projects")
    public List<Project> projects;
    public boolean isSyncSuccess;
    public boolean isRequestServerDataSuccess;
    public String syncErrorMessage;
}
