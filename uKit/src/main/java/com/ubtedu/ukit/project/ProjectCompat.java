package com.ubtedu.ukit.project;

import android.content.Context;

import androidx.fragment.app.FragmentManager;

import com.ubtedu.ukit.R;
import com.ubtedu.ukit.common.dialog.PromptDialogFragment;
import com.ubtedu.ukit.project.vo.Project;

/**
 * @Author qinicy
 * @Date 2019/6/11
 **/
public class ProjectCompat {
    public static void showOfficialCompatDialog(Context context, FragmentManager manager, final Runnable positiveTask) {
        if (context != null && manager != null) {
            PromptDialogFragment.newBuilder(context)
                    .title(context.getString(R.string.project_model_version_compat_title))
                    .message(context.getString(R.string.project_model_version_compat_desc))
                    .positiveButtonText(context.getString(R.string.project_model_version_compat_positive))
                    .negativeButtonText(context.getString(R.string.project_model_version_compat_negative))
                    .onPositiveClickListener(new PromptDialogFragment.OnConfirmClickListener() {
                        @Override
                        public boolean onClick() {
                            positiveTask.run();
                            return false;
                        }
                    })
                    .build()
                    .show(manager, "OfficialCompatDialog");
        }

    }
    public static void showTargetDeviceCompatDialog(Context context, FragmentManager manager, final Runnable positiveTask) {
        if (context != null && manager != null) {
            PromptDialogFragment.newBuilder(context)
                    .title(context.getString(R.string.project_target_device_compat_title))
                    .message(context.getString(R.string.project_target_device_compat_desc))
                    .positiveButtonText(context.getString(R.string.project_model_version_compat_positive))
                    .negativeButtonText(context.getString(R.string.project_model_version_compat_negative))
                    .onPositiveClickListener(new PromptDialogFragment.OnConfirmClickListener() {
                        @Override
                        public boolean onClick() {
                            positiveTask.run();
                            return false;
                        }
                    })
                    .build()
                    .show(manager, "TargetDeviceCompatDialog");
        }

    }
    public static void showProjectCompatDialog(Context context, FragmentManager manager) {
        if (context != null && manager != null) {
            PromptDialogFragment.newBuilder(context)
                    .title(context.getString(R.string.project_version_compat_title))
                    .message(context.getString(R.string.project_version_compat_desc))
                    .showNegativeButton(false)
                    .positiveButtonText(context.getString(R.string.project_version_compat_ok))

                    .build()
                    .show(manager, "ProjectCompatDialog");
        }
    }

}
