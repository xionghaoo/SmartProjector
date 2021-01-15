/**
 * @2018 UBTECH Robotics Corp. All rights reserved (C)
 **/
package com.ubtedu.ukit.launcher;

import android.content.Intent;
import android.os.Handler;
import android.os.Looper;

import com.ubtedu.alpha1x.utils.LogUtil;
import com.ubtedu.alpha1x.utils.SharedPreferenceUtils;
import com.ubtedu.ukit.application.AppConfigs;
import com.ubtedu.ukit.common.files.FileHelper;
import com.ubtedu.ukit.common.thread.ThreadPoolUtil;
import com.ubtedu.ukit.common.utils.AssetCopier;
import com.ubtedu.ukit.home.HomeActivity;
import com.ubtedu.ukit.user.UserConsts;
import com.ubtedu.ukit.user.UserManager;
import com.ubtedu.ukit.user.login.LoginActivity;
import com.ubtedu.ukit.user.vo.UserInfo;

import java.io.File;
import java.io.IOException;

/**
 * @Author qinicy
 * @Date 2018/11/6
 **/
public class LauncherPresenter extends LauncherContracts.Presenter {
    private LauncherActivity mActivity;
    private Handler mHandler;


    public LauncherPresenter(LauncherActivity activity) {
        mActivity = activity;
        mHandler = new Handler(Looper.getMainLooper());
    }


    public void init() {

        ThreadPoolUtil.execute(new Runnable() {
            @Override
            public void run() {
//                AppConfigs.handleConfig();
//                unzipBlockly();
            }
        });
        onInitComplete();
    }

    private void unzipBlockly() {

        LogUtil.i("start unzip blockly");
        AssetCopier copier = new AssetCopier(mContext);
        File destFile = new File(FileHelper.getPublicBlocklyPath());
        try {
            copier.copyDirectory("blockly", destFile);
            LogUtil.i("end unzip blockly:success");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void onAllPermissionsGranted() {
        init();
    }

    @Override
    public void skip() {
        UserInfo user = SharedPreferenceUtils.getInstance(getView().getContext())
                .getObjectValue(UserConsts.SP_CURRENT_LOGIN_USER, UserInfo.class);
        if (user != null) {
            UserManager.getInstance().setLoginUser(user);
            HomeActivity.open(mContext);
        } else {
            Intent intent = new Intent(getView().getContext().getApplicationContext(),
                    LoginActivity.class);
            mActivity.startActivity(intent);
        }
        getView().finishActivity();
        LogUtil.i("LauncherPresenter skip");
    }

    private void onInitComplete() {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                skip();
            }
        });
    }

}
