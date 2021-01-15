/**
 * @2018 UBTECH Robotics Corp. All rights reserved (C)
 **/
package com.ubtedu.ukit.project;

import com.ubtedu.alpha1x.core.mvp.BasePresenter;
import com.ubtedu.alpha1x.core.mvp.BaseUI;
import com.ubtedu.ukit.project.vo.Project;

import java.util.List;

import io.reactivex.Observable;

/**
 * @author qinicy
 * @date 2018/11/26
 */
public interface ProjectContracts {

    abstract class UI extends BaseUI<Presenter> {
       public abstract void updateProjects(List<Project> projects);

        public abstract void dismissLoadingView();

        public abstract void onSyncStart(boolean isOnlineSync);
       public abstract void onSyncEnd(boolean isOnlineSync, boolean success,String errorMessage);

        public abstract void onSyncCancel(boolean isOnlineSync);
    }

    abstract class Presenter extends BasePresenter<UI> {
       public abstract void syncProjects();
       public abstract Observable<Project> renameProject(Project project , String newName);
       public abstract Observable<Project> updateProject(Project project , String newName, String imagePath);

    }
}
