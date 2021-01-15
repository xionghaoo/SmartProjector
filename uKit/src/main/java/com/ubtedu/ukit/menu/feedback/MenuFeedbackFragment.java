/**
 * @2018 UBTECH Robotics Corp. All rights reserved (C)
 **/
package com.ubtedu.ukit.menu.feedback;

import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.ubtedu.alpha1x.utils.KeyBoardHelper;
import com.ubtedu.alpha1x.utils.StringUtil;
import com.ubtedu.ukit.R;
import com.ubtedu.ukit.common.analysis.Events;
import com.ubtedu.ukit.common.analysis.UBTReporter;
import com.ubtedu.ukit.common.base.UKitBaseFragment;
import com.ubtedu.ukit.common.view.KeyFactory;
import com.ubtedu.ukit.common.view.UKitCharsInputFilter;

/**
 * @Author qinicy
 * @Date 2018/12/12
 **/
public class MenuFeedbackFragment extends UKitBaseFragment<MenuFeedbackContracts.Presenter, MenuFeedbackContracts.UI> {
    private final static int FEEDBACK_LENGTH_MAX = 2000;
    private EditText mContentEdt;
    private EditText mEmailEdt;
    private TextView mCharsCountTv;
    private Button mSubmitBtn;
    private View mRootView;
    private String mEmail;
    private String mContent;
    private StringBuilder mEmailBuilder;
    private static final String EMAIL_SYMBOL = "@";

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_menu_feedback, null);
        mRootView = root.findViewById(R.id.menu_feedback_root_lyt);
        mContentEdt = root.findViewById(R.id.menu_feedback_edit);
        mContentEdt.setFilters(new InputFilter[]{new UKitCharsInputFilter(FEEDBACK_LENGTH_MAX)});

        mEmailEdt = root.findViewById(R.id.menu_feedback_email_edit);
        mEmailEdt.setKeyListener(KeyFactory.createEmailDigitsKeyListener());
        mSubmitBtn = root.findViewById(R.id.menu_feedback_submit_btn);
        mCharsCountTv = root.findViewById(R.id.menu_feedback_chars_limit_tv);
        bindSafeClickListener(mRootView);
        bindSafeClickListener(mSubmitBtn);
        handleTextChange();
        updateSubmitButtonState();
        return root;
    }

    @Override
    public void onClick(View v, boolean isSafeClick) {
        super.onClick(v, isSafeClick);
        if (v == mSubmitBtn) {
            onSubmit();
            UBTReporter.onEvent(Events.Ids.app_menu_feedback_submit_btn_click,null);
        }
        if (v == mRootView) {
            KeyBoardHelper.hideSoftKeyBoard(mRootView);
        }
    }

    private void onSubmit() {
        getPresenter().feedback(mEmail, mContent);
    }

    private void handleTextChange() {
        mContentEdt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                mContent = s.toString();
                updateSubmitButtonState();
                updateCharsCount();
            }
        });
        mEmailEdt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                mEmail = s.toString();
                updateSubmitButtonState();
            }
        });
    }

    private void updateCharsCount() {

        int count = FEEDBACK_LENGTH_MAX;
        if (mContent != null) {
            count = FEEDBACK_LENGTH_MAX - mContent.length();
        }

        String countText = getString(R.string.menu_tab_feedback_limit, count);
        mCharsCountTv.setText(countText);
    }

    private void updateSubmitButtonState() {
        handleEmailSymbol();
        if (!TextUtils.isEmpty(mContent) && StringUtil.isEmail(mEmail)) {
            mSubmitBtn.setEnabled(true);
            mSubmitBtn.setAlpha(1.0f);
        } else {
            mSubmitBtn.setEnabled(false);
            mSubmitBtn.setAlpha(0.5f);
        }
    }

    /**
     * 处理@字符:
     * 1.@不能在开头或结尾位置
     * 2.不能包含一个以上@
     */
    private void handleEmailSymbol() {
        if (mEmailBuilder == null) {
            mEmailBuilder = new StringBuilder();
        }

        if (mEmailBuilder.length() > 0) {
            mEmailBuilder.delete(0, mEmailBuilder.length());
        }
        mEmailBuilder.append(mEmail);

        int sourceLength = mEmailBuilder.length();
        if (sourceLength > 0) {
            //去掉开头的@
            if (mEmailBuilder.indexOf(EMAIL_SYMBOL) == 0) {
                mEmailBuilder.deleteCharAt(0);
            }
            //如果有两个@，去掉第二个
            int lastIndex = mEmailBuilder.lastIndexOf(EMAIL_SYMBOL);
            //检测是否有多个@，有的话需要去掉
            if (mEmailBuilder.indexOf(EMAIL_SYMBOL) != lastIndex) {
                mEmailBuilder.deleteCharAt(lastIndex);
            }
        }
        //如果长度不一致，说明有删除@的操作，更新输入框
        if (sourceLength != mEmailBuilder.length()) {
            String newAccount = mEmailBuilder.toString();
            mEmailEdt.setText(newAccount);
            mEmailEdt.setSelection(newAccount.length());
            mEmail = newAccount;
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

    }

    @Override
    protected MenuFeedbackContracts.Presenter createPresenter() {
        return new MenuFeedbackPresenter();
    }

    @Override
    protected MenuFeedbackContracts.UI createUIView() {
        return new MenuFeedbackUI();
    }

    class MenuFeedbackUI extends MenuFeedbackContracts.UI {

        @Override
        public void onFeedbackResult(boolean isSuccess) {
            mRootView.setTranslationY(0);
        }
    }
}
