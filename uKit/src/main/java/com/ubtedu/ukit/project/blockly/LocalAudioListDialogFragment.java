/**
 * @2018 UBTECH Robotics Corp. All rights reserved (C)
 **/
package com.ubtedu.ukit.project.blockly;

import android.Manifest;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.ubtedu.alpha1x.core.base.fragment.OnDialogFragmentDismissListener;
import com.ubtedu.ukit.application.UKitApplication;
import com.ubtedu.ukit.permission.PermissionUtil;
import com.ubtedu.ukit.project.blockly.model.AudioItem;
import com.ubtedu.ukit.project.bridge.api.ToastHelper;

import java.util.ArrayList;
import java.util.List;

import static android.content.Context.TELEPHONY_SERVICE;

/**
 * @Author naOKi
 * @Date 2019/12/17
 **/
public class LocalAudioListDialogFragment extends AudioListDialogFragment {

    private TelephonyManager mTelephonyManager;
    private AudioRecordDialogFragment mRecordDialogFragment;
    public static final int REQUEST_PERMISSION_CODE = 38;
    private static final String[] mRequiredPermissions = new String[]{Manifest.permission.RECORD_AUDIO};

    public static LocalAudioListDialogFragment newInstance(String currentAudioId) {
        LocalAudioListDialogFragment fragment = new LocalAudioListDialogFragment();
        fragment.setCancelable(false);
        fragment.mCurrentAudioId = currentAudioId;
        return fragment;
    }

    @NonNull
    @Override
    protected AudioListUI createUi() {
        return new LocalAudioListUI();
    }

    @NonNull
    @Override
    protected AudioListPresenter createPresenter(AudioListUI ui) {
        return new LocalAudioListPresenter(ui);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mTelephonyManager = (TelephonyManager) UKitApplication.getInstance().getSystemService(TELEPHONY_SERVICE);
    }

    @Override
    protected void onVisibilityChangedToUser(boolean isVisibleToUser) {
        super.onVisibilityChangedToUser(isVisibleToUser);
        if (mTelephonyManager != null) {
            int events = isVisibleToUser ? PhoneStateListener.LISTEN_CALL_STATE : PhoneStateListener.LISTEN_NONE;
            mTelephonyManager.listen(mPhoneStateListener, events);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_PERMISSION_CODE) {
            List<String> deniedPermissions = PermissionUtil.getDeniedPermissions(permissions, grantResults);
            if (permissions.length != 0 && deniedPermissions.size() == 0) {
                addNew();
            } else {
                PermissionUtil.showDeniedDialog(getActivity(), deniedPermissions, REQUEST_PERMISSION_CODE, new PermissionUtil.OnClickDeniedDialogListener() {
                    @Override
                    public void onCloseBtnClick() {
                        dismiss();
                    }
                });
            }
        }
    }

    @Override
    protected void addNew() {
        if (!PermissionUtil.isPermissionsGranted(getActivity(),mRequiredPermissions)){
            LocalAudioListDialogFragment.this.requestPermissions(mRequiredPermissions,REQUEST_PERMISSION_CODE);
            return;
        }
        super.addNew();
    }

    private PhoneStateListener mPhoneStateListener = new PhoneStateListener() {
        @Override
        public void onCallStateChanged(int state, String phoneNumber) {
            onCallPhoneStateChange(state);
        }
    };

    private void onCallPhoneStateChange(final int state) {
        if (state == TelephonyManager.CALL_STATE_RINGING) {
            Handler handler = new Handler(Looper.getMainLooper());
            handler.post(new Runnable() {
                @Override
                public void run() {
                    stopPlaying();
                    if (mRecordDialogFragment != null) {
                        mRecordDialogFragment.cancel();
                        mRecordDialogFragment.dismiss();
                    }
                }
            });
        }
    }

    public class LocalAudioListUI implements AudioListUI {

        public void updateAudioList(ArrayList<AudioItem> list) {
        }

        @Override
        public void showAudioRecordUI() {
            mRecordDialogFragment = new LocalAudioRecordDialogFragment();
            mRecordDialogFragment.setDismissListener(new OnDialogFragmentDismissListener() {
                @Override
                public void onDismiss(Object... value) {
                    hideSystemUI();
                    mRecordDialogFragment = null;
                    if (value != null && value.length > 2
                            && value[0] instanceof Boolean
                            && (value[1] == null || value[1] instanceof String)
                            && (value[2] == null || value[2] instanceof AudioItem)) {
                        Boolean result = (Boolean) value[0];
                        String errorMsg = (String) value[1];
                        AudioItem audio = (AudioItem) value[2];
                        if(result) {
                            if(audio != null) {
                                showAudioSavePromptDialog(audio);
                            }
                        } else {
                            if(!TextUtils.isEmpty(errorMsg)) {
                                ToastHelper.toastShort(errorMsg);
                            }
                        }
                    }
                }
            });
            mRecordDialogFragment.setViewCreatedCallback(new AudioRecordDialogFragment.OnViewCreatedCallback() {
                @Override
                public void onViewCreated() {
                    hideSystemUI();
                }
            });
            mRecordDialogFragment.show(getFragmentManager(), "AudioRecordDialogFragment");
        }

        @Override
        public void showAudioRecordFullUI() {

        }
    
        @Override
        public void changeUIStatus(UIStatus status) {
            LocalAudioListDialogFragment.this.changeUIStatus(status);
        }
    
        @Override
        public void dismissUI() {
            dismiss();
        }
        
    }

}
