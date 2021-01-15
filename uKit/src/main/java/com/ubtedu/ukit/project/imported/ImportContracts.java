/**
 * ©2018, UBTECH Robotics, Inc. All rights reserved (C)
 **/
package com.ubtedu.ukit.project.imported;

import android.net.Uri;

import com.ubtedu.alpha1x.core.mvp.BasePresenter;
import com.ubtedu.alpha1x.core.mvp.BaseUI;
import com.ubtedu.ukit.project.vo.Project;

import java.io.File;

/**
 * @Author hai.li
 * @Date 2020/05/12
 */
public interface ImportContracts {

    abstract class UI extends BaseUI<Presenter> {
        public abstract void onProjectImportSuccess(Project project);

    }

    abstract class Presenter extends BasePresenter<UI> {
        public abstract void doProjectImport(Uri uriFile);

        public abstract void doCleanImportCache();

        public abstract File getFileForUri(Uri uri);

    }
}
