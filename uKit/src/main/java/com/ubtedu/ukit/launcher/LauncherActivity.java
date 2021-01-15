/**
 * @2018 UBTECH Robotics Corp. All rights reserved (C)
 **/
package com.ubtedu.ukit.launcher;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.gson.Gson;
import com.ubtedu.alpha1x.core.ActivityStack;
import com.ubtedu.alpha1x.utils.LogUtil;
import com.ubtedu.ukit.R;
import com.ubtedu.ukit.application.AudioServiceContext;
import com.ubtedu.ukit.common.base.UKitBaseActivity;
import com.ubtedu.ukit.common.dialog.PromptDialogFragment;
import com.ubtedu.ukit.permission.UBTPermissionSetting;

import java.util.ArrayList;
import java.util.List;


/**
 *
 * @deprecated 不再使用，现在使用{@link LauncherDialogFragment} 作为启动页面
 * @Author qinicy
 * @Date 2018/11/6
 **/
public class LauncherActivity extends UKitBaseActivity<LauncherContracts.Presenter, LauncherContracts.UI> {
    public static final int REQUEST_PERMISSION_CODE = 38;
    private VideoView mVideoView;
    private ViewGroup mVideoViewContainerLyt;
    private boolean isPause;

    private boolean isRequestingPermission;

    @Override
    protected void onInitViews() {
        setContentView(R.layout.activity_launcher);

    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        if (intent != null && !isTaskRoot()
                && (intent.getFlags() & Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT) != 0) {
            finish();
            return;
        }
        playSplashVideo();

    }

    private void playSplashVideo() {
        mVideoViewContainerLyt = findViewById(R.id.splash_vv_container_view);
        mVideoView = findViewById(R.id.splash_vv);

        String uri = "android.resource://" + getPackageName() + "/" + R.raw.splash;
        mVideoView.setVideoURI(Uri.parse(uri));
        mVideoView.setVisibility(View.VISIBLE);


        mVideoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                requestPermissions();
//                getPresenter().onVideoPlayComplete();
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
    protected void onPause() {
        super.onPause();
        if (mVideoView != null && mVideoView.isPlaying()) {
            mVideoView.pause();
            isPause = true;
            if (!isRequestingPermission) {
                mVideoView.setBackgroundColor(Color.BLACK);
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mVideoView != null && isPause) {
            isPause = false;
            mVideoView.start();
            mVideoView.setBackgroundColor(Color.TRANSPARENT);
        }
    }

    @Override
    protected void onDestroy() {
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

    /**
     * 修复VideoView内存泄漏
     *
     * @param newBase
     */
    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(AudioServiceContext.getContext(newBase));
    }

    private void requestPermissions() {
        String[] permissions = new String[4];
        permissions[0] = Manifest.permission.WRITE_EXTERNAL_STORAGE;
        permissions[1] = Manifest.permission.ACCESS_FINE_LOCATION;
        permissions[2] = Manifest.permission.CAMERA;
        permissions[3] = Manifest.permission.RECORD_AUDIO;
        List<String> permissionList = new ArrayList<>(4);


        for (int i = 0; i < permissions.length; i++) {
            int permissionCheck = ContextCompat.checkSelfPermission(this, permissions[i]);
            if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
                permissionList.add(permissions[i]);
            }
        }
        if (permissionList.size() > 0) {
            permissions = new String[permissionList.size()];
            permissions = permissionList.toArray(permissions);
            ActivityCompat.requestPermissions(this, permissionList.toArray(permissions), REQUEST_PERMISSION_CODE);
            isRequestingPermission = true;
        } else {
            onAllPermissionsGranted();
        }
    }

    private void onAllPermissionsGranted() {
        getPresenter().onAllPermissionsGranted();
    }

    public void showDeniedDialog(List<String> permissions) {
        //优先请求用户授予可以弹窗的权限（normalDeniedPermissions），
        // 再叫用户去设置界面打开不再询问弹窗的权限（alwaysDeniedPermissions）
        List<String> alwaysDeniedPermissions = new ArrayList<>(4);
        List<String> normalDeniedPermissions = new ArrayList<>(4);
        for (int i = 0; i < permissions.size(); i++) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    permissions.get(i))) {
                normalDeniedPermissions.add(permissions.get(i));
            } else {
                alwaysDeniedPermissions.add(permissions.get(i));
            }
        }
        final boolean hasNormalDenied = normalDeniedPermissions.size() > 0;

        StringBuilder permissionsTextBuilder = new StringBuilder();
        for (int i = 0; i < permissions.size(); i++) {
            permissionsTextBuilder.append(getPermissionName(permissions.get(i))).append(",");
        }
        //去掉最后一个空格
        if (permissionsTextBuilder.length() > 0) {
            permissionsTextBuilder.deleteCharAt(permissionsTextBuilder.length() - 1);
        }

        String positiveText = getString(R.string.permission_go);
        if (hasNormalDenied) {
            positiveText = getString(R.string.permission_reapply);
        }
        String message = getString(R.string.permission_list_need, permissionsTextBuilder.toString());
        if (!hasNormalDenied) {
            message = getString(R.string.permission_rationale, permissionsTextBuilder.toString());
        }
        PromptDialogFragment.newBuilder(this)
                .type(PromptDialogFragment.Type.NORMAL)
                .title(getString(R.string.permission_title))
                .negativeButtonText(getString(R.string.permission_cancel))
                .positiveButtonText(positiveText)
                .cancelable(false)
                .message(message)
                .onPositiveClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        isRequestingPermission = true;
                        if (hasNormalDenied) {
                            requestPermissions();
                        } else {
                            UBTPermissionSetting settingService = new UBTPermissionSetting(LauncherActivity.this);
                            settingService.start(REQUEST_PERMISSION_CODE);
                        }
                    }
                })
                .onNegativeClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        LauncherActivity.this.finish();
                    }
                })
                .build()
                .show(getSupportFragmentManager(), "permission_fragment");
    }

    private String getPermissionName(String permission) {
        if (Manifest.permission.WRITE_EXTERNAL_STORAGE.equals(permission)) {
            return getString(R.string.permission_storage);
        }
        if (Manifest.permission.ACCESS_FINE_LOCATION.equals(permission)) {
            return getString(R.string.permission_location);
        }
        if (Manifest.permission.CAMERA.equals(permission)) {
            return getString(R.string.permission_camera);
        }
        if (Manifest.permission.RECORD_AUDIO.equals(permission)) {
            return getString(R.string.permission_audio);
        }
        return "";
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        LogUtil.i("requestCode:" + requestCode);
        if (requestCode == REQUEST_PERMISSION_CODE) {
            if (mVideoView != null) {
                mVideoView.stopPlayback();
                mVideoView.start();
            }

        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_PERMISSION_CODE) {
            isRequestingPermission = false;
            List<String> deniedPermissions = new ArrayList<>(4);
            for (int i = 0; i < permissions.length; i++) {
                if (PackageManager.PERMISSION_DENIED == grantResults[i]) {
                    deniedPermissions.add(permissions[i]);
                }
            }
            if (deniedPermissions.size() == 0) {
                onAllPermissionsGranted();
            } else {
                showDeniedDialog(deniedPermissions);
            }
            LogUtil.i("onRequestPermissionsResult deniedPermissions:" + new Gson().toJson(deniedPermissions));
        }
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        ActivityStack.finishAll();
    }

    @Override
    protected LauncherContracts.Presenter createPresenter() {
        return new LauncherPresenter(this);
    }

    @Override
    protected LauncherContracts.UI createUIView() {
        return new LauncherUI();
    }

    class LauncherUI extends LauncherContracts.UI {
        @Override
        public void finishActivity() {
            LauncherActivity.this.finish();
        }
    }
}
