/**
 * @2018 UBTECH Robotics Corp. All rights reserved (C)
 **/
package com.ubtedu.ukit.menu;

import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.ubtedu.alpha1x.utils.LogUtil;
import com.ubtedu.ukit.R;
import com.ubtedu.ukit.common.analysis.Events;
import com.ubtedu.ukit.common.analysis.UBTReporter;
import com.ubtedu.ukit.common.base.UKitBaseActivity;
import com.ubtedu.ukit.common.base.UKitBaseFragment;
import com.ubtedu.ukit.common.eventbus.RegionChangeEvent;
import com.ubtedu.ukit.common.flavor.Channel;
import com.ubtedu.ukit.common.flavor.Flavor;
import com.ubtedu.ukit.common.ota.APPUpgradeBadgeNotifier;
import com.ubtedu.ukit.common.view.badge.BadgeView;
import com.ubtedu.ukit.menu.about.MenuAboutFragment;
import com.ubtedu.ukit.menu.account.MenuAccountFragment;
import com.ubtedu.ukit.menu.device.DeviceSelectFragment;
import com.ubtedu.ukit.menu.feedback.MenuFeedbackFragment;
import com.ubtedu.ukit.menu.help.MenuHelpFragment;
import com.ubtedu.ukit.menu.region.MenuRegionFragment;
import com.ubtedu.ukit.menu.region.RegionInfo;
import com.ubtedu.ukit.menu.settings.MenuSettingsFragment;
import com.ubtedu.ukit.menu.settings.Settings;

import org.greenrobot.eventbus.EventBus;

import java.util.HashMap;
import java.util.Map;

/**
 * @author qinicy
 * @date 2018/11/29
 */
public class MenuActivity extends UKitBaseActivity<MenuContracts.Presenter, MenuContracts.UI> {
    private final static String ORIGIN_LANGUAGE = "ORIGIN_LANGUAGE";
    private final static String[] FRAGMENT_TAGS;
    private UKitBaseFragment[] mFragments;
    private RadioGroup mRadioGroup;
    private UKitBaseFragment mCurrentFragment;
    private MenuRegionFragment mLanguageFragment;
    private View mBackBtn;
    private RegionInfo mOriginRegion;

    private RadioButton mAboutBtn;

    static {
        FRAGMENT_TAGS = new String[7];
        for (int i = 0; i < FRAGMENT_TAGS.length; i++) {
            FRAGMENT_TAGS[i] = "menu_fragment" + i;
        }
    }

    private View mAvatarLyt;
    private View mAvatarViewTv;
    private View mAvatarModifyTv;
    private MenuAccountFragment mMenuAccountFragment;
    private BadgeView mBadgeView;

    private View mDeviceBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        if (savedInstanceState != null) {
            mOriginRegion = (RegionInfo) savedInstanceState.getSerializable(ORIGIN_LANGUAGE);
        }
        if (mOriginRegion == null) {
            mOriginRegion = Settings.getRegion();
        }
        mBackBtn = findViewById(R.id.menu_back_btn);

        mAvatarLyt = findViewById(R.id.menu_modify_avatar_lyt);
        mAvatarViewTv = findViewById(R.id.menu_modify_avatar_view_tv);
        mAvatarModifyTv = findViewById(R.id.menu_modify_avatar_modify_tv);

        mDeviceBtn=findViewById(R.id.menu_sidebar_device);
        if (Flavor.getChannel().getId() == Channel.NA.getId()){
            mDeviceBtn.setVisibility(View.GONE);
        }

        mAboutBtn = findViewById(R.id.menu_sidebar_about);
        createBadgeView();
        APPUpgradeBadgeNotifier.addBadge(mBadgeView);
        bindSafeClickListener(mBackBtn);
        bindSafeClickListener(mAvatarLyt);
        bindSafeClickListener(mAvatarViewTv);
        bindSafeClickListener(mAvatarModifyTv);


        mRadioGroup = findViewById(R.id.menu_sidebar_radiogroup);
        final RadioGroup.OnCheckedChangeListener onCheckedChangeListener = new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                int position;
                String tab = null;
                if (checkedId == R.id.menu_sidebar_account) {
                    position = 0;
                    tab = "account";
                } else if (checkedId == R.id.menu_sidebar_settings) {
                    position = 1;
                    tab = "settings";
                } else if (checkedId == R.id.menu_sidebar_feedback) {
                    position = 2;
                    tab = "feedback";
                } else if (checkedId == R.id.menu_sidebar_help) {
                    position = 3;
                    tab = "help";
                } else if (checkedId == R.id.menu_sidebar_about) {
                    position = 4;
                    tab = "about";
                } else if (checkedId == R.id.menu_sidebar_language) {
                    position = 5;
                    tab = "region";
                } else if (checkedId == R.id.menu_sidebar_device) {
                    position = 6;
                    tab = "device";
                } else {
                    position = 0;
                }
                if (checkedId != R.id.menu_sidebar_about) {
                    mAboutBtn.setChecked(false);
                }
                showFragment(position);
                reportTabClickEvent(tab);
            }
        };
        mRadioGroup.setOnCheckedChangeListener(onCheckedChangeListener);

        mAboutBtn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    mRadioGroup.clearCheck();
                }
                mAboutBtn.setChecked(isChecked);
                onCheckedChangeListener.onCheckedChanged(mRadioGroup, R.id.menu_sidebar_about);
            }
        });
        initFragments(savedInstanceState);
    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    public MenuAccountFragment getMenuAccountFragment() {
        return mMenuAccountFragment;
    }

    private void createBadgeView() {
        mBadgeView = new BadgeView(this);
        mBadgeView.bindTarget(mAboutBtn);
        mBadgeView.setBadgeBackgroundColor(getResources().getColor(R.color.badge_view_bg)).setBadgeText("");
        mBadgeView.setShowShadow(false);
        int offsetX = getResources().getDimensionPixelOffset(R.dimen.ubt_dimen_10px);
        int offsetY = getResources().getDimensionPixelOffset(R.dimen.ubt_dimen_20px);
        mBadgeView.setGravityOffset(offsetX, offsetY, false);
        mBadgeView.setName("MenuActivity");
    }

    private void reportTabClickEvent(String tab) {
        Map<String, String> args = new HashMap<>(1);
        args.put("tab", tab);
        UBTReporter.onEvent(Events.Ids.app_menu_tab_click, args);
    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable(ORIGIN_LANGUAGE, mOriginRegion);
    }

    @Override
    public void onClick(View v, boolean isSafeClick) {
        super.onClick(v, isSafeClick);
        if (v == mBackBtn) {
            onBack();
        }
        if (v == mAvatarLyt) {
            showAvatarLayout(false);
        }
        if (v == mAvatarViewTv) {
            if (mMenuAccountFragment != null) {
                mMenuAccountFragment.viewAvatar();
            }
            showAvatarLayout(false);
        }
        if (v == mAvatarModifyTv) {
            if (mMenuAccountFragment != null) {
                mMenuAccountFragment.modifyAvatar();
            }
            showAvatarLayout(false);
            UBTReporter.onEvent(Events.Ids.app_menu_avatar_btn_click, null);
        }
    }

    public void showAvatarLayout(boolean show) {
        mAvatarLyt.setVisibility(show ? View.VISIBLE : View.GONE);
    }

    private void initFragments(Bundle savedInstanceState) {

        mFragments = new UKitBaseFragment[7];

        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();

        if (savedInstanceState == null) {

            mFragments[0] = new MenuAccountFragment();
            mFragments[1] = new MenuSettingsFragment();
            mFragments[2] = new MenuFeedbackFragment();
            mFragments[3] = new MenuHelpFragment();
            mFragments[4] = new MenuAboutFragment();
            mFragments[5] = new MenuRegionFragment();
            mFragments[6] = new DeviceSelectFragment();

            int position = 0;
            ft.add(R.id.menu_content_lyt, mFragments[position], FRAGMENT_TAGS[position]);
            ft.commit();
            mCurrentFragment = mFragments[position];
        } else {
            UKitBaseFragment fragment0 = (UKitBaseFragment) fm.findFragmentByTag(FRAGMENT_TAGS[0]);
            UKitBaseFragment fragment1 = (UKitBaseFragment) fm.findFragmentByTag(FRAGMENT_TAGS[1]);
            UKitBaseFragment fragment2 = (UKitBaseFragment) fm.findFragmentByTag(FRAGMENT_TAGS[2]);
            UKitBaseFragment fragment3 = (UKitBaseFragment) fm.findFragmentByTag(FRAGMENT_TAGS[3]);
            UKitBaseFragment fragment4 = (UKitBaseFragment) fm.findFragmentByTag(FRAGMENT_TAGS[4]);
            UKitBaseFragment fragment5 = (UKitBaseFragment) fm.findFragmentByTag(FRAGMENT_TAGS[5]);
            UKitBaseFragment fragment6 = (UKitBaseFragment) fm.findFragmentByTag(FRAGMENT_TAGS[6]);

            mFragments[0] = fragment0 != null ? fragment0 : new MenuAccountFragment();
            mFragments[1] = fragment1 != null ? fragment1 : new MenuSettingsFragment();
            mFragments[2] = fragment2 != null ? fragment2 : new MenuFeedbackFragment();
            mFragments[3] = fragment3 != null ? fragment3 : new MenuHelpFragment();
            mFragments[4] = fragment4 != null ? fragment4 : new MenuAboutFragment();
            mFragments[5] = fragment5 != null ? fragment5 : new MenuRegionFragment();
            mFragments[6] = fragment6 != null ? fragment6 : new DeviceSelectFragment();

            int position = 0;

            for (int i = 0; i < mFragments.length; i++) {
                if (i == position) {
                    ft.show(mFragments[position]);
                } else {
                    ft.hide(mFragments[position]);
                }
            }
            ft.commit();
            mCurrentFragment = mFragments[position];
        }

        mMenuAccountFragment = (MenuAccountFragment) mFragments[0];
        mLanguageFragment = (MenuRegionFragment) mFragments[5];
    }

    private void showFragment(int position) {

        UKitBaseFragment next = mFragments[position];
        if (mCurrentFragment == next) {
            return;
        }

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        if (!next.isAdded()) {
            transaction.hide(mCurrentFragment)
                    .add(R.id.menu_content_lyt, next, FRAGMENT_TAGS[position]).commit();
        } else {
            transaction.hide(mCurrentFragment).show(next).commit();
        }
        mCurrentFragment = next;
    }

    private void onBack() {

        RegionInfo region = Settings.getRegion();
        boolean isRegionChanged = !mOriginRegion.name.equals(region.name);
        boolean isLanguageChanged = mOriginRegion.locale != null && region.locale != null &&
                !mOriginRegion.locale.equals(region.locale);
        LogUtil.d("isRegionChanged:" + isRegionChanged + "  isLanguageChanged:" + isLanguageChanged);

        //避免多次切换语言发出通知
        if (isRegionChanged) {
            RegionChangeEvent event = new RegionChangeEvent();
            event.isLanguageChanged = isLanguageChanged;
            EventBus.getDefault().post(event);
        }
        finish();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        onBack();
    }
}