package com.ubtedu.ukit.bluetooth.peripheral.setting.modifymode;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ubtedu.ukit.R;
import com.ubtedu.ukit.common.base.UKitBaseActivity;
import com.ubtedu.ukit.project.controller.interfaces.IControllerItemClickListener;
import com.ubtedu.ukit.project.controller.settings.adapter.ControlItemAdapter;
import com.ubtedu.ukit.project.controller.widget.SteeringGearView;
import com.ubtedu.ukit.project.vo.ModelInfo;

import java.util.ArrayList;

/**
 * @Author naOKi
 * @Date 2018/12/04
 **/
public class PeripheralSettingModifyModeActivity extends UKitBaseActivity<PeripheralSettingModifyModeContracts.Presenter, PeripheralSettingModifyModeContracts.UI> {

    private ImageView mCloseIv;
    private Button mApplyIv;

    private RecyclerView mAngularList;
    private RecyclerView mWheelList;
    
    private ControlItemAdapter mAngularAdapter;
    private ControlItemAdapter mWheelAdapter;
    
    private ModelInfo mi = null;
    private ModelInfo originalMi = null;

    private static Intent makeSettingIntent(Context context) {
        Intent intent = new Intent(context, PeripheralSettingModifyModeActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        return intent;
    }
    
    public static void openSetting(Context context) {
        Intent intent = makeSettingIntent(context);
        context.startActivity(intent);
    }

    public static void openSetting(Fragment fragment, int requestCode) {
        Intent intent = makeSettingIntent(fragment.getContext());
        fragment.startActivityForResult(intent, requestCode);
    }

    public static void openSetting(Activity activity, int requestCode) {
        Intent intent = makeSettingIntent(activity);
        activity.startActivityForResult(intent, requestCode);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_peripheral_setting_modify_mode);
        initView();
        initData();
    }

    private void initView() {
        mAngularList = findViewById(R.id.project_peripheral_setting_steering_gear_angular_list);
        mWheelList = findViewById(R.id.project_peripheral_setting_steering_gear_wheel_list);

        mCloseIv = findViewById(R.id.peripheral_setting_modify_close_btn);
        mApplyIv = findViewById(R.id.peripheral_setting_modify_apply_btn);

        bindSafeClickListener(mCloseIv);
        bindSafeClickListener(mApplyIv);
    
        mAngularAdapter = new ControlItemAdapter();
        mAngularList.setOverScrollMode(View.OVER_SCROLL_NEVER);
        mAngularList.setAdapter(mAngularAdapter);
        mAngularList.setLayoutManager(new GridLayoutManager(this, 4));
        mAngularList.setHasFixedSize(true);
        mAngularAdapter.setItemClickListener(new IControllerItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                int id = mAngularAdapter.getItem(position);
                mi.addSteeringGearWheel(id);
                updateModelInfo();
            }
        });
    
        mWheelAdapter = new ControlItemAdapter();
        mWheelList.setOverScrollMode(View.OVER_SCROLL_NEVER);
        mWheelList.setAdapter(mWheelAdapter);
        mWheelList.setLayoutManager(new GridLayoutManager(this, 4));
        mWheelList.setHasFixedSize(true);
        mWheelAdapter.setItemClickListener(new IControllerItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                int id = mWheelAdapter.getItem(position);
                mi.addSteeringGearAngular(id);
                updateModelInfo();
            }
        });
    
        mAngularAdapter.setWorkMode(SteeringGearView.MODE_ANGULAR);
        mWheelAdapter.setWorkMode(SteeringGearView.MODE_WHEEL);
    }
    
    private void initData() {
        originalMi = ModelInfo.newInstance(getPresenter().getModelInfo());
        mi = ModelInfo.newInstance(originalMi);
        updateModelInfo();
    }
    
    private void updateModelInfo() {
        ModelInfo mi1, mi2;
        mi1 = ModelInfo.newInstance(mi);
        mi2 = ModelInfo.newInstance(mi);
        mAngularAdapter.setModelInfo(mi1);
        mWheelAdapter.setModelInfo(mi2);
        if(mi1.steeringGearAngular.size() != originalMi.steeringGearAngular.size()
                || !mi1.steeringGearAngular.containsAll(originalMi.steeringGearAngular)) {
            ArrayList<Integer> list1 = mi1.steeringGearAngular;
            ArrayList<Integer> list2 = mi2.steeringGearWheel;
            list1.removeAll(originalMi.steeringGearAngular);
            list2.removeAll(originalMi.steeringGearWheel);
            mAngularAdapter.setShakeItem(list1);
            mWheelAdapter.setShakeItem(list2);
            mApplyIv.setEnabled(true);
        } else {
            mAngularAdapter.cleanShakeItem();
            mWheelAdapter.cleanShakeItem();
            mApplyIv.setEnabled(false);
        }
    }
    
    @Override
    public void onClick(View v, boolean isSafeClick) {
        if(v == mCloseIv) {
            finish();
        } else if(v == mApplyIv) {
            setResult(RESULT_OK);
            getPresenter().updateModelInfo(mi);
            originalMi = ModelInfo.newInstance(mi);
            updateModelInfo();
            finish();
        }
    }

    @Override
    protected PeripheralSettingModifyModePresenter createPresenter() {
        return new PeripheralSettingModifyModePresenter();
    }

    @Override
    protected PeripheralSettingModifyModeUI createUIView() {
        return new PeripheralSettingModifyModeUI();
    }

    class PeripheralSettingModifyModeUI extends PeripheralSettingModifyModeContracts.UI {

    }

}
