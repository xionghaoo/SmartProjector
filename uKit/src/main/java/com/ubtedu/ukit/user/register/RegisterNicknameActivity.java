package com.ubtedu.ukit.user.register;

import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.ubtedu.alpha1x.utils.KeyBoardHelper;
import com.ubtedu.ukit.R;
import com.ubtedu.ukit.common.view.UKitCharsInputFilter;
import com.ubtedu.ukit.user.UserBaseActivity;

public class RegisterNicknameActivity extends UserBaseActivity {

    private View mBackBtn;
    private EditText mNickNameEdt;
    private String mNickname;
    private Button mSubmitBtn;
    private View mRootLayout;
    private Button mClearBtn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_nickname);

        mBackBtn = findViewById(R.id.register_nickname_back_btn);
        bindSafeClickListener(mBackBtn);
        mClearBtn = findViewById(R.id.register_nickname_input_clear_btn);
        bindSafeClickListener(mClearBtn);
        mRootLayout = findViewById(R.id.register_nickname_root_lyt);
        bindSafeClickListener(mRootLayout);
        mSubmitBtn = findViewById(R.id.register_nickname_submit_btn);
        bindSafeClickListener(mSubmitBtn);

        mNickNameEdt = findViewById(R.id.register_nickname_edt);
        mNickNameEdt.setFilters(new InputFilter[]{new UKitCharsInputFilter(20)});
        mNickNameEdt.addTextChangedListener(new TextWatcher() {
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
        updateButtonsState();
    }

    @Override
    public void onClick(View v, boolean isSafeClick) {
        super.onClick(v, isSafeClick);
        if (v == mBackBtn) {
            finish();
        }
        if (v == mSubmitBtn) {
            toRegisterAccount();
        }
        if (v == mRootLayout) {
            KeyBoardHelper.hideSoftKeyBoard(mRootLayout);
        }
        if (v == mClearBtn) {
            mNickNameEdt.setText("");
        }
    }

    private void toRegisterAccount() {
        RegisterAccountInfo account = new RegisterAccountInfo();
        account.nickName = mNickname;
        RegisterAccountActivity.open(this, account);
    }

    private void updateButtonsState() {
        mNickname = mNickNameEdt.getText().toString().trim();
        mSubmitBtn.setEnabled(!TextUtils.isEmpty(mNickname) && mNickname.length() > 0);
        mClearBtn.setVisibility(!TextUtils.isEmpty(mNickname) && mNickname.length() > 0 ? View.VISIBLE : View.INVISIBLE);

    }


}
