/**
 * @2018 UBTECH Robotics Corp. All rights reserved (C)
 **/
package com.ubtedu.ukit.user;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;

import com.ubtedu.ukit.R;
import com.ubtedu.ukit.common.base.UKitBaseActivity;
import com.ubtedu.ukit.common.eventbus.RegionChangeEvent;
import com.ubtedu.ukit.menu.MenuActivity;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

/**
 * @Author qinicy
 * @Date 2018/11/7
 **/
public class TestActivity extends UKitBaseActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        finish();
        setContentView(R.layout.activity_test2);
        EventBus.getDefault().register(this);
        findViewById(R.id.btn_assetview_postcomment).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(TestActivity.this,MenuActivity.class));
            }
        });
    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onAppLanguageChange(RegionChangeEvent event) {
        recreate();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
    public void test(String x){}
}
