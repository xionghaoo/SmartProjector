/**
 * @2018 UBTECH Robotics Corp. All rights reserved (C)
 **/
package com.ubtedu.ukit.project.vo;

/**
 * @Author qinicy
 * @Date 2019/4/23
 **/
public class OfficialProject extends Project {
    public String officialPath;


    @Override
    public String getProjectPath() {
        return officialPath;
    }
}
