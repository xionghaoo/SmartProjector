/**
 * @2018 UBTECH Robotics Corp. All rights reserved (C)
 **/
package com.ubtedu.ukit.project;

import android.text.TextUtils;

import androidx.annotation.Nullable;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.Observer;
import androidx.lifecycle.OnLifecycleEvent;

import com.ubtedu.alpha1x.utils.LogUtil;
import com.ubtedu.base.net.rxretrofit.subscriber.SimpleRxSubscriber;
import com.ubtedu.ukit.R;
import com.ubtedu.ukit.menu.settings.Settings;
import com.ubtedu.ukit.project.vo.Project;
import com.ubtedu.ukit.user.vo.UserData;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.functions.Function;

/**
 * @author qinicy
 * @date 2018/11/26
 */
public class ProjectPresenter extends ProjectContracts.Presenter {

    private UserDataViewModel mUserDataViewModel;

    private Workspace mWorkspace;


    public ProjectPresenter(Workspace workspace) {
        mWorkspace = workspace;
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    @Override
    public void onCreate() {
        super.onCreate();

        mUserDataViewModel = createViewModel(UserDataViewModel.class);
        mUserDataViewModel.initData();
        mUserDataViewModel.getProjectsData().observe(mLifecycleOwner, new Observer<List<Project>>() {
            @Override
            public void onChanged(@Nullable List<Project> projects) {
                if (projects != null) {
                    List<Project> compatProjects = new ArrayList<>();
                    for (int i = 0; i < projects.size(); i++) {
                        Project p = projects.get(i);
                        if (Settings.isTargetDevice(p.getTargetDevice())){
                            compatProjects.add(p);
                        }
                    }
                    projects = compatProjects;
                    //IllegalArgumentException:Comparison method violates its general contract!
                    // 1. 自反性，compare(x, y) = - compare(y, x)
                    // 2. 传递性，如果compare(x, y) > 0, compare(y, z) > 0, 则必须保证compare(x, z) > 0
                    // 3. 对称性, 如果compare(x, y) == 0, 则必须保证compare(x, z) == compare(y, z)
                    //---------------------
                    Comparator<Project> mReverseTimeComparator = new Comparator<Project>() {
                        @Override
                        public int compare(Project o1, Project o2) {
                            long result = o2.localModifyTime - o1.localModifyTime;
                            if (result < 0) {
                                return -1;
                            } else if (result > 0) {
                                return 1;
                            } else {
                                return 0;
                            }
                        }
                    };
                    Collections.sort(projects, mReverseTimeComparator);
                    if (getView() != null) {
                        getView().updateProjects(projects);
                    }
                }
            }
        });
        mUserDataViewModel.getSyncEventData().observe(mLifecycleOwner, new Observer<DataSyncEvent>() {
            @Override
            public void onChanged(@Nullable DataSyncEvent event) {
                if (event != null) {
                    LogUtil.d("DataSyncEvent2:" + event.getStringEvent());
                    String name = event.getStringEvent();
                    if (event.event == DataSyncEvent.SYNC_EVENT_SYNC_START) {
                        getView().onSyncStart(event.isOnlineSync);
                    }
                    if (event.event == DataSyncEvent.SYNC_EVENT_SYNCING) {
                        getView().dismissLoadingView();
                    }
                    if (event.event == DataSyncEvent.SYNC_EVENT_SYNC_END) {
                        String error = event.syncErrorMessage;
                        if (error == null) {
                            error = mContext.getString(R.string.project_sync_fail_tip);
                        }
                        getView().onSyncEnd(event.isOnlineSync, event.isSyncSuccess, error);
                    }
                    if (event.event == DataSyncEvent.SYNC_EVENT_SYNC_CANCEL) {
                        getView().onSyncCancel(event.isOnlineSync);
                    }
                    LogUtil.d("onChanged DataSyncEvent:" + name + " isOnlineSync:" + event.isOnlineSync);
                }
            }
        });
    }

    @Override
    public void syncProjects() {
        UserDataSynchronizer.getInstance().sync(true).subscribe(new SimpleRxSubscriber<>());
    }

    @Override
    public Observable<Project> renameProject(Project project, final String newName) {
        if (project == null || TextUtils.isEmpty(newName)) {
            return Observable.error(new Exception("Can not rename case project == null or newName is empty"));
        }
        return mWorkspace
                .load(project)
                .flatMap(new Function<Project, ObservableSource<Project>>() {
                    @Override
                    public ObservableSource<Project> apply(Project project) throws Exception {
                        return mWorkspace.updateProjectName(newName);
                    }
                })
                .flatMap(new Function<Project, ObservableSource<Project>>() {
                    @Override
                    public ObservableSource<Project> apply(Project project) throws Exception {
                        return mWorkspace.save();
                    }
                })
                .flatMap(new Function<Project, ObservableSource<Project>>() {
                    @Override
                    public ObservableSource<Project> apply(final Project project) throws Exception {
                        //fix #52329
                        Project p = UserDataSynchronizer.getInstance().getProject(project.projectId);
                        if (p != null) {
                            p.projectName = newName;
                        }
                        return UserDataSynchronizer.getInstance().sync(true)
                                .map(new Function<UserData, Project>() {
                                    @Override
                                    public Project apply(UserData data) throws Exception {
                                        return project;
                                    }
                                });
                    }
                });
    }

    @Override
    public Observable<Project> updateProject(Project project , final String newName, final String newImagePath) {
        if (project == null || TextUtils.isEmpty(newName)) {
            return Observable.error(new Exception("Can not edit case project == null or newName is empty"));
        }
        return mWorkspace
                .load(project)
                .flatMap(new Function<Project, ObservableSource<Project>>() {
                    @Override
                    public ObservableSource<Project> apply(Project project) throws Exception {
                        return mWorkspace.updateProjectName(newName);
                    }
                })
                .flatMap(new Function<Project, ObservableSource<Project>>() {
                    @Override
                    public ObservableSource<Project> apply(Project project) throws Exception {
                        return mWorkspace.updateProjectImage(newImagePath);
                    }
                })
                .flatMap(new Function<Project, ObservableSource<Project>>() {
                    @Override
                    public ObservableSource<Project> apply(Project project) throws Exception {
                        return mWorkspace.save();
                    }
                })
                .flatMap(new Function<Project, ObservableSource<Project>>() {
                    @Override
                    public ObservableSource<Project> apply(final Project project) throws Exception {
                        //fix #52329
                        Project p = UserDataSynchronizer.getInstance().getProject(project.projectId);
                        if (p != null) {
                            p.projectName = newName;
                            p.imgPath=newImagePath;
                        }
                        return UserDataSynchronizer.getInstance().sync(true)
                                .map(new Function<UserData, Project>() {
                                    @Override
                                    public Project apply(UserData data) throws Exception {
                                        return project;
                                    }
                                });
                    }
                });
    }
}
