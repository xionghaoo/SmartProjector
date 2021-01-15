/**
 * ©2018, UBTECH Robotics, Inc. All rights reserved (C)
 **/
package com.ubtedu.ukit.project.vo;

import android.text.TextUtils;

import com.google.gson.annotations.SerializedName;
import com.ubtedu.alpha1x.utils.GsonUtil;
import com.ubtedu.base.gson.Exclude;
import com.ubtedu.ukit.common.files.FileHelper;
import com.ubtedu.ukit.common.utils.UuidUtil;
import com.ubtedu.ukit.common.vo.SerializablePOJO;
import com.ubtedu.ukit.menu.settings.Settings;
import com.ubtedu.ukit.menu.settings.TargetDevice;
import com.ubtedu.ukit.project.blockly.BlocklyAudio;
import com.ubtedu.ukit.user.UserManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * @Author qinicy
 * @Date 2018/11/5
 **/
public class Project extends SerializablePOJO {
    public final static int PROJECT_VERSION_BASE = 1;
    public final static int PROJECT_VERSION_WITH_PROJECTC_CONTROL = 2;
    public final static int PROJECT_VERSION_WITH_MICRO_PYTHON = 3;
    public final static int PROJECT_VERSION_WITH_MULTI_DEVICE = 4;
    public final static int PROJECT_VERSION_WITH_LIGHTBOX_DEVICE = 5;
    public final static int PROJECT_VERSION_WITH_CUSTOMIZE_BLOCKLY = 6;
    public final static int PROJECT_VERSION_WITH_MULTI_DEVICE_COMMUNICATION = 7;
    public final static int PROJECT_VERSION_WITH_ASR = 8;
    public final static int PROJECT_VERSION_WITH_NLP = 9;

    public final static int UKIT1_PROJECT_VERSION = PROJECT_VERSION_WITH_MULTI_DEVICE_COMMUNICATION;
    public final static int UKIT2_PROJECT_VERSION = PROJECT_VERSION_WITH_NLP;
    public final static String PROJECTS_DIR_NAME = "projects";
    public final static String PROJECT_MANIFEST_NAME = "manifest.json";
    public final static String BLOCKLY_DIR_NAME = "blockly";
    public final static String MOTION_DIR_NAME = "motion";
    public final static String AUDIO_DIR_NAME = "audio";
    public final static String CONTROLLER_DIR_NAME = "controller";
    public final static String DOODLE_DIR_NAME = "doodle";
    public final static String MODEL_FILE_NAME = "model.json";
    @SerializedName("userId")
    public String userId;
    @SerializedName("projectId")
    public String projectId;
    @SerializedName("projectName")
    public String projectName;
    @SerializedName("ossObjectKey")
    public String objectKey;

    @Exclude(serialize = true)
    @SerializedName("imgPath")
    public String imgPath;
    @SerializedName("imgUrl")
    public String imgUrl;
    @SerializedName("md5")
    public String md5;

    @SerializedName("createTime")
    public long createTime;
    @SerializedName("modifyTime")
    public long serverModifyTime;
    @SerializedName("localModifyTime")
    public long localModifyTime;

    @Exclude(serialize = true)
    @SerializedName("zipFilePath")
    public String zipFilePath;

    @Exclude(serialize = true)
    @SerializedName("downloadPath")
    public String downloadPath;
    @Exclude(serialize = true)
    @SerializedName("modelInfo")
    public ModelInfo modelInfo;
    @Exclude(serialize = true)
    @SerializedName("blocklyList")
    public List<Blockly> blocklyList;

    @Exclude(serialize = true)
    @SerializedName("controllerList")
    public List<Controller> controllerList;

    @Exclude(serialize = true)
    @SerializedName("motionList")
    public List<Motion> motionList;

    @Exclude(serialize = true)
    @SerializedName("doodleList")
    public List<Doodle> doodleList;

    @Exclude(serialize = true)
    @SerializedName("audioList")
    public List<BlocklyAudio> audioList;

    @SerializedName("isModified")
    public boolean isModified;
    @Exclude(serialize = true)
    @SerializedName("isDeletedAtLocal")
    public boolean isDeletedAtLocal;

    @SerializedName("projectVersion")
    public int projectVersion;
    @SerializedName("targetDevice")
    private int targetDevice;

    public Project() {
        projectId = UuidUtil.createUUID();
        userId = UserManager.getInstance().getLoginUserId();
        createTime = System.currentTimeMillis();
        projectVersion = getCurrentProjectVersion();
        targetDevice = Settings.getTargetDevice();
    }

    public static Project createFromJson(String projectJson) {
        Project project = GsonUtil.get().toObject(projectJson, Project.class);
        try {
            JSONObject jsonObject = new JSONObject(projectJson);
            if (!jsonObject.has("projectVersion")) {
                project.projectVersion = PROJECT_VERSION_BASE;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return project;
    }

    public int getTargetDevice() {
        return projectVersion < PROJECT_VERSION_WITH_MULTI_DEVICE ? TargetDevice.UKIT1 : targetDevice;
    }

    public void setTargetDevice(int targetDevice) {
        this.targetDevice = targetDevice;
    }

    public boolean isCompat() {
        int compactProjectVersion = getTargetDevice() == TargetDevice.UKIT1 ? Project.UKIT1_PROJECT_VERSION : Project.UKIT2_PROJECT_VERSION;
        return projectVersion <= compactProjectVersion;
    }

    public static int getCurrentProjectVersion(){
        return Settings.getTargetDevice() == TargetDevice.UKIT1 ? Project.UKIT1_PROJECT_VERSION : Project.UKIT2_PROJECT_VERSION;
    }

    public String getProjectPath() {
        return FileHelper.join(FileHelper.getUserPath(userId), Project.PROJECTS_DIR_NAME, projectId);
    }

    public Blockly getBlockly(String id) {
        if (!TextUtils.isEmpty(id) && blocklyList != null) {
            Iterator<Blockly> iterator = blocklyList.iterator();
            while (iterator.hasNext()) {
                Blockly blockly = iterator.next();
                if (id.equals(blockly.id)) {
                    return blockly;
                }
            }
        }
        return null;
    }

    public Motion getMotion(String id) {
        if (!TextUtils.isEmpty(id) && motionList != null) {
            Iterator<Motion> iterator = motionList.iterator();
            while (iterator.hasNext()) {
                Motion motion = iterator.next();
                if (id.equals(motion.id)) {
                    return motion;
                }
            }
        }
        return null;
    }

    public Controller getController(String id) {
        if (!TextUtils.isEmpty(id) && controllerList != null) {
            Iterator<Controller> iterator = controllerList.iterator();
            while (iterator.hasNext()) {
                Controller controller = iterator.next();
                if (id.equals(controller.id)) {
                    return controller;
                }
            }
        }
        return null;
    }

    public Doodle getDoodle(String id) {
        if (!TextUtils.isEmpty(id) && doodleList != null) {
            Iterator<Doodle> iterator = doodleList.iterator();
            while (iterator.hasNext()) {
                Doodle doodle = iterator.next();
                if (id.equals(doodle.id)) {
                    return doodle;
                }
            }
        }
        return null;
    }

    public BlocklyAudio getAudio(String id) {
        if (!TextUtils.isEmpty(id) && audioList != null) {
            Iterator<BlocklyAudio> iterator = audioList.iterator();
            while (iterator.hasNext()) {
                BlocklyAudio audio = iterator.next();
                if (id.equals(audio.id)) {
                    return audio;
                }
            }
        }
        return null;
    }


    public boolean removeProjectFile(ProjectFile projectFile) {
        if (projectFile instanceof Blockly && blocklyList != null) {
            return blocklyList.remove(projectFile);
        }
        if (projectFile instanceof Motion && motionList != null) {
            return motionList.remove(projectFile);
        }
        if (projectFile instanceof Controller && controllerList != null) {
            return controllerList.remove(projectFile);
        }
        if (projectFile instanceof Doodle && doodleList != null) {
            return doodleList.remove(projectFile);
        }
        if (projectFile instanceof BlocklyAudio && audioList != null) {
            return audioList.remove(projectFile);
        }
        return false;
    }

    public boolean addProjectFile(ProjectFile projectFile) {
        if (projectFile instanceof Blockly) {
            if (blocklyList == null) {
                blocklyList = new ArrayList<>();
            }
            Blockly blockly = getBlockly(projectFile.id);
            if (blockly == null) {
                return blocklyList.add((Blockly) projectFile);
            }
        }
        if (projectFile instanceof Motion) {
            if (motionList == null) {
                motionList = new ArrayList<>();
            }
            Motion motion = getMotion(projectFile.id);
            if (motion == null) {

                return motionList.add((Motion) projectFile);
            }
        }
        if (projectFile instanceof Controller) {
            if (controllerList == null) {
                controllerList = new ArrayList<>();
            }
            Controller controller = getController(projectFile.id);
            if (controller == null) {
                return controllerList.add((Controller) projectFile);
            }
        }
        if (projectFile instanceof Doodle) {
            if (doodleList == null) {
                doodleList = new ArrayList<>();
            }
            Doodle doodle = getDoodle(projectFile.id);
            if (doodle == null) {
                return doodleList.add((Doodle) projectFile);
            }
        }
        if (projectFile instanceof BlocklyAudio) {
            if (audioList == null) {
                audioList = new ArrayList<>();
            }
            BlocklyAudio audio = getAudio(projectFile.id);
            if (audio == null) {
                return audioList.add((BlocklyAudio) projectFile);
            }
        }
        return false;
    }

    public void replaceProperties(Project newProject) {
        if (newProject != null) {
            int sourceProjectVersion = projectVersion;
            Field[] fields = Project.class.getFields();
            try {
                for (Field field : fields) {
                    if (!Modifier.isFinal(field.getModifiers())) {
                        field.set(this, field.get(newProject));
                    }
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
            this.projectVersion = Math.max(sourceProjectVersion, newProject.projectVersion);
        }
    }
}
