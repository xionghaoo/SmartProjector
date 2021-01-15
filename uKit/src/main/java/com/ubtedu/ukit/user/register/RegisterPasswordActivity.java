/**
 * @2018 UBTECH Robotics Corp. All rights reserved (C)
 **/
package com.ubtedu.ukit.user.register;

import android.content.Context;
import android.content.Intent;
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
import android.widget.TextView;

import com.ubtedu.alpha1x.utils.KeyBoardHelper;
import com.ubtedu.base.net.rxretrofit.subscriber.SimpleRxSubscriber;
import com.ubtedu.ukit.R;
import com.ubtedu.ukit.common.view.KeyFactory;
import com.ubtedu.ukit.menu.region.RegionHelper;
import com.ubtedu.ukit.project.UserDataSynchronizer;
import com.ubtedu.ukit.user.UserActivityStack;
import com.ubtedu.ukit.user.UserBaseActivity;

import static com.ubtedu.ukit.user.register.RegisterAccountActivity.REGISTER_ACCOUNT;

/**
 * @author binghua.qin
 * @date 2019/02/23
 */
public class RegisterPasswordActivity extends UserBaseActivity<RegisterPasswordContracts.Presenter, RegisterPasswordContracts.UI> {
    private static final KeyListener PASSWORD_DIGITS = KeyFactory.createPasswordDigitsKeyListener();
    private static final KeyListener PASSWORD_VISIBLE_DIGITS = KeyFactory.createPasswordVisibleDigitsKeyListener();
    private View mBackBtn;
    private EditText mPasswordEdt;
    private String mPassword;
    private Button mSubmitBtn;
    private View mRootLayout;
    private CheckBox mPasswordEyeCheckBox;
    private RegisterAccountInfo mAccount;
    private TextView mTitleTv;
    private TextView mDescTv;

    public static void open(Context context, RegisterAccountInfo account) {
        if (context == null || account == null) {
            return;
        }
        Intent intent = new Intent(context, RegisterPasswordActivity.class);
        intent.putExtra(REGISTER_ACCOUNT, account);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        initRegisterAccount();
        super.onCreate(savedInstanceState);
        updateButtonsState();

        initIntentTypeUI();
    }

    private void initIntentTypeUI() {
        mTitleTv.setText(mAccount.isRegister() ? R.string.account_register_password_title : R.string.account_reset_password_title);
    }

    @Override
    protected void onInitViews() {
        super.onInitViews();
        setContentView(R.layout.activity_register_password);
        mBackBtn = findViewById(R.id.register_password_back_btn);
        mRootLayout = findViewById(R.id.register_password_root_lyt);
        mTitleTv = findViewById(R.id.register_password_title_tv);
        mDescTv = findViewById(R.id.register_password_desc_tv);
        mSubmitBtn = findViewById(R.id.register_password_submit_btn);
        mPasswordEdt = findViewById(R.id.register_password_edt);
        mPasswordEdt.setKeyListener(PASSWORD_DIGITS);
        mPasswordEyeCheckBox = findViewById(R.id.register_password_eye_view);
        bindSafeClickListener(mBackBtn);
        bindSafeClickListener(mRootLayout);
        bindSafeClickListener(mSubmitBtn);
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
    }

    private void initRegisterAccount() {
        mAccount = (RegisterAccountInfo) getIntent().getSerializableExtra(REGISTER_ACCOUNT);
        if (mAccount == null) {
            mAccount = new RegisterAccountInfo();
        }
        if (mAccount.locale == null) {
            mAccount.locale = RegionHelper.getDefaultRegionAccountLocale();
        }

    }

    @Override
    public void onClick(View v, boolean isSafeClick) {
        super.onClick(v, isSafeClick);
        if (v == mBackBtn) {
            finish();
        }
        if (v == mSubmitBtn) {
            onSubmit();
        }
        if (v == mRootLayout) {
            KeyBoardHelper.hideSoftKeyBoard(mRootLayout);
        }

    }

    private void onSubmit() {
        if (mAccount.isRegister()) {
            getPresenter().register(mAccount, mPassword);
        } else {
            getPresenter().resetPassword(mAccount, mPassword);
        }
    }


    private void updateButtonsState() {
        mPassword = mPasswordEdt.getText().toString();
        boolean valid = !TextUtils.isEmpty(mPassword) && mPassword.length() >= 6;
        mSubmitBtn.setEnabled(valid);
    }

    @Override
    protected RegisterPasswordContracts.Presenter createPresenter() {
        return new RegisterPasswordPresenter();
    }

    @Override
    protected RegisterPasswordContracts.UI createUIView() {
        return new RegisterPasswordUI();
    }

    class RegisterPasswordUI extends RegisterPasswordContracts.UI {
        @Override
        public void onLoginSuccess(boolean isGuest) {
            if (!isGuest) {
                getUIDelegate().toastShort(getString(R.string.account_register_success));
            }
            UserDataSynchronizer.getInstance().sync(true).subscribe(new SimpleRxSubscriber<>());
            UserActivityStack.finishAll();
        }
    }
}