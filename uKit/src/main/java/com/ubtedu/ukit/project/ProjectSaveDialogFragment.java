/**
 * @2018 UBTECH Robotics Corp. All rights reserved (C)
 **/
package com.ubtedu.ukit.project;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
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
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.ubtedu.alpha1x.utils.KeyBoardHelper;
import com.ubtedu.alpha1x.utils.LogUtil;
import com.ubtedu.ukit.R;
import com.ubtedu.ukit.common.base.UKitBaseDialogFragment;
import com.ubtedu.ukit.common.image.ImageChooseActivity;
import com.ubtedu.ukit.common.image.ImageCropActivity;
import com.ubtedu.ukit.common.image.ImageCropConfigFactory;
import com.ubtedu.ukit.common.view.UKitCharsInputFilter;
import com.ubtedu.ukit.project.vo.Project;

import java.util.List;

import static com.ubtedu.ukit.common.image.ImageChooseActivity.REQUEST_CODE_CHOOSE_IMAGE;

/**
 * @Author qinicy
 * @Date 2018/11/15
 **/
public class ProjectSaveDialogFragment extends UKitBaseDialogFragment {
    private EditText mProjectNameEdt;
    private TextView mTitleTv;
    private View mClearBtn;
    private View mNegativeBtn;
    private View mPositiveBtn;
    private View mAddImageLyt;
    private ImageView mProjectIv;
    private boolean isPositive;
    protected String mProjectName;
    protected String mImagePath;
    protected String mTitle;
    private View mContentView;
    private ImageView mImageView;
    private Button mEditBtn;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View layout = inflater.inflate(R.layout.dialog_project_save, null);

        mContentView = layout.findViewById(R.id.dialog_fragment_root_view);
        bindSafeClickListener(mContentView);
        mProjectNameEdt = layout.findViewById(R.id.dialog_project_save_input_edt);
        mTitleTv = layout.findViewById(R.id.dialog_project_save_title_tv);
        mClearBtn = layout.findViewById(R.id.dialog_project_save_input_clear_btn);
        mNegativeBtn = layout.findViewById(R.id.dialog_project_save_negative_btn);
        mPositiveBtn = layout.findViewById(R.id.dialog_project_save_positive_btn);
        mAddImageLyt = layout.findViewById(R.id.dialog_project_save_add_image_lyt);
        mImageView = layout.findViewById(R.id.dialog_project_save_image_iv);
        mProjectIv = layout.findViewById(R.id.dialog_project_save_add_image_iv);
        mEditBtn = layout.findViewById(R.id.dialog_project_edit_btn);
        if (!TextUtils.isEmpty(mTitle)) {
            mTitleTv.setText(mTitle);
        }
        if (!TextUtils.isEmpty(mImagePath)) {
            updateProjectImage();
        }
        if (TextUtils.isEmpty(mProjectName)) {
            mProjectName = generateProjectName();
        }
        mProjectNameEdt.postDelayed(new Runnable() {
            @Override
            public void run() {
                mProjectNameEdt.setFocusable(true);
                mProjectNameEdt.setFocusableInTouchMode(true);
                mProjectNameEdt.requestFocus();
                mProjectNameEdt.setText(mProjectName);
                mProjectNameEdt.setSelection(mProjectName.length());
                KeyBoardHelper.openSoftKeyboard(mProjectNameEdt.getContext());
            }
        }, 20);


        mProjectNameEdt.setFilters(new InputFilter[]{new UKitCharsInputFilter(20)});
        mProjectNameEdt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                mProjectName = s.toString().trim();
                updateButtonsState();
            }
        });

        bindClickListener(mClearBtn);
        bindSafeClickListener(mAddImageLyt);
        bindSafeClickListener(mPositiveBtn);
        bindSafeClickListener(mNegativeBtn);
        bindSafeClickListener(mEditBtn);

        updateButtonsState();
        return layout;
    }

    private void updateProjectImage() {
        mEditBtn.setVisibility(View.VISIBLE);
        RequestOptions requestOptions = new RequestOptions();
        requestOptions = requestOptions.transforms(new CenterCrop(), new RoundedCorners(getResources().getDimensionPixelOffset(R.dimen.ubt_dimen_60px)));
        Glide.with(mImageView)
                .load(mImagePath)

                .apply(requestOptions)
                .into(mImageView);
    }

    private void updateButtonsState() {
        if (mProjectName != null && mProjectName.length() > 0) {
            mClearBtn.setVisibility(View.VISIBLE);
        } else {
            mClearBtn.setVisibility(View.INVISIBLE);
        }
        if (isButtonEnable()) {
            mPositiveBtn.setEnabled(true);
            mPositiveBtn.setAlpha(1.0f);
        } else {
            mPositiveBtn.setEnabled(false);
            mPositiveBtn.setAlpha(0.5f);
        }
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
        if (mDismissListener != null) {
            mDismissListener.onDismiss(isPositive, mProjectName, mImagePath);
        }
    }

    @Override
    public void onClick(View v, boolean isSafeClick) {
        super.onClick(v, isSafeClick);
        if (v == mClearBtn) {
            mProjectNameEdt.setText("");
        }
        if (v == mNegativeBtn) {
            isPositive = false;
            dismiss();
        }
        if (v == mPositiveBtn) {
            handlePositive();
        }
        if (v == mAddImageLyt || v == mEditBtn) {
            ImageChooseActivity.open(this, ImageCropConfigFactory.createProjectImageCropConfig());
        }
        if (v == mContentView) {
            KeyBoardHelper.hideSoftKeyBoard(mContentView);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_CHOOSE_IMAGE && resultCode == Activity.RESULT_OK && data != null) {
            mImagePath = data.getStringExtra(ImageCropActivity.IMAGE_PATH_KEY);

            LogUtil.d("imagePath:" + mImagePath);
            if (mImagePath != null) {
                updateProjectImage();
                updateButtonsState();
            }
        }
    }

    private void handlePositive() {
        if (mProjectName != null) {
            boolean isDuplicate = isDuplicate();
            if (isDuplicate) {
                getUIDelegate().toastShort(getString(R.string.project_save_duplicate_name));
                mProjectNameEdt.setSelection(0, mProjectName.length());
                return;
            }
        }
        isPositive = true;
        dismiss();
    }

    private String generateProjectName() {
        int projectNum = 1;
        String prefixName = getString(R.string.project_save_default_name);
        List<Project> projects = UserDataSynchronizer.getInstance().getTargetDeviceProjects();
        if (projects != null) {
            for (int i = 0; i < projects.size(); i++) {
                Project project = null;
                for (Project temp : projects) {
                    if ((prefixName + projectNum).equals(temp.projectName)) {
                        project = temp;
                    }
                }
                if (project == null) {
                    break;
                }
                projectNum++;
            }
        }
        return prefixName + projectNum;
    }

    protected boolean isButtonEnable() {
        return mProjectName != null && mProjectName.length() > 0;
    }

    protected boolean isDuplicate() {
        return UserDataSynchronizer.getInstance().isProjectNameDuplicate(mProjectName);
    }
}
