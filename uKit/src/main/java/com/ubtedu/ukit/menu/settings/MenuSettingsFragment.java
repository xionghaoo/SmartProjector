/**
 * @2018 UBTECH Robotics Corp. All rights reserved (C)
 **/
package com.ubtedu.ukit.menu.settings;

import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.ubtedu.alpha1x.utils.FileUtil;
import com.ubtedu.base.net.manager.NetworkManager;
import com.ubtedu.ukit.R;
import com.ubtedu.ukit.bluetooth.peripheral.setting.modifyid.PeripheralSettingModifyIdActivity;
import com.ubtedu.ukit.bluetooth.utils.LocationServiceHelper;
import com.ubtedu.ukit.bluetooth.wifi.BatchWifiConfigActivity;
import com.ubtedu.ukit.common.analysis.Events;
import com.ubtedu.ukit.common.analysis.UBTReporter;
import com.ubtedu.ukit.common.base.UKitBaseFragment;
import com.ubtedu.ukit.common.files.FileHelper;

import java.math.RoundingMode;
import java.text.NumberFormat;
import java.util.HashMap;
import java.util.Map;


/**
 * @Author qinicy
 * @Date 2018/12/12
 **/
public class MenuSettingsFragment extends UKitBaseFragment {
    private View mClearCacheView;
    private TextView mCacheSizeTv;
    private View mModifyIdView;
    private View mLanguageView;
    private View mBatchConfigWifiView;
    private CheckBox mChargingProtectionCb;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_menu_settings, null);
        mClearCacheView = root.findViewById(R.id.menu_settings_clear_cache_lyt);
        mCacheSizeTv = root.findViewById(R.id.menu_settings_clear_cache_size_tv);
        mModifyIdView = root.findViewById(R.id.menu_settings_modify_id_lyt);
        mLanguageView = root.findViewById(R.id.menu_settings_language_lyt);
        mBatchConfigWifiView = root.findViewById(R.id.menu_settings_batch_config_wifi_lyt);
        mChargingProtectionCb = root.findViewById(R.id.menu_settings_charging_protection_cb);
        bindSafeClickListener(mClearCacheView);
        bindSafeClickListener(mModifyIdView);
        bindSafeClickListener(mLanguageView);
        bindSafeClickListener(mBatchConfigWifiView);
        boolean isChargingProtection = Settings.isChargingProtection();
        mChargingProtectionCb.setChecked(isChargingProtection);
        mChargingProtectionCb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                Settings.setChargingProtectionState(isChecked);
                Map<String, String> args = new HashMap<>(1);
                args.put("checked", Boolean.valueOf(isChecked).toString());
                UBTReporter.onEvent(Events.Ids.app_menu_charging_protection_btn_click, args);

            }
        });
        calculateCacheSizes();
        refreshView();
        return root;

    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        refreshView();
    }

    private void refreshView() {
        mBatchConfigWifiView.setVisibility(Settings.isTargetDevice(TargetDevice.UKIT2) ? View.VISIBLE : View.GONE);
    }

    @Override
    public void onClick(View v, boolean isSafeClick) {
        super.onClick(v, isSafeClick);
        if (v == mClearCacheView) {
            clearCache();
        }
        if (v == mModifyIdView) {
            startActivity(new Intent(getContext(), PeripheralSettingModifyIdActivity.class));
            UBTReporter.onEvent(Events.Ids.app_menu_modify_id_btn_click, null);
        }
        if (v == mBatchConfigWifiView) {
            if (NetworkManager.getInstance().isNetworkConnected() && NetworkManager.getInstance().getCurrentNetwork().getType() == ConnectivityManager.TYPE_WIFI) {
                if (LocationServiceHelper.isCompactLocationServiceAvailable(getActivity())) {
                    startActivity(new Intent(getContext(), BatchWifiConfigActivity.class));
                } else {
                    LocationServiceHelper.openLocationServiceDialog(getActivity());
                }
            } else {
                getUIDelegate().toastShort(getString(R.string.batch_config_wifi_mobile_wifi_none_msg));
            }
        }
    }

    private void calculateCacheSizes() {
        double size = FileUtil.getFileOrFilesSize(FileHelper.getCacheRootPath(), 3);
        NumberFormat df = NumberFormat.getNumberInstance(Settings.getLocale());
        // 保留两位小数
        df.setMaximumFractionDigits(2);
        // 如果不需要四舍五入，可以使用RoundingMode.DOWN
        df.setRoundingMode(RoundingMode.UP);
        String sizeString = df.format(size) + " MB";

        if (size == 0) {
            sizeString = "0 MB";
        }
        mCacheSizeTv.setText(sizeString);
    }

    private void clearCache() {

        double size = FileUtil.getFileOrFilesSize(FileHelper.getCacheRootPath(), 3);
        if (size > 0) {
            FileUtil.deleteFolderFile(FileHelper.getCacheRootPath(), false);
            getUIDelegate().toastShort(getString(R.string.menu_tab_settings_cache_success));
            calculateCacheSizes();
        }
    }
}
