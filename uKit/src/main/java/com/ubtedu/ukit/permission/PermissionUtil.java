package com.ubtedu.ukit.permission;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.view.View;

import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import com.ubtedu.ukit.R;
import com.ubtedu.ukit.application.UKitApplication;
import com.ubtedu.ukit.common.dialog.PromptDialogFragment;

import java.util.ArrayList;
import java.util.List;

public class PermissionUtil {
    public static boolean isPermissionsGranted(Activity context, String[] permissions) {
        if (context == null) {
            return false;
        }
        if (permissions == null || permissions.length == 0) {
            return true;
        }
        for (int i = 0; i < permissions.length; i++) {
            if (ActivityCompat.checkSelfPermission(context, permissions[i]) != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    public static ArrayList<String> getDeniedPermissions(String[] permissions, int[] grantResults) {
        ArrayList<String> deniedPermissions = new ArrayList<>();
        if (permissions != null && permissions.length > 0) {
            for (int i = 0; i < permissions.length; i++) {
                if (PackageManager.PERMISSION_DENIED == grantResults[i]) {
                    deniedPermissions.add(permissions[i]);
                }
            }
        }
        return deniedPermissions;
    }

    public static void requestPermissions(Activity context, String[] permissions, final int requestCode) {
        if (context == null) {
            return;
        }
        ArrayList<String> requestPermissionList = new ArrayList<>();//没有授权的权限
        for (int i = 0; i < permissions.length; i++) {
            if (ActivityCompat.checkSelfPermission(context, permissions[i]) != PackageManager.PERMISSION_GRANTED) {
                requestPermissionList.add(permissions[i]);
            }
        }
        if (requestPermissionList.size() > 0) {
            String[] permissionArray = new String[requestPermissionList.size()];
            ActivityCompat.requestPermissions(context, requestPermissionList.toArray(permissionArray), requestCode);
        }
    }

    public static PromptDialogFragment showDeniedDialog(final FragmentActivity activity, final List<String> deniedPermissions, final int requestCode, final OnClickDeniedDialogListener onClickDeniedDialogListener) {
        if (activity == null || deniedPermissions == null || deniedPermissions.size() == 0) {
            return null;
        }
        //优先请求用户授予可以弹窗的权限（deniedPermissionsShowRational），
        // 再叫用户去设置界面打开不再询问弹窗的权限（deniedPermissionsNotAskAnymore）
        List<String> deniedPermissionsNotAskAnymore = new ArrayList<>();
        List<String> deniedPermissionsShowRational = new ArrayList<>();
        for (int i = 0; i < deniedPermissions.size(); i++) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(activity,
                    deniedPermissions.get(i))) {
                deniedPermissionsShowRational.add(deniedPermissions.get(i));
            } else {
                deniedPermissionsNotAskAnymore.add(deniedPermissions.get(i));
            }
        }
        final boolean shouldShowRationalDialog = deniedPermissionsShowRational.size() > 0;

        StringBuilder permissionsTextBuilder = new StringBuilder();
        for (int i = 0; i < deniedPermissions.size(); i++) {
            permissionsTextBuilder.append(getPermissionName(deniedPermissions.get(i))).append(",");
        }
        //去掉最后一个空格
        if (permissionsTextBuilder.length() > 0) {
            permissionsTextBuilder.deleteCharAt(permissionsTextBuilder.length() - 1);
        }

        String positiveText;
        String message;
        View.OnClickListener positiveClickListener;
        if (shouldShowRationalDialog) {
            positiveText = activity.getString(R.string.permission_reapply);
            message = activity.getString(R.string.permission_list_need, permissionsTextBuilder.toString());
            final String[] requestPermission = new String[deniedPermissionsShowRational.size()];
            deniedPermissionsShowRational.toArray(requestPermission);
            positiveClickListener = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ActivityCompat.requestPermissions(activity, requestPermission, requestCode);
                }
            };
        } else {
            positiveText = activity.getString(R.string.permission_go);
            message = activity.getString(R.string.permission_rationale, permissionsTextBuilder.toString());
            positiveClickListener = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    UBTPermissionSetting settingService = new UBTPermissionSetting(activity);
                    settingService.start(requestCode);
                }
            };
        }
        PromptDialogFragment dialogFragment = PromptDialogFragment.newBuilder(activity)
                .type(PromptDialogFragment.Type.NORMAL)
                .title(activity.getString(R.string.permission_title))
                .negativeButtonText(activity.getString(R.string.permission_cancel))
                .positiveButtonText(positiveText)
                .cancelable(false)
                .message(message)
                .onPositiveClickListener(positiveClickListener)
                .onNegativeClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (onClickDeniedDialogListener != null) {
                            onClickDeniedDialogListener.onCloseBtnClick();
                        }
                    }
                })
                .build();
        dialogFragment.show(activity.getSupportFragmentManager(), "permission_fragment");
        return dialogFragment;
    }

    private static String getPermissionName(String permission) {
        Context context = UKitApplication.getInstance().getApplicationContext();
        if (Manifest.permission.WRITE_EXTERNAL_STORAGE.equals(permission)) {
            return context.getString(R.string.permission_storage);
        }
        if (Manifest.permission.ACCESS_FINE_LOCATION.equals(permission)) {
            return context.getString(R.string.permission_location);
        }
        if (Manifest.permission.CAMERA.equals(permission)) {
            return context.getString(R.string.permission_camera);
        }
        if (Manifest.permission.RECORD_AUDIO.equals(permission)) {
            return context.getString(R.string.permission_audio);
        }
        return "";
    }

    public interface OnClickDeniedDialogListener {
        void onCloseBtnClick();
    }
}