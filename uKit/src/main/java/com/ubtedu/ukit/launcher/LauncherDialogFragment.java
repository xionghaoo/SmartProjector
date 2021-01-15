/**
 * @2018 UBTECH Robotics Corp. All rights reserved (C)
 **/
package com.ubtedu.ukit.launcher;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Process;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;

import com.google.gson.Gson;
import com.ubtedu.alpha1x.core.ActivityStack;
import com.ubtedu.alpha1x.core.base.fragment.OnDialogFragmentDismissListener;
import com.ubtedu.alpha1x.utils.LogUtil;
import com.ubtedu.alpha1x.utils.SharedPreferenceUtils;
import com.ubtedu.ukit.R;
import com.ubtedu.ukit.application.AppConfigs;
import com.ubtedu.ukit.application.AudioServiceContext;
import com.ubtedu.ukit.common.base.UKitBaseDialogFragment;
import com.ubtedu.ukit.common.eventbus.LoginFinishEvent;
import com.ubtedu.ukit.common.thread.ThreadPoolUtil;
import com.ubtedu.ukit.menu.about.GdprPactDialogFragment;
import com.ubtedu.ukit.permission.PermissionUtil;
import com.ubtedu.ukit.user.UserConsts;
import com.ubtedu.ukit.user.UserManager;
import com.ubtedu.ukit.user.login.LoginActivity;
import com.ubtedu.ukit.user.vo.GdprUserPactInfo;
import com.ubtedu.ukit.user.vo.UserInfo;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author qinicy
 * @Date 2019/1/4
 **/
public class LauncherDialogFragment extends UKitBaseDialogFragment {
    public static final int REQUEST_PERMISSION_CODE = 38;
    private static final String[] mRequiredPermissions = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};
    private VideoView mVideoView;
    private ViewGroup mVideoViewContainerLyt;
    private boolean isPause;

    private boolean isRequestingPermission;
    private ViewGroup mRootGroupView;

    private boolean isHomeBack;

    private GdprPactDialogFragment mPrivacyDialogFragment;
    private GdprPactDialogFragment mTermsOfUseDialogFragment;
    /**
     * 修复VideoView内存泄漏
     */
    @Override
    public void onAttach(Context context) {
        super.onAttach(AudioServiceContext.getContext(context));

    }

    @Override
    public void onDetach() {
        super.onDetach();

    }

    @Override
    public void dismiss() {
        super.dismiss();
        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onLoginFinish(LoginFinishEvent event) {
        dismiss();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mRootGroupView = (ViewGroup) inflater.inflate(R.layout.activity_launcher, null);

        initVideoView();
        return mRootGroupView;
    }


    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);

        if (mDismissListener != null) {
            mDismissListener.onDismiss();
        }
        LogUtil.i("");
    }


    private void initVideoView() {
        mVideoViewContainerLyt = mRootGroupView.findViewById(R.id.splash_vv_container_view);
        mVideoView = mRootGroupView.findViewById(R.id.splash_vv);
        String uri = "android.resource://" + getContext().getPackageName() + "/" + R.raw.splash;
        mVideoView.setVideoURI(Uri.parse(uri));


        mVideoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                int launcherGdprVersion = SharedPreferenceUtils.getInstance(getContext()).getIntValue(UserConsts.SP_GDPR_LAUNCHER_AGREE);
                if (launcherGdprVersion == UserConsts.SP_GDPR_LAUNCHER_AGREE_VERSION) {//用户已同意该版本的隐私协议和使用条款
                    //开始请求权限的流程
                    if (!isRequestingPermission) {
                        requestPermissions();
                    }
                } else {//需要显示阅读隐私条款
                    if (mTermsOfUseDialogFragment != null && mTermsOfUseDialogFragment.isVisible()) {
                        return;
                    }
                    if (mPrivacyDialogFragment != null && mPrivacyDialogFragment.isVisible()) {
                        return;
                    }
                    UserManager.getInstance().clearLocalAccount();
                    showPrivacyPolicyDialog();
                }
            }
        });


        mVideoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {

                mp.setOnInfoListener(new MediaPlayer.OnInfoListener() {
                    @Override
                    public boolean onInfo(MediaPlayer mp, int what, int extra) {
                        if (what == MediaPlayer.MEDIA_INFO_VIDEO_RENDERING_START)
                            mVideoView.setBackgroundColor(Color.TRANSPARENT);
                        return true;
                    }
                });

            }
        });

        mVideoView.start();

    }

    @Override
    protected void onVisibilityChangedToUser(boolean isVisibleToUser) {
        super.onVisibilityChangedToUser(isVisibleToUser);
        if (isVisibleToUser) {
            if (mVideoView != null && isPause) {
                isPause = false;
                mVideoView.setBackgroundColor(Color.TRANSPARENT);
                mVideoView.start();
                if (isHomeBack) {
                    isHomeBack = false;
                    mVideoView.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            requestPermissions();
                        }
                    }, 100);

                }
            }

        } else {
            if (mVideoView != null) {
                mVideoView.pause();
                isPause = true;
                if (!isRequestingPermission) {
                    mVideoView.setBackgroundColor(Color.WHITE);
                }
            }
        }
    }


    @Override
    public void onStop() {
        super.onStop();

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mVideoViewContainerLyt != null) {
            mVideoViewContainerLyt.removeAllViews();
        }
        if (mVideoView != null) {
            mVideoView.stopPlayback();
            mVideoView.setOnPreparedListener(null);
            mVideoView.setOnCompletionListener(null);
            mVideoView = null;
        }

    }


    private void requestPermissions() {
        if (PermissionUtil.isPermissionsGranted(getActivity(), mRequiredPermissions)) {
            onAllPermissionsGranted();
        } else {
            ActivityCompat.requestPermissions(getActivity(), mRequiredPermissions, REQUEST_PERMISSION_CODE);
            isRequestingPermission = true;
        }
    }

    private void onAllPermissionsGranted() {
        init();
    }

    private void onInitComplete() {
        skip();
    }

    public void skip() {
        UserInfo user = SharedPreferenceUtils.getInstance(getView().getContext())
                .getObjectValue(UserConsts.SP_CURRENT_LOGIN_USER, UserInfo.class);
        if (user == null) {
            LoginActivity.open(getContext(), false);
            //重复注册会崩溃
            if (!EventBus.getDefault().isRegistered(this)) {
                EventBus.getDefault().register(this);
            }
        } else {
            UserManager.getInstance().setLoginUser(user);
            mRootGroupView.postDelayed(new Runnable() {
                @Override
                public void run() {
                    dismiss();
                }
            }, 100);
        }

        LogUtil.i("LauncherPresenter skip");
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        LogUtil.i("requestCode:" + requestCode);
        if (requestCode == REQUEST_PERMISSION_CODE) {
            if (mVideoView != null) {
                mVideoView.stopPlayback();
                mVideoView.start();
                isRequestingPermission = false;
            }

        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        LogUtil.i("");
        if (requestCode == REQUEST_PERMISSION_CODE) {

            List<String> deniedPermissions = new ArrayList<>(4);
            for (int i = 0; i < permissions.length; i++) {
                if (PackageManager.PERMISSION_DENIED == grantResults[i]) {
                    deniedPermissions.add(permissions[i]);
                }
            }
            if (deniedPermissions.size() == 0) {
                if (permissions.length != 0) {
                    isRequestingPermission = false;
                    onAllPermissionsGranted();
                } else {
                    //在授权对话框的过程中用户按下Home建，再回来也会回调onRequestPermissionsResult，
                    // 此时permissions长度为0

                    isHomeBack = true;
                }

            } else {
                isRequestingPermission = true;
                PermissionUtil.showDeniedDialog(getActivity(), deniedPermissions, REQUEST_PERMISSION_CODE, new PermissionUtil.OnClickDeniedDialogListener() {
                    @Override
                    public void onCloseBtnClick() {
                        exitApp();
                    }
                });
            }
            LogUtil.i("onRequestPermissionsResult deniedPermissions:" + new Gson().toJson(deniedPermissions));
        }
    }

    /**
     * 使用条款
     */
    private void showTermsOfUseDialog() {
        if (mTermsOfUseDialogFragment != null && mTermsOfUseDialogFragment.isVisible()) {
            return;
        }
        mTermsOfUseDialogFragment = GdprPactDialogFragment.newInstance(
                GdprUserPactInfo.GDPR_TYPE_TERMS_OF_USE, GdprPactDialogFragment.PACT_STYLE_TYPE_NEED_HAVE_READ, getActivity().getString(R.string.gdpr_terms_of_use_summary_default), getActivity().getString(R.string.gdpr_terms_of_use_url_default));
        mTermsOfUseDialogFragment.setCancelable(false);
        mTermsOfUseDialogFragment.setDismissListener(new OnDialogFragmentDismissListener() {
            @Override
            public void onDismiss(Object... value) {
                if (value.length > 0 && (boolean) value[0] == true) {//同意
                    // 标记用户已同意该版本隐私条款
                    SharedPreferenceUtils.getInstance(getActivity()).setValue(UserConsts.SP_GDPR_LAUNCHER_AGREE, UserConsts.SP_GDPR_LAUNCHER_AGREE_VERSION);
                    //开始请求权限
                    if (!isRequestingPermission) {
                        requestPermissions();
                    }
                } else {//拒绝，退出程序
                    exitApp();
                }
                mTermsOfUseDialogFragment = null;
            }
        });
        mTermsOfUseDialogFragment.show(getFragmentManager(), "GDPR_TYPE_TERMS_OF_USE");
    }

    /**
     * 隐私协议
     */
    private void showPrivacyPolicyDialog() {
        if (mPrivacyDialogFragment != null && mPrivacyDialogFragment.isVisible()) {
            return;
        }
        mPrivacyDialogFragment = GdprPactDialogFragment.newInstance(
                GdprUserPactInfo.GDPR_TYPE_PRIVACY_POLICY, GdprPactDialogFragment.PACT_STYLE_TYPE_NEED_HAVE_READ, getActivity().getString(R.string.gdpr_privacy_policy_summary_default), getActivity().getString(R.string.gdpr_privacy_policy_url_default));
        mPrivacyDialogFragment.setCancelable(false);
        mPrivacyDialogFragment.setDismissListener(new OnDialogFragmentDismissListener() {
            @Override
            public void onDismiss(Object... value) {
                if (value.length > 0 && (boolean) value[0] == true) {//同意，弹出使用条款
                    showTermsOfUseDialog();
                } else {//拒绝，退出程序
                    exitApp();
                }
                mPrivacyDialogFragment = null;
            }
        });
        mPrivacyDialogFragment.show(getFragmentManager(), "GDPR_TYPE_PRIVACY_POLICY");
    }

    private void exitApp() {
        ActivityStack.finishAll();

        Process.killProcess(Process.myPid());
        System.exit(0);
    }
}
