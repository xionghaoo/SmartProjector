/**
 * @2018 UBTECH Robotics Corp. All rights reserved (C)
 **/
package com.ubtedu.ukit.user.gdpr;

import androidx.appcompat.app.AppCompatActivity;

import com.ubtedu.alpha1x.core.base.fragment.OnDialogFragmentDismissListener;
import com.ubtedu.ukit.menu.about.GdprPactDialogFragment;
import com.ubtedu.ukit.user.vo.GdprUserPactInfo;

import java.util.List;

/**
 * @Author qinicy
 * @Date 2019/3/1
 **/
public class GdprPactHandler {

    private AppCompatActivity mActivity;
    private GdprUserPactInfo mTermsOfUse;
    private GdprUserPactInfo mPrivacyPolicy;
    private OnGdprPactCallback mCallback;

    public GdprPactHandler(AppCompatActivity activity) {
        mActivity = activity;
    }


    public void start(List<GdprUserPactInfo> pactInfos, OnGdprPactCallback callback) {
        mCallback = callback;
        if (pactInfos != null && pactInfos.size() == 2) {
            for (int i = 0; i < 2; i++) {
                if (pactInfos.get(i).type == GdprUserPactInfo.GDPR_TYPE_TERMS_OF_USE) {
                    mTermsOfUse = pactInfos.get(i);
                }
                if (pactInfos.get(i).type == GdprUserPactInfo.GDPR_TYPE_PRIVACY_POLICY) {
                    mPrivacyPolicy = pactInfos.get(i);
                }
            }
        }
        if (mTermsOfUse != null && mPrivacyPolicy != null) {
            showPrivacyPolicyDialog();
        }
    }

    private void showTermsOfUseDialog() {
        GdprPactDialogFragment dialogFragment = GdprPactDialogFragment.newInstance(
                GdprUserPactInfo.GDPR_TYPE_TERMS_OF_USE, GdprPactDialogFragment.PACT_STYLE_TYPE_NORMAL, mTermsOfUse.abstractText, mTermsOfUse.url);
        dialogFragment.setCancelable(false);
        dialogFragment.show(mActivity.getSupportFragmentManager(), "GDPR_TYPE_TERMS_OF_USE");
        dialogFragment.setDismissListener(new OnDialogFragmentDismissListener() {
            @Override
            public void onDismiss(Object... value) {
                boolean agree = (boolean) value[0];
                finish(agree);
            }
        });
    }

    private void showPrivacyPolicyDialog() {
        GdprPactDialogFragment dialogFragment = GdprPactDialogFragment.newInstance(
                GdprUserPactInfo.GDPR_TYPE_PRIVACY_POLICY, GdprPactDialogFragment.PACT_STYLE_TYPE_NORMAL, mPrivacyPolicy.abstractText, mPrivacyPolicy.url);
        dialogFragment.setCancelable(false);
        dialogFragment.show(mActivity.getSupportFragmentManager(), "GDPR_TYPE_PRIVACY_POLICY");
        dialogFragment.setDismissListener(new OnDialogFragmentDismissListener() {
            @Override
            public void onDismiss(Object... value) {
                boolean agree = (boolean) value[0];
                if (agree) {
                    showTermsOfUseDialog();
                } else {
                    finish(false);
                }
            }
        });
    }

    private void finish(boolean isAccepted) {
        if (mCallback != null) {
            mCallback.onResult(isAccepted);
        }
    }

    public interface OnGdprPactCallback {
        void onResult(boolean isAccepted);
    }
}
