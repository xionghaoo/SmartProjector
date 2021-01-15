/**
 * @2018 UBTECH Robotics Corp. All rights reserved (C)
 **/
package com.ubtedu.ukit.bluetooth.error;

import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.ubtedu.ukit.R;
import com.ubtedu.ukit.application.UKitApplication;
import com.ubtedu.ukit.bluetooth.search.BluetoothSearchActivity;
import com.ubtedu.ukit.common.analysis.Events;
import com.ubtedu.ukit.common.analysis.UBTReporter;
import com.ubtedu.ukit.common.base.UKitBaseActivity;
import com.ubtedu.ukit.common.dialog.PromptDialogFragment;
import com.ubtedu.ukit.project.bridge.api.ActivityHelper;

/**
 * @Author naOKi
 * @Date 2018-12-14
 **/
public class BluetoothCommonErrorHelper {

    private BluetoothCommonErrorHelper() {
    }

    public enum CommonError {
        BLUETOOTH_NOT_CONNECTED(R.string.bluetooth_not_connect_title, R.string.bluetooth_not_connect_msg, R.string.bluetooth_connect_text, R.string.bluetooth_search_cancel, true),
        BLUETOOTH_NOT_CONNECTED_IGNORE_UPGRADE_AND_WIFI(R.string.bluetooth_not_connect_title, R.string.bluetooth_not_connect_msg, R.string.bluetooth_connect_text, R.string.bluetooth_search_cancel, true),
        CHARGING_PROTECTION(R.string.bluetooth_charging_protection_title, R.string.bluetooth_charging_protection_msg, R.string.bluetooth_connect_confirm_btn, -1, true),
        EMPTY_POWER(R.string.bluetooth_low_power_title, R.string.bluetooth_low_power_msg, R.string.bluetooth_connect_confirm_btn, -1, true),
        LOW_POWER(R.string.bluetooth_low_power_title, R.string.bluetooth_low_power_diconnect_msg, R.string.bluetooth_connect_confirm_btn, -1, true),
        MODE_CONFLICT(R.string.bluetooth_steering_gear_mode_title, -1, R.string.bluetooth_connect_confirm_btn, -1, true),
        ANGLE_ANOMALY(R.string.bluetooth_angle_anomaly_title, -1, R.string.bluetooth_connect_confirm_btn, -1, true),
        MOTION_DELETE(R.string.bluetooth_motion_delete_title, R.string.bluetooth_motion_delete_msg, R.string.bluetooth_connect_confirm_btn, -1, true),
        JOYSTICK_UNSET(R.string.bluetooth_joystick_unsetup_title, R.string.bluetooth_joystick_unsetup_msg, R.string.bluetooth_connect_confirm_btn, -1, true),
        PLAY_RECORDING_ERROR(R.string.recording_not_exist_dialog_title, -1, R.string.recording_not_exist_dialog_confirm_btn_text, -1, true);

        private int titleRes;
        private int msgRes;
        private int positiveRes;
        private int negativeRes;
        private boolean cancelable;

        CommonError(int titleRes, int msgRes, int positiveRes, int negativeRes, boolean cancelable) {
            this.titleRes = titleRes;
            this.msgRes = msgRes;
            this.positiveRes = positiveRes;
            this.negativeRes = negativeRes;
            this.cancelable = cancelable;
        }
    }

    private static final Handler handler = new Handler(Looper.getMainLooper());

    private static String getDisplayString(String value, int fallbackResId) {
        if (value != null) {
            return value;
        }
        if (fallbackResId != -1) {
            return UKitApplication.getInstance().getString(fallbackResId);
        }
        return null;
    }

    public static void openBluetoothCommonErrorActivity(CommonError type) {
        openBluetoothCommonErrorActivity(type, null, null, null, null);
    }

    public static void openBluetoothCommonErrorActivity(final CommonError error, String title, String msg, String positive, String negative) {
        if (error == null || ActivityHelper.getResumeActivity() == null) {
            return;
        }
        final ErrorInfo errorInfo = new ErrorInfo();
        errorInfo.title = getDisplayString(title, error.titleRes);
        errorInfo.message = getDisplayString(msg, error.msgRes);
        errorInfo.positive = getDisplayString(positive, error.positiveRes);
        errorInfo.negative = getDisplayString(negative, error.negativeRes);
        errorInfo.error = error;
        errorInfo.cancelable = error.cancelable;
        handler.post(new Runnable() {
            @Override
            public void run() {
                showErrorDialog(errorInfo);
            }
        });
    }

    private static void showErrorDialog(ErrorInfo errorInfo) {
        UKitBaseActivity activity = ActivityHelper.getResumeActivity();
        if (activity == null) {
            return;
        }
        PromptDialogFragment fragment = createErrorDialog(activity, errorInfo);
        Fragment history = activity.getSupportFragmentManager().findFragmentByTag(errorInfo.title);
        if (history != null) {
            activity.getSupportFragmentManager().beginTransaction().remove(history).commitAllowingStateLoss();
        }
        fragment.show(activity.getSupportFragmentManager(), errorInfo.title);


        if (errorInfo.error == CommonError.MODE_CONFLICT) {
            reportModeConflictEvent();
        }
    }

    private static void reportModeConflictEvent() {
        UBTReporter.onEvent(Events.Ids.app_servo_mode_conflict, null);
    }

    private static PromptDialogFragment createErrorDialog(final AppCompatActivity activity, final ErrorInfo error) {
        return PromptDialogFragment.newBuilder(activity)
                .type(PromptDialogFragment.Type.NORMAL)
                .title(error.title)
                .message(error.message)
                .cancelable(error.cancelable)
                .showPositiveButton(!TextUtils.isEmpty(error.positive))
                .positiveButtonText(error.positive)
                .onPositiveClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (CommonError.BLUETOOTH_NOT_CONNECTED.equals(error.error)) {
                            Intent intent = new Intent(activity, BluetoothSearchActivity.class);
                            activity.startActivity(intent);
                        } else if (CommonError.BLUETOOTH_NOT_CONNECTED_IGNORE_UPGRADE_AND_WIFI.equals(error.error)) {
                            Intent intent = new Intent(activity, BluetoothSearchActivity.class);
                            intent.putExtra(BluetoothSearchActivity.EXTRA_SHOW_UPGRADE_IDENTITY, false);
                            intent.putExtra(BluetoothSearchActivity.EXTRA_SHOW_WIFI_CONFIG_IDENTITY, false);
                            activity.startActivity(intent);
                        }
                    }
                })
                .showNegativeButton(!TextUtils.isEmpty(error.negative))
                .negativeButtonText(error.negative)
                .build();
    }

    private static class ErrorInfo {
        CommonError error;
        String title;
        String message;
        String positive;
        String negative;
        boolean cancelable;
    }

}
