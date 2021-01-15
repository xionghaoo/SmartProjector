/**
 * @2018 UBTECH Robotics Corp. All rights reserved (C)
 **/
package com.ubtedu.ukit.project.host;

import android.net.Uri;
import com.ubtedu.alpha1x.core.mvp.BasePresenter;
import com.ubtedu.alpha1x.core.mvp.BaseUI;
import com.ubtedu.ukit.project.Workspace;
import com.ubtedu.ukit.project.vo.AppInfo;
import com.ubtedu.ukit.project.vo.Project;
import com.ubtedu.ukit.project.vo.ProjectFile;

import java.io.File;
import java.util.List;
import io.reactivex.Observable;
/**
 * @author qinicy
 * @date 2018/11/15
 */
public interface ProjectHostContracts {

    abstract class UI extends BaseUI<Presenter> {

        public abstract void onWorkspaceInitComplete(Project project);

        public abstract void showSaveResultMessage(boolean isSuccess);

        public abstract void showProjectNameDuplicateMessage();



        //蓝牙状态改变，更新图标
        public abstract void showBluetoothConnectState(boolean isConnected);
    }

    abstract class Presenter extends BasePresenter<UI> {


        public abstract void init(Project sourceProject);

        public abstract boolean isFirstSave();
        public abstract boolean isWorkspaceModified();

        public abstract Workspace getWorkspace();

        public abstract void saveProjectFile(ProjectFile projectFile);

        public abstract void removeProjectFile(ProjectFile projectFile);
        public abstract void renameProjectFile(ProjectFile projectFile,String newName);

        public abstract void saveProject(String projectName, String imagePath);

        public abstract Observable<String> getProjectShareFile();

        public abstract List<AppInfo> getShareAppInfoList(Uri shareFileUri);

        public abstract Uri getUriByFile(File file);
    }
}
