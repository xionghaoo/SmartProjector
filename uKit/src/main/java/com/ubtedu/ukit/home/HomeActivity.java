/**
 * ©2018, UBTECH Robotics, Inc. All rights reserved (C)
 **/
package com.ubtedu.ukit.home;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Keep;
import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager.widget.ViewPager;
import com.ubtedu.alpha1x.core.base.fragment.OnDialogFragmentDismissListener;
import com.ubtedu.alpha1x.utils.LogUtil;
import com.ubtedu.base.net.manager.NetworkManager;
import com.ubtedu.base.net.manager.NetworkObserver;
import com.ubtedu.ukit.R;
import com.ubtedu.ukit.ai.AiFragment;
import com.ubtedu.ukit.application.BasicEventDelegate;
import com.ubtedu.ukit.common.analysis.Events;
import com.ubtedu.ukit.common.analysis.UBTReporter;
import com.ubtedu.ukit.common.base.UKitBaseFragment;
import com.ubtedu.ukit.common.dialog.PromptDialogFragment;
import com.ubtedu.ukit.common.eventbus.ActivityOrientationEvent;
import com.ubtedu.ukit.common.eventbus.RegionChangeEvent;
import com.ubtedu.ukit.common.eventbus.UnityVisibilityEvent;
import com.ubtedu.ukit.common.cloud.AWSCloudStorage;
import com.ubtedu.ukit.common.flavor.Channel;
import com.ubtedu.ukit.common.flavor.Flavor;
import com.ubtedu.ukit.common.ota.APPUpgradeBadgeNotifier;
import com.ubtedu.ukit.common.view.badge.BadgeView;
import com.ubtedu.ukit.home.widget.MainTabItemView;
import com.ubtedu.ukit.launcher.LauncherDialogFragment;
import com.ubtedu.ukit.menu.MenuActivity;
import com.ubtedu.ukit.menu.device.DeviceSelectDialogFragment;
import com.ubtedu.ukit.menu.region.RegionInfo;
import com.ubtedu.ukit.menu.settings.Settings;
import com.ubtedu.ukit.menu.settings.TargetDevice;
import com.ubtedu.ukit.project.ProjectCompat;
import com.ubtedu.ukit.project.ProjectFragment;
import com.ubtedu.ukit.project.bridge.ActivityOrientationController;
import com.ubtedu.ukit.project.bridge.BridgeCommunicator;
import com.ubtedu.ukit.project.bridge.arguments.ConfigArguments;
import com.ubtedu.ukit.project.bridge.arguments.NetworkStateArguments;
import com.ubtedu.ukit.project.bridge.arguments.Unity3DArguments;
import com.ubtedu.ukit.project.bridge.functions.Unity3DFunctions;
import com.ubtedu.ukit.project.host.ProjectHostFragment;
import com.ubtedu.ukit.project.vo.OfficialProject;
import com.ubtedu.ukit.project.vo.Project;
import com.ubtedu.ukit.unity.UnityPlayerActivity;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.greenrobot.eventbus.EventBus.getDefault;

/**
 * @author qinicy
 * @date 2018/11/07
 */
public class HomeActivity extends UnityPlayerActivity<HomeContracts.Presenter, HomeContracts.UI> {
    public static final int TAB_POSITION_TUTORIAL = 0;
    public static final int TAB_POSITION_PROJECT = 1;
    public static final int TAB_POSITION_AI = 2;
    public static final String IS_SHARE_OPEN_KEY = "IS_SHARE_OPEN_KEY";
    public static final String SHARE_RUSULT_TIP_KEY = "SHARE_RUSULT_TIP_KEY";

    private static final String TAG = "==AWS==";
    private MainTabItemView mTutorialTab;
    private MainTabItemView mProjectTab;
    private MainTabItemView mAiTab;
    private MainTabItemView mCurrentTab;
    private View mSettingBtn;

    private ViewPager mHomeViewPager;
    private List<UKitBaseFragment> mHomeFragments;
    private ViewGroup mProjectContainerLyt;
    private ProjectHostFragment mProjectHostFragment;

    private boolean isInHomePage;
    private View mTabbarLyt;
    private ViewGroup mUnityContainerView;
    private View mUnityLoadingView;
    private ViewGroup mHomeUnityLyt;
    private LauncherDialogFragment mLauncherDialogFragment;
    private View mHomeLaunchMaskView;
    private ViewGroup mHomeRootLyt;

    private ActivityOrientationController mOrientationController;
    private Handler mHomeHandle;
    private boolean isRecreating;
    private BadgeView mBadgeView;
    private boolean isShowingLoginOverdueDialog;

    private NetworkObserver mNetworkObserver = new NetworkObserver() {
        @Override
        public void onNetworkStateChanged(boolean networkConnected, NetworkInfo currentNetwork, NetworkInfo lastNetwork) {
            if (BridgeCommunicator.getInstance().getUnity3DBridge().isCommunicable()) {
                BridgeCommunicator.getInstance().getUnity3DBridge().call(Unity3DFunctions.notifyNetworkStateChange, new Object[]{NetworkStateArguments.getNetworkState()}, null);
            }
        }
    };

    public static void open(Context context) {
        if (context != null) {
            Intent intent = new Intent(context, HomeActivity.class);
            context.startActivity(intent);
        }
    }

    public static void open(Context context,boolean isShareEnter, String resultTip){
        Intent intent = new Intent(context, HomeActivity.class);
        intent.putExtra(IS_SHARE_OPEN_KEY, isShareEnter);
        intent.putExtra(SHARE_RUSULT_TIP_KEY, resultTip);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            //屏蔽掉系统恢复
            savedInstanceState.clear();
        }
        super.onCreate(savedInstanceState);

        initAWSCloudService();
        recreateIfNeed();
        if (isRecreating) {
            return;
        }

        mUnityPlayer.setBlockQuit(false);
        mHomeHandle = new Handler();

        getDefault().register(this);
        BridgeCommunicator.getInstance().init();
        NetworkManager.getInstance().registerNetworkObserver(mNetworkObserver);
        isInHomePage = true;
        initFragments();
        initViews();

        // 不显示启动动画
        onLaunchCompleted();
//        if (isRegionChangedRecreate()) {
//            setRegionChangedRecreate(false);
//            onLaunchCompleted();
//        } else {
//            showLauncherDialog();
//        }

    }

    private void initAWSCloudService() {
        if (Flavor.getChannel().getId() == Channel.NA.getId()) {
            AWSCloudStorage.startService(this);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (isRecreating) {
            return;
        }
        //某些手机从后台回来，会竖屏
        onActivityOrientationEvent(new ActivityOrientationEvent(false));
        playCurrentTabAnimation();
        checkLoginOverdue();
        onHomeResume();
        APPUpgradeBadgeNotifier.addBadge(mBadgeView);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (isRecreating) {
            return;
        }
        mHomeHandle.removeCallbacksAndMessages(null);
        onHomePause();
    }

    public boolean inProject() {
        return !isInHomePage;
    }

    private void checkLoginOverdue() {
        if (isLauncherCompleted() && mUnityPlayer.isLoadingComplete()) {
            getPresenter().checkLoginOverdue();
        }
    }

    private void showLauncherDialog() {

        mLauncherDialogFragment = new LauncherDialogFragment();
        mLauncherDialogFragment.setDismissListener(new OnDialogFragmentDismissListener() {
            @Override
            public void onDismiss(Object... value) {
                mLauncherDialogFragment = null;
                onLaunchCompleted();

                dealShareProjectResult();
            }
        });
        mLauncherDialogFragment.show(getSupportFragmentManager(), "LauncherDialogFragment");

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (mLauncherDialogFragment != null) {
            mLauncherDialogFragment.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (mLauncherDialogFragment != null) {
            mLauncherDialogFragment.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (isRecreating) {
            return;
        }
        getDefault().unregister(this);
        BridgeCommunicator.getInstance().release();
    }

    private BasicEventDelegate getBasicEventDelegate() {
        if (getEventDelegate() instanceof BasicEventDelegate) {
            return (BasicEventDelegate) getEventDelegate();
        }
        return null;
    }

    private boolean isLauncherCompleted() {
        BasicEventDelegate delegate = getBasicEventDelegate();
        return delegate != null && delegate.isLauncherComplete();
    }

    private boolean isRegionChangedRecreate() {
        BasicEventDelegate delegate = getBasicEventDelegate();
        return delegate != null && delegate.isRegionChangedRecreate();
    }

    private void setRegionChangedRecreate(boolean changed) {
        BasicEventDelegate delegate = getBasicEventDelegate();
        if (delegate != null) {
            delegate.setRegionChangedRecreate(changed);
        }
    }

    private void setLauncherCompleted(boolean completed) {
        BasicEventDelegate delegate = getBasicEventDelegate();
        if (delegate != null) {
            delegate.setLauncherComplete(completed);
        }
    }

    private void recreateIfNeed() {
        BasicEventDelegate delegate = getBasicEventDelegate();
        if (delegate != null && delegate.isAppRestartBySystem()) {
            isRecreating = true;
            //需要更新重启状态，否则会陷入recreate死循环
            delegate.setAppRestartBySystem(false);
            recreate();

        }
    }

    private SurfaceView findSurfaceView() {
        if (mUnityPlayer == null) {
            return null;
        }
        int childCount = mUnityPlayer.getChildCount();
        for (int i = 0; i < childCount; i++) {
            View view = mUnityPlayer.getChildAt(i);
            if (view instanceof SurfaceView) {
                return (SurfaceView) view;
            }
        }
        return null;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onActivityOrientationEvent(ActivityOrientationEvent event) {
        if (mOrientationController == null) {
            mOrientationController = new ActivityOrientationController(this, ActivityInfo.SCREEN_ORIENTATION_USER_LANDSCAPE);
        }
        if (event.lock) {
            mOrientationController.lock();
        } else {
            mOrientationController.reverseOriginOrientation();
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onVisibilityEvent(UnityVisibilityEvent event) {
        synchronized (HomeActivity.class) {
            SurfaceView view = findSurfaceView();
            if (event.isShown) {
                if (view != null) {
                    view.setWillNotDraw(false);
                }
                mUnityPlayer.setVisibility(View.VISIBLE);
            } else {
                mUnityPlayer.setVisibility(View.INVISIBLE);
                if (view != null) {
                    view.setWillNotDraw(true);
                }
            }
        }
    }

    private void initViews() {
        setContentView(R.layout.activity_home);
        mHomeRootLyt = findViewById(R.id.home_root_lyt);
        mHomeLaunchMaskView = findViewById(R.id.home_launch_mask_view);
        mHomeUnityLyt = findViewById(R.id.home_unity_lyt);
        mUnityLoadingView = findViewById(R.id.home_unity_loading_lyt);
        mUnityContainerView = findViewById(R.id.home_unity_container);
        mUnityContainerView.addView(mUnityPlayer, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        if (mUnityPlayer.isLoadingComplete()) {
            BridgeCommunicator.getInstance().getUnity3DBridge().setBridgeCommunicable(true);
            onUnityLoadingComplete();
        }
        mHomeViewPager = findViewById(R.id.home_main_view_pager);
        HomePagerAdapter homePagerAdapter = new HomePagerAdapter(getSupportFragmentManager(), mHomeFragments);
        mHomeViewPager.setAdapter(homePagerAdapter);

        mTabbarLyt = findViewById(R.id.home_tab_lyt);
        mSettingBtn = findViewById(R.id.home_setting_btn);
        mBadgeView = new BadgeView(this);
        mBadgeView.bindTarget(mSettingBtn);
        mBadgeView.setBadgeBackgroundColor(getResources().getColor(R.color.badge_view_bg)).setBadgeText("");
        int offset = getResources().getDimensionPixelOffset(R.dimen.ubt_dimen_24px);
        mBadgeView.setGravityOffset(offset, false);
        mBadgeView.setShowShadow(false);
        mBadgeView.setName("Home");
        APPUpgradeBadgeNotifier.addBadge(mBadgeView);

        mProjectContainerLyt = findViewById(R.id.home_project_container_lyt);
        mTutorialTab = findViewById(R.id.home_tutorial_tab);
        mProjectTab = findViewById(R.id.home_project_tab);
        mAiTab = findViewById(R.id.home_ai_tab);
        mTutorialTab.setPosition(TAB_POSITION_TUTORIAL);
        mProjectTab.setPosition(TAB_POSITION_PROJECT);
        mAiTab.setPosition(TAB_POSITION_AI);

        bindSafeClickListener(mSettingBtn);
        bindSafeClickListener(mTutorialTab);
        bindSafeClickListener(mProjectTab);
        bindSafeClickListener(mAiTab);

        mTutorialTab.post(new Runnable() {
            @Override
            public void run() {
                mTutorialTab.setLottieAnimation(R.raw.course);
                mProjectTab.setLottieAnimation(R.raw.project);
                mAiTab.setLottieAnimation(R.raw.ai);
            }
        });

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onAppRegionChange(RegionChangeEvent event) {
        //需要通知Unity语言变化了
        ConfigArguments arguments = new ConfigArguments();
        String lang = Settings.getUnityLanguage();
        if (TextUtils.isEmpty(lang)) {
            lang = RegionInfo.LANGUAGE_CN;
        }
        arguments.language = lang;
        arguments.tagName = Settings.getUnityTag();

        BridgeCommunicator.getInstance().getUnity3DBridge().call(Unity3DFunctions.notifyConfigsChange, new Object[]{arguments}, null);

        //需要把UnityPlayer移除，避免下次重复加入到父布局中
        mUnityContainerView.removeView(mUnityPlayer);
        setRegionChangedRecreate(true);
        recreate();
    }


    @Override
    public void recreate() {
        LogUtil.d("");
        //防止UnityPlayer退出杀掉当前进程
        mUnityPlayer.setBlockQuit(true);
        BridgeCommunicator.getInstance().release();
        getPresenter().skipDisconnectBt(true);
//        APPUpgradeBadgeNotifier.clear();
        super.recreate();
    }

    private void initFragments() {
        ProjectFragment projectFragment = new ProjectFragment();
        AiFragment aiFragment = new AiFragment();
        mHomeFragments = new ArrayList<>();
        mHomeFragments.add(new TutorialFragment());
        mHomeFragments.add(projectFragment);
        mHomeFragments.add(aiFragment);
    }


    /**
     * 提供给Unity调用
     *
     * @param project
     */
    @Keep
    public void openOfficialProject(final Project project) {
        openProject(project);
    }

    public void openProject(Project project) {
        if (project == null) {
            project = new Project();
        }
        //如果版本不兼容，项目列表打开弹窗提示；官方示例则直接打开一个新的,并toast提示
        if (!project.isCompat() || !Settings.isTargetDevice(project.getTargetDevice())) {
            handleIncompatibility(project);
        } else {
            doOpenProject(project);
        }
    }

    private void handleIncompatibility(Project project) {
        if ((project instanceof OfficialProject)) {
            if (!project.isCompat()) {
                ProjectCompat.showOfficialCompatDialog(this, getSupportFragmentManager(), () -> doOpenProject(new Project()));
            }
            if (!Settings.isTargetDevice(project.getTargetDevice())) {
                ProjectCompat.showTargetDeviceCompatDialog(this, getSupportFragmentManager(), () -> doOpenProject(new Project()));
            }
        } else {
            ProjectCompat.showProjectCompatDialog(this, getSupportFragmentManager());
        }
    }


    private void doOpenProject(Project project) {
        isInHomePage = false;
        if (mProjectHostFragment == null) {
            mProjectHostFragment = new ProjectHostFragment();
        }
        mProjectHostFragment.setProject(project);
        toggleProjectFragments(true);
    }

    public void onHomeResume() {
        LogUtil.i("");
        UBTReporter.onPageStart("home");
    }


    public void onHomePause() {
        LogUtil.i("");

        UBTReporter.onPageEnd("home");
    }

    public void onProjectPageResume() {
        mHomeViewPager.setVisibility(View.INVISIBLE);
        toggleToggleShowHomeTabbar(false);
    }

    public void openHome() {

        isInHomePage = true;

        navigate(mProjectTab);

        toggleProjectFragments(false);

        BridgeCommunicator.getInstance().getUnity3DBridge().call(Unity3DFunctions.enterScene,
                new Object[]{Unity3DArguments.SCENE_MAIN}, null);

        mHomeViewPager.setVisibility(View.VISIBLE);
        toggleToggleShowHomeTabbar(true);
        onHomeResume();
    }

    public void onUnityLoadingComplete() {
        LogUtil.i("");
        mHomeUnityLyt.removeView(mUnityLoadingView);
        mUnityPlayer.setLoadingComplete(true);
        checkLoginOverdue();
        checkIfNeedShowDeviceSelectDialog();
    }

    private void onLaunchCompleted() {
        mHomeRootLyt.removeView(mHomeLaunchMaskView);
        getPresenter().otaUpgrade();
        setLauncherCompleted(true);
        checkLoginOverdue();
        getPresenter().loadUnityInfo();
        checkIfNeedShowDeviceSelectDialog();
        mHomeHandle.postDelayed(new Runnable() {
            @Override
            public void run() {
                getPresenter().reportActiveUserEvent();
            }
        }, 10000);
    }

    private void checkIfNeedShowDeviceSelectDialog() {
        if (isLauncherCompleted() && mUnityPlayer.isLoadingComplete()) {
            if (TargetDevice.NONE == Settings.getTargetDevice()) {
                mHomeHandle.postDelayed(() -> {
                    DeviceSelectDialogFragment fragment = new DeviceSelectDialogFragment();
                    fragment.show(getSupportFragmentManager(), "device-select");
                }, 200);

            }
        }
    }

    /**
     * 导入分享项目
     */
    private void dealShareProjectResult(){
        if(getIntent() != null){
            boolean isShareEnter = getIntent().getBooleanExtra(IS_SHARE_OPEN_KEY, false);
            if(isShareEnter){
                openHome();
                String resultTip = getIntent().getStringExtra(SHARE_RUSULT_TIP_KEY);
                getUIView().getUIDelegate().toastShort(resultTip);
            }
        }
    }

    @Keep
    public void toggleToggleShowHomeTabbar(final boolean isShow) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (mTabbarLyt != null) {
                    if (isShow) {
                        mTabbarLyt.setVisibility(View.VISIBLE);
                    } else {
                        mTabbarLyt.setVisibility(View.GONE);
                    }
                }

            }
        });

    }

    private void toggleProjectFragments(boolean isShow) {
        mProjectContainerLyt.setVisibility(isShow ? View.VISIBLE : View.GONE);
        if (mProjectHostFragment != null) {
            FragmentManager fm = getSupportFragmentManager();

            FragmentTransaction transaction = fm.beginTransaction();
            if (isShow) {
                mSettingBtn.setVisibility(View.GONE);
                transaction.replace(R.id.home_project_container_lyt, mProjectHostFragment);
            } else {
                mSettingBtn.setVisibility(View.VISIBLE);
                transaction.remove(mProjectHostFragment);
                mProjectHostFragment = null;
            }
            transaction.commitAllowingStateLoss();
        }
    }

    @Override
    public void onClick(View v, boolean isSafeClick) {
        super.onClick(v, isSafeClick);
        LogUtil.d(Thread.currentThread().getName());
        LogUtil.d("");
        if (v == mTutorialTab || v == mProjectTab || v == mAiTab) {
            navigate((MainTabItemView) v);
            //数据采集
            reportTabClickEvent(v);
        }
        if (v == mSettingBtn) {
            // 退出uKit
            exitUKit();

            //跳转到菜单不暂停Unity，避免切换语言黑屏
//            mUnityPlayer.blockLifecycleOneTime();
//            startActivity(new Intent(this, MenuActivity.class));
//            UBTReporter.onEvent(Events.Ids.app_setting_btn_click, null);
        }
    }

    private void exitUKit() {
        PromptDialogFragment dialog = PromptDialogFragment.newBuilder(this)
                .type(PromptDialogFragment.Type.NORMAL)
                .title("提示")
                .message("是否退出uKit？")
                .onPositiveClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mUnityContainerView.removeView(mUnityPlayer);
                        mUnityPlayer.setBlockQuit(true);
                        BridgeCommunicator.getInstance().release();
                        getPresenter().skipDisconnectBt(true);
                        finish();
                    }
                })
                .build();
        dialog.show(getSupportFragmentManager(), "delete project dialog");
    }

    private void reportTabClickEvent(View v) {
        Map<String, String> args = new HashMap<>(1);
        String eventId = Events.Ids.app_home_tab_click;
        String tabName = "tutorial";
        if (v == mProjectTab) {
            tabName = "project";
        }
        if (v == mAiTab) {
            tabName = "ai";
        }
        args.put("tab", tabName);
        UBTReporter.onEvent(eventId, args);
    }

    private void playCurrentTabAnimation() {
        if (mCurrentTab != null) {
            mCurrentTab.playAnimation();
        }
    }

    public void navigate(MainTabItemView tab) {
        mTutorialTab.setCheck(false);
        mProjectTab.setCheck(false);
        mAiTab.setCheck(false);
        mTutorialTab.clearAnimation();
        mProjectTab.clearAnimation();
        mAiTab.clearAnimation();
        tab.setCheck(true);
        tab.playAnimation();
        mHomeViewPager.setCurrentItem(tab.getPosition());
    }

    @Override
    protected HomeContracts.Presenter createPresenter() {
        return new HomePresenter();
    }

    @Override
    protected HomeContracts.UI createUIView() {
        return new HomeUI();
    }

    class HomeUI extends HomeContracts.UI {

    }

}