/**
 * @2018 UBTECH Robotics Corp. All rights reserved (C)
 **/
package com.ubtedu.ukit.project.host;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Build;
import android.text.TextUtils;

import androidx.core.content.FileProvider;

import com.ubtedu.alpha1x.utils.LogUtil;
import com.ubtedu.base.net.rxretrofit.exception.RxException;
import com.ubtedu.base.net.rxretrofit.subscriber.SimpleRxSubscriber;
import com.ubtedu.deviceconnect.libs.base.interfaces.URoConnectStatusChangeListener;
import com.ubtedu.deviceconnect.libs.base.model.URoMainBoardInfo;
import com.ubtedu.deviceconnect.libs.base.product.URoConnectStatus;
import com.ubtedu.deviceconnect.libs.base.product.URoProduct;
import com.ubtedu.ukit.R;
import com.ubtedu.ukit.bluetooth.BluetoothHelper;
import com.ubtedu.ukit.common.files.FileHelper;
import com.ubtedu.ukit.project.UserDataSynchronizer;
import com.ubtedu.ukit.project.Workspace;
import com.ubtedu.ukit.project.bridge.MotionDesigner;
import com.ubtedu.ukit.project.vo.AppInfo;
import com.ubtedu.ukit.project.vo.ModelInfo;
import com.ubtedu.ukit.project.vo.Project;
import com.ubtedu.ukit.project.vo.ProjectFile;
import com.ubtedu.ukit.user.vo.UserData;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.functions.Function;

/**
 * @author qinicy
 * @date 2018/11/15
 */
public class ProjectHostPresenter extends ProjectHostContracts.Presenter implements URoConnectStatusChangeListener {

    private Workspace mWorkspace;
    private Project mProject;

    private boolean isFirstSave;


    public ProjectHostPresenter() {
        mWorkspace = Workspace.getInstance();
        BluetoothHelper.addConnectStatusChangeListener(this);
    }

    @Override
    public void init(Project sourceProject) {
        mProject = sourceProject;
        Project project = UserDataSynchronizer.getInstance().getProject(mProject.projectId);
        isFirstSave = project == null;
        initWorkspace();
    }

    @Override
    public void release() {
        super.release();
        mWorkspace.clear();
        BluetoothHelper.removeConnectStatusChangeListener(this);
    }


    @Override
    public boolean isFirstSave() {
        return isFirstSave;
    }

    @Override
    public boolean isWorkspaceModified() {
        return mWorkspace.isModified();
    }

    private void initWorkspace() {
        if (mWorkspace != null && mProject != null) {
            //产品需求，打开新项目时如果不是同一个工程，需要断开蓝牙连接
            if (BluetoothHelper.isBluetoothConnected()) {
                if (!mWorkspace.isOpenSameProject(mProject.projectId)) {
                    BluetoothHelper.disconnect();
                } else {
                    if (getView() != null) {
                        getView().showBluetoothConnectState(true);
                    }
                }
            }
            mWorkspace.load(mProject).subscribe(new SimpleRxSubscriber<Project>() {
                @Override
                public void onNext(Project project) {
                    super.onNext(project);
                    if (project != null && getView() != null) {
                        getView().onWorkspaceInitComplete(project);
                    }

                    if (BluetoothHelper.isBluetoothConnected()) {
                        //产品需求：如果蓝牙处于连接状态，则需要以当前外设信息更新到连线图中
                        URoMainBoardInfo bia = BluetoothHelper.getBoardInfoData();
                        ModelInfo modelInfo = ModelInfo.newInstance(bia);
                        if (mProject.modelInfo != null) {
                            ModelInfo newModelInfo = ModelInfo.newInstance(mProject.modelInfo);
                            ModelInfo.mergeModelInfo(modelInfo, newModelInfo);
                            if (!newModelInfo.isSameContent(mProject.modelInfo)) {
                                mWorkspace.saveProjectFile(newModelInfo);
                            }
                        } else {
                            mWorkspace.saveProjectFile(modelInfo);
                        }
                    }
                }
            });
        }

        //需要重置动作设计充电保护弹窗的标识
        MotionDesigner.getInstance().resetShowChargingProtectionFlag();
    }


    @Override
    public Workspace getWorkspace() {
        return mWorkspace;
    }

    @Override
    public synchronized void saveProjectFile(ProjectFile projectFile) {
        if (mWorkspace != null && projectFile != null) {
            mWorkspace.saveProjectFile(projectFile);
        }
    }

    @Override
    public void removeProjectFile(ProjectFile projectFile) {
        if (mWorkspace != null) {
            mWorkspace.removeProjectFile(projectFile);
        }
    }

    @Override
    public void renameProjectFile(ProjectFile projectFile, String newName) {
        if (mWorkspace != null) {
            mWorkspace.renameProjectFile(projectFile, newName);
        }
    }

    @Override
    public void saveProject(String projectName, String imagePath) {

        if (isFirstSave() && !TextUtils.isEmpty(projectName)) {
            if (UserDataSynchronizer.getInstance().isProjectNameDuplicate(projectName)) {
                if (getView() != null) {
                    getView().showProjectNameDuplicateMessage();
                }

                return;
            }
            mWorkspace.updateProjectName(projectName);
            mWorkspace.updateProjectImage(imagePath);
        }

        if (!mWorkspace.isNeedSave()) {
            if (getView() != null) {
                getView().getUIDelegate().toastShort(mContext.getString(R.string.project_save_no_need));
            }
            return;
        }
        mWorkspace.save()

                .flatMap(new Function<Project, ObservableSource<Project>>() {

                    @Override
                    public ObservableSource<Project> apply(final Project project) throws Exception {

                        return UserDataSynchronizer.getInstance().sync(false).map(new Function<UserData, Project>() {
                            @Override
                            public Project apply(UserData data) throws Exception {
                                return project;
                            }
                        });
                    }
                })
                .subscribe(new SimpleRxSubscriber<Project>() {

                    @Override
                    public void onComplete() {
                        super.onComplete();
                        LogUtil.i("save  complete!");
                        isFirstSave = false;
                        //不需要等同步完成才提示保存成功
                        if (getView() != null) {
                            getView().showSaveResultMessage(true);
                        }
                    }

                    @Override
                    public void onError(RxException e) {
                        super.onError(e);
                        if (getView() != null) {
                            getView().showSaveResultMessage(false);
                        }
                    }
                });
    }

    @Override
    public Observable<String> getProjectShareFile() {
        final String sourceProjectDir = mProject.getProjectPath();
        final String projectUkt = FileHelper.getUktCacheAbsolutePath() +
                File.separator + mProject.projectId + FileHelper.UKT_FILE_SUFFIX;

        //LogUtil.d("sourceProjectDir = " + sourceProjectDir);
        return FileHelper.zipFiles(projectUkt, sourceProjectDir);
    }

    @Override
    public List<AppInfo> getShareAppInfoList(Uri shareFileUri) {

        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_SEND);
        intent.addCategory(Intent.CATEGORY_DEFAULT);

        LogUtil.d("shareProject path = " + shareFileUri.getPath());
        intent.putExtra(Intent.EXTRA_STREAM, shareFileUri);
        intent.setType("application/ukt");

        PackageManager packageManager = getView().getContext().getPackageManager();
        List<ResolveInfo> resolveInfoList = packageManager.queryIntentActivities(intent, 0);

        List<AppInfo> shareAppList = new ArrayList<>();
        AppInfo appInfo;

        for(int i = 0; i < resolveInfoList.size(); i++){
            //LogUtil.d("resolveInfoList = " + i + "     " + resolveInfoList.get(i).activityInfo.packageName);
            ResolveInfo resInfo = resolveInfoList.get(i);
            String packageName = resInfo.activityInfo.packageName;

            appInfo = new AppInfo();
            appInfo.setAppPackageName(packageName);
            appInfo.setAppLauncherClassName(resInfo.activityInfo.name);
            appInfo.setAppName(resInfo.loadLabel(packageManager).toString());
            appInfo.setAppIcon(resInfo.loadIcon(packageManager));
            shareAppList.add(appInfo);
        }
        return shareAppList;
    }

    @Override
    public Uri getUriByFile(File file) {
        Uri uri;
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.N){
            uri = FileProvider.getUriForFile(
                    getView().getContext(),
                    getView().getContext().getPackageName() + ".fileProvider",
                    file);
        }else {
            uri = Uri.fromFile(file);
        }
        return uri;
    }

//    @Override
//    public void onConnectStateChanged(UKitRemoteDevice device, boolean oldState, boolean newState, boolean verified) {
//        if (getView() != null) {
//            getView().showBluetoothConnectState(newState);
//        }
//    }

    @Override
    public void onConnectStatusChanged(URoProduct product, URoConnectStatus connectStatus) {
        if (getView() != null) {
            getView().showBluetoothConnectState(connectStatus == URoConnectStatus.CONNECTED);
        }
    }
}
