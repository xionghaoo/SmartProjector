/**
 * ©2018, UBTECH Robotics, Inc. All rights reserved (C)
 **/
package com.ubtedu.ukit.project.vo;

import android.text.TextUtils;

import com.google.gson.annotations.SerializedName;
import com.ubtedu.ukit.project.controller.model.config.WidgetConfig;

import java.util.ArrayList;
import java.util.concurrent.CopyOnWriteArrayList;


/**
 * @Author qinicy
 * @Date 2018/11/5
 **/
public class Controller extends ProjectFile {

    public final static String CONFIG_FILE_NAME = "WidgetConfigs.json";
    @SerializedName("WidgetConfig")
    public ArrayList<WidgetConfig> configs = new ArrayList<>();

    @SerializedName("ControllerBlockly")
    public ArrayList<ControllerBlockly> blocklies = new ArrayList<>();

    public ControllerBlockly getControllerBlocklyById(String id) {
        ArrayList<ControllerBlockly> blocklies = new ArrayList<>(this.blocklies);
        for(ControllerBlockly blockly : blocklies) {
            if(TextUtils.equals(id, blockly.id)) {
                return blockly;
            }
        }
        return null;
    }

    public void clearPyFileContent() {
        ArrayList<ControllerBlockly> blocklies = new ArrayList<>(this.blocklies);
        if (blocklies.isEmpty()) {
            return;
        }
        for (ControllerBlockly controllerBlockly : blocklies) {
            CopyOnWriteArrayList<BlocklyFile> blocklyFiles = controllerBlockly.blocklyFiles;
            ArrayList<BlocklyFile> pyBlocklyFiles = new ArrayList<>();
            if (blocklyFiles != null && !blocklyFiles.isEmpty()) {
                for (BlocklyFile blocklyFile : blocklyFiles) {
                    if (blocklyFile != null && TextUtils.equals(BlocklyFile.NAME_MICRO_PYTHON, blocklyFile.getFileName())) {
                        pyBlocklyFiles.add(blocklyFile);
                    }
                }
                if (!pyBlocklyFiles.isEmpty()){
                    blocklyFiles.removeAll(pyBlocklyFiles);
                }
            }
        }
    }
}
