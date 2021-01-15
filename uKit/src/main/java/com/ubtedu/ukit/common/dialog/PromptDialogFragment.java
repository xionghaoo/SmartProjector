/**
 * @2018 UBTECH Robotics Corp. All rights reserved (C)
 **/
package com.ubtedu.ukit.common.dialog;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.DrawableRes;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.ubtedu.ukit.R;
import com.ubtedu.ukit.common.base.UKitBaseDialogFragment;

/**
 * @Author qinicy
 * @Date 2018/11/6
 **/
public class PromptDialogFragment extends UKitBaseDialogFragment implements OnClickListener {
    private final static String TYPE_KEY = "type";
    private final static String TITTLE_KEY = "tittle";
    private final static String MESSAGE_KEY = "message";
    private final static String POSITIVE_TEXT_KEY = "positive_text";
    private final static String NEGATIVE_TEXT_KEY = "negative_text";
    private final static String SHOW_POSITIVE_KEY = "show_positive";
    private final static String SHOW_NEGATIVE_KEY = "show_negative";

    private int mType;
    private TextView mTitleTv;
    private String mTitle;
    private String mMessage;
    private String mNegativeText;
    private String mPositiveText;
    private boolean isPositive;
    private boolean isShowPositiveButton;
    private boolean isShowNegativeButton;
    private boolean isCancelable;
    private Drawable mImage;
    private Button mNegativeBtn;
    private Button mPositiveBtn;
    private View.OnClickListener mOnNegativeClickListener;
    private View.OnClickListener mOnPositiveClickListener;
    private DialogInterface.OnDismissListener mOnDismissListener;

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
            mType = args.getInt(TYPE_KEY);
            mTitle = args.getString(TITTLE_KEY);
            mMessage = args.getString(MESSAGE_KEY);
            mPositiveText = args.getString(POSITIVE_TEXT_KEY);
            mNegativeText = args.getString(NEGATIVE_TEXT_KEY);
            isShowPositiveButton = args.getBoolean(SHOW_POSITIVE_KEY, true);
            isShowNegativeButton = args.getBoolean(SHOW_NEGATIVE_KEY, true);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(TYPE_KEY, mType);
        outState.putString(TITTLE_KEY, mTitle);
        outState.putString(MESSAGE_KEY, mMessage);
        outState.putString(POSITIVE_TEXT_KEY, mPositiveText);
        outState.putString(NEGATIVE_TEXT_KEY, mNegativeText);
        outState.putBoolean(SHOW_POSITIVE_KEY, isShowPositiveButton);
        outState.putBoolean(SHOW_NEGATIVE_KEY, isShowNegativeButton);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = createContentView(inflater);
        if (isCancelable()) {
            super.mRootView = root.findViewById(R.id.dialog_fragment_root_view);
        }
        // 内容
        TextView contentTv = root.findViewById(R.id.prompt_content_tv);
        contentTv.setText(mMessage);

        // 标题
        mTitleTv = root.findViewById(R.id.prompt_title_tv);
        if (mTitleTv != null) {
            mTitleTv.setText(mTitle);
        }

        ImageView imageView = root.findViewById(R.id.prompt_image_iv);
        if (imageView != null) {
            if (mImage == null) {
                mImage = ContextCompat.getDrawable(getContext(), R.drawable.bluetooth_popup_pic_upgradefailed);
            }
            imageView.setImageDrawable(mImage);
        }

        //如果两个按钮都显示，使用短按钮，否则是长按钮
        int buttonWidth = getResources().getDimensionPixelOffset(R.dimen.ubt_dimen_540px);
        if (isShowPositiveButton && isShowNegativeButton) {
            buttonWidth = getResources().getDimensionPixelOffset(R.dimen.ubt_dimen_414px);
        }

        mNegativeBtn = root.findViewById(R.id.prompt_negative_btn);
        if (mNegativeBtn != null) {
            ViewGroup.LayoutParams lp = mNegativeBtn.getLayoutParams();
            lp.width = buttonWidth;
            mNegativeBtn.setLayoutParams(lp);
            int visibility = isShowNegativeButton ? View.VISIBLE : View.GONE;
            mNegativeBtn.setVisibility(visibility);
            if (!TextUtils.isEmpty(mNegativeText)) {
                mNegativeBtn.setText(mNegativeText);
            }
            mNegativeBtn.setOnClickListener(this);
        }


        mPositiveBtn = root.findViewById(R.id.prompt_positive_btn);
        if (mPositiveBtn != null) {
            ViewGroup.LayoutParams lp = mPositiveBtn.getLayoutParams();
            lp.width = buttonWidth;
            mPositiveBtn.setLayoutParams(lp);
            int visibility = isShowPositiveButton ? View.VISIBLE : View.GONE;
            mPositiveBtn.setVisibility(visibility);
            if (!TextUtils.isEmpty(mPositiveText)) {
                mPositiveBtn.setText(mPositiveText);
            }
            mPositiveBtn.setOnClickListener(this);
        }

        return root;
    }


    protected View createContentView(LayoutInflater inflater) {
        int id = R.layout.dialog_prompt_normal;
        switch (mType) {
            case Type.NORMAL:
                id = R.layout.dialog_prompt_normal;
                break;
            case Type.BIG:
                id = R.layout.dialog_prompt_big;
                break;
        }
        return inflater.inflate(id, null);
    }


    @Override
    public void onClick(View v) {

        if (v == mNegativeBtn) {
            isPositive = false;
            if (mOnNegativeClickListener != null) {
                mOnNegativeClickListener.onClick(v);
            }
            dismiss();
        }

        if (v == mPositiveBtn) {
            isPositive = true;
            if (mOnPositiveClickListener != null) {
                mOnPositiveClickListener.onClick(v);
                if (mOnPositiveClickListener instanceof OnConfirmClickListener) {
                    boolean eat = ((OnConfirmClickListener) mOnPositiveClickListener).onClick();
                    if (eat) {
                        return;
                    }
                }
            }
            dismiss();
        }

    }


    @Override
    public void show(FragmentManager manager, String tag) {
        super.show(manager, tag);
        isPositive = false;

    }

    @Override
    public int show(FragmentTransaction transaction, String tag) {
        isPositive = false;
        return super.show(transaction, tag);
    }

    @Override
    public void dismiss() {
        if (mDismissListener != null) {
            mDismissListener.onDismiss(isPositive);
        }
        if (getActivity() != null && !getActivity().isFinishing()) {
            super.dismissAllowingStateLoss();
        }
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
        if (mOnDismissListener != null) {
            mOnDismissListener.onDismiss(dialog);
        }
    }

    public static Builder newBuilder(Context context) {
        return new Builder(context);
    }

    public abstract static class OnConfirmClickListener implements OnClickListener {
        @Override
        public void onClick(View v) {

        }

        public abstract boolean onClick();

    }

    public static class Builder {

        private Context mContext;
        private int mType;
        private String mTitle;
        private String mMessage;
        private String mNegativeText;
        private String mPositiveText;
        private boolean isShowPositiveButton = true;
        private boolean isShowNegativeButton = true;
        private boolean isCancelable = true;
        private Drawable mImage;
        private View.OnClickListener mOnNegativeClickListener;
        private View.OnClickListener mOnPositiveClickListener;
        private DialogInterface.OnDismissListener mOnDismissListener;

        public Builder(Context context) {
            mContext = context;
        }

        public Builder title(String title) {
            mTitle = title;
            return this;
        }

        public Builder message(String message) {
            mMessage = message;
            return this;
        }

        public Builder type(int type) {
            mType = type;
            return this;
        }

        public Builder positiveButtonText(String text) {
            mPositiveText = text;
            return this;
        }

        public Builder showPositiveButton(boolean show) {
            isShowPositiveButton = show;
            return this;
        }

        public Builder negativeButtonText(String text) {
            mNegativeText = text;
            return this;
        }

        public Builder showNegativeButton(boolean show) {
            isShowNegativeButton = show;
            return this;
        }

        public Builder image(@DrawableRes int id) {
            mImage = ContextCompat.getDrawable(mContext, id);
            return this;
        }

        public Builder image(Drawable image) {
            mImage = image;
            return this;
        }

        public Builder cancelable(boolean isCancelable) {
            this.isCancelable = isCancelable;
            return this;
        }

        public Builder onNegativeClickListener(OnClickListener listener) {
            mOnNegativeClickListener = listener;
            return this;
        }

        public Builder onPositiveClickListener(OnClickListener listener) {
            mOnPositiveClickListener = listener;
            return this;
        }

        public Builder onDismissListener(DialogInterface.OnDismissListener listener) {
            mOnDismissListener = listener;
            return this;
        }

        public PromptDialogFragment build() {
            PromptDialogFragment fragment = createFragment();
            fragment.setCancelable(isCancelable);
            fragment.mImage = mImage;
            fragment.mOnPositiveClickListener = mOnPositiveClickListener;
            fragment.mOnNegativeClickListener = mOnNegativeClickListener;
            fragment.mOnDismissListener = mOnDismissListener;
            Bundle args = new Bundle();
            args.putInt(TYPE_KEY, mType);
            args.putString(TITTLE_KEY, mTitle);
            args.putString(MESSAGE_KEY, mMessage);
            args.putString(POSITIVE_TEXT_KEY, mPositiveText);
            args.putString(NEGATIVE_TEXT_KEY, mNegativeText);
            args.putBoolean(SHOW_POSITIVE_KEY, isShowPositiveButton);
            args.putBoolean(SHOW_NEGATIVE_KEY, isShowNegativeButton);
            fragment.setArguments(args);
            return fragment;
        }

        public PromptDialogFragment createFragment() {
            return new PromptDialogFragment();
        }
    }

    public static class Type {
        public static final int NORMAL = 1;
        public static final int BIG = 2;
    }
}
