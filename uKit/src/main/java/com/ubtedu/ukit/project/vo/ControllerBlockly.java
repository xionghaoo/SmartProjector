/**
 * ©2018, UBTECH Robotics, Inc. All rights reserved (C)
 **/
package com.ubtedu.ukit.project.vo;

import android.text.TextUtils;

import com.google.gson.annotations.SerializedName;
import com.ubtedu.base.gson.Exclude;

import java.util.ArrayList;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @Author naOKi
 * @Date 2018/12/07
 **/
public class ControllerBlockly extends Blockly {

    @SerializedName("blocklyLuaFiles")
    @Deprecated
    @Exclude(serialize = true)
    public ArrayList<BlocklyFile> blocklyLuaFiles = new ArrayList<>();

    @SerializedName("blocklyFiles")
    public CopyOnWriteArrayList<BlocklyFile> blocklyFiles = new CopyOnWriteArrayList<>();

    public BlocklyFile getBlocklyFileByName(String name) {
        ArrayList<BlocklyFile> blocklyFiles = new ArrayList<>(this.blocklyFiles);
        for(BlocklyFile blocklyFile : blocklyFiles) {
            if(TextUtils.equals(name, blocklyFile.name)) {
                return blocklyFile;
            }
        }
        return null;
    }

}
