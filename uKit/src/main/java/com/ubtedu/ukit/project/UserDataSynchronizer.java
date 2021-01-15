/**
 * @2018 UBTECH Robotics Corp. All rights reserved (C)
 **/
package com.ubtedu.ukit.project;

import android.text.TextUtils;

import com.ubtedu.alpha1x.utils.GsonUtil;
import com.ubtedu.alpha1x.utils.LogUtil;
import com.ubtedu.alpha1x.utils.MD5Util;
import com.ubtedu.base.net.rxretrofit.exception.RxException;
import com.ubtedu.base.net.rxretrofit.mode.ApiCode;
import com.ubtedu.base.net.rxretrofit.mode.ApiResult;
import com.ubtedu.ukit.R;
import com.ubtedu.ukit.application.UKitApplication;
import com.ubtedu.ukit.common.exception.ExceptionHelper;
import com.ubtedu.ukit.common.exception.ResultCode;
import com.ubtedu.ukit.common.files.FileHelper;
import com.ubtedu.ukit.common.cloud.CloudStorageManager;
import com.ubtedu.ukit.common.cloud.CloudResult;
import com.ubtedu.ukit.menu.settings.Settings;
import com.ubtedu.ukit.project.blockly.BlocklyAudio;
import com.ubtedu.ukit.project.vo.Blockly;
import com.ubtedu.ukit.project.vo.Controller;
import com.ubtedu.ukit.project.vo.Doodle;
import com.ubtedu.ukit.project.vo.ModelInfo;
import com.ubtedu.ukit.project.vo.Motion;
import com.ubtedu.ukit.project.vo.Project;
import com.ubtedu.ukit.project.vo.ProjectFile;
import com.ubtedu.ukit.user.UserManager;
import com.ubtedu.ukit.user.vo.UserData;
import com.ubtedu.ukit.user.vo.response.EmptyResponse;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.ObservableSource;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.BiFunction;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.observables.ConnectableObservable;
import io.reactivex.schedulers.Schedulers;


/**
 * @Author qinicy
 * @Date 2018/11/21
 **/
public class UserDataSynchronizer {
    private ConnectableObservable<DataSyncEvent> mSynchronizerObservable;
    private ObservableEmitter<DataSyncEvent> mSynchronizerEmitter;

    private SyncDisposable mSyncDisposable;
    /**
     * 该project list包含1代和2代主控的project，没区分
     */
    private List<Project> mSyncedProjects;


    /**
     * 只提示第一次网络请求错误，但成功之后再失败也需要提示
     */
    private boolean isToastNetworkError = true;

    private UserDataSynchronizer() {

        initRxSynchronizer();
    }


    private static class SingletonHolder {
        private final static UserDataSynchronizer instance = new UserDataSynchronizer();
    }


    private void initRxSynchronizer() {
        mSynchronizerObservable = Observable.create(new ObservableOnSubscribe<DataSyncEvent>() {
            @Override
            public void subscribe(ObservableEmitter<DataSyncEvent> emitter) throws Exception {
                mSynchronizerEmitter = emitter;
            }
        }).publish();
        mSynchronizerObservable.connect();
    }

    public static UserDataSynchronizer getInstance() {
        return UserDataSynchronizer.SingletonHolder.instance;
    }

    public ConnectableObservable<DataSyncEvent> getSynchronizerObservable() {
        return mSynchronizerObservable;
    }

    public Observable<UserData> sync(final boolean onlineSync) {
        cancel();
        final boolean isRealCanOnlineSync = onlineSync && !UserManager.getInstance().isGuest();
        LogUtil.d("sync start...onlineSync:arg =>" + onlineSync + "  isRealCanOnlineSync:" + isRealCanOnlineSync);
        final UserData temp = new UserData();
        return handleSync(isRealCanOnlineSync)
                .doOnSubscribe(new Consumer<Disposable>() {
                    @Override
                    public void accept(Disposable disposable) throws Exception {
                        mSyncDisposable = new SyncDisposable(isRealCanOnlineSync, disposable);
                    }
                })
                .doAfterNext(new Consumer<UserData>() {
                    @Override
                    public void accept(UserData data) throws Exception {
                        if (data != null) {
                            temp.projects = data.projects;
                            temp.isSyncSuccess = data.isSyncSuccess;
                            temp.syncErrorMessage = data.syncErrorMessage;
                            DataSyncEvent event = new DataSyncEvent();
                            event.event = DataSyncEvent.SYNC_EVENT_SYNCING;
                            event.isOnlineSync = isRealCanOnlineSync;
                            event.isSyncSuccess = data.isSyncSuccess;
                            event.syncErrorMessage = data.syncErrorMessage;
                            event.projects = new ArrayList<>();
                            if (data.projects != null) {
                                List<Project> projects = new ArrayList<>(data.projects);
                                for (Project project : projects) {
                                    if (!project.isDeletedAtLocal) {
                                        event.projects.add(project);
                                    }
                                }
                            }
                            emitterDataChangeEvent(event);
                        }
                    }
                })

                .doOnComplete(new Action() {
                    @Override
                    public void run() throws Exception {

                        DataSyncEvent event = new DataSyncEvent();
                        event.event = DataSyncEvent.SYNC_EVENT_SYNC_END;
                        event.isOnlineSync = isRealCanOnlineSync;
                        event.isSyncSuccess = temp.isSyncSuccess;
                        event.syncErrorMessage = temp.syncErrorMessage;
                        event.projects = new ArrayList<>();
                        if (temp.projects != null) {
                            List<Project> projects = new ArrayList<>(temp.projects);
                            for (Project project : projects) {
                                if (!project.isDeletedAtLocal) {
                                    event.projects.add(project);
                                }
                            }
                        }
                        emitterDataChangeEvent(event);

                        mSyncedProjects = temp.projects;
                        mSyncDisposable = null;
                    }
                });
    }


    /**
     * Cancel sync
     */
    public void cancel() {
        if (mSyncDisposable != null && !mSyncDisposable.isDisposed()) {
            LogUtil.d("cancel sync...");
            DataSyncEvent event = new DataSyncEvent();
            event.event = DataSyncEvent.SYNC_EVENT_SYNC_CANCEL;
            event.isOnlineSync = mSyncDisposable.isOnlineSync();
            emitterDataChangeEvent(event);
            mSyncDisposable.dispose();
            mSyncDisposable = null;
        }
    }

    /**
     * 注意：返回的项目不区分主控类型
     *
     * @return
     */
    public List<Project> getProjects() {
        if (mSyncedProjects != null) {
            return new ArrayList<>(mSyncedProjects);
        }
        return null;
    }

    public List<Project> getTargetDeviceProjects() {
        List<Project> projects = getProjects();
        if (projects != null) {
            List<Project> temps = new ArrayList<>(projects.size());
            for (Project p : projects) {
                if (Settings.isTargetDevice(p.getTargetDevice())) {
                    temps.add(p);
                }
            }
            return temps;
        }
        return null;
    }

    public Project getProject(String projectId) {
        if (projectId != null && mSyncedProjects != null) {
            List<Project> localProjects = new ArrayList<>(mSyncedProjects);
            for (Project project : localProjects) {
                if (projectId.equals(project.projectId)) {
                    return project;
                }
            }
        }
        return null;
    }

    public Observable<Boolean> deleteProject(final String projectId) {
        if (TextUtils.isEmpty(projectId)) {
            return Observable.just(false);
        }
        return deleteProjectOnServer(projectId)
                .onErrorReturn(new Function<Throwable, Boolean>() {
                    @Override
                    public Boolean apply(Throwable throwable) throws Exception {
                        LogUtil.e("deleteProjectOnServer fail:" + projectId);
                        throwable.printStackTrace();
                        if (isToastNetworkError) {
                            isToastNetworkError = false;
//                            ExceptionHelper.handleException(ApiCode.Request.HTTP_ERROR, throwable.getMessage());
                        }
                        return false;
                    }
                })

                .flatMap(new Function<Boolean, ObservableSource<Boolean>>() {
                    @Override
                    public ObservableSource<Boolean> apply(Boolean success) throws Exception {
                        Project project = getProject(projectId);
                        if (project != null) {
                            if (success) {
                                if (mSyncedProjects != null) {
                                    mSyncedProjects.remove(project);
                                }

                                File projectDirectory = new File(project.getProjectPath());
                                if (projectDirectory.exists()) {
                                    boolean isSuccess = FileHelper.removeDir(projectDirectory);
                                    LogUtil.i("removeDir projectDirectory " + projectDirectory);
                                    return Observable.just(isSuccess);
                                }
                            } else {
                                //记个标记，下次同步的时候再删
                                project.isDeletedAtLocal = true;
                                //删除也算是编辑
                                project.localModifyTime = System.currentTimeMillis();
                                project.isModified = true;
                                return updateProjectManifestFiles(project)
                                        .map(new Function<Project, Boolean>() {
                                            @Override
                                            public Boolean apply(Project data) throws Exception {
                                                return true;
                                            }
                                        });
                            }

                        }
                        return Observable.just(false);
                    }
                });
    }


    private void onSyncStart(boolean onlineSync) {
        DataSyncEvent event = new DataSyncEvent();
        event.event = DataSyncEvent.SYNC_EVENT_SYNC_START;
        event.isOnlineSync = onlineSync;
        emitterDataChangeEvent(event);
    }

    private void emitterDataChangeEvent(DataSyncEvent event) {
        if (event != null && mSynchronizerEmitter != null && !mSynchronizerEmitter.isDisposed()) {
            mSynchronizerEmitter.onNext(event);
        }
    }

    public boolean isProjectNameDuplicate(String projectName) {
        if (projectName != null) {
            List<Project> projects = getTargetDeviceProjects();
            if (projects != null) {
                List<Project> localProjects = new ArrayList<>(projects);
                for (Project project : localProjects) {
                    if (projectName.equals(project.projectName)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /**
     * @param newProjectName
     * @param originProjectName
     * @return
     */
    public boolean isProjectNameDuplicate(String newProjectName, String originProjectName) {
        if (newProjectName != null) {
            List<Project> projects = getTargetDeviceProjects();
            if (projects != null) {
                List<Project> localProjects = new ArrayList<>(projects);
                for (Project project : localProjects) {
                    //如果projectName与originProjectName相同，则不认为重复
                    if (newProjectName.equals(project.projectName) && !newProjectName.equals(originProjectName)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private Observable<UserData> handleSync(boolean onlineSync) {
        onSyncStart(onlineSync);
        if (onlineSync) {
            return onlineSync();
        } else {
            return offlineSync();
        }
    }

    private Observable<UserData> offlineSync() {
        return loadLocalProjects();
    }

    private Observable<UserData> onlineSync() {
        return Observable
                .zip(loadLocalProjects(), requestServerData(), new BiFunction<UserData, UserData, UserData[]>() {
                    @Override
                    public UserData[] apply(UserData localData, UserData serverData) throws Exception {
                        if (serverData.projects != null && serverData.projects.size() > 0) {
                            List<Project> compactList = new ArrayList<>();
                            for (Project project : serverData.projects) {
                                if (project.isCompat()) {
                                    compactList.add(0, project);
                                } else {
                                    compactList.add(project);
                                }
                            }
                            serverData.projects = compactList;
                        }
                        return new UserData[]{localData, serverData};
                    }
                })
                .flatMap(new Function<UserData[], ObservableSource<Project>>() {
                    @Override
                    public ObservableSource<Project> apply(UserData[] data) throws Exception {
                        return merge(data[0], data[1]);
                    }
                })
                .flatMap(new Function<Project, ObservableSource<UserData>>() {
                    @Override
                    public ObservableSource<UserData> apply(Project project) throws Exception {
                        return loadLocalProjects();
                    }
                })
                .map(new Function<UserData, UserData>() {
                    @Override
                    public UserData apply(UserData data) throws Exception {
                        data.isSyncSuccess = true;
                        return data;
                    }
                })
                //中途异常，返回本地数据
                .onErrorResumeNext(new Function<Throwable, ObservableSource<? extends UserData>>() {
                    @Override
                    public ObservableSource<? extends UserData> apply(final Throwable throwable) throws Exception {

                        return offlineProjects().map(new Function<UserData, UserData>() {
                            @Override
                            public UserData apply(UserData userData) throws Exception {
                                userData.isSyncSuccess = false;
                                if (throwable instanceof SyncCompatException) {
                                    userData.syncErrorMessage = UKitApplication.getInstance().getString(R.string.project_sync_fail_low_version);
                                }
                                return userData;
                            }
                        });
                    }
                });
    }

    private Observable<UserData> offlineProjects() {
        return loadLocalProjects()
                .map(new Function<UserData, UserData>() {
                    @Override
                    public UserData apply(UserData userData) throws Exception {
                        userData.isSyncSuccess = false;
                        return userData;
                    }
                });
    }

    private Observable<Boolean> deleteProjectOnServer(final String projectId) {
        if (UserManager.getInstance().isGuest()) {
            return Observable.just(true);
        }
        LogUtil.d("deleteProjectOnServer start:" + projectId);
        return UserManager.getInstance().deleteUserProject(projectId)
                .map(new Function<ApiResult<EmptyResponse>, Boolean>() {
                    @Override
                    public Boolean apply(ApiResult<EmptyResponse> result) throws Exception {
                        if (result.code == 0 && "success".equals(result.msg)) {
                            LogUtil.d("deleteProjectOnServer success:" + projectId);
                            return true;
                        } else {
                            return false;
                        }
                    }
                });

    }


    /**
     * 先遍历服务器已删除，本地还存在的工程（判断逻辑：本地的项目如果不是已编辑状态且服务端没有该项目）
     *
     * @param localProjects
     * @param serverProjects
     */
    private void deleteOnlyLocal(List<Project> localProjects, List<Project> serverProjects) {
        final List<Project> tempProjects = new ArrayList<>(localProjects);
        for (int i = 0; i < tempProjects.size(); i++) {
            final Project localProject = tempProjects.get(i);
            Project serverProject = isProjectInList(localProject, serverProjects);
            if (!localProject.isModified && serverProject == null) {
                localProjects.remove(localProject);
                final File projectDirFile = new File(localProject.getProjectPath());
                if (projectDirFile.exists() && projectDirFile.isDirectory()) {
                    FileHelper.removeDir(projectDirFile);
                }
            }
        }
    }

    /**
     * 注释给自己：返回值的project没有任何意义，不要使用
     */
    private Observable<Project> merge(final UserData local, UserData server) {
        List<Project> localProjects = new ArrayList<>();
        List<Project> serverProjects = new ArrayList<>();
        if (local.projects != null) {
            localProjects = new ArrayList<>(local.projects);
        }
        if (server.projects != null) {
            serverProjects = new ArrayList<>(server.projects);
        }


        List<Observable<Project>> mergeTasks = new ArrayList<>();

        //先遍历服务器已删除，本地还存在的工程（判断逻辑：本地的项目如果不是已编辑状态且服务端没有该项目）
        if (server.isRequestServerDataSuccess) {
            deleteOnlyLocal(localProjects, serverProjects);
        }


        int size = Math.max(localProjects.size(), serverProjects.size());
        if (size > 0) {
            boolean isLocalMoreThenServer = localProjects.size() > serverProjects.size();

            List<Project> tempProjects = isLocalMoreThenServer ? localProjects : serverProjects;
            List<Project> tempProjects2 = isLocalMoreThenServer ? serverProjects : localProjects;
            for (int i = 0; i < size; i++) {
                final Project p1 = tempProjects.get(i);
                Project p2 = isProjectInList(p1, tempProjects2);

                Observable<Project> mergeTask;
                if (isLocalMoreThenServer) {
                    mergeTask = mergeProject(p1, p2);
                } else {
                    mergeTask = mergeProject(p2, p1);
                }
                if (mergeTask != null) {
                    mergeTasks.add(mergeTask);
                }
            }
        }

        local.projects = localProjects;

        //合并Task大于0，执行合并，否则直接返回本地数据
        if (mergeTasks.size() > 0) {

            return Observable.merge(mergeTasks)

                    .flatMap(new Function<Project, ObservableSource<Project>>() {
                        @Override
                        public ObservableSource<Project> apply(Project project) throws Exception {
                            return updateProjectManifestFiles(project);
                        }
                    })
                    .flatMap(new Function<Project, ObservableSource<Project>>() {
                        @Override
                        public ObservableSource<Project> apply(Project project) throws Exception {
                            return uploadProjects(project);
                        }
                    })

                    //更新编辑状态
                    .map(new Function<Project, Project>() {
                        @Override
                        public Project apply(Project project) throws Exception {
                            project.isModified = false;

                            return project;
                        }
                    })
                    .flatMap(new Function<Project, ObservableSource<Project>>() {
                        @Override
                        public ObservableSource<Project> apply(Project project) throws Exception {
                            return updateProjectManifestFiles(project);
                        }
                    });

        } else {
            return Observable.just(new Project());
        }
    }

    private Project isProjectInList(Project project, List<Project> list) {
        if (project != null && list != null) {
            for (int i = 0; i < list.size(); i++) {
                if (project.projectId.equals(list.get(i).projectId)) {
                    return list.get(i);
                }
            }
        }
        return null;
    }

    private Observable<Project> mergeProject(final Project local, final Project server) {

        String localName = local == null ? "null" : local.projectName + "-" + local.projectId;
        String serverName = server == null ? "null" : server.projectName + "-" + server.projectId;
        LogUtil.d("local:" + localName + "  server:" + serverName);

        if (local != null) {
            //如果不兼容，不进行任何合并
            if (!local.isCompat()) {
                return Observable.error(new SyncCompatException());
            }
            if (server != null) {

                //先检查local是不是已在本地删除，如果已删除，根据编辑时间决定是否要删除
                if (local.isDeletedAtLocal && local.localModifyTime >= server.serverModifyTime) {
                    return deleteProject(local.projectId).flatMap(new Function<Boolean, ObservableSource<Project>>() {
                        @Override
                        public ObservableSource<Project> apply(Boolean project) throws Exception {
                            return Observable.empty();
                        }
                    });
                }

                final boolean isNeedDownloadZip = server.serverModifyTime > local.serverModifyTime;
                //不需要下载，也就是说本地 >= 服务端，所以也不需要合并
                if (!isNeedDownloadZip) {
                    //fix bug#47803
                    if (local.serverModifyTime == local.localModifyTime && local.localModifyTime == server.serverModifyTime) {
                        local.projectName = server.projectName;
                        local.imgUrl = server.imgUrl;
                    }
                    return Observable.just(local);
                }


                return downloadProject(server)
                        .flatMap(new Function<Project, ObservableSource<Project>>() {
                            @Override
                            public ObservableSource<Project> apply(Project project) throws Exception {
                                //如果不兼容，不进行任何合并
                                if (!project.isCompat()) {
                                    return Observable.error(new SyncCompatException());
                                }
                                return mergeProjectFiles(local, project);
                            }
                        });
            } else {
                if (local.isDeletedAtLocal) {
                    final File projectDirFile = new File(local.getProjectPath());
                    if (projectDirFile.exists() && projectDirFile.isDirectory()) {
                        FileHelper.removeDir(projectDirFile);
                    }
                    return null;
                }

                return Observable.just(local);
            }
        } else {
            if (server != null) {
                //服务器有，本地没有。
                server.isModified = false;
                server.localModifyTime = server.serverModifyTime;
                return downloadProject(server)
                        .flatMap(new Function<Project, ObservableSource<Boolean>>() {
                            @Override
                            public ObservableSource<Boolean> apply(Project project) throws Exception {
                                //如果不兼容，不进行任何合并
                                if (!project.isCompat()) {
                                    return Observable.error(new SyncCompatException());
                                }
                                String projectsDir = FileHelper.getProjectsPath(project.userId);
                                String name = project.projectId;

                                return FileHelper.copyDir(project.downloadPath, projectsDir, name);
                            }
                        })
                        .map(new Function<Boolean, Project>() {
                            @Override
                            public Project apply(Boolean aBoolean) throws Exception {
                                return server;
                            }
                        });
            } else {
                return Observable.error(new Exception("Mr.Iron，local == null && server == null,unbelievable!"));
            }
        }
    }

    private Observable<Boolean> mergeProjectFile(@NonNull final Project localProject,
                                                 final ProjectFile localProjectFile,
                                                 @NonNull Project serverProject,
                                                 @NonNull final ProjectFile serverProjectFile) {

        String typeDir = "";
        if (serverProjectFile instanceof Blockly) {
            typeDir = Project.BLOCKLY_DIR_NAME;
        }
        if (serverProjectFile instanceof Motion) {
            typeDir = Project.MOTION_DIR_NAME;
        }
        if (serverProjectFile instanceof Controller) {
            typeDir = Project.CONTROLLER_DIR_NAME;
        }
        if (serverProjectFile instanceof Doodle) {
            typeDir = Project.DOODLE_DIR_NAME;
        }
        if (serverProjectFile instanceof Doodle) {
            typeDir = Project.AUDIO_DIR_NAME;
        }
        final boolean isModelInfo = serverProjectFile instanceof ModelInfo;


        //动作文件比较特殊，并没有外层文件夹
        String projectFileName = serverProjectFile.id;
        if (serverProjectFile instanceof Motion) {
            projectFileName += Motion.MOTION_FILE_SUFFIX;
        } else if (serverProjectFile instanceof BlocklyAudio) {
            projectFileName += BlocklyAudio.AAC_SUFFIX;
        }
        String srcFile = isModelInfo ?
                FileHelper.join(
                        serverProject.downloadPath,
                        Project.MODEL_FILE_NAME)
                :
                FileHelper.join(
                        serverProject.downloadPath,
                        typeDir,
                        projectFileName);

        final String destPath =
                isModelInfo ?
                        FileHelper.join(
                                serverProject.downloadPath,
                                Project.MODEL_FILE_NAME)
                        :
                        FileHelper.join(
                                localProject.getProjectPath(),
                                typeDir,
                                projectFileName);

        LogUtil.d("mergeProjectFile:" + srcFile + "  \ndestPath:" + destPath);
        return copyProjectFile(srcFile, destPath)
                //在拷贝服务端到本地之前，先删除本地的
                .doOnSubscribe(new Consumer<Disposable>() {
                    @Override
                    public void accept(Disposable disposable) throws Exception {
                        File destFile = new File(destPath);
                        if (!destFile.exists()) {
                            return;
                        }
                        if (destFile.isFile()) {
                            destFile.delete();
                        } else {
                            FileHelper.removeDir(destFile);
                        }
                        LogUtil.i("mergeProjectFile before:" + destPath);
                    }
                })
                .subscribeOn(Schedulers.io())
                //合并文件后，合并列表
                .map(new Function<Boolean, Boolean>() {
                    @Override
                    public Boolean apply(Boolean aBoolean) throws Exception {
                        LogUtil.i("mergeProjectFile finish:" + destPath);
                        if (!isModelInfo) {
                            if (localProjectFile != null) {
                                localProject.removeProjectFile(localProjectFile);
                            }
                            localProject.addProjectFile(serverProjectFile);
                        }
                        return true;
                    }
                });
    }

    private Observable<Boolean> copyProjectFile(String srcPath, String destPath) {
        File srcFile = new File(srcPath);
        if (srcFile.isFile()) {
            return FileHelper.asyncCopyFile(srcPath, destPath);
        } else {
            return FileHelper.asyncCopyDir(srcPath, destPath);
        }
    }

    private Observable<Project> mergeProjectFiles(final Project localProject, final Project serverProject) {

        //如果不兼容，不进行任何合并
        if (!localProject.isCompat() || !serverProject.isCompat()) {
            return Observable.error(new SyncCompatException());
        }
        //local无修改，且local.localModifyTime < server.serverModifyTime,则直接拷贝服务器的覆盖掉本地
        if (!localProject.isModified && localProject.localModifyTime < serverProject.serverModifyTime) {
            String projectsDir = FileHelper.getProjectsPath(localProject.userId);
            String name = localProject.projectId;
            final String destPath = FileHelper.join(projectsDir, name);
            serverProject.isModified = false;
            serverProject.isDeletedAtLocal = false;
            return
                    FileHelper.asyncCopyDir(serverProject.downloadPath, destPath)
                            .doOnSubscribe(new Consumer<Disposable>() {
                                @Override
                                public void accept(Disposable disposable) throws Exception {
                                    FileHelper.removeDir(new File(destPath));
                                }
                            })
                            .map(new Function<Boolean, Project>() {
                                @Override
                                public Project apply(Boolean aBoolean) throws Exception {
                                    LogUtil.d("本地无修改，服务器有更新，直接使用服务器文件覆盖本地");
                                    //覆盖完文件，还要覆盖对象
                                    localProject.replaceProperties(serverProject);
                                    //需要使用serverProject的serverModifyTime覆盖localProject的localModifyTime
                                    localProject.localModifyTime = serverProject.serverModifyTime;
                                    return localProject;
                                }
                            });
        }

        //如果不是以上情况，则分别合并文件
        List<Observable<Boolean>> copyTasks = new ArrayList<>();


        //合并Blockly
        if (serverProject.blocklyList != null && serverProject.blocklyList.size() > 0) {
            for (int j = 0; j < serverProject.blocklyList.size(); j++) {
                final Blockly serverBlockly = serverProject.blocklyList.get(j);
                final Blockly localBlockly = localProject.getBlockly(serverBlockly.id);

                if (localBlockly == null || localBlockly.modifyTime < serverBlockly.modifyTime) {
                    Observable<Boolean> mergeBlockly = mergeProjectFile(
                            localProject,
                            localBlockly,
                            serverProject,
                            serverBlockly);
                    copyTasks.add(mergeBlockly);
                }
            }
        }

        //合并motion
        if (serverProject.motionList != null && serverProject.motionList.size() > 0) {
            for (int j = 0; j < serverProject.motionList.size(); j++) {
                final Motion serverMotion = serverProject.motionList.get(j);
                final Motion localMotion = localProject.getMotion(serverMotion.id);
                if (localMotion == null || localMotion.modifyTime < serverMotion.modifyTime) {
                    Observable<Boolean> mergeMotion = mergeProjectFile(
                            localProject,
                            localMotion,
                            serverProject,
                            serverMotion);
                    copyTasks.add(mergeMotion);
                }
            }
        }

        //合并controller
        if (serverProject.controllerList != null && serverProject.controllerList.size() > 0) {
            for (int j = 0; j < serverProject.controllerList.size(); j++) {
                final Controller serverController = serverProject.controllerList.get(j);
                final Controller localController = localProject.getController(serverController.id);
                if (localController == null || localController.modifyTime < serverController.modifyTime) {
                    Observable<Boolean> mergeController = mergeProjectFile(
                            localProject,
                            localController,
                            serverProject,
                            serverController);
                    copyTasks.add(mergeController);
                }
            }
        }
        //合并doodle
        if (serverProject.doodleList != null && serverProject.doodleList.size() > 0) {
            for (int j = 0; j < serverProject.doodleList.size(); j++) {
                final Doodle serverController = serverProject.doodleList.get(j);
                final Doodle localController = localProject.getDoodle(serverController.id);
                if (localController == null || localController.modifyTime < serverController.modifyTime) {
                    Observable<Boolean> mergeDoodle = mergeProjectFile(
                            localProject,
                            localController,
                            serverProject,
                            serverController);
                    copyTasks.add(mergeDoodle);
                }
            }
        }
        //合并audio
        if (serverProject.audioList != null && serverProject.audioList.size() > 0) {
            for (int j = 0; j < serverProject.audioList.size(); j++) {
                final BlocklyAudio serverAudio = serverProject.audioList.get(j);
                final BlocklyAudio localAudio = localProject.getAudio(serverAudio.id);
                if (localAudio == null) {
                    Observable<Boolean> mergeDoodle = mergeProjectFile(
                            localProject,
                            localAudio,
                            serverProject,
                            serverAudio);
                    copyTasks.add(mergeDoodle);
                }
            }
        }
        //合并model
        if (serverProject.modelInfo != null) {
            if (localProject.modelInfo != null && localProject.modelInfo.modifyTime < serverProject.modelInfo.modifyTime) {
                localProject.modelInfo = serverProject.modelInfo;

                Observable<Boolean> mergeModelInfo = mergeProjectFile(
                        localProject,
                        localProject.modelInfo,
                        serverProject,
                        serverProject.modelInfo);
                copyTasks.add(mergeModelInfo);
            }
        }
        //合并Project本身

        if (localProject.localModifyTime <= serverProject.serverModifyTime) {
            localProject.localModifyTime = serverProject.serverModifyTime;
            localProject.projectName = serverProject.projectName;
            localProject.imgUrl = serverProject.imgUrl;
        }

        localProject.projectVersion = Math.max(localProject.projectVersion, serverProject.projectVersion);

        localProject.serverModifyTime = localProject.localModifyTime;

        localProject.isDeletedAtLocal = false;
        if (copyTasks.size() > 0) {
            localProject.isModified = true;
            return Observable
                    .zip(copyTasks, new Function<Object[], Boolean>() {
                        @Override
                        public Boolean apply(Object[] objects) throws Exception {
                            LogUtil.d("mergeProjects1");
                            return true;
                        }
                    })
                    .takeLast(1)
                    .map(new Function<Boolean, Project>() {
                        @Override
                        public Project apply(Boolean aBoolean) throws Exception {
                            LogUtil.d("mergeProjects2");
                            return localProject;
                        }
                    });
        } else {
            return Observable.just(localProject);
        }
    }


    private Observable<Project> downloadProject(final Project project) {
        if (project == null) {
            return Observable.error(new Throwable("Can not download a null project"));
        }
        final String projectZip = FileHelper.getProjectsDownloadPath() +
                File.separator + project.projectId + FileHelper.ZIP_FILE_SUFFIX;
        final String projectsDownloadDir = FileHelper.getProjectsDownloadPath();

        return CloudStorageManager.getInstance().download(project.objectKey, projectZip)
                .takeLast(1)
                .flatMap(new Function<Integer, ObservableSource<String>>() {
                    @Override
                    public ObservableSource<String> apply(Integer integer) throws Exception {
                        return FileHelper.unzipFile(projectZip, projectsDownloadDir);
                    }
                })
                .map(new Function<String, Boolean>() {
                    @Override
                    public Boolean apply(String projectsDownloadDir) throws Exception {

                        project.downloadPath = FileHelper.join(projectsDownloadDir, project.projectId);
                        return true;
                    }
                })
                .map(new Function<Boolean, Project>() {
                    @Override
                    public Project apply(Boolean aBoolean) throws Exception {
                        String path = project.downloadPath;
                        if (path != null) {
                            Project p = loadProjectFromFile(new File(path));
                            if (p != null) {
                                project.blocklyList = p.blocklyList;
                                project.motionList = p.motionList;
                                project.controllerList = p.controllerList;
                                project.doodleList = p.doodleList;
                                project.audioList = p.audioList;
                                project.modelInfo = p.modelInfo;
                                project.projectVersion = p.projectVersion;
                                project.setTargetDevice(p.getTargetDevice());
                            }
                        }
                        project.isModified = false;
                        LogUtil.d("download project " + project.projectName + " " + project.projectId);
                        return project;
                    }
                })
                .subscribeOn(Schedulers.io());
    }

    private Observable<Project> updateProjectManifestFiles(final Project data) {

        if (data == null) {
            return Observable.empty();
        }
        String projectPath = data.getProjectPath() + File.separator + Project.PROJECT_MANIFEST_NAME;
        String json = GsonUtil.get().toJson(data);

        return FileHelper.asyncWriteTxtFile(projectPath, json)
                .map(new Function<Boolean, Project>() {
                    @Override
                    public Project apply(Boolean aBoolean) throws Exception {
                        return data;
                    }
                });
    }


    private Observable<UserData> loadLocalProjects() {

        final UserData data = new UserData();
        return Observable
                .create(new ObservableOnSubscribe<List<Project>>() {
                    @Override
                    public void subscribe(ObservableEmitter<List<Project>> emitter) throws Exception {
                        List<Project> projects = loadProjectsFromDirectory();
                        emitter.onNext(projects);
                        emitter.onComplete();
                    }
                })
                .map(new Function<List<Project>, UserData>() {
                    @Override
                    public UserData apply(List<Project> projects) throws Exception {
                        data.projects = projects;
                        return data;
                    }
                })
                .subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 请求服务器数据
     *
     * @return 如果发生异常，返回一个空的userdata
     */
    private Observable<UserData> requestServerData() {
        return UserManager.getInstance().getUserData()
                .flatMap(new Function<ApiResult<UserData>, ObservableSource<UserData>>() {
                    @Override
                    public ObservableSource<UserData> apply(ApiResult<UserData> result) throws Exception {

                        if (result.sourceRawData != null) {
                            UserData data = result.sourceRawData;
                            data.isRequestServerDataSuccess = true;
                            return Observable.just(data);
                        }
                        return Observable.error(new Exception("request server data fail"));
                    }
                })
                .doOnError(new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        boolean isTokenExpired = false;
                        if (throwable instanceof RxException) {
                            RxException e = (RxException) throwable;
                            isTokenExpired = ExceptionHelper.isTokenExpired(e);
                            if (isTokenExpired) {
                                ExceptionHelper.handleException(ResultCode.SERVER_LOGIN_TOKEN_EXPIRED, throwable.getMessage());
                                isToastNetworkError = false;
                                return;
                            }
                        }

                        if (isToastNetworkError) {
                            isToastNetworkError = false;
                            ExceptionHelper.handleException(ApiCode.Request.HTTP_ERROR, throwable.getMessage());
                        }
                    }
                })
                .doOnComplete(new Action() {
                    @Override
                    public void run() throws Exception {
                        isToastNetworkError = true;
                    }
                });
    }

    private Observable<Project> uploadProjects(final Project project) {
        //只有修改过的才需要重新上传
        if (project.isModified && !project.isDeletedAtLocal) {
            final String projectDir = project.getProjectPath();
            final String projectZip = FileHelper.getZipCacheAbsolutePath() +
                    File.separator + project.projectId + FileHelper.ZIP_FILE_SUFFIX;

            if (projectDir != null && new File(projectDir).exists()) {
                //上传工程文件:先压缩zip，再上传
                Observable<Project> uploadTask = FileHelper.zipFiles(projectZip, projectDir)
                        .flatMap(new Function<String, ObservableSource<CloudResult>>() {
                            @Override
                            public ObservableSource<CloudResult> apply(String s) throws Exception {
                                LogUtil.d("upload project zip " + project.projectName + " " + project.projectId);
                                return CloudStorageManager.getInstance().upload(s).takeLast(1);
                            }
                        })
                        .map(new Function<CloudResult, Boolean>() {
                            @Override
                            public Boolean apply(CloudResult result) throws Exception {
                                project.objectKey = result.objectKey;
                                return true;
                            }
                        })
                        //Check md5
                        .flatMap(new Function<Boolean, ObservableSource<String>>() {
                            @Override
                            public ObservableSource<String> apply(Boolean aBoolean) throws Exception {
                                return checkMd5(projectZip);
                            }
                        })

                        //上传图片
                        .flatMap(new Function<String, ObservableSource<CloudResult>>() {
                            @Override
                            public ObservableSource<CloudResult> apply(String md5) throws Exception {
                                project.md5 = md5;
                                String imagePath = project.imgPath;
                                if (imagePath != null && new File(imagePath).exists()) {
                                    LogUtil.d("upload project img " + project.projectName + " " + project.projectId);
                                    return CloudStorageManager.getInstance().upload(imagePath)
                                            .takeLast(1);
                                }

                                return Observable.just(new CloudResult());
                            }
                        })
                        .map(new Function<CloudResult, Boolean>() {
                            @Override
                            public Boolean apply(CloudResult result) throws Exception {
                                if (result.objectKey != null) {
                                    String imageUrl = CloudStorageManager.getInstance().generateFileUrl(result.objectKey);
                                    project.imgUrl = imageUrl;
                                }
                                return true;
                            }
                        })
                        //把信息更新到服务器
                        .flatMap(new Function<Boolean, ObservableSource<ApiResult<EmptyResponse>>>() {
                            @Override
                            public ObservableSource<ApiResult<EmptyResponse>> apply(Boolean aBoolean) throws Exception {
                                LogUtil.d("update project " + project.projectName + " " + project.projectId);

                                Project args = project.deepClone();
                                if (args == null) {
                                    args = project;
                                }
                                //使用本地修改的时间作为modifyTime参数
                                args.serverModifyTime = project.localModifyTime;
                                return UserManager.getInstance().syncUserProject(args);
                            }
                        })
                        .map(new Function<ApiResult<EmptyResponse>, Project>() {
                            @Override
                            public Project apply(ApiResult<EmptyResponse> result) throws Exception {
                                //更新serverModifyTime
                                project.serverModifyTime = project.localModifyTime;
                                LogUtil.d("update project success " + project.projectName + " " + project.projectId);
                                return project;
                            }
                        });
                return uploadTask;
            }
        }
        return Observable.just(project);
    }

    private Observable<String> checkMd5(final String path) {
        if (path != null) {
            final File file = new File(path);
            if (file.exists() && file.canRead() && file.length() > 0) {
                return Observable.create(new ObservableOnSubscribe<String>() {
                    @Override
                    public void subscribe(ObservableEmitter<String> emitter) throws Exception {
                        String md5 = MD5Util.encodeFileMd5(path);
                        if (!emitter.isDisposed()) {
                            if (md5 != null) {
                                emitter.onNext(md5);
                            } else {
                                emitter.onNext("");
                            }
                            emitter.onComplete();
                        }

                    }
                })
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread());
            }
        }
        return Observable.just("");
    }

    public static List<Project> loadProjectsFromDirectory() {
        String projectsPath = FileHelper.getProjectsPath(UserManager.getInstance().getLoginUserId());
        File projectsFile = new File(projectsPath);
        File[] projectDirectoryArray = projectsFile.listFiles();
        List<Project> projects = new ArrayList<>();
        if (projectDirectoryArray != null) {
            for (File directory : projectDirectoryArray) {
                Project project = loadProjectFromFile(directory);
                if (project != null) {
                    projects.add(project);
                }
            }
        }
        //兼容的项目放前面，不兼容的项目放后面，方便同步合并
        List<Project> compactList = new ArrayList<>();
        if (projects.size() > 0) {
            for (Project project : projects) {
                if (project.isCompat()) {
                    compactList.add(0, project);
                } else {
                    compactList.add(project);
                }
            }
        }
        return compactList;
    }

    public static Project loadProjectFromFile(File projectDirectory) {
        File manifestJsonFile = new File(projectDirectory, Project.PROJECT_MANIFEST_NAME);

        if (projectDirectory.isDirectory() && manifestJsonFile.exists()) {
            String projectJson = FileHelper.readTxtFile(manifestJsonFile.getAbsolutePath());
            if (projectJson != null) {
                try {
                    Project project = Project.createFromJson(projectJson);
                    if (project != null) {
                        File modelJsonFile = new File(projectDirectory, Project.MODEL_FILE_NAME);
                        if (modelJsonFile.isFile() && modelJsonFile.exists()) {
                            String modelJson = FileHelper.readTxtFile(modelJsonFile.getAbsolutePath());
                            ModelInfo modelInfo = GsonUtil.get().toObject(modelJson, ModelInfo.class);
                            if (project.modelInfo != null && modelInfo != null) {
                                modelInfo.id = project.modelInfo.id;
                                modelInfo.createTime = project.modelInfo.createTime;
                                modelInfo.modifyTime = project.modelInfo.modifyTime;
                                modelInfo.name = project.modelInfo.name;
                            }
                            project.modelInfo = modelInfo;
                        }
                        return project;
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }
}
