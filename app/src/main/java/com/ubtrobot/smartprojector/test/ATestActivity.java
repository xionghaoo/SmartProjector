package com.ubtrobot.smartprojector.test;

import android.content.pm.ActivityInfo;
import android.os.Bundle;

import androidx.annotation.Nullable;

import com.tuya.smart.panel.base.activity.TYRCTSmartPanelActivity;

public class ATestActivity extends TYRCTSmartPanelActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        super.onCreate(savedInstanceState);


    }
}
