/**
 * @2018 UBTECH Robotics Corp. All rights reserved (C)
 **/
package com.ubtedu.ukit.menu.about;

import android.content.DialogInterface;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.StyleSpan;
import android.text.style.URLSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.IntDef;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.ubtedu.ukit.R;
import com.ubtedu.ukit.common.base.UKitBaseDialogFragment;
import com.ubtedu.ukit.common.flavor.Channel;
import com.ubtedu.ukit.common.flavor.Flavor;
import com.ubtedu.ukit.user.vo.GdprUserPactInfo;

import java.io.Serializable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * @Author qinicy
 * @Date 2018/12/19
 **/
public class GdprPactDialogFragment extends UKitBaseDialogFragment {
    private final static String PACT_TYPE = "pact_type";
    private final static String PACT_URL = "pact_url";
    private final static String PACT_ABSTRACT = "pact_abstract";
    private final static String PACT_STYLE_TYPE = "pact_style_type";
    public final static int PACT_STYLE_TYPE_NEED_HAVE_READ = 0;
    public final static int PACT_STYLE_TYPE_READ_ONLY = 1;
    public final static int PACT_STYLE_TYPE_NORMAL = 2;

    @IntDef({PACT_STYLE_TYPE_NEED_HAVE_READ, PACT_STYLE_TYPE_READ_ONLY, PACT_STYLE_TYPE_NORMAL})
    @Retention(RetentionPolicy.SOURCE)
    public @interface PactStyleType {
    }

    @PactStyleType
    private int mPackStyleType = PACT_STYLE_TYPE_NORMAL;

    @GdprUserPactInfo.PactType
    private int mPactType;
    private String mPactUrl;
    private String mPactAbstractText;
    private TextView mContentTv;
    private Button mRefuseBtn;
    private Button mAgreeBtn;
    private View mBtnsLyt;
    private boolean isAgree;
    private View mCloseBtn;
    private TextView mTittleTv;
    private TextView mAbstractTitleTv;
    private LinearLayout mHaveReadLyt;
    private CheckBox mHaveAgreeCheckBox;
    private TextView mAgreeTitle;

    public static GdprPactDialogFragment newInstance(@GdprUserPactInfo.PactType int type, @PactStyleType int pactStyleType, String abstractText, String url) {
        GdprPactDialogFragment fragment = new GdprPactDialogFragment();
        Bundle args = new Bundle();
        args.putInt(PACT_TYPE, type);
        args.putInt(PACT_STYLE_TYPE, pactStyleType);
        args.putString(PACT_URL, url);
        args.putString(PACT_ABSTRACT, abstractText);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            initArguments(savedInstanceState);
        } else {
            initArguments(getArguments());
        }
    }

    private void initArguments(Bundle args) {
        if (args != null) {
            mPactType = args.getInt(PACT_TYPE);
            mPackStyleType = args.getInt(PACT_STYLE_TYPE);
            mPactUrl = args.getString(PACT_URL);
            mPactAbstractText = args.getString(PACT_ABSTRACT);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(PACT_TYPE, mPactType);
        outState.putInt(PACT_STYLE_TYPE, mPackStyleType);
        outState.putString(PACT_URL, mPactUrl);
        outState.putString(PACT_ABSTRACT, mPactAbstractText);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.dialog_gdpr_pact, null);
        if (isCancelable()) {
            super.mRootView = root.findViewById(R.id.dialog_fragment_root_view);
        }
        mTittleTv = root.findViewById(R.id.dialog_gdpr_title_tv);
        mContentTv = root.findViewById(R.id.dialog_gdpr_abstract_tv);
        mRefuseBtn = root.findViewById(R.id.dialog_gdpr_refuse_btn);
        mAgreeBtn = root.findViewById(R.id.dialog_gdpr_agree_btn);
        mCloseBtn = root.findViewById(R.id.dialog_gdpr_close_btn);
        mBtnsLyt = root.findViewById(R.id.dialog_gdpr_buttons_lyt);
        mAbstractTitleTv = root.findViewById(R.id.dialog_gdpr_abstract_title_tv);
        mHaveReadLyt = root.findViewById(R.id.dialog_gdpr_have_read_layout);
        mHaveAgreeCheckBox = root.findViewById(R.id.dialog_gdpr_have_read_checkbox);
        mAgreeTitle = root.findViewById(R.id.dialog_gdpr_title);
        bindSafeClickListener(mRefuseBtn);
        bindSafeClickListener(mAgreeBtn);
        bindSafeClickListener(mCloseBtn);
        initViewContent();
        return root;
    }

    private void initViewContent() {
        String abstractContent = mPactAbstractText;
        if (abstractContent == null) {
            abstractContent = "";
        }
        mTittleTv.setText(R.string.gdpr_privacy_policy);
        if (mPactType == GdprUserPactInfo.GDPR_TYPE_TERMS_OF_USE) {
            mTittleTv.setText(R.string.gdpr_terms_of_use);
        }
        mAbstractTitleTv.setVisibility(Flavor.getChannel().getId()== Channel.NA.getId()?View.GONE:View.VISIBLE);
        SpannableStringBuilder builder = new SpannableStringBuilder();
        SpannableString contentSpan = new SpannableString(abstractContent);
        builder.append(contentSpan);
        String detail = getString(R.string.gdpr_pact_abstract_see_detail);
        SpannableString seeDetailSpan = new SpannableString(detail);
        seeDetailSpan.setSpan(new URLSpan(mPactUrl), 0, seeDetailSpan.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        seeDetailSpan.setSpan(new StyleSpan(android.graphics.Typeface.BOLD), 0, seeDetailSpan.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        builder.append(seeDetailSpan);
        SpannableString emptySpan = new SpannableString(" \n");
        builder.append(emptySpan);
        mContentTv.setText(builder);

        mContentTv.setMovementMethod(LinkMovementMethod.getInstance());

        switch (mPackStyleType) {
            case PACT_STYLE_TYPE_NEED_HAVE_READ:
                mBtnsLyt.setVisibility(View.VISIBLE);
                mHaveReadLyt.setVisibility(View.VISIBLE);
                mCloseBtn.setVisibility(View.INVISIBLE);
                mAgreeBtn.setEnabled(mHaveAgreeCheckBox.isChecked());
                mHaveAgreeCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        mAgreeBtn.setEnabled(isChecked);
                    }
                });
                mAgreeTitle.setText("《" + getString(mPactType == GdprUserPactInfo.GDPR_TYPE_TERMS_OF_USE ? R.string.gdpr_terms_of_use : R.string.gdpr_privacy_policy) + "》");
                mRefuseBtn.setText(R.string.gdpr_disagree_exit);
                break;
            case PACT_STYLE_TYPE_READ_ONLY:
                mBtnsLyt.setVisibility(View.GONE);
                mCloseBtn.setVisibility(View.VISIBLE);
                mHaveReadLyt.setVisibility(View.INVISIBLE);
                break;
            default:
                mHaveReadLyt.setVisibility(View.INVISIBLE);
                mBtnsLyt.setVisibility(View.VISIBLE);
                mCloseBtn.setVisibility(View.GONE);
        }
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
        if (mDismissListener != null) {
            mDismissListener.onDismiss(isAgree);
        }
    }

    @Override
    public void onClick(View v, boolean isSafeClick) {
        super.onClick(v, isSafeClick);
        if (v == mRefuseBtn) {
            isAgree = false;
            dismiss();
        }
        if (v == mAgreeBtn) {
            isAgree = true;
            dismiss();
        }
        if (v == mCloseBtn) {
            isAgree = false;
            dismiss();
        }
    }
}
