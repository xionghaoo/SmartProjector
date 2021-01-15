package com.ubtedu.ukit.user.gdpr;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;

import com.ubtedu.alpha1x.core.base.fragment.OnDialogFragmentDismissListener;
import com.ubtedu.alpha1x.utils.GsonUtil;
import com.ubtedu.ukit.common.base.UKitBaseActivity;
import com.ubtedu.ukit.menu.about.GdprPactDialogFragment;
import com.ubtedu.ukit.user.vo.GdprUserPactInfo;

import java.util.ArrayList;
import java.util.List;

@Deprecated
public class GdprPactActivity extends UKitBaseActivity<GdprPactContract.Presenter, GdprPactContract.UI> {


    public final static int AGREE_CODE = 10;
    public final static int REFUSE_CODE = 11;
    public final static int REQUEST_CODE = 20;
    private final static String UBT_SCHEME = "ubt";
    private final static String AGREE = "agree";
    private final static String PACT_INFO_KEY = "pact";
    private GdprUserPactInfo mTermsOfUse;
    private GdprUserPactInfo mPrivacyPolicy;


    public static void openWithResult(Activity activity, List<GdprUserPactInfo> mPactList) {
        if (activity == null || mPactList == null || mPactList.size() != 2) {
            return;
        }
        Intent intent = new Intent(activity, GdprPactActivity.class);
        intent.putExtra(PACT_INFO_KEY, GsonUtil.get().toJson(mPactList));
        activity.startActivityForResult(intent, REQUEST_CODE);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        View view = new View(this);
        view.setBackgroundColor(Color.TRANSPARENT);
        setContentView(view);

        String pactJson = getIntent().getStringExtra(PACT_INFO_KEY);

        ArrayList<GdprUserPactInfo> pactInfos = GsonUtil.get().toObjectList(pactJson, GdprUserPactInfo.class);
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

        if (mTermsOfUse == null && mPrivacyPolicy == null) {
            finish(REFUSE_CODE);
            return;
        }


        if (mTermsOfUse != null) {
            showTermsOfUseDialog();
        } else {
            showPrivacyPolicyDialog();
        }

    }

    private void showTermsOfUseDialog() {
        GdprPactDialogFragment dialogFragment = GdprPactDialogFragment.newInstance(
                GdprUserPactInfo.GDPR_TYPE_TERMS_OF_USE, GdprPactDialogFragment.PACT_STYLE_TYPE_NORMAL,mTermsOfUse.abstractText, mTermsOfUse.url);
        dialogFragment.setCancelable(false);
        dialogFragment.show(getSupportFragmentManager(), "GDPR_TYPE_TERMS_OF_USE");
        dialogFragment.setDismissListener(new OnDialogFragmentDismissListener() {
            @Override
            public void onDismiss(Object... value) {
                boolean agree = (boolean) value[0];
                if (agree) {
                    showPrivacyPolicyDialog();
                } else {
                    finish(REFUSE_CODE);
                }
            }
        });
    }

    private void showPrivacyPolicyDialog() {
        GdprPactDialogFragment dialogFragment = GdprPactDialogFragment.newInstance(
                GdprUserPactInfo.GDPR_TYPE_PRIVACY_POLICY,GdprPactDialogFragment.PACT_STYLE_TYPE_NORMAL, mPrivacyPolicy.abstractText, mPrivacyPolicy.url);
        dialogFragment.setCancelable(false);
        dialogFragment.show(getSupportFragmentManager(), "GDPR_TYPE_PRIVACY_POLICY");
        dialogFragment.setDismissListener(new OnDialogFragmentDismissListener() {
            @Override
            public void onDismiss(Object... value) {
                boolean agree = (boolean) value[0];
                if (agree) {
                    finish(AGREE_CODE);
                } else {
                    finish(REFUSE_CODE);
                }
            }
        });
    }

    public void finish(int result) {
        setResult(result);
        finish();
    }

    @Override
    protected GdprPactContract.Presenter createPresenter() {
        return new GdprPactPresenter();
    }

    @Override
    protected GdprPactContract.UI createUIView() {
        return new GdprPactUI();
    }

    class GdprPactUI extends GdprPactContract.UI {

    }

}
