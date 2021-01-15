package com.ubtedu.ukit.bluetooth.utils;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.provider.Settings;
import android.view.View;

import androidx.fragment.app.FragmentActivity;

import com.ubtedu.alpha1x.utils.LogUtil;
import com.ubtedu.ukit.R;
import com.ubtedu.ukit.application.UKitApplication;
import com.ubtedu.ukit.common.dialog.PromptDialogFragment;
import com.ubtedu.ukit.common.ota.AppMarketUtils;

public class LocationServiceHelper {

    public static boolean isCompactLocationServiceAvailable(Context context) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true;
        }
        return isLocationEnabled(context);
    }

    public static boolean isLocationEnabled(Context context) {
        if (context == null) {
            return false;
        }
        int locationMode;
        try {
            locationMode = Settings.Secure.getInt(context.getContentResolver(), Settings.Secure.LOCATION_MODE);
        } catch (Settings.SettingNotFoundException e) {
            e.printStackTrace();
            return false;
        }
        LogUtil.i("LocationService", (locationMode != Settings.Secure.LOCATION_MODE_OFF) + "");
        return locationMode != Settings.Secure.LOCATION_MODE_OFF;
    }

    public static void openLocationService(Context context) {
        if (context == null) {
            context = UKitApplication.getInstance().getApplicationContext();
        }
        Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        if (AppMarketUtils.isIntentAvailable(context, intent)) {
            context.startActivity(intent);
        } else {
            intent = new Intent(Settings.ACTION_SETTINGS);
            if (AppMarketUtils.isIntentAvailable(context, intent)) {
                context.startActivity(intent);
            }
        }
    }

    public static void openLocationServiceDialog(FragmentActivity context) {
        if (context == null) {
            return;
        }
        PromptDialogFragment.newBuilder(context)
                .type(PromptDialogFragment.Type.NORMAL)
                .title(context.getString(R.string.bluetooth_connect_access_location_service_title))
                .message(context.getString(R.string.bluetooth_connect_access_location_service_msg))
                .positiveButtonText(context.getString(R.string.bluetooth_connect_confirm_btn))
                .onPositiveClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        LocationServiceHelper.openLocationService(context);
                    }
                })
                .showNegativeButton(false)
                .cancelable(true)
                .build()
                .show(context.getSupportFragmentManager(), "open_location_service");
    }


}