/**
 * @2018 UBTECH Robotics Corp. All rights reserved (C)
 **/
package com.ubtedu.ukit.project.blockly;

import android.text.InputFilter;
import android.text.TextUtils;

import com.ubtedu.alpha1x.core.base.fragment.OnDialogFragmentDismissListener;
import com.ubtedu.ukit.R;
import com.ubtedu.ukit.common.view.UKitCharsInputFilter;
import com.ubtedu.ukit.project.blockly.model.AudioItem;
import com.ubtedu.ukit.project.bridge.api.ToastHelper;

import java.nio.charset.Charset;
import java.util.ArrayList;

import androidx.annotation.NonNull;

/**
 * @Author naOKi
 * @Date 2019/12/17
 **/
public class UKitAudioListDialogFragment extends AudioListDialogFragment {

    //    private TelephonyManager mTelephonyManager;
    private AudioRecordDialogFragment mRecordDialogFragment;

    public static UKitAudioListDialogFragment newInstance(String currentAudioId) {
        UKitAudioListDialogFragment fragment = new UKitAudioListDialogFragment();
        fragment.setCancelable(false);
        fragment.mCurrentAudioId = currentAudioId;
        return fragment;
    }

    @NonNull
    @Override
    protected AudioListUI createUi() {
        return new UKitAudioListUI();
    }

    @NonNull
    @Override
    protected AudioListPresenter createPresenter(AudioListUI ui) {
        return new UKitAudioListPresenter(ui);
    }

//    @Override
//    public void onCreate(@Nullable Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        mTelephonyManager = (TelephonyManager) UKitApplication.getInstance().getSystemService(TELEPHONY_SERVICE);
//    }

//    @Override
//    protected void onVisibilityChangedToUser(boolean isVisibleToUser) {
//        super.onVisibilityChangedToUser(isVisibleToUser);
//        if (mTelephonyManager != null) {
//            int events = isVisibleToUser ? PhoneStateListener.LISTEN_CALL_STATE : PhoneStateListener.LISTEN_NONE;
//            mTelephonyManager.listen(mPhoneStateListener, events);
//        }
//    }

//    private PhoneStateListener mPhoneStateListener = new PhoneStateListener() {
//        @Override
//        public void onCallStateChanged(int state, String phoneNumber) {
//            onCallPhoneStateChange(state);
//        }
//    };

    //    private void onCallPhoneStateChange(final int state) {
//        if (state == TelephonyManager.CALL_STATE_RINGING) {
//            Handler handler = new Handler(Looper.getMainLooper());
//            handler.post(new Runnable() {
//                @Override
//                public void run() {
//                    stopPlaying();
//                    if (mRecordDialogFragment != null) {
//                        mRecordDialogFragment.cancel();
//                        mRecordDialogFragment.dismiss();
//                    }
//                }
//            });
//        }
//    }
    @Override
    protected boolean checkNameLengthExceedLimit(String text) {
        if (TextUtils.isEmpty(text)) {
            return false;
        }
        int length = text.getBytes(Charset.forName("utf-8")).length;
        return length > 19;
    }

    @Override
    protected InputFilter[] getAudioNameInputFilters() {
        return new InputFilter[]{new UkitAudioNameInputFilter(20)};
    }

    public class UKitAudioListUI implements AudioListUI {

        public void updateAudioList(ArrayList<AudioItem> list) {
            UKitAudioListDialogFragment.this.updateAudioList(list);
        }

        @Override
        public void showAudioRecordUI() {
            mRecordDialogFragment = new UKitAudioRecordDialogFragment();
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
                        if (result) {
                            if (audio != null) {
                                showAudioSavePromptDialog(audio);
                            }
                        } else {
                            if (!TextUtils.isEmpty(errorMsg)) {
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
            ToastHelper.toastShort(getString(R.string.audio_record_num_reached_the_limit));
        }

        @Override
        public void changeUIStatus(UIStatus status) {
            UKitAudioListDialogFragment.this.changeUIStatus(status);
        }

        @Override
        public void dismissUI() {
            dismiss();
        }

    }

}
