/**
 * @2018 UBTECH Robotics Corp. All rights reserved (C)
 **/
package com.ubtedu.ukit.project;

import android.text.TextUtils;

import androidx.annotation.NonNull;

/**
 * @Author duanxinning
 * @Date 2019/7/3
 */
public class ProjectEditDialogFragment extends ProjectSaveDialogFragment {
    private String mOriginalProjectName;
    private String mOriginalImagePath;


    @Override
    protected boolean isButtonEnable() {
        if (TextUtils.isEmpty(mProjectName)) {
            return false;
        }
        //项目名称有改动，按钮可用
        if (!mProjectName.equals(mOriginalProjectName)) {
            return true;
        }
        //当前设置了封面,不与原始封面相同则按钮可用
        if (!TextUtils.isEmpty(mImagePath)) {
            return !mImagePath.equals(mOriginalImagePath);
        }
        //当前没有设置封面,原始封面不为空，说明封面有改动则按钮可用
        return !TextUtils.isEmpty(mOriginalImagePath);
    }

    @Override
    protected boolean isDuplicate() {
        return UserDataSynchronizer.getInstance().isProjectNameDuplicate(mProjectName, mOriginalProjectName);
    }

    public static ProjectEditDialogFragment newInstance(String title, @NonNull String projectName, String imagePath) {
        ProjectEditDialogFragment editDialogFragment = new ProjectEditDialogFragment();
        editDialogFragment.mProjectName = projectName;
        editDialogFragment.mImagePath = imagePath;
        editDialogFragment.mOriginalProjectName = projectName;
        editDialogFragment.mOriginalImagePath = imagePath;
        editDialogFragment.mTitle = title;
        return editDialogFragment;
    }
}
