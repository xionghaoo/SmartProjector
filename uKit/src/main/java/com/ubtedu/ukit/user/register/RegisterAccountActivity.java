/**
 * @2018 UBTECH Robotics Corp. All rights reserved (C)
 **/
package com.ubtedu.ukit.user.register;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.KeyListener;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.ubtedu.alpha1x.core.base.fragment.OnDialogFragmentDismissListener;
import com.ubtedu.alpha1x.utils.AppUtil;
import com.ubtedu.alpha1x.utils.KeyBoardHelper;
import com.ubtedu.alpha1x.utils.LogUtil;
import com.ubtedu.alpha1x.utils.StringUtil;
import com.ubtedu.ukit.R;
import com.ubtedu.ukit.common.dialog.PromptDialogFragment;
import com.ubtedu.ukit.common.locale.LocaleSelectDialogFragment;
import com.ubtedu.ukit.common.locale.UBTLocale;
import com.ubtedu.ukit.common.view.KeyFactory;
import com.ubtedu.ukit.menu.region.RegionHelper;
import com.ubtedu.ukit.user.UserBaseActivity;
import com.ubtedu.ukit.user.UserConsts;
import com.ubtedu.ukit.user.edu.AccountTypeHelper;
import com.ubtedu.ukit.user.login.LoginActivity;
import com.ubtedu.ukit.user.vo.LoginAccountInfo;

import java.util.List;

/**
 * @author binghua.qin
 * @date 2019/02/22
 */
public class RegisterAccountActivity extends UserBaseActivity<RegisterAccountContracts.Presenter, RegisterAccountContracts.UI> {
    public static final String REGISTER_ACCOUNT = "register_account";
    private static final KeyListener PHONE_DIGITS = KeyFactory.createPhoneDigitsKeyListener();
    private static final KeyListener EMAIL_DIGITS = KeyFactory.createEmailDigitsKeyListener();
    private static final String EMAIL_SYMBOL = "@";
    private View mBackBtn;
    private EditText mAccountEdt;
    private String mNickname;
    private Button mSubmitBtn;
    private View mRootLayout;


    private List<UBTLocale> mLocaleList;
    private RegisterAccountInfo mAccount;
    private TextView mTitleTv;
    private TextView mDescTv;
    private View mRegisterTypeView;
    private TextView mCountryCodeTv;
    private Button mSwitchBtn;
    private StringBuilder mAccountBuilder;
    private Button mClearBtn;

    private AccountTypeHelper mAccountTypeHelper;

    public static void open(Context context, RegisterAccountInfo account) {
        if (context == null || account == null) {
            return;
        }
        Intent intent = new Intent(context, RegisterAccountActivity.class);
        intent.putExtra(REGISTER_ACCOUNT, account);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAccountTypeHelper = new AccountTypeHelper();
        getPresenter().loadLocaleInfos();
        getPresenter().initRegisterViewModel();
        initRegisterAccount();
        initIntentTypeUI();

        updateButtonsState();
    }

    private void initIntentTypeUI() {
        boolean isRegister = mAccount.isRegister();
        mSwitchBtn.setVisibility((isRegister || mAccount.intentType == RegisterAccountInfo.INTENT_TYPE_RESET_PASSWORD) ? View.VISIBLE : View.INVISIBLE);
        mDescTv.setText(isRegister ? R.string.account_register_account_address_desc : R.string.account_reset_password_desc);
    }

    private void initRegisterAccount() {
        mAccount = (RegisterAccountInfo) getIntent().getSerializableExtra(REGISTER_ACCOUNT);
        if (mAccount == null) {
            mAccount = new RegisterAccountInfo();
        }
        if (mAccount.locale == null) {
            mAccount.locale = RegionHelper.getDefaultRegionAccountLocale();
        }

        mAccountTypeHelper.recordAccount(mAccount.getType(), mAccount.account);
        String localeInfo = "+" + mAccount.locale.dial_code;
        if (AppUtil.isSimpleChinese(AppUtil.getAppLanguage(this))) {
            localeInfo = "+" + mAccount.locale.dial_code;
        }
        mCountryCodeTv.setText(localeInfo);
        switchLoginType(mAccount.isPhoneAccount());
    }

    @Override
    protected void onInitViews() {
        super.onInitViews();
        setContentView(R.layout.activity_register_account);
        mBackBtn = findViewById(R.id.register_account_back_btn);
        mTitleTv = findViewById(R.id.register_account_title_tv);
        mDescTv = findViewById(R.id.register_account_desc_tv);
        mRegisterTypeView = findViewById(R.id.register_account_icon_view);
        mCountryCodeTv = findViewById(R.id.register_account_country_code_tv);
        mSwitchBtn = findViewById(R.id.register_account_switch_btn);
        mRootLayout = findViewById(R.id.register_account_root_lyt);
        mSubmitBtn = findViewById(R.id.register_account_submit_btn);
        mAccountEdt = findViewById(R.id.register_account_edt);
        mClearBtn = findViewById(R.id.register_account_input_clear_btn);

        bindSafeClickListener(mBackBtn);
        bindSafeClickListener(mSwitchBtn);
        bindSafeClickListener(mCountryCodeTv);
        bindSafeClickListener(mRootLayout);
        bindSafeClickListener(mSubmitBtn);
        bindSafeClickListener(mClearBtn);


        mAccountEdt.addTextChangedListener(new TextWatcher() {
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

    private void togglePushUpContentRightLyout(boolean push) {
        int offset = push ? getResources().getDimensionPixelOffset(R.dimen.ubt_dimen_80px) : 0;
        mRootLayout.setTranslationY(-offset);
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
        if (v == mCountryCodeTv) {
            LogUtil.d("onClick mCountryCodeTv");
            showLocaleSelectDialogFragment();
        }
        if (v == mSwitchBtn) {
            switchLoginType(!mAccount.isPhoneAccount());
        }
        if (v == mClearBtn) {
            mAccountEdt.setText("");
        }
    }

    private void switchLoginType(boolean isPhone) {

        int type = isPhone ? LoginAccountInfo.LOGIN_TYPE_PHONE : LoginAccountInfo.LOGIN_TYPE_EMAIL;
        String lastAccount = mAccountTypeHelper.getLastAccount(type);
        //把当前的账号类型的账号存下来
        mAccountTypeHelper.recordAccount(mAccount.getType(),mAccount.account);
        //把之前的账号类型的账号还原到输入框
        mAccountEdt.setText(lastAccount);
        if (!TextUtils.isEmpty(lastAccount)) {
            mAccountEdt.setSelection(lastAccount.length());
        }

        mRegisterTypeView.setBackgroundResource(AccountTypeHelper.getAccountIconId(type));
        mCountryCodeTv.setVisibility(isPhone ? View.VISIBLE : View.GONE);
        mAccountEdt.setKeyListener(AccountTypeHelper.getEditTextKeyListener(type));
        mAccountEdt.setHint(AccountTypeHelper.getAccountLoginHintStringId(type));
        int length = AccountTypeHelper.getAccountMaxLength(type);
        mAccountEdt.setFilters(new InputFilter[]{new InputFilter.LengthFilter(length)});
        mSwitchBtn.setText(isPhone ? R.string.account_register_use_email : R.string.account_register_use_phone);

        mAccount.setType(type);
        mAccount.account = mAccountEdt.getText().toString().trim();
        updateButtonsState();
    }

    private void updateButtonsState() {
        mAccount.account = mAccountEdt.getText().toString().trim();
        AccountTypeHelper.handleEmailSymbol(mAccountEdt,mAccount);
        if (mAccount.account.length() > 0) {
            mClearBtn.setVisibility(View.VISIBLE);
        } else {
            mClearBtn.setVisibility(View.INVISIBLE);
        }

        boolean accountValid = AccountTypeHelper.isAccountValid(mAccount.getType(),mAccount.account);
        if (accountValid) {
            mSubmitBtn.setEnabled(true);
        } else {
            mSubmitBtn.setEnabled(false);
        }
    }


    private void onSubmit() {
        getPresenter().checkUserExists(mAccount);
    }


    private void showLocaleSelectDialogFragment() {
        LocaleSelectDialogFragment fragment = new LocaleSelectDialogFragment();
        fragment.setCancelable(false);
        fragment.setData(mLocaleList, mAccount.locale);
        fragment.show(getSupportFragmentManager(), "LocaleSelectDialogFragment");
        final boolean isChinese = AppUtil.isSimpleChinese(AppUtil.getAppLanguage(this));
        fragment.setDismissListener(new OnDialogFragmentDismissListener() {
            @Override
            public void onDismiss(Object... value) {
                togglePushUpContentRightLyout(false);
                if (value != null && value.length > 0 && value[0] instanceof UBTLocale) {
                    mAccount.locale = (UBTLocale) value[0];
                    mCountryCodeTv.setText("+" + mAccount.locale.dial_code);

                }
            }
        });
        togglePushUpContentRightLyout(true);
    }

    @Override
    protected RegisterAccountContracts.Presenter createPresenter() {
        return new RegisterAccountPresenter();
    }

    @Override
    protected RegisterAccountContracts.UI createUIView() {
        return new RegisterAccountUI();
    }

    class RegisterAccountUI extends RegisterAccountContracts.UI {
        @Override
        public void onLoadLocaleInfosFinish(List<UBTLocale> localeList) {
            mLocaleList = localeList;
        }

        @Override
        public void showAccountExistDialog() {
            PromptDialogFragment.newBuilder(RegisterAccountActivity.this)
                    .title(getString(R.string.account_register_exist_title))
                    .message(getString(R.string.account_register_exist_desc))
                    .negativeButtonText(getString(R.string.account_register_exist_negative))
                    .positiveButtonText(getString(R.string.account_register_exist_position))
                    .cancelable(false)
                    .onPositiveClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            LoginActivity.open(RegisterAccountActivity.this, mAccount, true);
                            finish();
                        }
                    })
                    .build()
                    .show(getSupportFragmentManager(), "showAccountExistDialog");
        }

        @Override
        public void toRegisterVerify() {
            RegisterVerifyActivity.open(RegisterAccountActivity.this, mAccount);
        }
    }


}