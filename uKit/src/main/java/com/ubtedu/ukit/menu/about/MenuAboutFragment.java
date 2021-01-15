/**
 * @2018 UBTECH Robotics Corp. All rights reserved (C)
 **/
package com.ubtedu.ukit.menu.about;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.ubtedu.alpha1x.utils.SharedPreferenceUtils;
import com.ubtedu.ukit.BuildConfig;
import com.ubtedu.ukit.R;
import com.ubtedu.ukit.application.ServerConfig;
import com.ubtedu.ukit.application.UKitApplication;
import com.ubtedu.ukit.common.actions.APPUpgradeAction;
import com.ubtedu.ukit.common.actions.CheckUpgradeAction;
import com.ubtedu.ukit.common.analysis.Events;
import com.ubtedu.ukit.common.analysis.UBTReporter;
import com.ubtedu.ukit.common.base.UKitBaseActivity;
import com.ubtedu.ukit.common.base.UKitBaseFragment;
import com.ubtedu.ukit.common.dialog.PromptDialogFragment;
import com.ubtedu.ukit.common.flavor.Flavor;
import com.ubtedu.ukit.common.ota.APPUpgradeBadgeNotifier;
import com.ubtedu.ukit.common.ota.AppMarketUtils;
import com.ubtedu.ukit.common.ota.UpgradeConstants;
import com.ubtedu.ukit.common.ota.UpgradeInfo;
import com.ubtedu.ukit.common.utils.VersionUtil;
import com.ubtedu.ukit.common.view.badge.BadgeView;
import com.ubtedu.ukit.user.vo.GdprUserPactInfo;

/**
 * @Author qinicy
 * @Date 2018/12/12
 **/
public class MenuAboutFragment extends UKitBaseFragment<MenuAboutContracts.Presenter, MenuAboutContracts.UI> {
    public final static String SP_UNITY_VERSION = "unity_version";
    private View mContractUsView;
    private View mTermsOfUseView;
    private View mPrivacyPolicyView;
    private TextView mVersionTv;
    private boolean isShowDevelopVersion;
    private View mLogoView;
    private long mTouchLogoStartTime;
    private View mBadeLyt;
    private BadgeView mBadgeView;
    private View mUpgradeLyt;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_menu_about, null);
        mLogoView = root.findViewById(R.id.menu_about_version_logo_view);
        mVersionTv = root.findViewById(R.id.menu_about_version_tv);
        mUpgradeLyt = root.findViewById(R.id.menu_about_version_upgrade_lyt);
        mContractUsView = root.findViewById(R.id.menu_about_contract_us_lyt);
        mTermsOfUseView = root.findViewById(R.id.menu_about_terms_of_use_lyt);
        mPrivacyPolicyView = root.findViewById(R.id.menu_about_privacy_policy_lyt);
        mBadeLyt = root.findViewById(R.id.menu_about_version_upgrade_badge_lyt);
        mBadgeView = new BadgeView(getContext());
        mBadgeView.bindTarget(mBadeLyt);
        mBadgeView.setBadgeBackgroundColor(getResources().getColor(R.color.badge_view_bg)).setBadgeText("");
        mBadgeView.setShowShadow(false);
        mBadgeView.setName("MenuAboutFragment");
        APPUpgradeBadgeNotifier.addBadge(mBadgeView);
        bindSafeClickListener(mLogoView);
        bindSafeClickListener(mUpgradeLyt);
        bindSafeClickListener(mContractUsView);
        bindSafeClickListener(mTermsOfUseView);
        bindSafeClickListener(mPrivacyPolicyView);

        updateAppVersionText(isShowDevelopVersion);

        mLogoView.setOnTouchListener(mDebugTouch);
        return root;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getPresenter().initGdprs();
    }

    private void updateAppVersionText(boolean develop) {
        String version = VersionUtil.generateAppStoreVersionName();

        if (develop) {
            String unityVersion = SharedPreferenceUtils.getInstance(getContext()).getStringValue(SP_UNITY_VERSION);
            final StringBuilder builder = new StringBuilder();
            builder.append("app:")
                    .append(VersionUtil.generateDevelopVersionName())
                    .append(".")
                    .append(BuildConfig.VERSION_CODE)
                    .append(".")
                    .append(ServerConfig.getServerEnvConfig().ordinal())
                    .append(",blockly:")
                    .append(getPresenter().getBlocklyVersion())
                    .append(",Unity:")
                    .append(unityVersion);

            version = builder.toString();
        }


        mVersionTv.setText(version);
        isShowDevelopVersion = develop;
    }

    @Override
    public void onClick(View v, boolean isSafeClick) {
        super.onClick(v, isSafeClick);
        if (v == mContractUsView) {
            ContractsDialogFragment dialogFragment = new ContractsDialogFragment();
            dialogFragment.setCancelable(true);
            dialogFragment.show(getFragmentManager(), "ContractsDialogFragment");
            UBTReporter.onEvent(Events.Ids.app_menu_contact_btn_click, null);
        }
        if (v == mTermsOfUseView) {
            getPresenter().toShowGdprPact(GdprUserPactInfo.GDPR_TYPE_TERMS_OF_USE);
            UBTReporter.onEvent(Events.Ids.app_menu_terms_of_usage_btn_click, null);
        }
        if (v == mPrivacyPolicyView) {
            getPresenter().toShowGdprPact(GdprUserPactInfo.GDPR_TYPE_PRIVACY_POLICY);
            UBTReporter.onEvent(Events.Ids.app_menu_privacy_policy_btn_click, null);
        }
        if (v == mLogoView) {
            updateAppVersionText(!isShowDevelopVersion);
        }
        if (v == mUpgradeLyt) {
            handleUpgradeClick();
        }
    }

    private void handleUpgradeClick() {
        UpgradeInfo info = SharedPreferenceUtils.getInstance(UKitApplication.getInstance()).getObjectValue(UpgradeConstants.SP_UPGRADE_INFO, UpgradeInfo.class);
        if (getActivity() instanceof UKitBaseActivity) {
            final UKitBaseActivity activity = (UKitBaseActivity) getActivity();
            if (info != null) {
                String version = info.versionName + "." + info.versionCode;
                String msg = getString(R.string.app_upgradle_desc, version);
                PromptDialogFragment.newBuilder(activity)
                        .type(PromptDialogFragment.Type.NORMAL)
                        .title(getString(R.string.app_upgradle_title))
                        .message(msg)
                        .positiveButtonText(getString(R.string.menu_upgrade_positive_text))
                        .negativeButtonText(getString(R.string.menu_upgrade_negative_text))
                        .cancelable(true)
                        .onPositiveClickListener(new PromptDialogFragment.OnConfirmClickListener() {
                            @Override
                            public boolean onClick() {
                                onUpgradeClick(activity);
                                return false;
                            }
                        })
                        .build()
                        .show(getFragmentManager(), "showAppUpgradleDialogFragment-menu");
            } else {
                if (activity.getEventDelegate() != null) {
                    activity.getEventDelegate().onAction(activity, new CheckUpgradeAction());
                }
            }
        }
    }

    private void onUpgradeClick(UKitBaseActivity activity) {
        if (AppMarketUtils.isMarketInstalled(Flavor.getChannel())) {
            AppMarketUtils.openMarket(Flavor.getChannel());
        } else {
            if (activity.getEventDelegate() != null) {
                activity.getEventDelegate().onAction(activity, new APPUpgradeAction());
            }
        }
    }

    private void showTermsOfUseDialog(String abstractText, String url) {
        GdprPactDialogFragment dialogFragment = GdprPactDialogFragment.newInstance(
                GdprUserPactInfo.GDPR_TYPE_TERMS_OF_USE, GdprPactDialogFragment.PACT_STYLE_TYPE_READ_ONLY, abstractText, url);
        dialogFragment.setCancelable(true);
        dialogFragment.show(getFragmentManager(), "GDPR_TYPE_TERMS_OF_USE");
    }

    private void showPrivacyPolicyDialog(String abstractText, String url) {
        GdprPactDialogFragment dialogFragment = GdprPactDialogFragment.newInstance(
                GdprUserPactInfo.GDPR_TYPE_PRIVACY_POLICY, GdprPactDialogFragment.PACT_STYLE_TYPE_READ_ONLY, abstractText, url);
        dialogFragment.setCancelable(true);
        dialogFragment.show(getFragmentManager(), "GDPR_TYPE_PRIVACY_POLICY");
    }

    @Override
    protected MenuAboutContracts.UI createUIView() {
        return new MenuAboutUI();
    }

    @Override
    protected MenuAboutContracts.Presenter createPresenter() {
        return new MenuAboutPresenter();
    }


    private View.OnTouchListener mDebugTouch = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                mTouchLogoStartTime = System.currentTimeMillis();
            } else if (event.getAction() == MotionEvent.ACTION_UP) {
                long touchTime = System.currentTimeMillis() - mTouchLogoStartTime;
                if (Math.abs(touchTime - 20 * 1000) < 2 * 1000) {
                    ServerConfigDialogFragment fragment = new ServerConfigDialogFragment();
                    fragment.show(getFragmentManager(), "ServerConfigDialogFragment");
                }

            }
            return false;
        }
    };

    class MenuAboutUI extends MenuAboutContracts.UI {

        @Override
        public void showGdprPactDialog(int type, String abstractText, String url) {
            if (type == GdprUserPactInfo.GDPR_TYPE_TERMS_OF_USE) {
                showTermsOfUseDialog(abstractText, url);
            } else {
                showPrivacyPolicyDialog(abstractText, url);
            }
        }
    }
}
