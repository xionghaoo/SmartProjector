/**
 * @2018 UBTECH Robotics Corp. All rights reserved (C)
 **/
package com.ubtedu.ukit.project.host;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.LongSparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.ubtedu.alpha1x.core.base.fragment.OnDialogFragmentDismissListener;
import com.ubtedu.alpha1x.utils.GsonUtil;
import com.ubtedu.alpha1x.utils.LogUtil;
import com.ubtedu.base.net.rxretrofit.exception.RxException;
import com.ubtedu.base.net.rxretrofit.subscriber.SimpleRxSubscriber;
import com.ubtedu.bridge.BridgeBoolean;
import com.ubtedu.bridge.BridgeImpl;
import com.ubtedu.bridge.BridgeResult;
import com.ubtedu.bridge.BridgeResultCode;
import com.ubtedu.ukit.R;
import com.ubtedu.ukit.bluetooth.BluetoothHelper;
import com.ubtedu.ukit.common.analysis.Events;
import com.ubtedu.ukit.common.analysis.UBTReporter;
import com.ubtedu.ukit.common.base.UKitBaseFragment;
import com.ubtedu.ukit.common.dialog.PromptDialogFragment;
import com.ubtedu.ukit.common.eventbus.UnityVisibilityEvent;
import com.ubtedu.ukit.common.eventbus.VisibilityEvent;
import com.ubtedu.ukit.common.utils.UuidUtil;
import com.ubtedu.ukit.home.HomeActivity;
import com.ubtedu.ukit.home.HomePagerAdapter;
import com.ubtedu.ukit.project.MotionDesignFragment;
import com.ubtedu.ukit.project.ProjectListDialogFragment;
import com.ubtedu.ukit.project.ProjectSaveDialogFragment;
import com.ubtedu.ukit.project.ProjectShareDialogFragment;
import com.ubtedu.ukit.project.UserDataSynchronizer;
import com.ubtedu.ukit.project.blockly.BlocklyFragment;
import com.ubtedu.ukit.project.bridge.BridgeCommunicator;
import com.ubtedu.ukit.project.bridge.MotionDesigner;
import com.ubtedu.ukit.project.bridge.RxBridge;
import com.ubtedu.ukit.project.bridge.api.ActivityHelper;
import com.ubtedu.ukit.project.bridge.arguments.MotionArguments;
import com.ubtedu.ukit.project.bridge.functions.BlocklyFunctions;
import com.ubtedu.ukit.project.bridge.functions.Unity3DFunctions;
import com.ubtedu.ukit.project.controller.ControllerFragment;
import com.ubtedu.ukit.project.controller.manager.ControllerManager;
import com.ubtedu.ukit.project.doodle.DoodleFragment;
import com.ubtedu.ukit.project.vo.Blockly;
import com.ubtedu.ukit.project.vo.Controller;
import com.ubtedu.ukit.project.vo.DataWrapper;
import com.ubtedu.ukit.project.vo.Doodle;
import com.ubtedu.ukit.project.vo.Motion;
import com.ubtedu.ukit.project.vo.Project;
import com.ubtedu.ukit.project.vo.ProjectFile;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.functions.Function4;

import static com.ubtedu.ukit.project.controller.utils.ControllerConstValue.DEFAULT_ANIMATION_DURATION;


/**
 * 容纳我的项目下各个模块的宿主fragment
 *
 * @Author qinicy
 * @Date 2018/11/9
 **/
public class ProjectHostFragment extends UKitBaseFragment<ProjectHostContracts.Presenter, ProjectHostContracts.UI> {
    private static final String PROJECT_KEY = "project";
    public final static int POSITION_BLOCKLY = 0;
    public final static int POSITION_MOTION = 1;
    public final static int POSITION_CONTROLLER = 2;
    public final static int POSITION_DOODLE = 3;
    private final static int BRIDGE_API_TIMEOUT = 5;
    private RadioGroup mTabRadioGroup;
    private CustomScrollViewPager mProjectViewPager;
    private List<UKitBaseFragment> mProjectFragments;
    private HomePagerAdapter mProjectPagerAdapter;
    private Project mSourceProject;
    private Project mWorkspaceProject;
    private boolean isNeedExitAfterSave;
    private boolean isNeedShareAfterSave;
    private View mSaveBtn;
    private View mBluetoothBtn;
    private View mListBtn;
    private View mShareBtn;
    private View mBackBtn;
    private ControllerFragment mControllerFragment;
    private DoodleFragment mDoodleFragment;
    private View mTabbarLyt;
    private int mCurrentTabPosition;
    private int mDefaultPosition = POSITION_BLOCKLY;
    private boolean mShownMenu = true;
    private AnimatorSet mAnimatorSet;

    private ProjectFile mCurrentBlocklyFile;
    private ProjectFile mCurrentMotionFile;
    private ProjectFile mCurrentContollerFile;
    private ProjectFile mCurrentDoodleFile;
    private BlocklyFragment mBlocklyFragment;
    private View mLoadingLyt;

    private Handler mHandler;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        LogUtil.d("savedInstanceState:" + savedInstanceState);
        if (savedInstanceState != null) {
            String json = savedInstanceState.getString(PROJECT_KEY);
            mSourceProject = GsonUtil.get().toObject(json, Project.class);
            LogUtil.d("mSourceProject:" + mSourceProject);
        }
        super.onCreate(savedInstanceState);

        UserDataSynchronizer.getInstance().cancel();
        initFragments();
        MotionDesigner.getInstance().reset();
        EventBus.getDefault().register(this);
        mHandler = new Handler(Looper.getMainLooper());

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_project_host, null);
        mLoadingLyt = rootView.findViewById(R.id.fragment_host_loading_lyt);
        mTabbarLyt = rootView.findViewById(R.id.project_tab_lyt);
        mBackBtn = rootView.findViewById(R.id.project_back_btn);
        bindClickListener(mBackBtn);
        mBluetoothBtn = rootView.findViewById(R.id.project_bluetooth_btn);
        bindSafeClickListener(mBluetoothBtn);
        mSaveBtn = rootView.findViewById(R.id.project_save_btn);
        bindSafeClickListener(mSaveBtn);
        mListBtn = rootView.findViewById(R.id.project_list_btn);
        bindSafeClickListener(mListBtn);
        mShareBtn = rootView.findViewById(R.id.project_share_btn);
        bindSafeClickListener(mShareBtn);

        mProjectViewPager = rootView.findViewById(R.id.project_view_pager);
        mProjectViewPager.setOffscreenPageLimit(4);
        mProjectPagerAdapter = new HomePagerAdapter(getChildFragmentManager(), mProjectFragments);
        mProjectViewPager.setAdapter(mProjectPagerAdapter);
        mTabRadioGroup = rootView.findViewById(R.id.project_tab_radiogroup);
        mTabRadioGroup.post(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < mTabRadioGroup.getChildCount(); i++) {
                    RadioButton button = (RadioButton) mTabRadioGroup.getChildAt(i);
                    Drawable[] drawables = button.getCompoundDrawables();
                    if (drawables != null && drawables.length >= 2 && drawables[1] != null) {
                        Drawable drawable = drawables[1];
                        int w = getResources().getDimensionPixelOffset(R.dimen.ubt_dimen_124px);
                        int h = getResources().getDimensionPixelOffset(R.dimen.ubt_dimen_108px);
                        drawable.setBounds(0, 0, w, h);
                        button.setCompoundDrawables(null, drawable, null, null);
                    }
                }
            }
        });
        mTabRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                // RadioGroup setEnabled(false)之后还是可以响应onCheckedChanged，因为RadioButton没有禁用
                if (!group.isEnabled()) {
                    //还原选中成mCurrentTabPosition对应的RadioButton
                    int id;
                    switch (mCurrentTabPosition) {
                        case POSITION_BLOCKLY:
                            id = R.id.project_tab_blockly_btn;
                            break;
                        case POSITION_MOTION:
                            id = R.id.project_tab_motion_btn;
                            break;
                        case POSITION_CONTROLLER:
                            id = R.id.project_tab_controller_btn;
                            break;
                        case POSITION_DOODLE:
                            id = R.id.project_tab_doodle_btn;
                            break;
                        default:
                            id = mDefaultPosition;
                            break;
                    }
                    mTabRadioGroup.check(id);
                    return;
                }
                if (checkedId == R.id.project_tab_blockly_btn) {
                    mCurrentTabPosition = POSITION_BLOCKLY;
                } else if (checkedId == R.id.project_tab_motion_btn) {
                    mCurrentTabPosition = POSITION_MOTION;
                } else if (checkedId == R.id.project_tab_controller_btn) {
                    mCurrentTabPosition = POSITION_CONTROLLER;
                } else if (checkedId == R.id.project_tab_doodle_btn) {
                    mCurrentTabPosition = POSITION_DOODLE;
                }

                updateCurrentTab();
                reportTabEvents();
            }
        });
        if (mDefaultPosition == POSITION_MOTION) {
            mTabRadioGroup.check(R.id.project_tab_motion_btn);
        }

        return rootView;
    }

    private void reportTabEvents() {
        String eventId = Events.Ids.app_project_tab_click;
        String tabName = null;
        switch (mCurrentTabPosition) {
            case POSITION_BLOCKLY:
                tabName = "blockly";
                break;
            case POSITION_MOTION:
                tabName = "motion";
                break;
            case POSITION_CONTROLLER:
                tabName = "remote";
                break;
            case POSITION_DOODLE:
                tabName = "doodle";
                break;
        }
        Map<String, String> args = new HashMap<>(1);
        args.put("tab", tabName);
        UBTReporter.onEvent(eventId, args);
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getPresenter().init(mSourceProject);
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable(PROJECT_KEY, GsonUtil.get().toJson(mSourceProject));
    }

    private void updateCurrentTab() {
        mProjectViewPager.setCurrentItem(mCurrentTabPosition);
        setCommonButtonsVisibility(mCurrentTabPosition != POSITION_DOODLE);
        EventBus.getDefault().post(new UnityVisibilityEvent(mCurrentTabPosition == POSITION_MOTION));
        if (mCurrentTabPosition == POSITION_BLOCKLY || mCurrentTabPosition == POSITION_CONTROLLER) {
            mBackBtn.setBackgroundResource(R.drawable.bluetooth_home_icon);
        } else {
            mBackBtn.setBackgroundResource(R.drawable.bluetooth_blackhome_icon);
        }
        if (mCurrentTabPosition != POSITION_MOTION) {
            updateMotionToWorkspaceWithoutResetModified().subscribe(new SimpleRxSubscriber());
        }
    }

    private void setCommonButtonsVisibility(boolean isShow) {
        int visibility = isShow ? View.VISIBLE : View.INVISIBLE;
        mListBtn.setVisibility(visibility);
        mShareBtn.setVisibility(visibility);
        mSaveBtn.setVisibility(visibility);
        mBluetoothBtn.setVisibility(visibility);
    }

    @Override
    public void onPause() {
        BluetoothHelper.terminateExecution(false);
        MotionDesigner.getInstance().pause();
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        MotionDesigner.getInstance().resume();
    }

    @Override
    public void onDestroy() {
        ((BridgeImpl) BridgeCommunicator.getInstance().getBlocklyBridge(false)).clearStartupCalls();
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                ActivityHelper.hideLoading();
            }
        }, 200);
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }

    @Override
    public void onClick(View v, boolean isSafeClick) {
        super.onClick(v, isSafeClick);
        if (v.getId() == R.id.project_save_btn) {
            saveProject();
            UBTReporter.onEvent(Events.Ids.app_project_save_btn_click, null);
        } else if (v.getId() == R.id.project_bluetooth_btn) {
            bluetoothConnect();
            UBTReporter.onEvent(Events.Ids.app_project_bt_btn_click, null);
        }
        if (v == mBackBtn) {
            onBackPressed();
        }
        if (v == mListBtn) {
            openProjectList();
            reportListEvent();
        }
        if(v == mShareBtn){
            doShareProject();
        }
    }

    private void doShareProject(){

        //如果workspace还没保存，需要保存
        if (getPresenter().isWorkspaceModified()) {
            isNeedShareAfterSave = true;
            saveProject();
        }else {
            //workspace保存了，检擦子项目有没有修改
            isProjectFilesModified().subscribe(new SimpleRxSubscriber<Boolean>() {
                @Override
                public void onNext(Boolean isModified) {
                    super.onNext(isModified);
                    LogUtil.d("doShareProject isModified = " + isModified);
                    if (isModified) {
                        isNeedShareAfterSave = true;
                        saveProject();
                    } else {
                        shareProject();
                    }
                }
            });
        }

    }

    private void shareProject(){

        final String workspaceProjectDir = mWorkspaceProject.getProjectPath();
        LogUtil.d("workspaceProjectDir = " + workspaceProjectDir);

        getPresenter().getProjectShareFile().subscribe(new Consumer<String>() {
            @Override
            public void accept(String outPath) throws Exception {
                File shareProjectFile = new File(outPath);
                if(shareProjectFile.exists()){
                    showProjectShareDialog(shareProjectFile);
                }
            }
        });

    }

    private void showProjectShareDialog(File shareFile){
        Uri projectUri = getPresenter().getUriByFile(shareFile);
        List shareAppList = getPresenter().getShareAppInfoList(projectUri);

        if(!shareAppList.isEmpty()){
            ProjectShareDialogFragment.newInstance(shareAppList, projectUri)
                    .show(getFragmentManager(), "ProjectShareDialogFragment");
        }else {
            getUIDelegate().toastShort(getString(R.string.project_share_app_none_tip));
        }
    }

    private void reportListEvent() {
        String eventId = Events.Ids.app_project_sub_list_btn_click;
        String type = null;
        if (mCurrentTabPosition == POSITION_BLOCKLY) {
            type = "blockly";
        }
        if (mCurrentTabPosition == POSITION_MOTION) {
            type = "motion";
        }
        if (mCurrentTabPosition == POSITION_CONTROLLER) {
            type = "remote";
        }
        if (mCurrentTabPosition == POSITION_DOODLE) {
            type = "doodle";
        }
        Map<String, String> args = new HashMap<>(1);
        args.put("type", type);
        UBTReporter.onEvent(eventId, args);
    }

    private void openProjectList() {
        ProjectFile file = null;
        if (mCurrentTabPosition == POSITION_BLOCKLY) {
            file = mCurrentBlocklyFile;
        }
        if (mCurrentTabPosition == POSITION_MOTION) {
            file = mCurrentMotionFile;
        }
        if (mCurrentTabPosition == POSITION_CONTROLLER) {
            file = mCurrentContollerFile;
        }
        if (mCurrentTabPosition == POSITION_DOODLE) {
            file = mCurrentDoodleFile;
        }

        final ProjectFile projectFile = file;
        if (mCurrentTabPosition == POSITION_MOTION) {
            updateMotionToWorkspaceWithoutResetModified().subscribe(new SimpleRxSubscriber<Boolean>() {
                @Override
                public void onNext(Boolean o) {
                    super.onNext(o);
                    ProjectListDialogFragment.newInstance(ProjectHostFragment.this, mCurrentTabPosition, projectFile != null ? projectFile.id : null, mWorkspaceProject)
                            .show(getFragmentManager(), "ProjectListDialogFragment");
                }
            });
        } else {
            ProjectListDialogFragment.newInstance(ProjectHostFragment.this, mCurrentTabPosition, projectFile != null ? projectFile.id : null, mWorkspaceProject)
                    .show(getFragmentManager(), "ProjectListDialogFragment");
        }

    }

    public void setProject(Project project) {
        mSourceProject = project;
    }

    private ProjectFile createProjectFile() {
        int position = mCurrentTabPosition;
        switch (position) {
            case POSITION_BLOCKLY:
                return createBlockly();
            case POSITION_MOTION:
                return createMotion();
            case POSITION_CONTROLLER:
                return createController();
            case POSITION_DOODLE:
                return createDoodle();
        }

        return createBlockly();
    }

    private Blockly createBlockly() {
        Blockly projectFile = new Blockly();
        int num = 1;
        String prefixName = getString(R.string.project_sub_new_name_blockly);
        if (mWorkspaceProject != null && mWorkspaceProject.blocklyList != null) {
            for (int i = 0; i < mWorkspaceProject.blocklyList.size(); i++) {
                Blockly blockly = null;
                for (Blockly temp : mWorkspaceProject.blocklyList) {
                    if ((prefixName + num).equals(temp.name)) {
                        blockly = temp;
                    }
                }
                if (blockly == null) {
                    break;
                }
                num++;
            }
        }
        projectFile.name = prefixName + num;
        return projectFile;
    }

    private Motion createMotion() {
        Motion projectFile = new Motion();
        int num = 1;
        String prefixName = getString(R.string.project_sub_new_name_motion);
        if (mWorkspaceProject != null && mWorkspaceProject.motionList != null) {
            for (int i = 0; i < mWorkspaceProject.motionList.size(); i++) {
                Motion motion = null;
                for (Motion temp : mWorkspaceProject.motionList) {
                    if ((prefixName + num).equals(temp.name)) {
                        motion = temp;
                    }
                }
                if (motion == null) {
                    break;
                }
                num++;
            }
        }
        projectFile.name = prefixName + num;
        return projectFile;
    }

    private Controller createController() {
        Controller projectFile = new Controller();
        int num = 1;
        String prefixName = getString(R.string.project_sub_new_name_controller);
        if (mWorkspaceProject != null && mWorkspaceProject.controllerList != null) {
            for (int i = 0; i < mWorkspaceProject.controllerList.size(); i++) {
                Controller controller = null;
                for (Controller temp : mWorkspaceProject.controllerList) {
                    if ((prefixName + num).equals(temp.name)) {
                        controller = temp;
                    }
                }
                if (controller == null) {
                    break;
                }
                num++;
            }
        }
        projectFile.name = prefixName + num;
        return projectFile;
    }

    private Doodle createDoodle() {
        Doodle projectFile = new Doodle();
        int num = 1;
        String prefixName = getString(R.string.project_sub_new_name_doodle);
        if (mWorkspaceProject != null && mWorkspaceProject.doodleList != null) {
            for (int i = 0; i < mWorkspaceProject.doodleList.size(); i++) {
                Doodle doodle = null;
                for (Doodle temp : mWorkspaceProject.doodleList) {
                    if ((prefixName + num).equals(temp.name)) {
                        doodle = temp;
                    }
                }
                if (doodle == null) {
                    break;
                }
                num++;
            }
        }
        projectFile.name = prefixName + num;
        return projectFile;
    }

    public void addNewSubProject() {
        ProjectFile projectFile = createProjectFile();
        if (projectFile != null) {
            getPresenter().saveProjectFile(projectFile);
            openSubProject(projectFile, true);
        }
    }

    public void addSubProjectDuplication(ProjectFile projectFile, String duplicationName) {
        if (projectFile != null) {
            ProjectFile duplication = projectFile.deepClone();
            duplication.id = UuidUtil.createUUID();
            duplication.name = duplicationName;
            duplication.createTime = System.currentTimeMillis();
            getPresenter().saveProjectFile(duplication);
            openSubProject(duplication, true);
        }
    }

    public void removeProjectFile(ProjectFile projectFile) {
        if (projectFile instanceof Motion){
            onMotionChange();
        }
        getPresenter().removeProjectFile(projectFile);
    }

    public void renameProjectFile(ProjectFile projectFile, String newName) {
        getPresenter().renameProjectFile(projectFile, newName);
    }

    private Observable<DataWrapper> getCurrentBlockly() {
        final RxBridge bridge = BridgeCommunicator.getInstance().getBlocklyBridge(false);

        final DataWrapper wrapper = new DataWrapper();
        if (!bridge.isCommunicable()) {
            return Observable.just(wrapper);
        }
        return bridge.call(BlocklyFunctions.getBlockly, null)
                .map(new Function<BridgeResult, DataWrapper>() {
                    @Override
                    public DataWrapper apply(BridgeResult result) throws Exception {
                        if (result.code == BridgeResultCode.SUCCESS) {
                            String json = result.data.toString();
                            wrapper.data = GsonUtil.get().toObject(json, Blockly.class);
                        }
                        LogUtil.i("getCurrentBlockly");
                        return wrapper;
                    }
                })
                //防止Blockly端没有返回该调用
                .timeout(BRIDGE_API_TIMEOUT, TimeUnit.SECONDS, Observable.just(new DataWrapper()))
                .observeOn(AndroidSchedulers.mainThread());

    }

    private Observable<DataWrapper> getCurrentMotion() {
        final RxBridge bridge = BridgeCommunicator.getInstance().getUnity3DBridge();
        final DataWrapper wrapper = new DataWrapper();
        if (!bridge.isCommunicable()) {
            return Observable.just(wrapper);
        }
        return bridge.call(Unity3DFunctions.getMotion, null)
                .map(new Function<BridgeResult, DataWrapper>() {
                    @Override
                    public DataWrapper apply(BridgeResult result) throws Exception {
                        if (result.code == BridgeResultCode.SUCCESS) {
                            String json = result.data.toString();
                            LogUtil.i("getCurrentMotion:" + json);
                            wrapper.data = GsonUtil.get().toObject(json, Motion.class);
                        }
                        LogUtil.i("getCurrentMotion2");
                        return wrapper;

                    }
                })
                //防止Unity端没有返回该调用
                .timeout(BRIDGE_API_TIMEOUT, TimeUnit.SECONDS, Observable.just(new DataWrapper()))
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Observable<Boolean> isCurrentProjectFileModify() {
        int position = mCurrentTabPosition;
        switch (position) {
            case POSITION_BLOCKLY:
                return isCurrentBlocklyModify();
            case POSITION_MOTION:
                return isCurrentMotionModify();
            case POSITION_CONTROLLER:
                return isCurrentControllerModify();
            case POSITION_DOODLE:
                return isCurrentDoodleModify();
        }
        return Observable.just(true);
    }

    private Observable<Boolean> isProjectFilesModified() {
        return Observable.zip(
                isCurrentBlocklyModify(),
                isCurrentMotionModify(),
                isCurrentControllerModify(),
                isCurrentDoodleModify(),
                new Function4<Boolean, Boolean, Boolean, Boolean, Boolean>() {
                    @Override
                    public Boolean apply(Boolean aBoolean, Boolean aBoolean2,
                                         Boolean aBoolean3, Boolean aBoolean4) throws Exception {
                        LogUtil.i("isProjectFilesModified:" + aBoolean + " " + aBoolean2 + " " + aBoolean3 + " " + aBoolean4);
                        return aBoolean || aBoolean2 || aBoolean3 || aBoolean4;
                    }
                })
                .observeOn(AndroidSchedulers.mainThread());
    }

    private Observable<Boolean> isCurrentBlocklyModify() {
        final RxBridge bridge = BridgeCommunicator.getInstance().getBlocklyBridge(false);
        if (!bridge.isCommunicable()) {
            return Observable.just(false);
        }
        return bridge.call(BlocklyFunctions.isBlocklyModify, null)
                .map(new Function<BridgeResult, Boolean>() {
                    @Override
                    public Boolean apply(BridgeResult result) throws Exception {
                        LogUtil.i("isCurrentBlocklyModify");
                        boolean isModify = false;
                        if (result.code == BridgeResultCode.SUCCESS && result.data instanceof Number) {
                            Number modifyState = (Number) result.data;
                            isModify = BridgeBoolean.isTrue(modifyState.intValue());

                        } else {
                            LogUtil.e("Get blockly modify state fail");
                        }

                        return isModify;
                    }
                })
                //防止Blockly端没有返回该调用
                .timeout(BRIDGE_API_TIMEOUT, TimeUnit.SECONDS, Observable.just(false))
                .observeOn(AndroidSchedulers.mainThread());

    }

    private Observable<Boolean> isCurrentMotionModify() {
        final RxBridge bridge = BridgeCommunicator.getInstance().getUnity3DBridge();

        if (!bridge.isCommunicable()) {
            return Observable.just(false);
        }
        return bridge.call(Unity3DFunctions.isMotionModify, null)
                .map(new Function<BridgeResult, Boolean>() {
                    @Override
                    public Boolean apply(BridgeResult result) throws Exception {
                        boolean isModify = false;
                        if (result.code == BridgeResultCode.SUCCESS && result.data instanceof Number) {
                            Number modifyState = (Number) result.data;
                            isModify = BridgeBoolean.isTrue(modifyState.intValue());
                        } else {
                            LogUtil.e("Get motion modify state fail");
                        }
                        LogUtil.i("isCurrentMotionModify");
                        return isModify;
                    }
                })
                //防止Unity端没有返回该调用
                .timeout(BRIDGE_API_TIMEOUT, TimeUnit.SECONDS, Observable.just(false))
                .observeOn(AndroidSchedulers.mainThread());
    }

    private Observable<Boolean> isCurrentControllerModify() {
        LogUtil.i("isCurrentControllerModify");
        return Observable.just(mControllerFragment.isControllerModified());
    }

    private Observable<Boolean> isCurrentDoodleModify() {
        LogUtil.i("isCurrentDoodleModify");
        return Observable.just(mDoodleFragment.isDoodleModified());
    }

    public Observable<DataWrapper> getCurrentProjectFile() {

        int position = mCurrentTabPosition;
        if (position == POSITION_BLOCKLY) {
            return getCurrentBlockly();
        }
        if (position == POSITION_MOTION) {
            return getCurrentMotion();
        }
        if (position == POSITION_CONTROLLER) {
            return getCurrentController();
        }
        if (position == POSITION_DOODLE) {
            return getCurrentDoodle();
        }
        return Observable.just(new DataWrapper());
    }

    private Observable<DataWrapper> getCurrentDoodle() {
        DataWrapper wrapper = new DataWrapper();
        wrapper.data = mDoodleFragment.getDoodle();
        LogUtil.i("getCurrentDoodle");
        return Observable.just(wrapper);
    }

    private Observable<DataWrapper> getCurrentController() {
        DataWrapper wrapper = new DataWrapper();

        wrapper.data = mControllerFragment.getController();
        LogUtil.i("getCurrentController");
        return Observable.just(wrapper);
    }

    public Observable<Boolean> updateMotionToWorkspaceWithoutResetModified() {
        return isCurrentMotionModify()
                .flatMap(new Function<Boolean, ObservableSource<DataWrapper>>() {
                    @Override
                    public ObservableSource<DataWrapper> apply(Boolean isModified) throws Exception {
                        if (isModified) {
                            return getCurrentMotion();
                        } else {
                            return Observable.just(new DataWrapper());
                        }
                    }
                })
                .map(new Function<DataWrapper, Boolean>() {
                    @Override
                    public Boolean apply(DataWrapper wrapper) throws Exception {
                        LogUtil.i("updateMotion:" + GsonUtil.get().toJson(wrapper));
                        if (wrapper.data != null) {
                            if (wrapper.data instanceof Motion) {
                                BridgeCommunicator.getInstance()
                                        .getBlocklyBridge(false)
                                        .call(BlocklyFunctions.setMotion, new Object[]{new MotionArguments((Motion) wrapper.data)}, null);
                                onMotionChange();
                            }
                            getPresenter().saveProjectFile((ProjectFile) wrapper.data);
                        }
                        return true;
                    }
                });
    }

    public void openSubProject(final ProjectFile projectFile, final boolean fromList) {
        if (projectFile != null) {
            isCurrentProjectFileModify()
                    .flatMap(new Function<Boolean, ObservableSource<DataWrapper>>() {
                        @Override
                        public ObservableSource<DataWrapper> apply(Boolean aBoolean) throws Exception {
                            if (aBoolean) {
                                return getCurrentProjectFile();
                            } else {
                                return Observable.just(new DataWrapper());
                            }
                        }
                    })
                    .subscribe(new SimpleRxSubscriber<DataWrapper>() {
                        @Override
                        public void onNext(DataWrapper dataWrapper) {
                            super.onNext(dataWrapper);
                            if (dataWrapper.data != null) {
                                //保存当前的
                                getPresenter().saveProjectFile((ProjectFile) dataWrapper.data);
                            }
                            if (fromList) {
                                //为了当前打开的子项目是最新修改的项目，这样子处理
                                getPresenter().saveProjectFile(projectFile);
                            }
                            doOpenProjectFile(projectFile);
                        }
                    });
        }
    }

    private void doOpenProjectFile(ProjectFile projectFile) {
        if (projectFile instanceof Blockly) {
            mCurrentBlocklyFile = projectFile;
            openBlockly(projectFile);
        }
        if (projectFile instanceof Motion) {
            mCurrentMotionFile = projectFile;
            openMotion(projectFile);
        }
        if (projectFile instanceof Controller) {
            mCurrentContollerFile = projectFile;
            openController((Controller) projectFile);
        }
        if (projectFile instanceof Doodle) {
            mCurrentDoodleFile = projectFile;
            openDoodle((Doodle) projectFile);
        }
    }

    private void openDoodle(Doodle projectFile) {
        mDoodleFragment.setDoodle(projectFile);
    }

    private void openController(Controller projectFile) {
        mControllerFragment.setController(projectFile);
    }

    private void openMotion(ProjectFile projectFile) {
        Object[] args = new Object[]{projectFile};
        BridgeCommunicator.getInstance().getUnity3DBridge().call(Unity3DFunctions.openMotion, args, null);
    }

    private void openBlockly(ProjectFile projectFile) {
        Object[] args = new Object[]{Blockly.TYPE_NORMAL, projectFile};
        BridgeCommunicator.getInstance().getBlocklyBridge(false).call(BlocklyFunctions.openBlockly, args, null);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onVisibilityEvent(VisibilityEvent event) {
        updateButtonStatus(event.isShown);
    }

    private void updateButtonStatus(boolean isShown) {
        synchronized (ProjectHostFragment.class) {
            if (mShownMenu == isShown) {
                return;
            }
            mShownMenu = isShown;
            mTabRadioGroup.setEnabled(mShownMenu);
            int[] location = {0, 0};
            ArrayList<Animator> animators = new ArrayList<>();
            if (isShown) {
                mTabbarLyt.getLocationInWindow(location);
                animators.add(ObjectAnimator.ofFloat(mTabbarLyt, "translationY", -(location[1] + mTabbarLyt.getHeight()), 0));
                animators.add(ObjectAnimator.ofFloat(mTabbarLyt, "alpha", 0f, 1.0f));
                mBackBtn.getLocationInWindow(location);
                animators.add(ObjectAnimator.ofFloat(mBackBtn, "translationX", -(location[0] + mBackBtn.getWidth()), 0));
                animators.add(ObjectAnimator.ofFloat(mBackBtn, "alpha", 0f, 1.0f));
            } else {
                mTabbarLyt.getLocationInWindow(location);
                animators.add(ObjectAnimator.ofFloat(mTabbarLyt, "translationY", 0, -(location[1] + mTabbarLyt.getHeight())));
                animators.add(ObjectAnimator.ofFloat(mTabbarLyt, "alpha", 1.0f, 0f));
                mBackBtn.getLocationInWindow(location);
                animators.add(ObjectAnimator.ofFloat(mBackBtn, "translationX", 0, -(location[0] + mBackBtn.getWidth())));
                animators.add(ObjectAnimator.ofFloat(mBackBtn, "alpha", 1.0f, 0f));
            }

            if (mAnimatorSet != null && mAnimatorSet.isRunning()) {
                mAnimatorSet.cancel();
            }

            mAnimatorSet = new AnimatorSet();
            mAnimatorSet.setDuration(DEFAULT_ANIMATION_DURATION);
            mAnimatorSet.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {
                    if (mShownMenu) {
                        mTabbarLyt.setVisibility(View.VISIBLE);
                        mBackBtn.setVisibility(View.VISIBLE);
                    }
                    setBtnEnabled(mShownMenu && !animation.isRunning());
                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    mTabbarLyt.setTranslationY(0);
                    mBackBtn.setTranslationX(0);
                    if (!mShownMenu) {
                        mTabbarLyt.setVisibility(View.INVISIBLE);
                        mBackBtn.setVisibility(View.INVISIBLE);
                    }
                    setBtnEnabled(mShownMenu);
                }

                @Override
                public void onAnimationCancel(Animator animation) {
                }

                @Override
                public void onAnimationRepeat(Animator animation) {
                }

                private void setBtnEnabled(boolean enabled) {
                    mTabRadioGroup.setEnabled(enabled);
                    mBackBtn.setEnabled(enabled);
                    mListBtn.setEnabled(enabled);
                    mSaveBtn.setEnabled(enabled);
                    mBluetoothBtn.setEnabled(enabled);
                }
            });
            mAnimatorSet.playTogether(animators);
            mAnimatorSet.start();
        }
    }

    private void bluetoothConnect() {
        BluetoothHelper.openActivity(this);
    }

    private Observable<Boolean> updateBlockly() {
        return isCurrentBlocklyModify()
                .flatMap(new Function<Boolean, ObservableSource<DataWrapper>>() {
                    @Override
                    public ObservableSource<DataWrapper> apply(Boolean isModified) throws Exception {
                        if (isModified) {
                            return getCurrentBlockly();
                        } else {
                            return Observable.just(new DataWrapper());
                        }
                    }
                })
                .map(new Function<DataWrapper, Boolean>() {
                    @Override
                    public Boolean apply(DataWrapper wrapper) throws Exception {
                        LogUtil.i("updateBlockly");
                        if (wrapper.data != null) {

                            getPresenter().saveProjectFile((ProjectFile) wrapper.data);

                            BridgeCommunicator.getInstance()
                                    .getBlocklyBridge(false)
                                    .call(BlocklyFunctions.resetModifyState, null, null);
                        }
                        LogUtil.i("updateBlockly2");
                        return true;
                    }
                });
    }

    private Observable<Boolean> updateMotion() {
        return isCurrentMotionModify()
                .flatMap(new Function<Boolean, ObservableSource<DataWrapper>>() {
                    @Override
                    public ObservableSource<DataWrapper> apply(Boolean isModified) throws Exception {
                        if (isModified) {
                            return getCurrentMotion();
                        } else {
                            return Observable.just(new DataWrapper());
                        }
                    }
                })
                .map(new Function<DataWrapper, Boolean>() {
                    @Override
                    public Boolean apply(DataWrapper wrapper) throws Exception {
                        LogUtil.i("updateMotion:" + GsonUtil.get().toJson(wrapper));
                        if (wrapper.data != null) {
                            getPresenter().saveProjectFile((ProjectFile) wrapper.data);
                            BridgeCommunicator.getInstance()
                                    .getUnity3DBridge()
                                    .call(Unity3DFunctions.resetModifyState, null, null);
                        }
                        LogUtil.i("updateMotion2");
                        return true;
                    }
                });
    }

    private Observable<Boolean> updateController() {
        return isCurrentControllerModify()
                .flatMap(new Function<Boolean, ObservableSource<DataWrapper>>() {
                    @Override
                    public ObservableSource<DataWrapper> apply(Boolean isModified) throws Exception {
                        if (isModified) {
                            return getCurrentController();
                        } else {
                            return Observable.just(new DataWrapper());
                        }
                    }
                })
                .map(new Function<DataWrapper, Boolean>() {
                    @Override
                    public Boolean apply(DataWrapper wrapper) throws Exception {
                        if (wrapper.data != null) {
                            getPresenter().saveProjectFile((ProjectFile) wrapper.data);
                            mControllerFragment.resetModifyState();
                        }
                        return true;
                    }
                });
    }

    private Observable<Boolean> updateDoodle() {
        return isCurrentDoodleModify()
                .flatMap(new Function<Boolean, ObservableSource<DataWrapper>>() {
                    @Override
                    public ObservableSource<DataWrapper> apply(Boolean isModified) throws Exception {
                        if (isModified) {
                            return getCurrentDoodle();
                        } else {
                            return Observable.just(new DataWrapper());
                        }
                    }
                })
                .map(new Function<DataWrapper, Boolean>() {
                    @Override
                    public Boolean apply(DataWrapper wrapper) throws Exception {
                        if (wrapper.data != null) {
                            getPresenter().saveProjectFile((ProjectFile) wrapper.data);
                            mDoodleFragment.resetModifyState();
                        }
                        return true;
                    }
                });
    }

    private void saveProject() {
        if (getPresenter().isFirstSave()) {
            ProjectSaveDialogFragment dialogFragment = new ProjectSaveDialogFragment();
            dialogFragment.setCancelable(false);
            dialogFragment.setDismissListener(new OnDialogFragmentDismissListener() {
                @Override
                public void onDismiss(Object... value) {
                    if (value != null && value.length == 3) {
                        boolean isPositive = (boolean) value[0];
                        if (isPositive) {
                            String projectName = (String) value[1];
                            String imagePath = (String) value[2];
                            doSaveProject(projectName, imagePath);
                        } else {
                            isNeedExitAfterSave = false;
                            isNeedShareAfterSave = false;
                        }
                    }
                }
            });
            dialogFragment.show(getFragmentManager(), "ProjectSaveDialogFragment");
        } else {
            doSaveProject(null, null);
        }

    }

    private Observable<Boolean> updateAllProjectFiles() {
        return Observable.zip(
                updateBlockly(),
                updateMotion(),
                updateController(),
                updateDoodle(),
                new Function4<Boolean, Boolean, Boolean, Boolean, Boolean>() {
                    @Override
                    public Boolean apply(Boolean aBoolean, Boolean aBoolean2,
                                         Boolean aBoolean3, Boolean aBoolean4) throws Exception {
                        LogUtil.i("updateAllProjectFiles:" + aBoolean + " " + aBoolean2 + " " + aBoolean3 + " " + aBoolean4);
                        return aBoolean || aBoolean2 || aBoolean3 || aBoolean4;
                    }
                })
                .observeOn(AndroidSchedulers.mainThread());

    }

    private void doSaveProject(final String projectName, final String imagePath) {
        updateAllProjectFiles().subscribe(new SimpleRxSubscriber<Boolean>() {
            @Override
            public void onNext(Boolean aBoolean) {
                super.onNext(aBoolean);
                getPresenter().saveProject(projectName, imagePath);
            }

            @Override
            public void onError(RxException e) {
                super.onError(e);
                if(isNeedShareAfterSave){
                    isNeedExitAfterSave = false;
                }
                e.printStackTrace();
                getUIDelegate().toastShort(getString(R.string.project_save_fail));
            }
        });
    }

    private void initFragments() {
        mBlocklyFragment = BlocklyFragment.newInstance(false);
        mControllerFragment = new ControllerFragment();
        mDoodleFragment = new DoodleFragment();
        mProjectFragments = new ArrayList<>(4);
        mProjectFragments.add(mBlocklyFragment);
        mProjectFragments.add(new MotionDesignFragment());
        mProjectFragments.add(mControllerFragment);
        mProjectFragments.add(mDoodleFragment);
        mBlocklyFragment.setBlocklyLoadingFinishListener(new BlocklyFragment.OnBlocklyLoadingFinishListener() {
            @Override
            public void onLoadingFinish() {
                if (mLoadingLyt != null) {
                    if (mLoadingLyt.getParent() != null) {
                        ((ViewGroup) (mLoadingLyt.getParent())).removeView(mLoadingLyt);
                    }
                    mLoadingLyt = null;
                    if (getActivity() instanceof HomeActivity) {
                        HomeActivity activity = (HomeActivity) getActivity();
                        activity.onHomePause();
                        activity.onProjectPageResume();
                    }
                }
            }
        });

    }

    private void initSubProjects() {
        if (mWorkspaceProject != null) {
            initBlockly();
            initMotion();
            initController();
            initDoodle();
        }
    }

    private void initDoodle() {
        Doodle doodle = (Doodle) getRecentlyProjectFile(POSITION_DOODLE);
        //画板功能未开放，ios创建的项目doodle为空，初始化时不做改动
//        if (doodle == null) {
//            doodle = createDoodle();
//            getPresenter().saveProjectFile(doodle);
//        }
        mCurrentDoodleFile = doodle;
        openDoodle(doodle);
    }

    private void initController() {
        Controller controller = (Controller) getRecentlyProjectFile(POSITION_CONTROLLER);
        if (controller == null) {
            controller = createController();
            getPresenter().saveProjectFile(controller);
        }
        mCurrentContollerFile = controller;
        openController(controller);
    }

    private void initMotion() {
        ProjectFile motion = getRecentlyProjectFile(POSITION_MOTION);
        if (motion == null) {
            motion = createMotion();
            getPresenter().saveProjectFile(motion);
        }
        mCurrentMotionFile = motion;
        openMotion(motion);
    }


    private void initBlockly() {
        ProjectFile blockly = getRecentlyProjectFile(POSITION_BLOCKLY);
        if (blockly == null) {
            blockly = createBlockly();
            getPresenter().saveProjectFile(blockly);
        }
        mCurrentBlocklyFile = blockly;
        openBlockly(blockly);
    }

    private ProjectFile getRecentlyProjectFile(int positionType) {
        ProjectFile[] projectFiles = null;
        switch (positionType) {
            case POSITION_BLOCKLY:
                if (mWorkspaceProject.blocklyList != null && mWorkspaceProject.blocklyList.size() > 0) {
                    projectFiles = new ProjectFile[mWorkspaceProject.blocklyList.size()];
                    mWorkspaceProject.blocklyList.toArray(projectFiles);
                }
                break;
            case POSITION_MOTION:
                if (mWorkspaceProject.motionList != null && mWorkspaceProject.motionList.size() > 0) {
                    projectFiles = new ProjectFile[mWorkspaceProject.motionList.size()];
                    mWorkspaceProject.motionList.toArray(projectFiles);
                }
                break;
            case POSITION_CONTROLLER:
                if (mWorkspaceProject.controllerList != null && mWorkspaceProject.controllerList.size() > 0) {
                    projectFiles = new ProjectFile[mWorkspaceProject.controllerList.size()];
                    mWorkspaceProject.controllerList.toArray(projectFiles);
                }
                break;
            case POSITION_DOODLE:
                if (mWorkspaceProject.doodleList != null && mWorkspaceProject.doodleList.size() > 0) {
                    projectFiles = new ProjectFile[mWorkspaceProject.doodleList.size()];
                    mWorkspaceProject.doodleList.toArray(projectFiles);
                }
                break;
        }

        if (projectFiles != null) {
            LongSparseArray<ProjectFile> fileMap = new LongSparseArray<>();
            long maxModifyTime = 0;
            for (ProjectFile b : projectFiles) {
                maxModifyTime = Math.max(maxModifyTime, b.modifyTime);
                fileMap.put(b.modifyTime, b);
            }
            return fileMap.get(maxModifyTime);
        }

        return null;
    }


    public int getDefaultPosition() {
        return mDefaultPosition;
    }

    @Override
    public boolean onBackPressed() {
        //如果workspace还没保存，需要保存
        if (getPresenter().isWorkspaceModified()) {
            showExitPromptDialog();
            return true;
        }
        //workspace保存了，检擦子项目有没有修改
        isProjectFilesModified().subscribe(new SimpleRxSubscriber<Boolean>() {
            @Override
            public void onNext(Boolean isModified) {
                super.onNext(isModified);
                if (isModified) {
                    showExitPromptDialog();
                } else {
                    exit();
                }
            }
        });
        return true;
    }

    private void showExitPromptDialog() {
        if (!isAdded()) return;
        PromptDialogFragment.newBuilder(getContext())
                .title(getString(R.string.project_save_prompt_title))
                .message(getString(R.string.project_save_prompt_content))
                .onPositiveClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        isNeedExitAfterSave = true;
                        saveProject();
                    }
                })
                .cancelable(true)
                .positiveButtonText(getString(R.string.project_save_prompt_positive_text))
                .negativeButtonText(getString(R.string.project_save_prompt_negative_text))
                .onNegativeClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        exit();
                    }
                })
                .build()
                .show(getChildFragmentManager(), "ProjectHostFragment-showExitPromptDialog");
    }

    private void exit() {
        if (getActivity() != null && getActivity() instanceof HomeActivity) {
            EventBus.getDefault().post(new UnityVisibilityEvent(true));
            HomeActivity home = (HomeActivity) getActivity();
            home.openHome();
        }
        if (mBlocklyFragment != null) {
            mBlocklyFragment.close();
        }
        UserDataSynchronizer.getInstance().sync(true).subscribe(new SimpleRxSubscriber<>());
    }

    @Override
    protected ProjectHostContracts.Presenter createPresenter() {
        return new ProjectHostPresenter();
    }

    @Override
    protected ProjectHostContracts.UI createUIView() {
        return new ProjectHostUI();
    }

    class ProjectHostUI extends ProjectHostContracts.UI {

        @Override
        public void onWorkspaceInitComplete(Project project) {
            mWorkspaceProject = project;
            initSubProjects();
        }

        @Override
        public void showSaveResultMessage(boolean isSuccess) {
            getUIDelegate().hideLoading();
            if (isSuccess) {

                if(isNeedShareAfterSave){
                    isNeedShareAfterSave = false;
                    shareProject();
                    return;
                }

                getUIDelegate().toastShort(getString(R.string.project_save_success));
                if (isNeedExitAfterSave) {
                    isNeedExitAfterSave = false;
                    exit();
                }
            } else {
                if(isNeedShareAfterSave){
                    isNeedShareAfterSave = false;
                }
                getUIDelegate().toastShort(getString(R.string.project_save_fail));
            }
        }

        @Override
        public void showProjectNameDuplicateMessage() {
            getUIDelegate().toastShort(getString(R.string.project_save_duplicate_name));
        }


        @Override
        public void showBluetoothConnectState(boolean isConnected) {
            if (isConnected) {
                mBluetoothBtn.setBackgroundResource(R.drawable.selector_bluetooth_connect_btn);
            } else {
                mBluetoothBtn.setBackgroundResource(R.drawable.selector_bluetooth_disconnect_btn);
            }
        }
    }

    private void onMotionChange(){
        ControllerManager.clearControllerPyFileContent();
    }
}
