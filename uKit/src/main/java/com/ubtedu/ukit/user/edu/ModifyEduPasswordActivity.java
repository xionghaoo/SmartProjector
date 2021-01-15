package com.ubtedu.ukit.user.edu;

import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.KeyListener;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;

import androidx.annotation.Nullable;

import com.ubtedu.alpha1x.utils.KeyBoardHelper;
import com.ubtedu.base.net.rxretrofit.subscriber.SimpleRxSubscriber;
import com.ubtedu.ukit.R;
import com.ubtedu.ukit.common.base.UKitBaseActivity;
import com.ubtedu.ukit.common.view.KeyFactory;
import com.ubtedu.ukit.project.UserDataSynchronizer;
import com.ubtedu.ukit.user.UserManager;
import com.ubtedu.ukit.user.login.LoginActivity;

/**
 * @Author qinicy
 * @Date 2019/11/15
 **/
public class ModifyEduPasswordActivity extends UKitBaseActivity<ModifyEduPasswordContracts.Presenter, ModifyEduPasswordContracts.UI> {
    private static final KeyListener PASSWORD_DIGITS_OLD = KeyFactory.createPasswordDigitsKeyListener();
    private static final KeyListener PASSWORD_DIGITS_NEW = KeyFactory.createPasswordDigitsKeyListener();
    private static final KeyListener PASSWORD_VISIBLE_DIGITS_OLD = KeyFactory.createPasswordVisibleDigitsKeyListener();
    private static final KeyListener PASSWORD_VISIBLE_DIGITS_NEW = KeyFactory.createPasswordVisibleDigitsKeyListener();
    private EditText mOldPasswordEdt;
    private EditText mNewPasswordEdt;
    private CheckBox mOldPasswordEyeCheckBox;
    private CheckBox mNewPasswordEyeCheckBox;
    private Button mSubmitBtn;
    private View mRootLayout;

    private String mOldPassword;
    private String mNewPassword;
    private View mBackBtn;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modify_edu_password);
        mBackBtn = findViewById(R.id.modify_edu_password_back_btn);
        bindSafeClickListener(mBackBtn);
        mRootLayout = findViewById(R.id.modify_edu_password_root_layout);
        bindSafeClickListener(mRootLayout);
        mOldPasswordEdt = findViewById(R.id.modify_edu_password_edt_old);
        mOldPasswordEdt.setKeyListener(PASSWORD_DIGITS_OLD);
        mOldPasswordEyeCheckBox = findViewById(R.id.modify_edu_password_eye_view_old);
        mOldPasswordEdt.addTextChangedListener(new TextWatcher() {
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
        mOldPasswordEyeCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    //如果选中，显示密码
                    mOldPasswordEdt.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                    mOldPasswordEdt.setKeyListener(PASSWORD_VISIBLE_DIGITS_OLD);
                    mOldPasswordEdt.setSelection(mOldPasswordEdt.getText().length());
                } else {
                    //否则隐藏密码
                    mOldPasswordEdt.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    mOldPasswordEdt.setKeyListener(PASSWORD_DIGITS_OLD);
                    mOldPasswordEdt.setSelection(mOldPasswordEdt.getText().length());
                }
            }
        });
        mNewPasswordEdt = findViewById(R.id.modify_edu_password_edt_new);
        mNewPasswordEdt.setKeyListener(PASSWORD_DIGITS_NEW);
        mNewPasswordEyeCheckBox = findViewById(R.id.modify_edu_password_eye_view_new);


        mNewPasswordEdt.addTextChangedListener(new TextWatcher() {
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
        mNewPasswordEyeCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    //如果选中，显示密码
                    mNewPasswordEdt.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                    mNewPasswordEdt.setKeyListener(PASSWORD_VISIBLE_DIGITS_NEW);
                    mNewPasswordEdt.setSelection(mNewPasswordEdt.getText().length());
                } else {
                    //否则隐藏密码
                    mNewPasswordEdt.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    mNewPasswordEdt.setKeyListener(PASSWORD_DIGITS_NEW);
                    mNewPasswordEdt.setSelection(mNewPasswordEdt.getText().length());
                }
            }
        });
        mSubmitBtn = findViewById(R.id.modify_edu_password_submit_btn);
        bindSafeClickListener(mSubmitBtn);
        updateButtonsState();
    }

    private void updateButtonsState() {
        mOldPassword = mOldPasswordEdt.getText().toString();
        mNewPassword = mNewPasswordEdt.getText().toString();
        if ( !TextUtils.isEmpty(mOldPassword) && mOldPassword.length() >= 6 && !TextUtils.isEmpty(mNewPassword) && mNewPassword.length() >= 6) {
            mSubmitBtn.setEnabled(true);
        } else {
            mSubmitBtn.setEnabled(false);
        }

        if (TextUtils.isEmpty(mOldPassword)) {
            if (mOldPasswordEyeCheckBox.getVisibility() != View.INVISIBLE) {
                mOldPasswordEyeCheckBox.setVisibility(View.INVISIBLE);
            }
        } else {
            if (mOldPasswordEyeCheckBox.getVisibility() != View.VISIBLE) {
                mOldPasswordEyeCheckBox.setVisibility(View.VISIBLE);
            }
        }
        if (TextUtils.isEmpty(mNewPassword)) {
            if (mNewPasswordEyeCheckBox.getVisibility() != View.INVISIBLE) {
                mNewPasswordEyeCheckBox.setVisibility(View.INVISIBLE);
            }
        } else {
            if (mNewPasswordEyeCheckBox.getVisibility() != View.VISIBLE) {
                mNewPasswordEyeCheckBox.setVisibility(View.VISIBLE);
            }
        }
    }

    @Override
    public void onClick(View v, boolean isSafeClick) {
        super.onClick(v, isSafeClick);
        if (v == mBackBtn){
            finish();
        }
        if (v == mRootLayout){
            KeyBoardHelper.hideSoftKeyBoard(v);
        }
        if (v == mSubmitBtn){
            getPresenter().updatePassword(mOldPassword,mNewPassword);
        }
    }

    @Override
    protected ModifyEduPasswordContracts.Presenter createPresenter() {
        return new ModifyEduPasswordPresenter();
    }

    @Override
    protected ModifyEduPasswordContracts.UI createUIView() {
        return new ModifyEduPasswordUI();
    }

    class ModifyEduPasswordUI extends ModifyEduPasswordContracts.UI {
        @Override
        public void onUpdatePasswordSuccess() {
            getUIView().getUIDelegate().toastShort(getString(R.string.account_edu_modify_password_success));
            UserDataSynchronizer.getInstance().cancel();
            UserManager.getInstance().clearLocalAccount();
            LoginActivity.open(ModifyEduPasswordActivity.this, false);
            ModifyEduPasswordActivity.this.finish();
        }
    }
}
