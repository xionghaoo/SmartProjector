/**
 * @2018 UBTECH Robotics Corp. All rights reserved (C)
 **/
package com.ubtedu.ukit.menu.account;

import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.KeyListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.ubtedu.alpha1x.utils.KeyBoardHelper;
import com.ubtedu.alpha1x.utils.MD5Util;
import com.ubtedu.base.net.rxretrofit.exception.RxException;
import com.ubtedu.base.net.rxretrofit.mode.ApiResult;
import com.ubtedu.base.net.rxretrofit.subscriber.SimpleRxSubscriber;
import com.ubtedu.ukit.R;
import com.ubtedu.ukit.application.BasicEventDelegate;
import com.ubtedu.ukit.application.UKitApplication;
import com.ubtedu.ukit.common.analysis.LoginType;
import com.ubtedu.ukit.common.base.UKitBaseDialogFragment;
import com.ubtedu.ukit.common.exception.ExceptionHelper;
import com.ubtedu.ukit.common.exception.ResultCode;
import com.ubtedu.ukit.common.view.KeyFactory;
import com.ubtedu.ukit.project.UserDataSynchronizer;
import com.ubtedu.ukit.user.UserManager;
import com.ubtedu.ukit.user.vo.UserInfo;
import com.ubtedu.ukit.user.vo.response.EmptyResponse;

import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;

/**
 * @Author qinicy
 * @Date 2019/2/25
 **/
public class DeleteAccountDialogFragment extends UKitBaseDialogFragment {
    private static final KeyListener PASSWORD_DIGITS = KeyFactory.createPasswordDigitsKeyListener();
    private static final KeyListener PASSWORD_VISIBLE_DIGITS = KeyFactory.createPasswordVisibleDigitsKeyListener();
    private View mDeleteBtn;
    private EditText mPasswordEdt;
    private Button mCancelBtn;
    private String mPassword;
    private CompositeDisposable mDisposables;
    private View mContentView;
    private boolean isInvalidPassword;
    private boolean isDeleteSuccess;
    private CheckBox mPasswordEyeCheckBox;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mDisposables = new CompositeDisposable();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.dialog_fragment_delete_account, null);
        if (isCancelable()) {
            super.mRootView = root.findViewById(R.id.dialog_fragment_root_view);
        }
        mContentView = root.findViewById(R.id.account_delete_content_lyt);
        mPasswordEdt = root.findViewById(R.id.account_delete_password_edt);
        mPasswordEdt.setKeyListener(PASSWORD_DIGITS);
        mPasswordEyeCheckBox = root.findViewById(R.id.account_delete_password_eye_view);
        mDeleteBtn = root.findViewById(R.id.account_delete_delete_btn);
        mCancelBtn = root.findViewById(R.id.account_delete_cancel_btn);

        bindSafeClickListener(mContentView);
        bindSafeClickListener(mDeleteBtn);
        bindSafeClickListener(mCancelBtn);
        bindSafeClickListener(mPasswordEyeCheckBox);

        mPasswordEdt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                updateButtonsState();
            }
        });
        mPasswordEyeCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    //如果选中，显示密码
                    mPasswordEdt.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                    mPasswordEdt.setKeyListener(PASSWORD_VISIBLE_DIGITS);
                    mPasswordEdt.setSelection(mPasswordEdt.getText().length());
                } else {
                    //否则隐藏密码
                    mPasswordEdt.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    mPasswordEdt.setKeyListener(PASSWORD_DIGITS);
                    mPasswordEdt.setSelection(mPasswordEdt.getText().length());
                }
            }
        });
        updateButtonsState();
        return root;
    }

    @Override
    public void onClick(View v, boolean isSafeClick) {
        super.onClick(v, isSafeClick);

        if (v == mCancelBtn) {
            isDeleteSuccess = false;
            dismiss();
        }
        if (v == mDeleteBtn) {
            deleteAccount();
        }
        if (v == mContentView) {
            KeyBoardHelper.hideSoftKeyBoard(mContentView);
        }

    }

    @Override
    public void dismiss() {
        super.dismiss();
        if (mDismissListener != null) {
            mDismissListener.onDismiss(isDeleteSuccess);
        }
    }

    private void deleteAccount() {

        //避免一直访问网络
        if (isInvalidPassword){
            getUIDelegate().toastShort(getString(R.string.account_error_password));
            return;
        }
        String password = mPassword;
        getUIDelegate().showLoading(false);
        password = MD5Util.encodeByMD5(password);
        UserManager.getInstance().deleteUser(password)
                .subscribe(new SimpleRxSubscriber<ApiResult<EmptyResponse>>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        super.onSubscribe(d);
                        mDisposables.add(d);
                    }

                    @Override
                    public void onNext(ApiResult<EmptyResponse> result) {
                        super.onNext(result);
                        getUIDelegate().hideLoading();
                        if (result != null) {
                            if (result.code == 0) {
                                getUIDelegate().toastShort(getString(R.string.account_delete_success));
                                onDeleteAccountSuccess();
                            } else {
                                isInvalidPassword = result.code == ResultCode.PASSWD_ERROR;
                                boolean eat = ExceptionHelper.handleException(result.code, "");
                                if (!eat) {
                                    getUIDelegate().toastShort(getString(R.string.account_delete_fail));
                                }
                            }

                        } else {
                            getUIDelegate().toastShort(getString(R.string.account_delete_fail));
                        }
                    }

                    @Override
                    public void onError(RxException e) {
                        super.onError(e);
                        getUIDelegate().hideLoading();

                        if (e.getCode() == ResultCode.PASSWD_ERROR) {
                            getUIDelegate().toastShort(getString(R.string.account_error_password));
                            return;
                        }
                        boolean eat = ExceptionHelper.handleException(e.getCode(), e.getMessage());
                        if (!eat) {
                            getUIDelegate().toastShort(getString(R.string.account_delete_fail));
                        }

                    }
                });

    }

    private void onDeleteAccountSuccess() {
        UserManager.getInstance().clearLocalAccount();
        UserManager.getInstance().loginGuest();
        UserManager.getInstance().recordLoginAccountInfo(UserManager.getInstance().getCurrentUser(),null);
        UserDataSynchronizer.getInstance().sync(false).subscribe(new SimpleRxSubscriber<>());

        isDeleteSuccess = true;
        dismiss();
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

    private void updateButtonsState() {
        String password = mPasswordEdt.getText().toString();
        if (isInvalidPassword && !password.equals(mPassword)){
            isInvalidPassword = false;
        }
        mPassword = password;
        boolean valid = !TextUtils.isEmpty(mPassword) && mPassword.length() >= 6;
        mDeleteBtn.setEnabled(valid);
    }


}
