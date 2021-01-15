/**
 * @2018 UBTECH Robotics Corp. All rights reserved (C)
 **/
package com.ubtedu.ukit.project;

import android.text.TextUtils;

import com.ubtedu.alpha1x.utils.GsonUtil;
import com.ubtedu.alpha1x.utils.LogUtil;
import com.ubtedu.ukit.common.files.FileHelper;
import com.ubtedu.ukit.menu.settings.Settings;
import com.ubtedu.ukit.project.blockly.BlocklyAudio;
import com.ubtedu.ukit.project.controller.model.config.WidgetConfigWrapper;
import com.ubtedu.ukit.project.vo.Blockly;
import com.ubtedu.ukit.project.vo.BlocklyFile;
import com.ubtedu.ukit.project.vo.Controller;
import com.ubtedu.ukit.project.vo.ControllerBlockly;
import com.ubtedu.ukit.project.vo.Doodle;
import com.ubtedu.ukit.project.vo.ModelInfo;
import com.ubtedu.ukit.project.vo.Motion;
import com.ubtedu.ukit.project.vo.OfficialProject;
import com.ubtedu.ukit.project.vo.Project;
import com.ubtedu.ukit.project.vo.ProjectFile;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.ObservableSource;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

/**
 * @Author qinicy
 * @Date 2018/11/13
 **/
public class Workspace {
    public static final String MANIFEST_JSON_PATH;
    public static final String MODEL_INFO_PATH;
    public static final String BLOCKLY_DIR;
    public static final String CONTROLLER_DIR;
    public static final String DOODLE_DIR;
    public static final String MOTION_DIR;
    public static final String AUDIO_DIR;


    static {
        String workspacePath = FileHelper.getWorkspacePath();
        MANIFEST_JSON_PATH = FileHelper.join(workspacePath, Project.PROJECT_MANIFEST_NAME);
        MODEL_INFO_PATH = FileHelper.join(workspacePath, Project.MODEL_FILE_NAME);
        BLOCKLY_DIR = FileHelper.join(workspacePath, Project.BLOCKLY_DIR_NAME);
        CONTROLLER_DIR = FileHelper.join(workspacePath, Project.CONTROLLER_DIR_NAME);
        DOODLE_DIR = FileHelper.join(workspacePath, Project.DOODLE_DIR_NAME);
        MOTION_DIR = FileHelper.join(workspacePath, Project.MOTION_DIR_NAME);
        AUDIO_DIR = FileHelper.join(workspacePath, Project.AUDIO_DIR_NAME);
    }

    private Project mProject;
    private boolean isModified;
    private String mLastProjectId;

    public Workspace() {

    }

    private static class SingletonHolder {
        private final static Workspace instance = new Workspace();
    }

    public static Workspace getInstance() {
        return Workspace.SingletonHolder.instance;
    }

    public Project getProject() {
        return mProject;
    }

    public boolean isModified() {
        return isModified;
    }

    /**
     * 加载工程进工作空间.如果是新建一个工程，直接传进new project()即可
     *
     * @param project     项目对象
     * @return
     */
    public Observable<Project> load(@NonNull final Project project) {
        if (project == null) {
            return Observable.error(new Exception("project can not be null"));
        }

        mProject = project.deepClone();
        //处理反序列失败异常
        if (mProject == null) {
            mProject = new Project();
        }
        mLastProjectId = mProject.projectId;
        //clear workspace
        final File workspace = new File(FileHelper.getWorkspacePath());
        if (workspace.exists() && workspace.isDirectory()) {
            FileHelper.removeDir(workspace);
        }
        workspace.mkdirs();

        if (mProject.getProjectPath() != null) {
            File file = new File(mProject.getProjectPath());
            if (!file.exists() || file.isFile()) {
                return Observable.just(mProject);
            }
        }

        return Observable
                .create(new ObservableOnSubscribe<Project>() {
                    @Override
                    public void subscribe(ObservableEmitter<Project> emitter) throws Exception {
                        //copy project files into workspace
                        boolean isSuccess = FileHelper.copyDir(project.getProjectPath(), workspace.getAbsolutePath());
                        if (!emitter.isDisposed()) {
                            if (isSuccess) {
                                emitter.onNext(mProject);
                                emitter.onComplete();
                            } else {
                                emitter.onError(new Exception("copy project files into workspace fail"));
                            }
                        }

                    }
                })
                .flatMap(new Function<Project, ObservableSource<Project>>() {
                    @Override
                    public ObservableSource<Project> apply(Project project) throws Exception {
                        return loadProjectFiles();
                    }
                })
                .doOnComplete(new Action() {
                    @Override
                    public void run() throws Exception {
                        isModified = (mProject instanceof OfficialProject);
                    }
                })
                .subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 是否和上次打开的project是同一个project
     */
    public boolean isOpenSameProject(String projectId) {
        return projectId != null && projectId.equals(mLastProjectId);
    }

    private Observable<Project> loadProjectFiles() {
        List<Observable<Boolean>> loadTasks = new ArrayList<>();

        //load blockly
        if (mProject.blocklyList != null && mProject.blocklyList.size() > 0) {
            for (final Blockly blockly : mProject.blocklyList) {
                String workspaceFile = FileHelper.join(
                        BLOCKLY_DIR,
                        blockly.id,
                        Blockly.WORKSPACE_FILE_NAME);
                if (new File(workspaceFile).exists()) {
                    Observable<Boolean> loadBlocklyWorkspace = FileHelper.asyncReadTxtFile(workspaceFile)
                            .doOnNext(new Consumer<String>() {
                                @Override
                                public void accept(String s) throws Exception {
                                    blockly.workspace = s;
                                }
                            })
                            .map(new Function<String, Boolean>() {
                                @Override
                                public Boolean apply(String s) throws Exception {
                                    return true;
                                }
                            });
                    loadTasks.add(loadBlocklyWorkspace);
                }
            }
        }
        //load controller
        if (mProject.controllerList != null && mProject.controllerList.size() > 0) {
            for (final Controller controller : mProject.controllerList) {
                String configFilePath = FileHelper.join(
                        CONTROLLER_DIR,
                        controller.id,
                        Controller.CONFIG_FILE_NAME);
                if (new File(configFilePath).exists()) {
                    Observable<Boolean> loadConfig = FileHelper.asyncReadTxtFile(configFilePath)
                            .doOnNext(new Consumer<String>() {
                                @Override
                                public void accept(String s) throws Exception {
                                    WidgetConfigWrapper wrapper = GsonUtil.get().toObject(s, WidgetConfigWrapper.class);
                                    if (wrapper != null) {
                                        controller.configs = wrapper.configs;
                                    }
                                }
                            })
                            .map(new Function<String, Boolean>() {
                                @Override
                                public Boolean apply(String s) throws Exception {
                                    return true;
                                }
                            });
                    loadTasks.add(loadConfig);
                }

                if (controller.blocklies != null && controller.blocklies.size() > 0) {
                    for (final ControllerBlockly blockly : controller.blocklies) {
                        String workspaceFilePath = FileHelper.join(
                                CONTROLLER_DIR,
                                controller.id,
                                blockly.id,
                                Blockly.WORKSPACE_FILE_NAME);
                        boolean exists = new File(workspaceFilePath).exists();
                        LogUtil.d("workspaceFilePath:" + workspaceFilePath);
                        LogUtil.d("blockly exists:" + exists + "  blockly.id:" + blockly.id + "  controller.id:" + controller.id);
                        if (new File(workspaceFilePath).exists()) {
                            Observable<Boolean> loadControllerBlockly = FileHelper.asyncReadTxtFile(workspaceFilePath)
                                    .doOnNext(new Consumer<String>() {
                                        @Override
                                        public void accept(String s) throws Exception {
                                            blockly.workspace = s;

                                        }
                                    })
                                    .map(new Function<String, Boolean>() {
                                        @Override
                                        public Boolean apply(String s) throws Exception {
                                            return true;
                                        }
                                    });
                            loadTasks.add(loadControllerBlockly);

                            if (blockly.blocklyLuaFiles != null && blockly.blocklyLuaFiles.size() > 0) {
                                if (blockly.blocklyFiles == null) {
                                    blockly.blocklyFiles = new CopyOnWriteArrayList<>(blockly.blocklyLuaFiles);
                                }
                                blockly.blocklyFiles.addAll(blockly.blocklyLuaFiles);
                                blockly.blocklyLuaFiles.clear();
                            }
                            if (blockly.blocklyFiles != null && blockly.blocklyFiles.size() > 0) {
                                for (final BlocklyFile blocklyFile : blockly.blocklyFiles) {

                                    String blocklyFilePath = FileHelper.join(
                                            CONTROLLER_DIR,
                                            controller.id,
                                            blockly.id,
                                            blocklyFile.getFileName());
                                    if (new File(blocklyFilePath).exists()) {
                                        Observable<Boolean> loadBlocklyFile = FileHelper.asyncReadTxtFile(blocklyFilePath)
                                                .doOnNext(new Consumer<String>() {
                                                    @Override
                                                    public void accept(String s) throws Exception {
                                                        blocklyFile.content = s;
                                                    }
                                                })
                                                .map(new Function<String, Boolean>() {
                                                    @Override
                                                    public Boolean apply(String s) throws Exception {
                                                        return true;
                                                    }
                                                });
                                        loadTasks.add(loadBlocklyFile);
                                    }

                                }
                            }
                        }
                    }
                }

            }

        }

        //load motion
        if (mProject.motionList != null && mProject.motionList.size() > 0) {
            for (final Motion motion : mProject.motionList) {
                String motionFile = FileHelper.join(
                        MOTION_DIR,
                        motion.getMotionFileName());
                if (new File(motionFile).exists()) {
                    Observable<Boolean> loadMotionFile = FileHelper.asyncReadTxtFile(motionFile)
                            .doOnNext(new Consumer<String>() {
                                @Override
                                public void accept(String s) throws Exception {
                                    Motion m = GsonUtil.get().toObject(s, Motion.class);
                                    if (m != null) {
                                        motion.servos = m.servos;
                                        motion.frames = m.frames;
                                        motion.totaltime = m.totaltime;
                                    }
                                }
                            })
                            .map(new Function<String, Boolean>() {
                                @Override
                                public Boolean apply(String s) throws Exception {
                                    return true;
                                }
                            });
                    loadTasks.add(loadMotionFile);
                }
            }
        }
        //load modelInfo
        if (mProject.modelInfo == null) {

            String motionFile = MODEL_INFO_PATH;
            if (new File(motionFile).exists()) {
                Observable<Boolean> loadModelInfo = FileHelper.asyncReadTxtFile(motionFile)
                        .doOnNext(new Consumer<String>() {
                            @Override
                            public void accept(String s) throws Exception {
                                ModelInfo m = GsonUtil.get().toObject(s, ModelInfo.class);
                                if (m != null) {
                                    mProject.modelInfo = m;
                                }
                            }
                        })
                        .map(new Function<String, Boolean>() {
                            @Override
                            public Boolean apply(String s) throws Exception {
                                return true;
                            }
                        });
                loadTasks.add(loadModelInfo);
            }
        }
        return Observable
                .zip(loadTasks, new Function<Object[], Boolean>() {
                    @Override
                    public Boolean apply(@NonNull Object[] results) {
                        for (Object result : results) {
                            Boolean success = (Boolean) result;
                            if (!success) {
                                return false;
                            }
                        }
                        return true;
                    }
                })
                .map(new Function<Boolean, Project>() {
                    @Override
                    public Project apply(Boolean aBoolean) throws Exception {
                        return mProject;
                    }
                });
    }


    public boolean isNeedSave() {
        return isModified;
    }


    public synchronized void saveProjectFile(ProjectFile projectFile) {
        if (projectFile != null) {
            isModified = true;
            mProject.isModified = true;
            projectFile.modifyTime = System.currentTimeMillis();
            if (projectFile instanceof Blockly) {
                saveBlockly((Blockly) projectFile);
            }
            if (projectFile instanceof Motion) {
                saveMotion((Motion) projectFile);
            }
            if (projectFile instanceof Controller) {
                saveController((Controller) projectFile);
            }
            if (projectFile instanceof Doodle) {
                saveDoodle((Doodle) projectFile);
            }
            if (projectFile instanceof BlocklyAudio) {
                saveAudio((BlocklyAudio) projectFile);
            }
            if (projectFile instanceof ModelInfo) {
                saveModeInfo((ModelInfo) projectFile);
            }
        }

    }

    public void removeProjectFile(ProjectFile projectFile) {
        if (projectFile != null && mProject != null) {
            mProject.removeProjectFile(projectFile);
            isModified = true;
            if (projectFile instanceof BlocklyAudio) {
                BlocklyAudio audio = (BlocklyAudio) projectFile;
                File audioFile = new File(audio.getFilePathInWorkspace());
                boolean deleteSuccess = audioFile.delete();
            }
        }
    }

    public void renameProjectFile(ProjectFile projectFile, String newName) {
        if (projectFile != null && !TextUtils.isEmpty(newName)) {
            projectFile.name = newName;
            isModified = true;
        }
    }

    private void saveModeInfo(ModelInfo modelInfo) {
        LogUtil.d("");
        if (modelInfo != null) {
            mProject.modelInfo = modelInfo;
        }
    }

    private void saveAudio(BlocklyAudio audio) {
        LogUtil.d("");
        if (audio != null) {
            BlocklyAudio b = mProject.getAudio(audio.id);
            if (b != null) {
                b.modifyTime = audio.modifyTime;
            } else {
                mProject.addProjectFile(audio);
            }
        }
    }

    private void saveBlockly(Blockly blockly) {
        if (blockly != null) {
            Blockly b = mProject.getBlockly(blockly.id);
            if (b != null) {
                b.workspace = blockly.workspace;
                b.modifyTime = blockly.modifyTime;
            } else {
                mProject.addProjectFile(blockly);
            }
        }
    }

    private void saveMotion(Motion motion) {
        if (motion != null) {
            Motion m = mProject.getMotion(motion.id);
            if (m != null) {
                m.frames = motion.frames;
                m.servos = motion.servos;
                m.totaltime = motion.totaltime;
                m.modifyTime = motion.modifyTime;
            } else {
                mProject.addProjectFile(motion);
            }
        }
    }

    private void saveController(Controller controller) {
        if (controller != null) {
            controller.modifyTime = System.currentTimeMillis();
            controller.clearPyFileContent();
            Controller m = mProject.getController(controller.id);
            if (m != null) {
                m.configs = controller.configs;
                m.blocklies = controller.blocklies;
                m.modifyTime = controller.modifyTime;
            } else {
                mProject.addProjectFile(controller);
            }
        }
    }

    private void saveDoodle(Doodle doodle) {
        if (doodle != null) {
            doodle.modifyTime = System.currentTimeMillis();
            Doodle m = mProject.getDoodle(doodle.id);
            if (m != null) {
                m.modifyTime = doodle.modifyTime;
            } else {
                mProject.addProjectFile(doodle);
            }
        }
    }


    private Observable<Project> saveProjectFiles() {

        final List<Runnable> saveTasks = new ArrayList<>();

        //先删除原项目文件夹
        String projectRootPath = FileHelper.join(FileHelper.getUserPath(mProject.userId), Project.PROJECTS_DIR_NAME, mProject.projectId);
        final File file = new File(projectRootPath);
        if (file.exists()) {
            Runnable deleteOriginProject = new Runnable() {
                @Override
                public void run() {
                    FileHelper.removeDir(file);
                }
            };

            saveTasks.add(deleteOriginProject);
        }

        //save blockly
        if (mProject.blocklyList != null && mProject.blocklyList.size() > 0) {
            for (final Blockly blockly : mProject.blocklyList) {
                final String workspaceFile = FileHelper.join(
                        projectRootPath,
                        Project.BLOCKLY_DIR_NAME,
                        blockly.id,
                        Blockly.WORKSPACE_FILE_NAME);

                Runnable saveBlocklyWorkspace = new Runnable() {
                    @Override
                    public void run() {
                        FileHelper.writeTxtFile(workspaceFile, blockly.workspace);
                    }
                };
                saveTasks.add(saveBlocklyWorkspace);
            }
        }
        //save controller
        if (mProject.controllerList != null && mProject.controllerList.size() > 0) {
            for (final Controller controller : mProject.controllerList) {
                final String configFilePath = FileHelper.join(
                        projectRootPath,
                        Project.CONTROLLER_DIR_NAME,
                        controller.id,
                        Controller.CONFIG_FILE_NAME);

                WidgetConfigWrapper wrapper = new WidgetConfigWrapper(controller.configs);
                //确保能生成这个json字段
                if (wrapper.configs == null) {
                    wrapper.configs = new ArrayList<>();
                }
                final String configJson = GsonUtil.get().toJson(wrapper);

                Runnable saveConfig = new Runnable() {
                    @Override
                    public void run() {
                        FileHelper.writeTxtFile(configFilePath, configJson);
                    }
                };

                saveTasks.add(saveConfig);

                if (controller.blocklies != null && controller.blocklies.size() > 0) {
                    for (final ControllerBlockly blockly : controller.blocklies) {
                        final String workspaceFilePath = FileHelper.join(
                                projectRootPath,
                                Project.CONTROLLER_DIR_NAME,
                                controller.id,
                                blockly.id,
                                Blockly.WORKSPACE_FILE_NAME);

                        Runnable saveControllerBlockly = new Runnable() {
                            @Override
                            public void run() {
                                FileHelper.writeTxtFile(workspaceFilePath, blockly.workspace);
                            }
                        };
                        saveTasks.add(saveControllerBlockly);

                        if (blockly.blocklyFiles != null && blockly.blocklyFiles.size() > 0) {
                            for (final BlocklyFile blocklyFile : blockly.blocklyFiles) {

                                final String blocklyFilePath = FileHelper.join(
                                        projectRootPath,
                                        Project.CONTROLLER_DIR_NAME,
                                        controller.id,
                                        blockly.id,
                                        blocklyFile.getFileName());

                                Runnable saveLuaFile = new Runnable() {
                                    @Override
                                    public void run() {
                                        FileHelper.writeTxtFile(blocklyFilePath, blocklyFile.content);
                                    }
                                };
                                saveTasks.add(saveLuaFile);

                            }
                        }
                    }
                }
            }
        }

        //save motion
        if (mProject.motionList != null && mProject.motionList.size() > 0) {
            for (final Motion motion : mProject.motionList) {
                final String motionFile = FileHelper.join(
                        projectRootPath,
                        Project.MOTION_DIR_NAME,
                        motion.getMotionFileName());
                final String motionJson = GsonUtil.get().toJson(motion);

                Runnable saveMotionFile = new Runnable() {
                    @Override
                    public void run() {
                        FileHelper.writeTxtFile(motionFile, motionJson);
                    }
                };
                saveTasks.add(saveMotionFile);
            }
        }


        //save audio
        if (mProject.audioList != null) {
            final String projectAudioDir = FileHelper.join(projectRootPath, Project.AUDIO_DIR_NAME);
            for (final BlocklyAudio audio : mProject.audioList) {
                final String src = audio.getFilePathInWorkspace();
                File aac = new File(src);
                if (aac.exists()) {
                    final String dest = FileHelper.join(projectAudioDir, audio.id + BlocklyAudio.AAC_SUFFIX);
                    File audioDirFile = new File(dest).getParentFile();
                    audioDirFile.mkdirs();
                    Runnable copyAAC = new Runnable() {
                        @Override
                        public void run() {
                            FileHelper.copyFile(src, dest);
                        }
                    };
                    saveTasks.add(copyAAC);
                } else {
                    LogUtil.e("audio对应的aac文件不存在：" + audio.name + "" + audio.id);
                }
            }
        }

        //save model information
        if (mProject.modelInfo != null) {
            final String json = GsonUtil.get().toJson(mProject.modelInfo);
            final String modelInfoFile = FileHelper.join(projectRootPath, Project.MODEL_FILE_NAME);
            Runnable saveModelInfoFile = new Runnable() {
                @Override
                public void run() {
                    FileHelper.writeTxtFile(modelInfoFile, json);
                }
            };
            saveTasks.add(saveModelInfoFile);
        }

        //save manifest file
        final String json = GsonUtil.get().toJson(mProject);
        final String manifest = FileHelper.join(projectRootPath, Project.PROJECT_MANIFEST_NAME);

        Runnable saveManifestFile = new Runnable() {
            @Override
            public void run() {
                FileHelper.writeTxtFile(manifest, json);
            }
        };
        saveTasks.add(saveManifestFile);
        return Observable
                .create(new ObservableOnSubscribe<Project>() {
                    @Override
                    public void subscribe(ObservableEmitter<Project> emitter) throws Exception {
                        for (Runnable task : saveTasks) {
                            task.run();
                        }
                        if (!emitter.isDisposed()) {
                            emitter.onNext(mProject);
                            emitter.onComplete();
                        }
                    }
                })
                .subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }


    /**
     * 保存工作区的project文件到正式文件夹下（projects），
     */
    public Observable<Project> save() {
        //先写project的manifest.json文件，再把整个workspace拷贝到projects正式目录下,最后更新ProjectManager

        if (!isModified) {
            return Observable.empty();
        }
        mProject.isModified = true;
        mProject.localModifyTime = System.currentTimeMillis();
        mProject.projectVersion = Project.getCurrentProjectVersion();
        mProject.setTargetDevice(Settings.getTargetDevice());
        return saveProjectFiles()
                .doOnComplete(new Action() {
                    @Override
                    public void run() throws Exception {
                        isModified = false;
                    }
                })
                .subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Observable<Project> updateProjectName(String name) {
        if (!TextUtils.isEmpty(name) && !name.equals(mProject.projectName)) {
            isModified = true;
            mProject.projectName = name;
        }
        return Observable.just(mProject);
    }

    public Observable<Project> updateProjectImage(String imagePath) {
        if (!TextUtils.isEmpty(imagePath) && !imagePath.equals(mProject.imgPath)) {
            isModified = true;
            mProject.imgPath = imagePath;
        }
        return Observable.just(mProject);
    }

    public void clear() {
        mProject = null;
        isModified = false;
    }
}
