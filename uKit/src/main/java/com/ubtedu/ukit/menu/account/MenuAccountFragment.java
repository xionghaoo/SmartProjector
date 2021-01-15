/**
 * @2018 UBTECH Robotics Corp. All rights reserved (C)
 **/
package com.ubtedu.ukit.menu.account;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.request.transition.DrawableCrossFadeFactory;
import com.ubtedu.alpha1x.core.base.fragment.OnDialogFragmentDismissListener;
import com.ubtedu.alpha1x.utils.LogUtil;
import com.ubtedu.alpha1x.utils.StringUtil;
import com.ubtedu.base.net.rxretrofit.subscriber.SimpleRxSubscriber;
import com.ubtedu.ukit.R;
import com.ubtedu.ukit.application.BasicEventDelegate;
import com.ubtedu.ukit.application.UKitApplication;
import com.ubtedu.ukit.common.analysis.Events;
import com.ubtedu.ukit.common.analysis.LoginType;
import com.ubtedu.ukit.common.analysis.UBTReporter;
import com.ubtedu.ukit.common.base.UKitBaseFragment;
import com.ubtedu.ukit.common.dialog.PromptDialogFragment;
import com.ubtedu.ukit.common.dialog.PromptEditDialogFragment;
import com.ubtedu.ukit.common.image.ImageChooseActivity;
import com.ubtedu.ukit.common.image.ImageCropActivity;
import com.ubtedu.ukit.common.image.ImageCropConfigFactory;
import com.ubtedu.ukit.menu.MenuActivity;
import com.ubtedu.ukit.project.UserDataSynchronizer;
import com.ubtedu.ukit.project.bridge.api.ToastHelper;
import com.ubtedu.ukit.user.edu.AccountTypeHelper;
import com.ubtedu.ukit.user.UserManager;
import com.ubtedu.ukit.user.edu.ModifyEduPasswordActivity;
import com.ubtedu.ukit.user.login.LoginActivity;
import com.ubtedu.ukit.user.register.RegisterAccountActivity;
import com.ubtedu.ukit.user.register.RegisterAccountInfo;
import com.ubtedu.ukit.user.vo.LoginAccountInfo;
import com.ubtedu.ukit.user.vo.UserInfo;

import static com.ubtedu.ukit.common.image.ImageChooseActivity.REQUEST_CODE_CHOOSE_IMAGE;

/**
 * @Author qinicy
 * @Date 2018/12/12
 **/
public class MenuAccountFragment extends UKitBaseFragment<MenuAccountContracts.Presenter, MenuAccountContracts.UI> {

    private View mNotLoginLyt;
    private View mLoginLyt;
    private Button mLoginBtn;
    private Button mRenameBtn;
    private TextView mNickNameTv;
    private ImageView mUserHeadIv;
    private Button mLogoutBtn;
    private TextView mModifyPasswordIv;
    private TextView mDeleteIv;
    private String mAvatarPath;
    private DeleteAccountDialogFragment mDeleteAccountDialogFragment;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_menu_account, null);
        mNotLoginLyt = root.findViewById(R.id.menu_account_not_login_lyt);
        mLoginLyt = root.findViewById(R.id.menu_account_login_lyt);
        mRenameBtn = root.findViewById(R.id.menu_account_rename_btn);
        mLoginBtn = root.findViewById(R.id.menu_account_login_btn);
        mLogoutBtn = root.findViewById(R.id.menu_account_logout_btn);


        mNickNameTv = root.findViewById(R.id.menu_account_nickname_tv);
        mUserHeadIv = root.findViewById(R.id.menu_account_head_iv);
        mModifyPasswordIv = root.findViewById(R.id.menu_account_modify_password_tv);
        mDeleteIv = root.findViewById(R.id.menu_account_delete_tv);

        bindSafeClickListener(mLoginBtn);
        bindSafeClickListener(mLogoutBtn);
        bindSafeClickListener(mRenameBtn);
        bindSafeClickListener(mModifyPasswordIv);
        bindSafeClickListener(mDeleteIv);
        bindSafeClickListener(mUserHeadIv);

        updateLoginUI();

        return root;
    }

    private boolean isSessionExpiration() {
        return !UserManager.getInstance().isGuest() && !UserManager.getInstance().getCurrentUser().isLogin();
    }


    public void updateLoginUI() {
        UserInfo user = UserManager.getInstance().getCurrentUser();
        if (user.isGuest()) {
            mNotLoginLyt.setVisibility(View.VISIBLE);
            mLoginLyt.setVisibility(View.GONE);
        } else {
            mNotLoginLyt.setVisibility(View.GONE);
            mLoginLyt.setVisibility(View.VISIBLE);
            mNickNameTv.setText(user.getNickName());
            if (isSessionExpiration()) {
                mNickNameTv.append(getString(R.string.menu_tab_account_session_expiration));
                mLogoutBtn.setText(R.string.menu_tab_account_relogin);
            } else {
                mLogoutBtn.setText(R.string.menu_tab_account_logout);
            }
            loadUserAvatar();
        }
    }

    private void loadUserAvatar() {
        DrawableCrossFadeFactory fadeFactory = new DrawableCrossFadeFactory.Builder(300).setCrossFadeEnabled(true).build();
        RequestListener<Drawable> listener = new RequestListener<Drawable>() {
            @Override
            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                mUserHeadIv.post(new Runnable() {
                    @Override
                    public void run() {
                        loadDefaultAvatar();
                    }
                });
                return true;
            }

            @Override
            public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                return false;
            }
        };
        String imagePath = mAvatarPath != null ? mAvatarPath : UserManager.getInstance().getLoginUserAvatarUrl();
        //更换头像后，只加载一次这个本地头像就清空这个变量，避免切换设备更换头像后，还加载这个旧头像。BUG#44696
        mAvatarPath = null;
        if (imagePath != null) {
            Glide.with(mUserHeadIv)
                    .load(imagePath)
                    .listener(listener)
                    .transition(DrawableTransitionOptions.with(fadeFactory))
                    .apply(RequestOptions.circleCropTransform().placeholder(R.drawable.meun_account_portrait_icon))
                    .into(mUserHeadIv);

        } else {
            loadDefaultAvatar();
        }
    }

    private void loadDefaultAvatar() {
        DrawableCrossFadeFactory fadeFactory = new DrawableCrossFadeFactory.Builder(300).setCrossFadeEnabled(true).build();
        Glide.with(mUserHeadIv)
                .load(R.drawable.meun_account_portrait_icon2)
                .transition(DrawableTransitionOptions.with(fadeFactory))
                .apply(RequestOptions.circleCropTransform())
                .into(mUserHeadIv);
    }

    @Override
    public void onClick(View v, boolean isSafeClick) {
        super.onClick(v, isSafeClick);
        if (v == mLoginBtn) {
            LoginActivity.open(getContext(), true);
        }

        if (v == mRenameBtn) {
            LoginAccountInfo info = AccountTypeHelper.getLoginAccountHistory(getContext());
            if (info != null && info.isEduAccount()){
                getUIDelegate().toastShort(getString(R.string.account_edu_modify_tip_rename));
                return;
            }
            rename();
            UBTReporter.onEvent(Events.Ids.app_menu_nickname_modify_btn_click, null);
        }
        if (v == mLogoutBtn) {
            if (isSessionExpiration()) {
                toLogin();
            } else {
                loginGuest();
            }
            UBTReporter.onEvent(Events.Ids.app_menu_logout_btn_click, null);
        }
        if (v == mModifyPasswordIv) {
            RegisterAccountInfo accountInfo = new RegisterAccountInfo(AccountTypeHelper.getLoginAccountHistory(getContext()));
            if (accountInfo.isEduAccount()){
                startActivity(new Intent(getActivity(), ModifyEduPasswordActivity.class));
                return;
            }
            accountInfo.intentType = RegisterAccountInfo.INTENT_TYPE_RESET_PASSWORD;
            RegisterAccountActivity.open(getContext(), accountInfo);
            UBTReporter.onEvent(Events.Ids.app_menu_password_change_btn_click, null);
        }
        if (v == mDeleteIv) {
            LoginAccountInfo info = AccountTypeHelper.getLoginAccountHistory(getContext());
            if (info != null && info.isEduAccount()){
                getUIDelegate().toastShort(getString(R.string.account_edu_modify_tip_delete));
                return;
            }
            showDeleteAccountDialog();
            UBTReporter.onEvent(Events.Ids.app_menu_account_delete_btn_click, null);
        }
        if (v == mUserHeadIv) {
            LoginAccountInfo info = AccountTypeHelper.getLoginAccountHistory(getContext());
            if (info != null && info.isEduAccount()){
                getUIDelegate().toastShort(getString(R.string.account_edu_modify_tip_avatar));
                return;
            }
            if (getActivity() instanceof MenuActivity) {
                MenuActivity menuActivity = (MenuActivity) getActivity();
                menuActivity.showAvatarLayout(true);
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_CHOOSE_IMAGE && resultCode == Activity.RESULT_OK && data != null) {
            String mImagePath = data.getStringExtra(ImageCropActivity.IMAGE_PATH_KEY);

            LogUtil.d("imagePath:" + mImagePath);
            if (mImagePath != null) {
                updateUserAvatar(mImagePath);
            }
        }
    }

    private void updateUserAvatar(String path) {
        getPresenter().updateUserInfo(path);
    }

    public void viewAvatar() {
        UserAvatarActivity.open(getContext(), UserManager.getInstance().getLoginUserAvatarUrl());
    }

    public void modifyAvatar() {
        ImageChooseActivity.open(this, ImageCropConfigFactory.createUserAvatarCropConfig());
    }

    private void showDeleteAccountDialog() {
        if (mDeleteAccountDialogFragment != null) {
            mDeleteAccountDialogFragment = null;
        }
        mDeleteAccountDialogFragment = new DeleteAccountDialogFragment();
        mDeleteAccountDialogFragment.setCancelable(false);
        mDeleteAccountDialogFragment.setDismissListener(new OnDialogFragmentDismissListener() {
            @Override
            public void onDismiss(Object... value) {
                mDeleteAccountDialogFragment = null;
                if (value != null && value.length > 0) {
                    boolean isDeleted = (Boolean) value[0];
                    if (isDeleted) {
                        updateLoginUI();
                    }
                }

            }
        });
        mDeleteAccountDialogFragment.show(getFragmentManager(), "DeleteAccountDialogFragment");
    }

    private void rename() {
        PromptEditDialogFragment.newBuilder()
                .title(getString(R.string.account_nickname_rename_title))
                .message(UserManager.getInstance().getLoginUserNickname())
                .hint(getString(R.string.account_nickname_rename_hint))
                .maxLength(20)
                .cancelable(false)
                .onConfirmClickListener(new PromptEditDialogFragment.OnConfirmClickListener() {
                    @Override
                    public boolean confirm(String text) {
                        if (StringUtil.containsEmoji(text)) {
                            ToastHelper.toastShort(getString(R.string.account_nickname_cannot_contain_emoji));
                            return true;
                        }
                        getPresenter().rename(text);
                        return false;
                    }
                })
                .build()
                .show(getFragmentManager(), "rename_fragment");
    }
    public void dismissAccountDialogFragmentIfNeed() {
        if (mDeleteAccountDialogFragment != null &&
                (mDeleteAccountDialogFragment.isResumed() || mDeleteAccountDialogFragment.isVisible())) {
            mDeleteAccountDialogFragment.dismiss();
            mDeleteAccountDialogFragment = null;
        }
    }
    @Override
    public void onVisibilityChangedToUser(boolean isVisibleToUser) {
        super.onVisibilityChangedToUser(isVisibleToUser);
        updateLoginUI();
    }

    @Override
    protected MenuAccountContracts.Presenter createPresenter() {
        return new MenuAccountPresenter();
    }

    @Override
    protected MenuAccountContracts.UI createUIView() {
        return new MenuAccountUI();
    }

    class MenuAccountUI extends MenuAccountContracts.UI {

        @Override
        public void onRenameResult(boolean success) {
            if (success) {
                mNickNameTv.setText(UserManager.getInstance().getCurrentUser().getNickName());
                getUIDelegate().toastShort(getString(R.string.account_nickname_rename_success));
            } else {
                getUIDelegate().toastShort(getString(R.string.account_nickname_rename_fail));
            }
        }

        @Override
        public void onDeleteAccountSuccess() {

            updateLoginUI();
        }

        @Override
        public void onModifyAvatarSuccess(String avatarPath) {
            mAvatarPath = avatarPath;
            updateLoginUI();
        }

    }

    private void toLogin() {
        LoginActivity.open(getActivity(), false);
    }

    private void loginGuest() {

        UserDataSynchronizer.getInstance().cancel();
        UserManager.getInstance().clearLocalAccount();
        UserManager.getInstance().loginGuest();
        UserManager.getInstance().recordLoginAccountInfo(UserManager.getInstance().getCurrentUser(), null);
        UserDataSynchronizer.getInstance().sync(false).subscribe(new SimpleRxSubscriber<>());
        updateLoginUI();
        notifyLoginStateChanged();
    }

    private void notifyLoginStateChanged() {
        UserInfo user = UserManager.getInstance().getCurrentUser();
        if (user != null && UKitApplication.getInstance().getEventDelegate() instanceof BasicEventDelegate) {
            LoginType type = LoginType.guest;
            BasicEventDelegate delegate = (BasicEventDelegate) UKitApplication.getInstance().getEventDelegate();
            delegate.onLoginStateChange(user, type);
        }
    }
}
