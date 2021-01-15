package com.ubtedu.ukit.user.gdpr;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.fragment.app.FragmentTransaction;

import com.ubtedu.alpha1x.utils.LogUtil;
import com.ubtedu.ukit.R;
import com.ubtedu.ukit.common.base.UKitBaseFragment;
import com.ubtedu.ukit.user.UserBaseActivity;
import com.ubtedu.ukit.user.register.RegisterNicknameActivity;

public class GdprAgeActivity extends UserBaseActivity implements View.OnClickListener {

    public final static int REQUESTT_CODE = 10;
    public final static int RESULT_CODE_YES = 11;
    public final static int RESULT_CODE_NO = 12;

    private View mBackBtn;
    private boolean isStepOne;

    private UKitBaseFragment[] mFragments;
    private UKitBaseFragment mCurrentFragment;
    private GdprBirthdaySelectFragment mBirthdaySelectFragment;
    private GdprAgeGuardianFragment mGuardianFragment;

    public static void open(Activity context) {
        if (context == null) {
            LogUtil.d("context == null");
            return;
        }
        Intent intent = new Intent(context, GdprAgeActivity.class);
        context.startActivityForResult(intent, REQUESTT_CODE);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dgpr_age);

        mBackBtn = findViewById(R.id.gdpr_age_back_btn);
        mBackBtn.setOnClickListener(this);

        mBirthdaySelectFragment = new GdprBirthdaySelectFragment();
        mGuardianFragment = new GdprAgeGuardianFragment();
        mBirthdaySelectFragment.setOnConfirmBirthdayListener(new GdprBirthdaySelectFragment.OnConfirmBirthdayListener() {
            @Override
            public void onBirthdayConfirm(boolean isAgeAvailable) {
                if (isAgeAvailable) {
                    toRegisterNickname();
                } else {
                    toStep(2);
                }
            }
        });
        mGuardianFragment.setOnBtnClickListener(new GdprAgeGuardianFragment.OnBtnClickListener() {
            @Override
            public void onPositiveClick() {
                toStep(1);
            }

            @Override
            public void onNegativeClick() {
                finish(RESULT_CODE_NO);
            }
        });
        mFragments = new UKitBaseFragment[]{mBirthdaySelectFragment, mGuardianFragment};

        toStep(1);
    }

    private void toStep(int step) {
        isStepOne = step == 1;
        showFragment(step - 1);
    }

    private void finish(int result) {
        setResult(result);
        finish();
    }

    @Override
    public void onClick(View v) {
        if (v == mBackBtn) {
            if (isStepOne) {
                finish(RESULT_CODE_NO);
            } else {
                toStep(1);
            }
        }
    }

    private void toRegisterNickname() {
        startActivity(new Intent(this, RegisterNicknameActivity.class));
    }

    private void showFragment(int position) {

        UKitBaseFragment next = mFragments[position];
        if (mCurrentFragment == next) {
            return;
        }

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        if (mCurrentFragment != null) {
            transaction.hide(mCurrentFragment);
        }
        if (!next.isAdded()) {
            transaction.add(R.id.gdpr_age_content_lyt, next).commitAllowingStateLoss();
        } else {
            transaction.show(next).commitAllowingStateLoss();
        }
        mCurrentFragment = next;
    }
}
