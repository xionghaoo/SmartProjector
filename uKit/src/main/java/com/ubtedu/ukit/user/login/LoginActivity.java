/**
 * @2018 UBTECH Robotics Corp. All rights reserved (C)
 **/
package com.ubtedu.ukit.user.login;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.KeyListener;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.UnderlineSpan;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CheckedTextView;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.ubtedu.alpha1x.core.base.fragment.OnDialogFragmentDismissListener;
import com.ubtedu.alpha1x.utils.AppUtil;
import com.ubtedu.alpha1x.utils.KeyBoardHelper;
import com.ubtedu.base.net.rxretrofit.subscriber.SimpleRxSubscriber;
import com.ubtedu.ukit.R;
import com.ubtedu.ukit.common.eventbus.LoginFinishEvent;
import com.ubtedu.ukit.common.locale.LocaleSelectDialogFragment;
import com.ubtedu.ukit.common.locale.UBTLocale;
import com.ubtedu.ukit.common.view.KeyFactory;
import com.ubtedu.ukit.menu.about.GdprPactDialogFragment;
import com.ubtedu.ukit.menu.region.RegionHelper;
import com.ubtedu.ukit.menu.settings.Settings;
import com.ubtedu.ukit.menu.settings.TargetDevice;
import com.ubtedu.ukit.project.UserDataSynchronizer;
import com.ubtedu.ukit.user.edu.AccountTypeHelper;
import com.ubtedu.ukit.user.UserBaseActivity;
import com.ubtedu.ukit.user.register.RegisterAccountActivity;
import com.ubtedu.ukit.user.register.RegisterAccountInfo;
import com.ubtedu.ukit.user.vo.GdprUserPactInfo;
import com.ubtedu.ukit.user.vo.LoginAccountInfo;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

/**
 * @Author qinicy
 * @Date 2019/2/21
 **/
public class LoginActivity extends UserBaseActivity<LoginContracts.Presenter, LoginContracts.UI> {
    private static final KeyListener PASSWORD_DIGITS = KeyFactory.createPasswordDigitsKeyListener();
    private static final KeyListener PASSWORD_VISIBLE_DIGITS = KeyFactory.createPasswordVisibleDigitsKeyListener();
    public static final int TEXT_COLOR_SELECTED = Color.parseColor("#171E2B");
    public static final int TEXT_COLOR_NORMAL = Color.parseColor("#555B67");
    private static final String IS_SHOW_BACK_BUTTON = "is_show_back_button";
    public static final String LOGIN_ACCOUNT = "login_account";
    private static final String EMAIL_SYMBOL = "@";
    private View mCreateBtn;
    private View mGuestBtn;
    private CheckedTextView mPhoneLoginTv;
    private CheckedTextView mEmailLoginTv;
    private CheckedTextView mEduLoginTv;
    private View mAccountIconView;
    private EditText mAccountEdt;
    private View mPasswordIconView;
    private EditText mPasswordEdt;
    private CheckBox mPasswordEyeCheckBox;
    private Button mLoginBtn;
    private Button mForgotPasswordBtn;
    private TextView mGdprTv;
    private TextView mCountryCodeTv;
    private TextView mEduAccountTipTv;

    private String mPassword;
    private View mRootLayout;
    private List<UBTLocale> mLocaleList;
    public static boolean sIsShowBackButton;
    private View mBackBtn;
    private LoginAccountInfo mAccountInfo;
    private View mContentRightLyt;
    private Button mClearBtn;
    private AccountTypeHelper mAccountTypeHelper;

    public static void open(Context context, boolean isShowBackButton) {
        open(context, null, isShowBackButton);
    }

    public static void open(Context context, LoginAccountInfo accountInfo, boolean isShowBackButton) {
        Intent intent = new Intent(context, LoginActivity.class);
        intent.putExtra(IS_SHOW_BACK_BUTTON, isShowBackButton);
        if (accountInfo != null) {
            intent.putExtra(LOGIN_ACCOUNT, accountInfo);
        }
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAccountTypeHelper = new AccountTypeHelper();
        initLoginAccountInfo();
        getPresenter().initGdprPacts();
        getPresenter().loadLocaleInfos();
        updateButtonsState();
    }

    @Override
    protected void onResume() {
        super.onResume();
        initBackButtonState();
        if (getIntent().hasExtra(LOGIN_ACCOUNT)) {
            initLoginAccountInfo();
            updateButtonsState();
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
    }

    @Override
    protected void onInitViews() {
        super.onInitViews();
        setContentView(R.layout.activity_login);
        mRootLayout = findViewById(R.id.login_root_layout);
        mContentRightLyt = findViewById(R.id.login_content_right_lyt);
        mBackBtn = findViewById(R.id.login_back_btn);
        mCreateBtn = findViewById(R.id.login_create_account_btn);
        mGuestBtn = findViewById(R.id.login_guest_btn);
        mCountryCodeTv = findViewById(R.id.login_country_code_tv);
        mEduAccountTipTv = findViewById(R.id.edu_account_login_tip);

        mPhoneLoginTv = findViewById(R.id.login_phone_tv);
        mEmailLoginTv = findViewById(R.id.login_email_tv);
        mEduLoginTv = findViewById(R.id.login_edu_tv);
        mAccountIconView = findViewById(R.id.login_account_icon_view);
        mAccountEdt = findViewById(R.id.login_account_edt);
        mPasswordIconView = findViewById(R.id.login_password_icon_view);
        mPasswordEdt = findViewById(R.id.login_password_edt);
        mPasswordEdt.setKeyListener(PASSWORD_DIGITS);
        mPasswordEyeCheckBox = findViewById(R.id.login_password_eye_view);
        mLoginBtn = findViewById(R.id.login_submit_btn);
        mForgotPasswordBtn = findViewById(R.id.login_forgot_password_btn);
        mGdprTv = findViewById(R.id.login_gdpr_tv);


        mGdprTv.setText(createSpannableStringBuilder());
        mGdprTv.setMovementMethod(LinkMovementMethod.getInstance());
        mGdprTv.setHighlightColor(getResources().getColor(R.color.colorTransparent));

        mClearBtn = findViewById(R.id.login_account_input_clear_btn);

        bindClickListener(mBackBtn);
        bindClickListener(mRootLayout);
        bindSafeClickListener(mCreateBtn);
        bindSafeClickListener(mGuestBtn);
        bindSafeClickListener(mPhoneLoginTv);
        bindSafeClickListener(mEmailLoginTv);
        bindSafeClickListener(mEduLoginTv);
        bindSafeClickListener(mCountryCodeTv);
        bindSafeClickListener(mLoginBtn);
        bindSafeClickListener(mForgotPasswordBtn);
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

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @NonNull
    private SpannableStringBuilder createSpannableStringBuilder() {
        SpannableStringBuilder builder = new SpannableStringBuilder();
        SpannableString contentSpan = new SpannableString(getString(R.string.account_gdpr_see));
        builder.append(contentSpan);
        String and = getString(R.string.account_gdpr_and);
        String privacyPolicy = getString(R.string.gdpr_privacy_policy);
        String termsOfUse = getString(R.string.gdpr_terms_of_use);

        SpannableString privacyPolicySpan = new SpannableString(privacyPolicy);
        privacyPolicySpan.setSpan(new ClickableSpan() {
            @Override
            public void onClick(@NonNull View widget) {
                getPresenter().toShowReadOnlyGdprPact(GdprUserPactInfo.GDPR_TYPE_PRIVACY_POLICY);
            }
        }, 0, privacyPolicySpan.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        privacyPolicySpan.setSpan(new UnderlineSpan(), 0, privacyPolicySpan.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        privacyPolicySpan.setSpan(new ForegroundColorSpan(Color.parseColor("#68BCFF")), 0, privacyPolicySpan.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        builder.append(privacyPolicySpan);

        SpannableString andSpan = new SpannableString(and);
        builder.append(andSpan);


        SpannableString termsOfUseSpan = new SpannableString(termsOfUse);
        termsOfUseSpan.setSpan(new ClickableSpan() {
            @Override
            public void onClick(@NonNull View widget) {
                getPresenter().toShowReadOnlyGdprPact(GdprUserPactInfo.GDPR_TYPE_TERMS_OF_USE);
            }
        }, 0, termsOfUseSpan.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        termsOfUseSpan.setSpan(new UnderlineSpan(), 0, termsOfUseSpan.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        termsOfUseSpan.setSpan(new ForegroundColorSpan(Color.parseColor("#68BCFF")), 0, termsOfUseSpan.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        builder.append(termsOfUseSpan);
        return builder;
    }

    private void login() {
        getPresenter().login(mAccountInfo, mPassword);
    }


    private void updateButtonsState() {
        mAccountInfo.account = mAccountEdt.getText().toString().trim();

        AccountTypeHelper.handleEmailSymbol(mAccountEdt, mAccountInfo);

        if (mAccountInfo.account.length() > 0) {
            mClearBtn.setVisibility(View.VISIBLE);
        } else {
            mClearBtn.setVisibility(View.INVISIBLE);
        }

        mPassword = mPasswordEdt.getText().toString();
        boolean accountValid = AccountTypeHelper.isAccountValid(mAccountInfo.getType(), mAccountInfo.account);

        if (accountValid && !TextUtils.isEmpty(mPassword) && mPassword.length() >= 6) {
            mLoginBtn.setEnabled(true);
        } else {
            mLoginBtn.setEnabled(false);
        }

        if (TextUtils.isEmpty(mPassword)) {
            if (mPasswordEyeCheckBox.getVisibility() != View.INVISIBLE) {
                mPasswordEyeCheckBox.setVisibility(View.INVISIBLE);
            }
        } else {
            if (mPasswordEyeCheckBox.getVisibility() != View.VISIBLE) {
                mPasswordEyeCheckBox.setVisibility(View.VISIBLE);
            }
        }
        //处理校园账号提示是否隐藏显示
        if (mAccountInfo.isEduAccount()) {
            mEduAccountTipTv.setVisibility(View.VISIBLE);
            mForgotPasswordBtn.setVisibility(View.GONE);
        } else {
            mEduAccountTipTv.setVisibility(View.GONE);
            mForgotPasswordBtn.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onClick(View v, boolean isSafeClick) {
        super.onClick(v, isSafeClick);
        if (v == mBackBtn) {
            finish();
        }
        if (v == mClearBtn) {
            mAccountEdt.setText("");
        }
        if (v == mPhoneLoginTv) {
            switchLoginType(LoginAccountInfo.LOGIN_TYPE_PHONE);
        }
        if (v == mEmailLoginTv) {
            switchLoginType(LoginAccountInfo.LOGIN_TYPE_EMAIL);
        }
        if (v == mEduLoginTv) {
            switchLoginType(LoginAccountInfo.LOGIN_TYPE_EDU);
        }
        if (v == mRootLayout) {
            KeyBoardHelper.hideSoftKeyBoard(mRootLayout);
        }

        if (v == mLoginBtn) {
            login();
        }
        if (v == mGuestBtn) {
            getPresenter().loginGuest();
        }
        if (v == mForgotPasswordBtn) {
            RegisterAccountInfo accountInfo = new RegisterAccountInfo(mAccountInfo);
            accountInfo.intentType = RegisterAccountInfo.INTENT_TYPE_RESET_PASSWORD;
            RegisterAccountActivity.open(this, accountInfo);

        }
        if (v == mCountryCodeTv) {
            showLocaleSelectDialogFragment();
        }
        if (v == mCreateBtn) {
            getPresenter().toRegister();
        }

    }

    private void showLocaleSelectDialogFragment() {
        LocaleSelectDialogFragment fragment = new LocaleSelectDialogFragment();
        fragment.setCancelable(false);
        fragment.setData(mLocaleList, mAccountInfo.locale);
        fragment.show(getSupportFragmentManager(), "LocaleSelectDialogFragment");
        final boolean isChinese = AppUtil.isSimpleChinese(AppUtil.getAppLanguage(this));
        fragment.setDismissListener(new OnDialogFragmentDismissListener() {
            @Override
            public void onDismiss(Object... value) {
                togglePushUpContentRightLyout(false);
                if (value != null && value.length > 0 && value[0] instanceof UBTLocale) {
                    mAccountInfo.locale = (UBTLocale) value[0];
                    mCountryCodeTv.setText("+" + mAccountInfo.locale.dial_code);
                }
            }
        });
        togglePushUpContentRightLyout(true);
    }

    private void togglePushUpContentRightLyout(boolean push) {
        int offset = push ? getResources().getDimensionPixelOffset(R.dimen.ubt_dimen_200px) : 0;
        mContentRightLyt.setTranslationY(-offset);
    }


    private void initLoginAccountInfo() {
        if (getIntent().hasExtra(LOGIN_ACCOUNT)) {//来自注册页
            mAccountInfo = (LoginAccountInfo) getIntent().getSerializableExtra(LOGIN_ACCOUNT);
            //防止onResume中重复执行初始化账号
            getIntent().removeExtra(LOGIN_ACCOUNT);
        } else {
            mAccountInfo = AccountTypeHelper.getLoginAccountHistory(this);
        }
        if (mAccountInfo != null) {
            mAccountEdt.setText(mAccountInfo.account);
            mAccountTypeHelper.recordAccount(mAccountInfo.getType(), mAccountInfo.account);
        } else {
            mAccountInfo = new LoginAccountInfo();
        }
        if (mAccountInfo.locale == null) {
            mAccountInfo.locale = RegionHelper.getDefaultRegionAccountLocale();
        }
        String localeInfo = "+" + mAccountInfo.locale.dial_code;
        if (AppUtil.isSimpleChinese(AppUtil.getAppLanguage(this))) {
            localeInfo = "+" + mAccountInfo.locale.dial_code;
        }
        mCountryCodeTv.setText(localeInfo);
        switchLoginType(mAccountInfo.getType());
    }


    private void switchLoginType(@LoginAccountInfo.LoginAccountType int type) {

        String lastAccount = mAccountTypeHelper.getLastAccount(type);

        //把当前的账号类型的账号存下来

        mAccountTypeHelper.recordAccount(mAccountInfo.getType(), mAccountInfo.account);
        //把之前的账号类型的账号还原到输入框
        mAccountEdt.setText(lastAccount);
        if (!TextUtils.isEmpty(lastAccount)) {
            mAccountEdt.setSelection(lastAccount.length());
        }
        mAccountEdt.setHint(AccountTypeHelper.getAccountLoginHintStringId(type));
        int length = AccountTypeHelper.getAccountMaxLength(type);
        mAccountEdt.setFilters(new InputFilter[]{new InputFilter.LengthFilter(length)});
        mAccountEdt.setKeyListener(AccountTypeHelper.getEditTextKeyListener(type));
        mPhoneLoginTv.setChecked(type == LoginAccountInfo.LOGIN_TYPE_PHONE);
        mEmailLoginTv.setChecked(type == LoginAccountInfo.LOGIN_TYPE_EMAIL);
        mEduLoginTv.setChecked(type == LoginAccountInfo.LOGIN_TYPE_EDU);
        mAccountIconView.setBackgroundResource(AccountTypeHelper.getAccountIconId(type));
        mCountryCodeTv.setVisibility(type == LoginAccountInfo.LOGIN_TYPE_PHONE ? View.VISIBLE : View.GONE);

        int fontSizeNormal = getResources().getDimensionPixelSize(R.dimen.ubt_font_34px);
        int fontSizeBig = getResources().getDimensionPixelSize(R.dimen.ubt_font_70px);
        mPhoneLoginTv.setTextSize(TypedValue.COMPLEX_UNIT_PX, mPhoneLoginTv.isChecked() ? fontSizeBig : fontSizeNormal);
        mEmailLoginTv.setTextSize(TypedValue.COMPLEX_UNIT_PX, mEmailLoginTv.isChecked() ? fontSizeBig : fontSizeNormal);
        mEduLoginTv.setTextSize(TypedValue.COMPLEX_UNIT_PX, mEduLoginTv.isChecked() ? fontSizeBig : fontSizeNormal);
        mEduLoginTv.setTextColor(mPhoneLoginTv.isChecked() ? TEXT_COLOR_SELECTED : TEXT_COLOR_NORMAL);
        mEmailLoginTv.setTextColor(mEmailLoginTv.isChecked() ? TEXT_COLOR_SELECTED : TEXT_COLOR_NORMAL);
        mEduLoginTv.setTextColor(mEduLoginTv.isChecked() ? TEXT_COLOR_SELECTED : TEXT_COLOR_NORMAL);
        mPhoneLoginTv.setTypeface(Typeface.DEFAULT, mPhoneLoginTv.isChecked() ? Typeface.BOLD : Typeface.NORMAL);
        mEmailLoginTv.setTypeface(Typeface.DEFAULT, mEmailLoginTv.isChecked() ? Typeface.BOLD : Typeface.NORMAL);
        mEduLoginTv.setTypeface(Typeface.DEFAULT, mEduLoginTv.isChecked() ? Typeface.BOLD : Typeface.NORMAL);


        //清空密码
        mPasswordEdt.setText("");

        mAccountInfo.setType(type);
        mAccountInfo.account = mAccountEdt.getText().toString().trim();
        updateButtonsState();
    }

    private void showPrivacyPolicyDialog(String abstractText, String url) {
        GdprPactDialogFragment dialogFragment = GdprPactDialogFragment.newInstance(
                GdprUserPactInfo.GDPR_TYPE_PRIVACY_POLICY, GdprPactDialogFragment.PACT_STYLE_TYPE_READ_ONLY, abstractText, url);
        dialogFragment.setCancelable(true);
        dialogFragment.show(getSupportFragmentManager(), "GDPR_TYPE_PRIVACY_POLICY");
    }

    private void showTermsOfUseDialog(String abstractText, String url) {
        GdprPactDialogFragment dialogFragment = GdprPactDialogFragment.newInstance(
                GdprUserPactInfo.GDPR_TYPE_TERMS_OF_USE, GdprPactDialogFragment.PACT_STYLE_TYPE_READ_ONLY, abstractText, url);
        dialogFragment.setCancelable(true);
        dialogFragment.show(getSupportFragmentManager(), "GDPR_TYPE_PRIVACY_POLICY");
    }

    private void initBackButtonState() {
        sIsShowBackButton = getIntent().getBooleanExtra(IS_SHOW_BACK_BUTTON, false);
        mBackBtn.setVisibility(sIsShowBackButton ? View.VISIBLE : View.INVISIBLE);
    }


    @Override
    protected LoginContracts.Presenter createPresenter() {
        return new LoginPresenter();
    }

    @Override
    protected LoginContracts.UI createUIView() {
        return new LoginActivity.LoginUI();
    }

    class LoginUI extends LoginContracts.UI {

        @Override
        public void onLoginSuccess(boolean isGuest) {
            if (!isGuest) {
                getUIDelegate().toastShort(getString(R.string.account_login_success));
            }

            UserDataSynchronizer.getInstance().sync(true).subscribe(new SimpleRxSubscriber<>());
            EventBus.getDefault().post(new LoginFinishEvent());
            LoginActivity.this.finish();
        }

        @Override
        public void showGdprPactDialog(int type, String abstractText, String url) {
            if (type == GdprUserPactInfo.GDPR_TYPE_TERMS_OF_USE) {
                showTermsOfUseDialog(abstractText, url);
            } else {
                showPrivacyPolicyDialog(abstractText, url);
            }
        }

        @Override
        public void onLoadLocaleInfosFinish(List<UBTLocale> locales) {
            mLocaleList = locales;
        }
    }

}
