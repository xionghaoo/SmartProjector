/**
 * @2018 UBTECH Robotics Corp. All rights reserved (C)
 **/
package com.ubtedu.ukit.project;

import com.ubtedu.ukit.project.vo.Project;

import java.util.List;

/**
 *
 * @Author qinicy
 * @Date 2018/11/26
 **/
public class DataSyncEvent {
    /**
     * 同步开始
     */
    public static final int SYNC_EVENT_SYNC_START = 1001;
    /**
     * 同步中
     */
    public static final int SYNC_EVENT_SYNCING = 1002;

    /**
     * 同步结束
     */
    public static final int SYNC_EVENT_SYNC_END = 1003;
    /**
     * 同步取消
     */
    public static final int SYNC_EVENT_SYNC_CANCEL = 1004;


    public boolean isOnlineSync;
    public boolean isSyncSuccess;
    public int event;
    public List<Project> projects;
    public Project project;
    public String syncErrorMessage;
    public String getStringEvent(){
        switch (event){
            case SYNC_EVENT_SYNC_START:
                return "start";
            case SYNC_EVENT_SYNCING:
                return "syncing";
            case SYNC_EVENT_SYNC_END:
                return "end";
            case SYNC_EVENT_SYNC_CANCEL:
                return "cancel";
        }
        return "";
    }
}
