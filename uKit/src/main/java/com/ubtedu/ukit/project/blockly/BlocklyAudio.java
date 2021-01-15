/**
 * @2018 UBTECH Robotics Corp. All rights reserved (C)
 **/
package com.ubtedu.ukit.project.blockly;

import com.google.gson.annotations.SerializedName;
import com.ubtedu.ukit.common.files.FileHelper;
import com.ubtedu.ukit.project.Workspace;
import com.ubtedu.ukit.project.vo.ProjectFile;

/**
 * @Author qinicy
 * @Date 2019/4/17
 **/
public class BlocklyAudio extends ProjectFile {
    public final static String AAC_SUFFIX = ".aac";
    @SerializedName("duration")
    public long duration;

    public String getFilePathInWorkspace() {
        return FileHelper.join(Workspace.AUDIO_DIR, id + AAC_SUFFIX);
    }
}
