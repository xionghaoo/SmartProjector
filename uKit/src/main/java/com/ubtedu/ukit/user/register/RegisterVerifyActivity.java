/**
 * @2018 UBTECH Robotics Corp. All rights reserved (C)
 **/
package com.ubtedu.ukit.user.register;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.ubtedu.alpha1x.utils.KeyBoardHelper;
import com.ubtedu.alpha1x.utils.StringUtil;
import com.ubtedu.ukit.R;
import com.ubtedu.ukit.menu.region.RegionHelper;
import com.ubtedu.ukit.user.UserBaseActivity;

import static com.ubtedu.ukit.user.register.RegisterAccountActivity.REGISTER_ACCOUNT;

/**
 * @author binghua.qin
 * @date 2019/02/23
 */
public class RegisterVerifyActivity extends UserBaseActivity<RegisterVerifyContracts.Presenter, RegisterVerifyContracts.UI> {
    private View mBackBtn;
    private EditText mVerifyCodeEdt;
    private String mVerifyCode;
    private Button mSubmitBtn;
    private View mRootLayout;
    private RegisterAccountInfo mAccount;
    private TextView mVerifyDescTv;
    private TextView mVerifySendTv;

    public static void open(Context context, RegisterAccountInfo account) {
        if (context == null || account == null) {
            return;
        }
        Intent intent = new Intent(context, RegisterVerifyActivity.class);
        intent.putExtra(REGISTER_ACCOUNT, account);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        initRegisterAccount();
        super.onCreate(savedInstanceState);
        getPresenter().initVerificationCodeTimer();
        updateButtonsState();

    }

    @Override
    protected void onInitViews() {
        super.onInitViews();
        setContentView(R.layout.activity_register_verify);
        mBackBtn = findViewById(R.id.register_verify_back_btn);
        mRootLayout = findViewById(R.id.register_verify_root_lyt);
        mVerifyDescTv = findViewById(R.id.register_verify_desc_tv);
        mVerifySendTv = findViewById(R.id.register_verify_send_tv);
        mSubmitBtn = findViewById(R.id.register_verify_submit_btn);
        mVerifyCodeEdt = findViewById(R.id.register_verify_edt);
        bindSafeClickListener(mBackBtn);
        bindSafeClickListener(mRootLayout);
        bindSafeClickListener(mSubmitBtn);
        bindSafeClickListener(mVerifySendTv);

        mVerifyDescTv.setText(getString(R.string.account_register_verify_desc, generateAccount()));


        mVerifyCodeEdt.addTextChangedListener(new TextWatcher() {
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

    }

    private String generateAccount() {
        boolean isPhone = mAccount.isPhoneAccount();
        String account = mAccount.account;
        if (account != null) {
            if (isPhone) {
                int explicitLength = 3;
                int length = account.length();
                if (length <= 3) {
                    explicitLength = 1;
                } else if (length <= 5) {
                    explicitLength = 2;
                }
                return "+" + mAccount.locale.dial_code + " " +
                        account.substring(0, explicitLength) + "*****" +
                        account.substring(account.length() - explicitLength, account.length());
            } else {
                if (account.contains("@")) {
                    String[] strs = account.split("@");
                    if (strs.length == 2) {
                        String prefix = strs[0];
                        if (strs[0].length() >= 3) {
                            prefix = strs[0].substring(0, 3);
                        } else if (strs[0].length() >= 2) {
                            prefix = strs[0].substring(0, 2);
                        } else if (strs[0].length() >= 1) {
                            prefix = strs[0].substring(0, 1);
                        }
                        String[] suffixs = strs[1].split("\\.");
                        String suffix = suffixs[suffixs.length - 1];
                        return prefix + "*****" + suffix;
                    }

                }
                return account;
            }
        }
        return "";
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
            checkVerifyCode();
        }
        if (v == mRootLayout) {
            KeyBoardHelper.hideSoftKeyBoard(mRootLayout);
        }
        if (v == mVerifySendTv) {
            getPresenter().requestVerificationCode(mAccount.isRegister());
        }
    }

    private void checkVerifyCode() {
        getPresenter().checkVerifyCode(mVerifyCode, mAccount.isRegister());
    }


    private void updateButtonsState() {
        mVerifyCode = mVerifyCodeEdt.getText().toString().trim();
        boolean valid = !TextUtils.isEmpty(mVerifyCode) && mVerifyCode.length() >= 4 && StringUtil.isNumeric(mVerifyCode);
        mSubmitBtn.setEnabled(valid);
    }


    @Override
    protected RegisterVerifyContracts.Presenter createPresenter() {
        return new RegisterVerifyPresenter(mAccount);
    }

    @Override
    protected RegisterVerifyContracts.UI createUIView() {
        return new RegisterVerifyUI();
    }

    class RegisterVerifyUI extends RegisterVerifyContracts.UI {
        @Override
        public void onSendCodeComplete() {

        }

        @Override
        public void updateVerificationCodeTime(int second) {
            if (second != 0) {
                String text = String.valueOf(second) + "s";
                mVerifySendTv.setText(text);
                mVerifySendTv.setEnabled(false);
            } else {
                mVerifySendTv.setText(R.string.account_register_verify_send);
                mVerifySendTv.setEnabled(true);
            }
        }
    }


}