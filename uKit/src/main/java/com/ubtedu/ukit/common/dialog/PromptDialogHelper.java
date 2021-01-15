/**
 * @2018 UBTECH Robotics Corp. All rights reserved (C)
 **/
package com.ubtedu.ukit.common.dialog;

import android.content.DialogInterface;
import android.os.Handler;
import android.os.Looper;
import android.view.View;

import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import com.ubtedu.base.net.rxretrofit.subscriber.SimpleRxSubscriber;
import com.ubtedu.ukit.R;
import com.ubtedu.ukit.application.BasicEventDelegate;
import com.ubtedu.ukit.application.UKitApplication;
import com.ubtedu.ukit.common.analysis.LoginType;
import com.ubtedu.ukit.common.base.UKitBaseActivity;
import com.ubtedu.ukit.home.HomeActivity;
import com.ubtedu.ukit.menu.MenuActivity;
import com.ubtedu.ukit.project.UserDataSynchronizer;
import com.ubtedu.ukit.project.bridge.api.ActivityHelper;
import com.ubtedu.ukit.user.UserManager;
import com.ubtedu.ukit.user.edu.ModifyEduPasswordActivity;
import com.ubtedu.ukit.user.login.LoginActivity;
import com.ubtedu.ukit.user.vo.UserInfo;

import java.lang.ref.WeakReference;

/**
 * @Author qinicy
 * @Date 2019/11/28
 **/
public class PromptDialogHelper {
    private static final Handler handler = new Handler(Looper.getMainLooper());
    private static WeakReference<PromptDialogFragment> sOverdueDialogWef;

    public static void show(final DialogFragment dialog, final String tag) {

        final UKitBaseActivity activity = ActivityHelper.getResumeActivity();
        if (activity == null || dialog == null || tag == null) {
            return;
        }
        handler.post(() -> {
            Fragment history = activity.getSupportFragmentManager().findFragmentByTag(tag);
            if (history != null) {
                activity.getSupportFragmentManager().beginTransaction().remove(history).commitAllowingStateLoss();
            }
            dialog.show(activity.getSupportFragmentManager(), tag);
        });
    }


    public static void showLoginOverdueDialog() {
        //避免重复弹窗
        if (sOverdueDialogWef != null && sOverdueDialogWef.get() != null) {
            return;
        }
        final UKitBaseActivity activity = ActivityHelper.getResumeActivity();
        if (activity == null) {
            return;
        }

        //如果在项目里，不弹登录失效的弹窗
        if (activity instanceof HomeActivity) {
            HomeActivity home = (HomeActivity) activity;
            if (home.inProject()) {
                return;
            }
        }
        if (activity instanceof MenuActivity) {
            MenuActivity menuActivity = (MenuActivity) activity;
            if (menuActivity.getMenuAccountFragment() != null) {
                menuActivity.getMenuAccountFragment().dismissAccountDialogFragmentIfNeed();
            }
        }
        PromptDialogFragment dialog = PromptDialogFragment.newBuilder(activity)
                .type(PromptDialogFragment.Type.NORMAL)
                .title(activity.getString(R.string.account_login_overdue_title))
                .message(activity.getString(R.string.account_login_overdue_desc))
                .negativeButtonText(activity.getString(R.string.account_login_overdue_guest))
                .positiveButtonText(activity.getString(R.string.account_login_overdue_login))
                .onDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        sOverdueDialogWef.clear();
                        sOverdueDialogWef = null;
                        if (activity instanceof ModifyEduPasswordActivity) {
                            activity.finish();
                        }
                    }
                })
                .onNegativeClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        UserDataSynchronizer.getInstance().cancel();
                        UserManager.getInstance().clearLocalAccount();
                        UserManager.getInstance().loginGuest();
                        UserManager.getInstance().recordLoginAccountInfo(UserManager.getInstance().getCurrentUser(), null);
                        UserDataSynchronizer.getInstance().sync(false).subscribe(new SimpleRxSubscriber<>());
                        if (activity instanceof MenuActivity) {
                            MenuActivity menuActivity = (MenuActivity) activity;
                            if (menuActivity.getMenuAccountFragment() != null) {
                                menuActivity.getMenuAccountFragment().updateLoginUI();
                            }
                        }

                        notifyLoginStateChanged();
                    }
                })
                .onPositiveClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        LoginActivity.open(activity, false);
                    }
                })
                .cancelable(false)
                .build();
        sOverdueDialogWef = new WeakReference<>(dialog);
        show(dialog, "login_overdue");
    }

    private static void notifyLoginStateChanged() {
        UserInfo user = UserManager.getInstance().getCurrentUser();
        if (user != null && UKitApplication.getInstance().getEventDelegate() instanceof BasicEventDelegate) {
            LoginType type = LoginType.guest;
            BasicEventDelegate delegate = (BasicEventDelegate) UKitApplication.getInstance().getEventDelegate();
            delegate.onLoginStateChange(user, type);
        }
    }
}
