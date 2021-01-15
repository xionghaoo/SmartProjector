package com.ubtedu.ukit.project.controller.blockly;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.ubtedu.base.net.rxretrofit.subscriber.SimpleRxSubscriber;
import com.ubtedu.bridge.BridgeImpl;
import com.ubtedu.bridge.BridgeObject;
import com.ubtedu.bridge.BridgeResult;
import com.ubtedu.bridge.BridgeResultCode;
import com.ubtedu.ukit.R;
import com.ubtedu.ukit.common.base.UKitBaseActivity;
import com.ubtedu.ukit.common.eventbus.VisibilityEvent;
import com.ubtedu.ukit.project.blockly.BlocklyFragment;
import com.ubtedu.ukit.project.bridge.BridgeCommunicator;
import com.ubtedu.ukit.project.bridge.functions.BlocklyFunctions;
import com.ubtedu.ukit.project.controller.utils.ControllerConstValue;
import com.ubtedu.ukit.project.controller.utils.RcLogUtils;
import com.ubtedu.ukit.project.vo.Blockly;
import com.ubtedu.ukit.project.vo.BlocklyFile;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import static com.ubtedu.ukit.project.controller.utils.ControllerConstValue.DEFAULT_ANIMATION_DURATION;

/**
 * @Author naOKi
 * @Date 2018/12/14
 **/
public class ControllerBlocklyActivity extends UKitBaseActivity {

    public static final String EXTRA_TYPE = "_extra_type";
    public static final String EXTRA_NAME = "_extra_name";
    public static final String EXTRA_BLOCKLY_ID = "_extra_blockly_id";
    public static final String EXTRA_SUBSCRIBE_ID = "_extra_subscribe_id";
    public static final String EXTRA_WORKSPACE_CONTENT = "_extra_workspace_content";
    public static final String EXTRA_LUA_ON_CONTENT = "_extra_lua_on_content";
    public static final String EXTRA_LUA_OFF_CONTENT = "_extra_lua_off_content";
    public static final String EXTRA_LUA_CHANGE_CONTENT = "_extra_lua_change_content";
    public static final String EXTRA_LUA_DOWN_CONTENT = "_extra_lua_down_content";
    public static final String EXTRA_LUA_UP_CONTENT = "_extra_lua_up_content";
    public static final String EXTRA_MICRO_PYTHON_CONTENT = "_extra_micro_python_content";

    private ImageView mCloseBtn;
    private ImageView mConfirmBtn;
    private TextView mTitleTv;

    private String type;
    private String name;
    private String blocklyId;
    private String subscribeId;
    private String workspaceContent;

    private boolean mShownMenu = true;
    private AnimatorSet mAnimatorSet;
    private BlocklyFragment mBlocklyFragment;

    private static Intent makeIntent(Context context, String type, String name, String blocklyId, String subscribeId, String workspaceContent) {
        Intent intent = new Intent(context, ControllerBlocklyActivity.class);
        intent.putExtra(EXTRA_TYPE, type);
        intent.putExtra(EXTRA_NAME, name);
        intent.putExtra(EXTRA_BLOCKLY_ID, blocklyId);
        intent.putExtra(EXTRA_SUBSCRIBE_ID, subscribeId);
        intent.putExtra(EXTRA_WORKSPACE_CONTENT, workspaceContent);
        return intent;
    }

    public static void openControllerBlocklyActivity(Activity activity, int requestCode, String type, String name, String blocklyId, String subscribeId, String workspaceContent) {
        Intent intent = makeIntent(activity, type, name, blocklyId, subscribeId, workspaceContent);
        activity.startActivityForResult(intent, requestCode);
    }

    public static void openControllerBlocklyActivity(Fragment fragment, int requestCode, String type, String name, String blocklyId, String subscribeId, String workspaceContent) {
        Intent intent = makeIntent(fragment.getContext(), type, name, blocklyId, subscribeId, workspaceContent);
        fragment.startActivityForResult(intent, requestCode);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_controller_blockly);
        initView();
        if (savedInstanceState != null) {
            initArguments(savedInstanceState);
        } else {
            initArguments(getIntent().getExtras());
        }
        initContent();
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onDestroy() {
        ((BridgeImpl) BridgeCommunicator.getInstance().getBlocklyBridge(true)).clearStartupCalls();
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }

    private void initArguments(Bundle args) {
        if (args != null) {
            type = args.getString(EXTRA_TYPE);
            name = args.getString(EXTRA_NAME);
            blocklyId = args.getString(EXTRA_BLOCKLY_ID);
            subscribeId = args.getString(EXTRA_SUBSCRIBE_ID);
            workspaceContent = args.getString(EXTRA_WORKSPACE_CONTENT);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(EXTRA_TYPE, type);
        outState.putString(EXTRA_NAME, name);
        outState.putString(EXTRA_BLOCKLY_ID, blocklyId);
        outState.putString(EXTRA_SUBSCRIBE_ID, subscribeId);
        outState.putString(EXTRA_WORKSPACE_CONTENT, workspaceContent);
    }

    private void initView() {
        mCloseBtn = findViewById(R.id.project_controller_blockly_close_btn);
        bindSafeClickListener(mCloseBtn);
        mConfirmBtn = findViewById(R.id.project_controller_blockly_confirm_btn);
        bindSafeClickListener(mConfirmBtn);
        mTitleTv = findViewById(R.id.project_controller_blockly_title_tv);
    }

    private void initContent() {
        mTitleTv.setText(name);
        mBlocklyFragment = BlocklyFragment.newInstance(true);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.add(R.id.project_controller_blockly_content, mBlocklyFragment);
        transaction.commit();
        openBlockly();
    }

    private void openBlockly() {
        Blockly blockly = new Blockly();
        blockly.name = "";
        blockly.workspace = workspaceContent;
        Object[] args = new Object[]{type, blockly, subscribeId};
        BridgeCommunicator.getInstance().getBlocklyBridge(true).call(BlocklyFunctions.openBlockly, args, null);
    }

    @Override
    public void onClick(View v, boolean isSafeClick) {
        if (v == mCloseBtn) {

            finish();
        }
        if (v == mConfirmBtn) {
            BridgeCommunicator.getInstance().getBlocklyBridge(true).call(BlocklyFunctions.getBlocklyLua, null)
                    .subscribe(new SimpleRxSubscriber<BridgeResult>() {
                        @Override
                        public void onNext(BridgeResult result) {
                            super.onNext(result);
                            saveBlocklyLua(result);
                        }
                    });
        }
    }

    private void saveBlocklyLua(BridgeResult result) {
        if (result.code != BridgeResultCode.SUCCESS) {
            return;
        }
        Intent intent = new Intent();
        try {
            BridgeObject jsonObject = (BridgeObject) result.data;
//						String type = jsonObject.getString("type");
            JSONArray contents = jsonObject.getJSONArray("contents");
            for (int i = 0; i < contents.length(); i++) {
                JSONObject item = contents.getJSONObject(i);
                String name = item.getString("name");
                String content = item.getString("content");
                if (TextUtils.equals("workspace", name)) {
                    intent.putExtra(EXTRA_WORKSPACE_CONTENT, content);
                }
                if (Blockly.TYPE_CONTROLLER_BUTTON.equals(type) && TextUtils.equals(BlocklyFile.NAME_DOWN, name + BlocklyFile.LUA_SUFFIX)) {
                    RcLogUtils.e("\nDown content:\n%s", content);
                    intent.putExtra(EXTRA_LUA_DOWN_CONTENT, content);
                }
                if (Blockly.TYPE_CONTROLLER_BUTTON.equals(type) && TextUtils.equals(BlocklyFile.NAME_UP, name + BlocklyFile.LUA_SUFFIX)) {
                    RcLogUtils.e("\nUp content:\n%s", content);
                    intent.putExtra(EXTRA_LUA_UP_CONTENT, content);
                }
                if (Blockly.TYPE_CONTROLLER_SLIDER.equals(type) && TextUtils.equals(BlocklyFile.NAME_CHANGE, name + BlocklyFile.LUA_SUFFIX)) {
                    RcLogUtils.e("\nChange content:\n%s", content);
                    intent.putExtra(EXTRA_LUA_CHANGE_CONTENT, content);
                }
                if ((Blockly.TYPE_CONTROLLER_SWITCHTOGGLE.equals(type) || Blockly.TYPE_CONTROLLER_SWITCHTOUCH.equals(type)) && TextUtils.equals(BlocklyFile.NAME_ON, name + BlocklyFile.LUA_SUFFIX)) {
                    RcLogUtils.e("\nOn content:\n%s", content);
                    intent.putExtra(EXTRA_LUA_ON_CONTENT, content);
                }
                if ((Blockly.TYPE_CONTROLLER_SWITCHTOGGLE.equals(type) || Blockly.TYPE_CONTROLLER_SWITCHTOUCH.equals(type)) && TextUtils.equals(BlocklyFile.NAME_OFF, name + BlocklyFile.LUA_SUFFIX)) {
                    RcLogUtils.e("\nOff content:\n%s", content);
                    intent.putExtra(EXTRA_LUA_OFF_CONTENT, content);
                }
                if (TextUtils.equals(BlocklyFile.NAME_MICRO_PYTHON, name + BlocklyFile.PYTHON_SUFFIX)) {
                    RcLogUtils.e("\nMicroPython content:\n%s", content);
                    intent.putExtra(EXTRA_MICRO_PYTHON_CONTENT, content);
                }
            }
        } catch (Exception e) {
            return;
        }
        intent.putExtra(EXTRA_TYPE, type);
        intent.putExtra(EXTRA_BLOCKLY_ID, blocklyId);
        setResult(RESULT_OK, intent);
        finish();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onVisibilityEvent(VisibilityEvent event) {
        updateButtonStatus(event.isShown);
    }

    @Override
    public void finish() {
        super.finish();
        if (mBlocklyFragment != null) {
            mBlocklyFragment.close();
            mBlocklyFragment = null;
        }
    }

    private void updateButtonStatus(boolean isShown) {
        synchronized (ControllerBlocklyActivity.class) {
            if (mShownMenu == isShown) {
                return;
            }
            mShownMenu = isShown;
            int[] location = {0, 0};
            ArrayList<Animator> animators = new ArrayList<>();
            if (isShown) {
                mTitleTv.getLocationInWindow(location);
                animators.add(ObjectAnimator.ofFloat(mTitleTv, "translationY", -(location[1] + mTitleTv.getHeight()), 0));
                animators.add(ObjectAnimator.ofFloat(mTitleTv, "alpha", 0f, 1.0f));
                mCloseBtn.getLocationInWindow(location);
                animators.add(ObjectAnimator.ofFloat(mCloseBtn, "translationX", -(location[0] + mCloseBtn.getWidth()), 0));
                animators.add(ObjectAnimator.ofFloat(mCloseBtn, "alpha", 0f, 1.0f));
                mConfirmBtn.getLocationInWindow(location);
                animators.add(ObjectAnimator.ofFloat(mConfirmBtn, "translationX", ControllerConstValue.SCREEN_WIDTH - location[0], 0));
                animators.add(ObjectAnimator.ofFloat(mConfirmBtn, "alpha", 0f, 1.0f));
            } else {
                mTitleTv.getLocationInWindow(location);
                animators.add(ObjectAnimator.ofFloat(mTitleTv, "translationY", 0, -(location[1] + mTitleTv.getHeight())));
                animators.add(ObjectAnimator.ofFloat(mTitleTv, "alpha", 1.0f, 0f));
                mCloseBtn.getLocationInWindow(location);
                animators.add(ObjectAnimator.ofFloat(mCloseBtn, "translationX", 0, -(location[0] + mCloseBtn.getWidth())));
                animators.add(ObjectAnimator.ofFloat(mCloseBtn, "alpha", 1.0f, 0f));
                mConfirmBtn.getLocationInWindow(location);
                animators.add(ObjectAnimator.ofFloat(mConfirmBtn, "translationX", 0, ControllerConstValue.SCREEN_WIDTH - location[0]));
                animators.add(ObjectAnimator.ofFloat(mConfirmBtn, "alpha", 1.0f, 0f));
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
                        mTitleTv.setVisibility(View.VISIBLE);
                        mCloseBtn.setVisibility(View.VISIBLE);
                        mConfirmBtn.setVisibility(View.VISIBLE);
                    }
                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    mTitleTv.setTranslationY(0);
                    mCloseBtn.setTranslationX(0);
                    mConfirmBtn.setTranslationX(0);
                    if (!mShownMenu) {
                        mTitleTv.setVisibility(View.INVISIBLE);
                        mCloseBtn.setVisibility(View.INVISIBLE);
                        mConfirmBtn.setVisibility(View.INVISIBLE);
                    }
                }

                @Override
                public void onAnimationCancel(Animator animation) {
                }

                @Override
                public void onAnimationRepeat(Animator animation) {
                }
            });
            mAnimatorSet.playTogether(animators);
            mAnimatorSet.start();
        }
    }
}
