package com.ubtedu.ukit.main;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;

import com.tencent.bugly.beta.Beta;
import com.tencent.bugly.beta.UpgradeInfo;
import com.ubtedu.ukit.R;
import com.ubtedu.ukit.common.base.UKitBaseActivity;
import com.ubtedu.ukit.common.dialog.PromptDialogFragment;

/**
 * @Author qinicy
 * @Date 2019/6/10
 **/
public class UpgradeActivity extends UKitBaseActivity {
    private PromptDialogFragment mDialogFragment;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        showUpgradeDialog();
    }

    private void showUpgradeDialog() {
        UpgradeInfo info = Beta.getUpgradeInfo();
        if (info != null) {
            boolean forceUpgrade = info.upgradeType == 2;

            String message = "";
            mDialogFragment = PromptDialogFragment.newBuilder(this)
                    .type(PromptDialogFragment.Type.NORMAL)
                    .title(getString(R.string.menu_tab_about_app_upgrade))
                    .message(info.newFeature)
                    .cancelable(!forceUpgrade)
                    .showNegativeButton(!forceUpgrade)
                    .onPositiveClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                        }
                    })
                    .onDismissListener(new DialogInterface.OnDismissListener() {
                        @Override
                        public void onDismiss(DialogInterface dialog) {
                            finish();
                        }
                    })
                    .build();
            mDialogFragment.show(getSupportFragmentManager(), "UpgradeDialog");
        }

    }
}
