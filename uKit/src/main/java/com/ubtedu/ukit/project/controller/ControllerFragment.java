/**
 * ©2018, UBTECH Robotics, Inc. All rights reserved (C)
 **/
package com.ubtedu.ukit.project.controller;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.ClipData;
import android.content.ClipDescription;
import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Point;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.DragEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;

import androidx.annotation.Keep;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.LottieAnimationView;
import com.airbnb.lottie.LottieDrawable;
import com.ubtedu.alpha1x.utils.GsonUtil;
import com.ubtedu.deviceconnect.libs.base.interfaces.URoBatteryChangeListener;
import com.ubtedu.deviceconnect.libs.base.interfaces.URoConnectStatusChangeListener;
import com.ubtedu.deviceconnect.libs.base.model.URoBatteryInfo;
import com.ubtedu.deviceconnect.libs.base.product.URoConnectStatus;
import com.ubtedu.deviceconnect.libs.base.product.URoProduct;
import com.ubtedu.ukit.R;
import com.ubtedu.ukit.bluetooth.BluetoothHelper;
import com.ubtedu.ukit.bluetooth.BtInvocationFactory;
import com.ubtedu.ukit.bluetooth.error.BluetoothCommonErrorHelper;
import com.ubtedu.ukit.bluetooth.processor.PeripheralErrorCollector;
import com.ubtedu.ukit.bluetooth.search.BluetoothSearchActivity;
import com.ubtedu.ukit.common.analysis.Events;
import com.ubtedu.ukit.common.analysis.UBTReporter;
import com.ubtedu.ukit.common.base.UKitBaseFragment;
import com.ubtedu.ukit.common.base.UKitUIDelegate;
import com.ubtedu.ukit.common.dialog.PromptDialogFragment;
import com.ubtedu.ukit.common.dialog.PromptEditDialogFragment;
import com.ubtedu.ukit.common.eventbus.VisibilityEvent;
import com.ubtedu.ukit.menu.settings.Settings;
import com.ubtedu.ukit.project.bridge.api.ToastHelper;
import com.ubtedu.ukit.project.controller.adapter.ControllerToolbarItemAdapter;
import com.ubtedu.ukit.project.controller.adapter.ControllerWidgetItemAdapter;
import com.ubtedu.ukit.project.controller.blockly.ControllerBlocklyActivity;
import com.ubtedu.ukit.project.controller.factory.ControllerWidgetViewFactory;
import com.ubtedu.ukit.project.controller.interfaces.IControllerItemClickListener;
import com.ubtedu.ukit.project.controller.interfaces.IControllerModeChangeListener;
import com.ubtedu.ukit.project.controller.interfaces.IControllerWidgetListener;
import com.ubtedu.ukit.project.controller.manager.ControllerManager;
import com.ubtedu.ukit.project.controller.model.config.Joystick2DirectionHConfig;
import com.ubtedu.ukit.project.controller.model.config.Joystick2DirectionVConfig;
import com.ubtedu.ukit.project.controller.model.config.Joystick4DirectionConfig;
import com.ubtedu.ukit.project.controller.model.config.WidgetConfig;
import com.ubtedu.ukit.project.controller.model.define.ToolbarType;
import com.ubtedu.ukit.project.controller.model.define.WidgetProperty;
import com.ubtedu.ukit.project.controller.model.item.ToolbarItem;
import com.ubtedu.ukit.project.controller.model.item.WidgetItem;
import com.ubtedu.ukit.project.controller.settings.icon.ControllerIconDialogFragment;
import com.ubtedu.ukit.project.controller.settings.interfaces.IControllerIconSelectListener;
import com.ubtedu.ukit.project.controller.settings.interfaces.IControllerMotionSelectListener;
import com.ubtedu.ukit.project.controller.settings.interfaces.IControllerRangeConfirmListener;
import com.ubtedu.ukit.project.controller.settings.joystick.four.Joystick4DirectionSettingActivity;
import com.ubtedu.ukit.project.controller.settings.joystick.two.Joystick2DirectionSettingActivity;
import com.ubtedu.ukit.project.controller.settings.motion.ControllerMotionDialogFragment;
import com.ubtedu.ukit.project.controller.settings.slider.range.ControllerRangeDialogFragment;
import com.ubtedu.ukit.project.controller.utils.ControllerConstValue;
import com.ubtedu.ukit.project.controller.utils.RcLogUtils;
import com.ubtedu.ukit.project.controller.widget.ButtonCustomView;
import com.ubtedu.ukit.project.controller.widget.ButtonCustomViewV2;
import com.ubtedu.ukit.project.controller.widget.CellContainer;
import com.ubtedu.ukit.project.controller.widget.ControllerWidgetView;
import com.ubtedu.ukit.project.controller.widget.Joystick2DirectionViewH;
import com.ubtedu.ukit.project.controller.widget.Joystick2DirectionViewV;
import com.ubtedu.ukit.project.controller.widget.Joystick4DirectionView;
import com.ubtedu.ukit.project.controller.widget.ReaderColorView;
import com.ubtedu.ukit.project.controller.widget.ReaderValueView;
import com.ubtedu.ukit.project.controller.widget.SwitchToggleView;
import com.ubtedu.ukit.project.controller.widget.SwitchTouchView;
import com.ubtedu.ukit.project.controller.widget.SwitchTouchViewV2;
import com.ubtedu.ukit.project.vo.Blockly;
import com.ubtedu.ukit.project.vo.BlocklyFile;
import com.ubtedu.ukit.project.vo.Controller;
import com.ubtedu.ukit.project.vo.ControllerBlockly;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.ubtedu.ukit.project.controller.utils.ControllerConstValue.CELL_HEIGHT;
import static com.ubtedu.ukit.project.controller.utils.ControllerConstValue.CELL_WIDTH;
import static com.ubtedu.ukit.project.controller.utils.ControllerConstValue.DEFAULT_ANIMATION_DURATION;

/**
 * 遥控
 *
 * @Author qinicy
 * @Date 2018/11/5
 **/
public class ControllerFragment extends UKitBaseFragment<ControllerContracts.Presenter, ControllerContracts.UI> implements IControllerModeChangeListener, View.OnDragListener, ControllerManager.IExecutionStatusChangeListener, URoConnectStatusChangeListener, URoBatteryChangeListener {
    private CellContainer mCellContainer;
    private RecyclerView mToolbarRecyclerView;
    private RecyclerView mWidgetRecyclerView;
    private ImageButton mExecutionEntryBtn;
    private ImageButton mExecutionExitBtn;
    private ImageButton mExecutionPauseBtn;
    private LottieAnimationView mTrashBinIcon;

    private View mWidgetActionJoystick;
    private View mWidgetActionBlockly;
    private View mWidgetActionMotion;
    private View mWidgetActionRename;
    private View mWidgetActionRotate;
    private View mWidgetActionRange;
    private View mWidgetActionIcon;
    private View mWidgetActionUsage;
    private Button mBluetoothConnect;

    private View mTrashBinArea;
    private View mTitleArea;
    private View mToolbarListArea;
    private View mWidgetListArea;
    private View mWidgetActionArea;
    private View mButtonArea;
    private ImageView mBatteryIcon;
    private FrameLayout mDragLayer;

    private ControllerToolbarItemAdapter mToolbarListAdapter;
    private ControllerWidgetItemAdapter mWidgetListAdapter;

    private DragItemInfo mDragItemInfo;

    private boolean viewInited = false;

    private AnimatorSet changeDirectionAnimator = null;
    private final Object changeDirectionLock = new Object();

    private static final int REQUEST_CODE_SET_ROUND_JOYSTICK = 0x6789;
    private static final int REQUEST_CODE_SET_SCROLL_V_JOYSTICK = 0x6788;
    private static final int REQUEST_CODE_SET_SCROLL_H_JOYSTICK = 0x6787;
    private static final int REQUEST_CODE_BLOCKLY = 0x6786;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_project_controller, null);
        mCellContainer = view.findViewById(R.id.controller_cell_container);
        mToolbarRecyclerView = view.findViewById(R.id.controller_toolbar_list);
        mWidgetRecyclerView = view.findViewById(R.id.controller_widget_list);
        mExecutionEntryBtn = view.findViewById(R.id.controller_entry_execution);
        mExecutionExitBtn = view.findViewById(R.id.controller_exit_execution);
        mExecutionPauseBtn = view.findViewById(R.id.controller_pause_execution);
        mTrashBinIcon = view.findViewById(R.id.controller_trash_icon);
        mTitleArea = view.findViewById(R.id.controller_title_area);
        mToolbarListArea = view.findViewById(R.id.controller_toolbar_list_area);
        mWidgetListArea = view.findViewById(R.id.controller_widget_list_area);
        mTrashBinArea = view.findViewById(R.id.controller_trash_area);
        mWidgetActionArea = view.findViewById(R.id.controller_widget_action_area);
        mWidgetActionJoystick = view.findViewById(R.id.controller_widget_action_joystick);
        mWidgetActionBlockly = view.findViewById(R.id.controller_widget_action_blockly);
        mWidgetActionMotion = view.findViewById(R.id.controller_widget_action_motion);
        mWidgetActionRename = view.findViewById(R.id.controller_widget_action_rename);
        mWidgetActionRotate = view.findViewById(R.id.controller_widget_action_rotate);
        mWidgetActionRange = view.findViewById(R.id.controller_widget_action_range);
        mWidgetActionIcon = view.findViewById(R.id.controller_widget_action_icon);
        mWidgetActionUsage = view.findViewById(R.id.controller_widget_action_usage);
        mBluetoothConnect = view.findViewById(R.id.controller_bluetooth_btn);
        mDragLayer = view.findViewById(R.id.controller_drag_layer);
        mButtonArea = view.findViewById(R.id.controller_button_area);
        mBatteryIcon = view.findViewById(R.id.controller_battery_icon);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initViews();
        initData();
        initListener();
    }

    public void setController(Controller controller) {
        ControllerManager.setController(controller);
        if (viewInited) {
            getPresenter().entryControllerEditMode();
            getUIView().hideWidgetGroup();
            getUIView().hideWidgetAction();
            loadLayout();
        }
    }

    public boolean isControllerModified() {
        return ControllerManager.isModified();
    }

    public void resetModifyState() {
        ControllerManager.resetModifyState();
    }

    public Controller getController() {
        return ControllerManager.getController();
    }

    private void initViews() {
        mToolbarListArea.setZ(mWidgetListArea.getZ() + 1);
        ViewGroup.LayoutParams layoutParams = mWidgetListArea.getLayoutParams();
        layoutParams.width = ControllerConstValue.WIDGET_LIST_WIDTH_PIXEL;
        mWidgetListArea.setLayoutParams(layoutParams);
        mCellContainer.setGridSize(ControllerConstValue.GRID_X_CELLS, ControllerConstValue.GRID_Y_CELLS);

        mToolbarListAdapter = new ControllerToolbarItemAdapter();
        mToolbarRecyclerView.setOverScrollMode(View.OVER_SCROLL_NEVER);
        mToolbarRecyclerView.setAdapter(mToolbarListAdapter);
        mToolbarRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mToolbarRecyclerView.setHasFixedSize(true);

        mWidgetListAdapter = new ControllerWidgetItemAdapter();
        mWidgetRecyclerView.setOverScrollMode(View.OVER_SCROLL_NEVER);
        mWidgetRecyclerView.setAdapter(mWidgetListAdapter);
        mWidgetRecyclerView.setHasFixedSize(true);
        mWidgetRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        mTrashBinIcon.setRepeatMode(LottieDrawable.REVERSE);

        mDragLayer.removeAllViews();
        mDragLayer.setZ(mToolbarListArea.getZ() + 1);

        mButtonArea.setZ(mDragLayer.getZ() + 1);

        viewInited = true;

        ControllerManager.setCellContainer(mCellContainer);
    }

    private void initListener() {
        bindSafeClickListener(mExecutionEntryBtn);
        bindSafeClickListener(mExecutionExitBtn);
        bindSafeClickListener(mExecutionPauseBtn);
        bindClickListener(mCellContainer);
        bindSafeClickListener(mWidgetActionJoystick);
        bindSafeClickListener(mWidgetActionBlockly);
        bindSafeClickListener(mWidgetActionMotion);
        bindSafeClickListener(mWidgetActionRename);
        bindSafeClickListener(mWidgetActionRotate);
        bindSafeClickListener(mWidgetActionRange);
        bindSafeClickListener(mWidgetActionIcon);
        bindSafeClickListener(mWidgetActionUsage);
        bindSafeClickListener(mBluetoothConnect);
        ControllerModeHolder.addControllerModeChangeListener(this);
        ControllerManager.addExecutionStatusChangeListener(this);
        BluetoothHelper.addConnectStatusChangeListener(this);
        BluetoothHelper.addBatteryChangeListener(this);
        mCellContainer.setOnDragListener(this);
        mToolbarListArea.setOnDragListener(this);
        mTitleArea.setOnDragListener(this);
        mToolbarListAdapter.setItemClickListener(new IControllerItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                if (mToolbarListAdapter.isItemSelected(position)) {
                    getUIView().hideWidgetGroup();
                } else {
                    ToolbarItem item = mToolbarListAdapter.getItem(position);
                    mToolbarListAdapter.setSelectedItem(position, true);
                    getUIView().showWidgetGroup(item.getType());
                }
            }
        });
        mWidgetListAdapter.setButtonTouchListener(new IControllerWidgetListener() {
            @Override
            public void onWidgetClick(View view) {
                //按钮被点击
            }

            @Override
            public void onWidgetMove(View view) {
                int position = (int) view.getTag();
                WidgetItem item = mWidgetListAdapter.getItem(position);
                startDrag(DragItemInfo.DRAG_FROM_TOOLBAR, item, view);
            }

            @Override
            public String getBlocklyContent(String id, String name) {
                return null;
            }
        });
    }

    private void initData() {
        mToolbarListAdapter.addAll(getPresenter().loadToolbarItems());
        mWidgetListAdapter.addAll(getPresenter().loadButtonItems());
        getPresenter().entryControllerEditMode();
        loadLayout();
    }

    private boolean startDrag(int dragFrom, WidgetItem widgetItem, View view) {
        getUIView().hideWidgetGroup();
        getUIView().hideWidgetAction();
        ControllerWidgetView srcView = null;
        if (view instanceof ControllerWidgetView) {
            srcView = (ControllerWidgetView) view;
        }
        mDragItemInfo = new DragItemInfo(dragFrom, widgetItem, view, srcView);
        String timeTag = String.valueOf(System.currentTimeMillis());
        ClipData.Item item = new ClipData.Item(timeTag);
        String[] mimeTypes = {ClipDescription.MIMETYPE_TEXT_PLAIN};
        ClipData data = new ClipData(timeTag, mimeTypes, item);
        WidgetDragShadowBuilder dragShadow = new WidgetDragShadowBuilder(view);
        mDragItemInfo.dragShadow = dragShadow;
        if (!view.startDrag(data, dragShadow, null, 0)) {
            return false;
        }
        if (srcView != null) {
            int x = srcView.getWidgetConfig().getX();
            int y = srcView.getWidgetConfig().getY();
            mDragItemInfo.coordinate = new Point(x, y);
            if (dragFrom == DragItemInfo.DRAG_FROM_CONTAINER || (dragFrom == DragItemInfo.DRAG_FROM_TOOLBAR && !mCellContainer.checkOccupied(new Point(x, y), widgetItem.getSpanH(), widgetItem.getSpanV()))) {
                mCellContainer.updateDragMask(x, y, widgetItem.getSpanH(), widgetItem.getSpanV());
            }
            mDragItemInfo.dragLayerView = ControllerWidgetViewFactory.clone(srcView.getContext(), srcView);
            mDragItemInfo.dragLayerView.setShowBackground(true);
            mDragItemInfo.dragLayerView.setLayoutParams(new FrameLayout.LayoutParams(widgetItem.getSpanH() * CELL_WIDTH, widgetItem.getSpanV() * CELL_HEIGHT));
            mDragItemInfo.dragLayerView.setVisibility(View.INVISIBLE);
            mDragLayer.removeAllViews();
            mDragLayer.addView(mDragItemInfo.dragLayerView);
        }
        return true;
    }

    @Override
    public boolean onDrag(View v, DragEvent event) {
        int action = event.getAction();
        switch (action) {
            case DragEvent.ACTION_DRAG_STARTED:
//                RcLogUtils.e("ACTION_DRAG_STARTED");
                if (mDragItemInfo != null) {
                    //开始拖拽模式
                    showTrashBin();
                    return true;
                }
                return false;

            case DragEvent.ACTION_DRAG_ENTERED:
//                RcLogUtils.e("ACTION_DRAG_ENTERED");
                if (mDragItemInfo != null) {
                    //标识已经拖拽进入了事件响应区域
                    mDragItemInfo.outOfView = false;
                }
                if (v == mToolbarListArea) {
                    //垃圾桶开盖
                    showTrashBinOpenAnim();
                    mCellContainer.setDragMask(false);
                }
                return true;

            case DragEvent.ACTION_DRAG_LOCATION:
//                RcLogUtils.e("ACTION_DRAG_LOCATION");
                int x, y;
                boolean invalidFlag;
                if (v == mCellContainer) {
                    //更新阴影区域
                    updateDragMask((int) event.getX(), (int) event.getY());
                    x = ControllerConstValue.TOOLBAR_WIDTH_PIXEL + (int) event.getX();
                    y = ControllerConstValue.TITLE_HEIGHT_PIXEL + (int) event.getY();
                    invalidFlag = false;
                } else {
                    x = (int) event.getX();
                    y = (int) event.getY();
                    invalidFlag = true;
                }
                updateDragView(x, y, invalidFlag);
                return true;

            case DragEvent.ACTION_DRAG_EXITED:
//                RcLogUtils.e("ACTION_DRAG_EXITED");
                if (mDragItemInfo != null) {
                    //标识已经拖拽离开了事件响应区域
                    mDragItemInfo.outOfView = true;
                }
                if (v == mToolbarListArea) {
                    //垃圾桶关盖
                    showTrashBinCloseAnim();
                    mCellContainer.setDragMask(true);
                }
                return true;

            case DragEvent.ACTION_DROP:
//                RcLogUtils.e("ACTION_DROP");
                if (mDragItemInfo != null) {
                    mDragItemInfo.hasDropped = true;
                }
                if (v == mToolbarListArea) {
                    //删除按钮
                    if (mDragItemInfo != null && mDragItemInfo.dragFrom == DragItemInfo.DRAG_FROM_CONTAINER) {
                        mCellContainer.removeView(mDragItemInfo.srcView);
                        ControllerManager.removeWidgetConfigById(mDragItemInfo.srcView.getWidgetConfig().id);
                        mWidgetListAdapter.setHasJoystick(ControllerManager.hasRoundJoystick());
                    }
                } else if (v == mCellContainer) {
                    //尝试添加到工作区
                    tryAddViewToCellContainer((int) event.getX(), (int) event.getY());
                } else {
                    //尝试还原到工作区
                    tryRestoreToCellContainer();
                }
                return true;

            case DragEvent.ACTION_DRAG_ENDED:
//                RcLogUtils.e("ACTION_DRAG_ENDED");
                if (mDragItemInfo != null && (mDragItemInfo.outOfView || !mDragItemInfo.hasDropped)) {
                    //在无事件响应区域退出拖拽时，则尝试还原View到CellContainer
                    mDragItemInfo.outOfView = false;
                    mDragItemInfo.hasDropped = false;
                    tryRestoreToCellContainer();
                }
                //退出拖拽模式
                hideTrashBin();
                resetDragMask(true);
                return true;
        }
        return false;
    }

    @Override
    public void onVisibilityChangedToUser(boolean isVisibleToUser) {
        super.onVisibilityChangedToUser(isVisibleToUser);
        reportPageEvent(isVisibleToUser);
    }

    private void reportPageEvent(boolean isVisibleToUser) {
        String name = "remote_controller";
        if (isVisibleToUser) {
            UBTReporter.onPageStart(name);
        } else {
            UBTReporter.onPageEnd(name);
        }
    }

    private void tryRestoreToCellContainer() {
//        RcLogUtils.e("tryRestoreToCellContainer");
        if (mDragItemInfo == null) {
//            RcLogUtils.e("DragItemInfo is null");
            return;
        }
        if (mDragItemInfo.dragFrom == DragItemInfo.DRAG_FROM_TOOLBAR) {
//            RcLogUtils.e("Drag from toolbar");
            return;
        }
        if (mDragItemInfo.coordinate == null || mDragItemInfo.coordinate.x < 0 || mDragItemInfo.coordinate.y < 0) {
//            RcLogUtils.e("Invalid position");
            return;
        }
        mCellContainer.removeView(mDragItemInfo.srcView);
        mDragItemInfo.srcView.setShowBackground(true);
        mDragItemInfo.srcView.setInvalidFlag(false);
        mDragItemInfo.srcView.setVisibility(View.VISIBLE);
        mCellContainer.addViewToGrid(mDragItemInfo.srcView, mDragItemInfo.coordinate.x, mDragItemInfo.coordinate.y);
        ControllerManager.updateWidgetConfig(mDragItemInfo.srcView.getWidgetConfig());
    }

    private void tryAddViewToCellContainer(int x, int y) {
        if (mDragItemInfo == null) {
            return;
        }
        if (mDragItemInfo.coordinate == null || mDragItemInfo.coordinate.x < 0 || mDragItemInfo.coordinate.y < 0) {
            return;
        }
        ControllerWidgetView view;
        if (mDragItemInfo.dragFrom == DragItemInfo.DRAG_FROM_TOOLBAR) {
            view = ControllerWidgetViewFactory.createViewByWidgetItem(getContext(), mDragItemInfo.widgetItem);
            view.setWidgetTouchListener(newWidgetListener());
            ControllerManager.updateWidgetConfig(view.getWidgetConfig());
            view.setWidgetConfig(view.getWidgetConfig());
            mWidgetListAdapter.setHasJoystick(ControllerManager.hasRoundJoystick());
        } else {
            view = mDragItemInfo.srcView;
            mCellContainer.removeView(view);
            //ControllerManager.removeWidgetConfig(view.getWidgetConfig());
        }
        view.setShowBackground(true);
        view.setInvalidFlag(false);
        view.setVisibility(View.VISIBLE);
        mCellContainer.addViewToGrid(view, mDragItemInfo.coordinate.x, mDragItemInfo.coordinate.y);
        ControllerManager.updateWidgetConfig(view.getWidgetConfig());
    }

    private void loadLayout() {
        mWidgetListAdapter.setHasJoystick(ControllerManager.hasRoundJoystick());
        mCellContainer.removeAllViews();
        Controller controller = getController();
        if (controller == null) {
            return;
        }
        for (WidgetConfig widgetConfig : controller.configs) {
            ControllerWidgetView view = ControllerWidgetViewFactory.createViewByWidgetConfig(getContext(), widgetConfig);
            if (view != null) {
                view.setWidgetTouchListener(newWidgetListener());
                mCellContainer.addViewToGrid(view);
            }
        }
    }

    private void updateDragMask(int x, int y) {
        if (mDragItemInfo == null) {
            return;
        }
        Point coordinate = mCellContainer.checkDragMask(x, y, mDragItemInfo.widgetItem.getSpanH(), mDragItemInfo.widgetItem.getSpanV());
        if (coordinate != null) {
            mDragItemInfo.coordinate = coordinate;
            mCellContainer.updateDragMask(coordinate.x, coordinate.y, mDragItemInfo.widgetItem.getSpanH(), mDragItemInfo.widgetItem.getSpanV());
        }
        if (mDragItemInfo.dragLayerView != null) {
            mDragItemInfo.dragLayerView.setInvalidFlag(coordinate == null);
        }
    }

    private void updateDragView(int x, int y, boolean invalidFlag) {
        if (mDragItemInfo.dragLayerView != null) {
            if (mDragItemInfo.dragLayerView.getVisibility() != View.VISIBLE) {
                mDragItemInfo.dragLayerView.setVisibility(View.VISIBLE);
            }
            mDragItemInfo.dragLayerView.setTranslationX(x - (mDragItemInfo.dragLayerView.getWidth() / 2.0f));
            mDragItemInfo.dragLayerView.setTranslationY(y - (mDragItemInfo.dragLayerView.getHeight() / 2.0f));
            mDragItemInfo.dragLayerView.setAlpha(0.8f);
            if (invalidFlag) {
                mDragItemInfo.dragLayerView.setInvalidFlag(true);
            }
        }
    }

    private void resetDragMask(boolean cleanup) {
        mCellContainer.resetDragMask();
        if (cleanup) {
            mDragLayer.removeAllViews();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N && mDragItemInfo != null && mDragItemInfo.view != null) {
                mDragItemInfo.view.cancelDragAndDrop();
            }
            mDragItemInfo = null;
        }
    }

    private void showTrashBin() {
        mTrashBinArea.setVisibility(View.VISIBLE);
        if (mTrashBinIcon.isAnimating()) {
            mTrashBinIcon.cancelAnimation();
            mTrashBinIcon.setFrame(0);
        }
    }

    private void hideTrashBin() {
        mTrashBinArea.setVisibility(View.GONE);
    }

    private void showTrashBinOpenAnim() {
        if (mTrashBinArea.getVisibility() != View.VISIBLE) {
            return;
        }
        if (mTrashBinIcon.isAnimating()) {
            mTrashBinIcon.cancelAnimation();
        }
        mTrashBinIcon.playAnimation();
    }

    private void showTrashBinCloseAnim() {
        if (mTrashBinArea.getVisibility() != View.VISIBLE) {
            return;
        }
        if (mTrashBinIcon.isAnimating()) {
            mTrashBinIcon.cancelAnimation();
            mTrashBinIcon.setFrame(0);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ControllerModeHolder.removeControllerModeChangeListener(this);
        ControllerManager.removeExecutionStatusChangeListener(this);
        BluetoothHelper.removeBatteryChangeListener(this);
        BluetoothHelper.removeConnectStatusChangeListener(this);
    }

    @Override
    public void onControllerModeChanged(int controllerMode) {
        if (controllerMode == ControllerModeHolder.MODE_EXECUTION) {
            PeripheralErrorCollector.getInstance().updateCollectorState(PeripheralErrorCollector.CollectorState.START, PeripheralErrorCollector.ErrorCollectorType.CONTROLLER);
            PeripheralErrorCollector.getInstance().setBlocked(!ControllerManager.isSmartDevice());
            EventBus.getDefault().post(new VisibilityEvent(false));
            mCellContainer.setShowDivider(false);
            getUIView().hideAllOperationBar();
            if (!BluetoothHelper.isConnected()) {
                mBluetoothConnect.setVisibility(View.VISIBLE);
                mBatteryIcon.setVisibility(View.GONE);
            } else {
                mBatteryIcon.setVisibility(View.VISIBLE);
                mBatteryIcon.setImageLevel(getBatteryLevel());
            }
            reportPlayWidgetsEvent();
        } else {
            PeripheralErrorCollector.getInstance().updateCollectorState(PeripheralErrorCollector.CollectorState.STOP, null);
            EventBus.getDefault().post(new VisibilityEvent(true));
            mCellContainer.setShowDivider(true);
            getUIView().showAllOperationBar();
            mExecutionPauseBtn.setVisibility(View.GONE);
            mBluetoothConnect.setVisibility(View.GONE);
            mBatteryIcon.setVisibility(View.GONE);
        }
    }

    private int getBatteryLevel() {
        URoBatteryInfo batteryInfo = BluetoothHelper.getBatteryInfo();
        int level = batteryInfo != null ? batteryInfo.getBatteryRemaining() : 0;
        return (int) Math.ceil(level / 20f);
    }

    private void reportPlayWidgetsEvent() {
        if (ControllerManager.getController() != null && ControllerManager.getController().configs != null) {
            List<WidgetConfig> configs = ControllerManager.getController().configs;
            List<Map<String, String>> configMap = new ArrayList<>();
            for (int i = 0; i < configs.size(); i++) {
                WidgetConfig config = configs.get(i);
                Map<String, String> map = new HashMap<>();
                map.put("type", config.type);
                configMap.add(map);
            }
            Map<String, String> args = new HashMap<>();
            if (configMap.size() > 0) {
                args.put("widgets", GsonUtil.get().toJson(configMap));
            }
            UBTReporter.onEvent(Events.Ids.app_project_remote_play_widgets, args);

        }

    }

    @Override
    public void onExecutionStatusChanged(boolean hasExecution) {
        if (ControllerModeHolder.getControllerMode() != ControllerModeHolder.MODE_EXECUTION) {
            return;
        }
        PeripheralErrorCollector.getInstance().setBlocked(!ControllerManager.isSmartDevice() && !hasExecution);
//        if (hasExecution) {
//            BluetoothHelper.addRequest(UKitCmdBoardSelfCheck.newInstance(true));
//        } else {
//            BluetoothHelper.addRequest(UKitCmdBoardSelfCheck.newInstance(false));
//            PeripheralErrorCollector.getInstance().showErrorIfNotEmpty();
//        }
        if (!hasExecution) {
            PeripheralErrorCollector.getInstance().showErrorIfNotEmpty();
        }
        mExecutionPauseBtn.setVisibility(hasExecution ? View.VISIBLE : View.GONE);
    }

    @Override
    public void onPauseExecutionStart() {
        UKitUIDelegate ui = getUIDelegate();
        if (ui == null) {
            return;
        }
        ui.showLoading(false);
    }

    @Override
    public void onPauseExecutionEnd() {
        UKitUIDelegate ui = getUIDelegate();
        if (ui == null) {
            return;
        }
        ui.hideLoading();
    }
//    @Override
//    public void onConnectStateChanged(UKitRemoteDevice device, boolean oldState, boolean newState, boolean verified) {
//        if (ControllerModeHolder.getControllerMode() != ControllerModeHolder.MODE_EXECUTION) {
//            return;
//        }
//        if(newState) {
//            mBluetoothConnect.setVisibility(View.GONE);
//            BluetoothHelper.addCommand(BtInvocationFactory.boardSelfCheck(true));
//        } else {
//            mBluetoothConnect.setVisibility(View.VISIBLE);
//        }
//    }

    @Override
    public void onBatteryInfoUpdated(URoProduct product, URoBatteryInfo batteryInfo) {
        mBatteryIcon.setImageLevel(getBatteryLevel());
    }

    @Override
    public void onClick(View v, boolean isSafeClick) {
        super.onClick(v, isSafeClick);
        if (v == mExecutionEntryBtn) {
            if (!BluetoothHelper.isBluetoothConnected()) {
                BluetoothCommonErrorHelper.openBluetoothCommonErrorActivity(BluetoothCommonErrorHelper.CommonError.BLUETOOTH_NOT_CONNECTED);
            } else {
                if (Settings.isChargingProtection() && BluetoothHelper.isCharging()) {
                    BluetoothCommonErrorHelper.openBluetoothCommonErrorActivity(BluetoothCommonErrorHelper.CommonError.CHARGING_PROTECTION);
                    return;
                }
            }
            getPresenter().entryControllerExecutionMode();
        } else if (v == mExecutionExitBtn) {
            getPresenter().entryControllerEditMode();
        } else if (v == mExecutionPauseBtn) {
            ControllerManager.terminateExecution(false,true);
        } else if (v == mCellContainer) {
            if (ControllerModeHolder.getControllerMode() == ControllerModeHolder.MODE_EDIT) {
                getUIView().hideWidgetGroup();
                getUIView().hideWidgetAction();
            }
        } else if (v == mWidgetActionJoystick) {
            openJoystickSetting();
        } else if (v == mWidgetActionBlockly) {
            openControllerBlockly();
        } else if (v == mWidgetActionMotion) {
            showMotionDialog();
        } else if (v == mWidgetActionRename) {
            showRenameDialog();
        } else if (v == mWidgetActionRotate) {
            changeDirection();
        } else if (v == mWidgetActionRange) {
            showSetRangeDialog();
        } else if (v == mWidgetActionIcon) {
            showSelectIconDialog();
        } else if (v == mWidgetActionUsage) {
            showUsageDialog();
        } else if (v == mBluetoothConnect) {
            Intent intent = new Intent(getContext(), BluetoothSearchActivity.class);
            startActivity(intent);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_CODE_SET_ROUND_JOYSTICK:
                if (resultCode == Activity.RESULT_OK) {
                    ControllerWidgetView view = mCellContainer.findSelectWidget();
                    if (view instanceof Joystick4DirectionView) {
                        Joystick4DirectionView joystickView = (Joystick4DirectionView) view;
                        Joystick4DirectionConfig config = joystickView.getWidgetConfig();
                        Joystick4DirectionConfig newConfig = (Joystick4DirectionConfig) data.getSerializableExtra(Joystick4DirectionSettingActivity.EXTRA_JOYSTICK_CONFIG);
                        config.setControlType(newConfig.getControlType());
                        config.setMode(newConfig.getMode());
                        if (config.getMode() == Joystick4DirectionConfig.MODE_TWO_WHEEL_DRIVE) {
                            config.setLeftId(newConfig.getLeftId());
                            config.setRightId(newConfig.getRightId());
                        } else {
                            config.setLeftFrontId(newConfig.getLeftFrontId());
                            config.setRightFrontId(newConfig.getRightFrontId());
                            config.setLeftBehindId(newConfig.getLeftBehindId());
                            config.setRightBehindId(newConfig.getRightBehindId());
                        }
                        joystickView.setWidgetConfig(config);
                        ControllerManager.updateWidgetConfig(config);
                    }
                }
                break;

            case REQUEST_CODE_SET_SCROLL_H_JOYSTICK:
                if (resultCode == Activity.RESULT_OK) {
                    ControllerWidgetView view = mCellContainer.findSelectWidget();
                    if (view instanceof Joystick2DirectionViewH) {
                        Joystick2DirectionViewH joystickView = (Joystick2DirectionViewH) view;
                        Joystick2DirectionHConfig config = joystickView.getWidgetConfig();
                        config.setControlType(data.getIntExtra(Joystick2DirectionSettingActivity.EXTRA_CONTROL_TYPE, config.getControlType()));
                        config.setControlId(data.getIntExtra(Joystick2DirectionSettingActivity.EXTRA_CONTROL_ID, config.getControlId()));
                        config.setDirection(data.getIntExtra(Joystick2DirectionSettingActivity.EXTRA_JOYSTICK_DIRECTION, config.getDirection()));
                        joystickView.setWidgetConfig(config);
                        ControllerManager.updateWidgetConfig(config);
                    }
                }
                break;

            case REQUEST_CODE_SET_SCROLL_V_JOYSTICK:
                if (resultCode == Activity.RESULT_OK) {
                    ControllerWidgetView view = mCellContainer.findSelectWidget();
                    if (view instanceof Joystick2DirectionViewV) {
                        Joystick2DirectionViewV joystickView = (Joystick2DirectionViewV) view;
                        Joystick2DirectionVConfig config = joystickView.getWidgetConfig();
                        config.setControlType(data.getIntExtra(Joystick2DirectionSettingActivity.EXTRA_CONTROL_TYPE, config.getControlType()));
                        config.setControlId(data.getIntExtra(Joystick2DirectionSettingActivity.EXTRA_CONTROL_ID, config.getControlId()));
                        config.setDirection(data.getIntExtra(Joystick2DirectionSettingActivity.EXTRA_JOYSTICK_DIRECTION, config.getDirection()));
                        joystickView.setWidgetConfig(config);
                        ControllerManager.updateWidgetConfig(config);
                    }
                }
                break;

            case REQUEST_CODE_BLOCKLY:
                if (resultCode == Activity.RESULT_OK) {
                    ControllerWidgetView view = mCellContainer.findSelectWidget();
                    if (view == null
                            || view instanceof Joystick4DirectionView
                            || view instanceof Joystick2DirectionViewH
                            || view instanceof Joystick2DirectionViewV
                            || view instanceof ReaderValueView
                            || view instanceof ReaderColorView) {
                        return;
                    }
                    String type = data.getStringExtra(ControllerBlocklyActivity.EXTRA_TYPE);
                    String blocklyId = data.getStringExtra(ControllerBlocklyActivity.EXTRA_BLOCKLY_ID);
                    blocklyId = ControllerManager.setBlocklyContent(blocklyId, data.getStringExtra(ControllerBlocklyActivity.EXTRA_WORKSPACE_CONTENT));
                    view.setBlockId(blocklyId);
                    ControllerManager.updateWidgetConfig(view.getWidgetConfig());
                    if (TextUtils.equals(type, ControllerBlockly.TYPE_CONTROLLER_SLIDER) && data.hasExtra(ControllerBlocklyActivity.EXTRA_LUA_CHANGE_CONTENT)) {
                        ControllerManager.setBlocklyFileContent(blocklyId, BlocklyFile.NAME_CHANGE, data.getStringExtra(ControllerBlocklyActivity.EXTRA_LUA_CHANGE_CONTENT));
                    }
                    if ((TextUtils.equals(type, ControllerBlockly.TYPE_CONTROLLER_SWITCHTOGGLE) || TextUtils.equals(type, ControllerBlockly.TYPE_CONTROLLER_SWITCHTOUCH)) && data.hasExtra(ControllerBlocklyActivity.EXTRA_LUA_OFF_CONTENT)) {
                        ControllerManager.setBlocklyFileContent(blocklyId, BlocklyFile.NAME_OFF, data.getStringExtra(ControllerBlocklyActivity.EXTRA_LUA_OFF_CONTENT));
                    }
                    if ((TextUtils.equals(type, ControllerBlockly.TYPE_CONTROLLER_SWITCHTOGGLE) || TextUtils.equals(type, ControllerBlockly.TYPE_CONTROLLER_SWITCHTOUCH)) && data.hasExtra(ControllerBlocklyActivity.EXTRA_LUA_ON_CONTENT)) {
                        ControllerManager.setBlocklyFileContent(blocklyId, BlocklyFile.NAME_ON, data.getStringExtra(ControllerBlocklyActivity.EXTRA_LUA_ON_CONTENT));
                    }
                    if (TextUtils.equals(type, ControllerBlockly.TYPE_CONTROLLER_BUTTON) && data.hasExtra(ControllerBlocklyActivity.EXTRA_LUA_DOWN_CONTENT)) {
                        ControllerManager.setBlocklyFileContent(blocklyId, BlocklyFile.NAME_DOWN, data.getStringExtra(ControllerBlocklyActivity.EXTRA_LUA_DOWN_CONTENT));
                    }
                    if (TextUtils.equals(type, ControllerBlockly.TYPE_CONTROLLER_BUTTON) && data.hasExtra(ControllerBlocklyActivity.EXTRA_LUA_UP_CONTENT)) {
                        ControllerManager.setBlocklyFileContent(blocklyId, BlocklyFile.NAME_UP, data.getStringExtra(ControllerBlocklyActivity.EXTRA_LUA_UP_CONTENT));
                    }
                    if (data.hasExtra(ControllerBlocklyActivity.EXTRA_MICRO_PYTHON_CONTENT)) {
                        ControllerManager.setBlocklyFileContent(blocklyId, BlocklyFile.NAME_MICRO_PYTHON, data.getStringExtra(ControllerBlocklyActivity.EXTRA_MICRO_PYTHON_CONTENT));
                    }
                }
                break;

            default:
                super.onActivityResult(requestCode, resultCode, data);
                break;
        }
    }

    private void openControllerBlockly() {
        ControllerWidgetView view = mCellContainer.findSelectWidget();
        if (view == null
                || view instanceof Joystick4DirectionView
                || view instanceof Joystick2DirectionViewH
                || view instanceof Joystick2DirectionViewV
                || view instanceof ReaderValueView
                || view instanceof ReaderColorView) {
            return;
        }
        String type;
        if (view instanceof ButtonCustomView || view instanceof ButtonCustomViewV2) {
            type = Blockly.TYPE_CONTROLLER_BUTTON;
        } else if (view instanceof SwitchToggleView) {
            type = Blockly.TYPE_CONTROLLER_SWITCHTOGGLE;
        } else if (view instanceof SwitchTouchView || view instanceof SwitchTouchViewV2) {
            type = Blockly.TYPE_CONTROLLER_SWITCHTOUCH;
        } else {
            type = Blockly.TYPE_CONTROLLER_SLIDER;
        }
        String blockId = view.getBlockId();
        String workspaceContent = null;
        if (!TextUtils.isEmpty(blockId)) {
            ControllerBlockly controllerBlockly = ControllerManager.findControllerBlocklyById(blockId);
            if (controllerBlockly != null) {
                workspaceContent = controllerBlockly.workspace;
            }
        }
        if (TextUtils.isEmpty(blockId)) {
            blockId = "";
        }
        if (TextUtils.isEmpty(workspaceContent)) {
            workspaceContent = "";
        }
        String subscribeId = view.getWidgetConfig().subscribeId;
        ControllerBlocklyActivity.openControllerBlocklyActivity(this, REQUEST_CODE_BLOCKLY, type, view.getWidgetName(), blockId, subscribeId, workspaceContent);
    }

    private void openJoystickSetting() {
//        if(!BluetoothHelper.isConnected()) {
//            PromptDialogFragment.newBuilder(getContext())
//                .type(PromptDialogFragment.Type.NORMAL)
//                .title(getString(R.string.bluetooth_not_connect_title))
//                .message(getString(R.string.bluetooth_not_connect_msg))
//                .positiveButtonText(getString(R.string.bluetooth_connect_text))
//                .negativeButtonText(getString(R.string.bluetooth_search_cancel))
//                .onPositiveClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        startActivity(new Intent(getContext(), BluetoothSearchActivity.class));
//                    }
//                })
//                .cancelable(false)
//                .build()
//                .show(getChildFragmentManager(), "not_connect");
//            return;
//        }
        ControllerWidgetView view = mCellContainer.findSelectWidget();
        if (view == null || (!(view instanceof Joystick4DirectionView) && !(view instanceof Joystick2DirectionViewH) && !(view instanceof Joystick2DirectionViewV))) {
            return;
        }
        if (view instanceof Joystick4DirectionView) {
            Joystick4DirectionView joystickView = (Joystick4DirectionView) view;
            Joystick4DirectionSettingActivity.openSetting(this, REQUEST_CODE_SET_ROUND_JOYSTICK, joystickView.getWidgetConfig());
        } else if (view instanceof Joystick2DirectionViewH) {
            Joystick2DirectionViewH joystickView = (Joystick2DirectionViewH) view;
            Joystick2DirectionSettingActivity.openSetting(this, REQUEST_CODE_SET_SCROLL_H_JOYSTICK, joystickView.getWidgetConfig());
        } else {
            Joystick2DirectionViewV joystickView = (Joystick2DirectionViewV) view;
            Joystick2DirectionSettingActivity.openSetting(this, REQUEST_CODE_SET_SCROLL_V_JOYSTICK, joystickView.getWidgetConfig());
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        ControllerManager.resume();
    }

    @Override
    public void onPause() {
        ControllerManager.pause();
        super.onPause();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (getActivity() != null) {
            getActivity().getSupportFragmentManager().registerFragmentLifecycleCallbacks(controllerDialogCb, true);
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        if (getActivity() != null) {
            getActivity().getSupportFragmentManager().unregisterFragmentLifecycleCallbacks(controllerDialogCb);
        }
    }

    private Animator mergeAnimator(Animator... animators) {
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(animators);
        return animatorSet;
    }

    private void changeDirection() {
        synchronized (changeDirectionLock) {
            if (changeDirectionAnimator != null) {
                return;
            }
            ControllerWidgetView view = mCellContainer.findSelectWidget();
            if (view == null || (!(view instanceof Joystick2DirectionViewH) && !(view instanceof Joystick2DirectionViewV))) {
                return;
            }
            boolean _enoughSpace = false;
            int currX = view.getWidgetConfig().getX();
            int currY = view.getWidgetConfig().getY();
            ControllerWidgetView _appendView;
            ControllerWidgetView _newView;
            Joystick2DirectionViewH _viewH;
            Joystick2DirectionViewV _viewV;
            ArrayList<Animator> animators = new ArrayList<>();
            if (view instanceof Joystick2DirectionViewH) {
                int spanX = 3;
                int spanY = 2;
                Point start1, start2;
                start1 = new Point(currX + 2, currY - 2);
                start2 = new Point(currX + 2, currY + 3);
                if (!mCellContainer.checkOccupied(start1, spanX, spanY) && !mCellContainer.checkOccupied(start2, spanX, spanY)) {
                    _enoughSpace = true;
                }
                _viewH = (Joystick2DirectionViewH) view;
                _viewV = ControllerWidgetViewFactory.conversionView(getContext(), _viewH);
                _newView = _viewV;
                _viewV.setSelectedFlag(_viewH.isSelected());
                mCellContainer.addViewToGrid(_viewV, false);
                animators.add(mergeAnimator(
                        ObjectAnimator.ofFloat(_viewH, "rotation", 0, 90.0f),
                        ObjectAnimator.ofFloat(_viewV, "rotation", -90.f, 0),
                        ObjectAnimator.ofFloat(_viewH, "alpha", 1.0f, 0),
                        ObjectAnimator.ofFloat(_viewV, "alpha", 0, 1.0f)
                ).setDuration(DEFAULT_ANIMATION_DURATION));
                if (!_enoughSpace) {
                    animators.add(ObjectAnimator.ofFloat(_viewV, "rotation", 0, 45.0f, -45.0f, 0).setDuration(DEFAULT_ANIMATION_DURATION));
                    animators.add(mergeAnimator(
                            ObjectAnimator.ofFloat(_viewH, "rotation", 90.0f, 0),
                            ObjectAnimator.ofFloat(_viewV, "rotation", 0, -90.0f),
                            ObjectAnimator.ofFloat(_viewH, "alpha", 0, 1.0f),
                            ObjectAnimator.ofFloat(_viewV, "alpha", 1.0f, 0)
                    ).setDuration(DEFAULT_ANIMATION_DURATION));
                    _appendView = _viewH;
                } else {
                    _appendView = _viewV;
                }
            } else {
                int spanX = 2;
                int spanY = 3;
                Point start1, start2;
                start1 = new Point(currX - 2, currY + 2);
                start2 = new Point(currX + 3, currY + 2);
                if (!mCellContainer.checkOccupied(start1, spanX, spanY) && !mCellContainer.checkOccupied(start2, spanX, spanY)) {
                    _enoughSpace = true;
                }
                _viewV = (Joystick2DirectionViewV) view;
                _viewH = ControllerWidgetViewFactory.conversionView(getContext(), _viewV);
                _newView = _viewH;
                _viewH.setSelectedFlag(_viewV.isSelected());
                mCellContainer.addViewToGrid(_viewH, false);
                animators.add(mergeAnimator(
                        ObjectAnimator.ofFloat(_viewH, "rotation", 90.0f, 0),
                        ObjectAnimator.ofFloat(_viewV, "rotation", 0, -90.f),
                        ObjectAnimator.ofFloat(_viewH, "alpha", 0, 1.0f),
                        ObjectAnimator.ofFloat(_viewV, "alpha", 1.0f, 0)
                ).setDuration(DEFAULT_ANIMATION_DURATION));
                if (!_enoughSpace) {
                    animators.add(ObjectAnimator.ofFloat(_viewH, "rotation", 0, -45.0f, 45.0f, 0).setDuration(DEFAULT_ANIMATION_DURATION));
                    animators.add(mergeAnimator(
                            ObjectAnimator.ofFloat(_viewH, "rotation", 0, 90.0f),
                            ObjectAnimator.ofFloat(_viewV, "rotation", -90.0f, 0),
                            ObjectAnimator.ofFloat(_viewH, "alpha", 1.0f, 0),
                            ObjectAnimator.ofFloat(_viewV, "alpha", 0, 1.0f)
                    ).setDuration(DEFAULT_ANIMATION_DURATION));
                    _appendView = _viewV;
                } else {
                    _appendView = _viewH;
                }
            }
            final ControllerWidgetView appendView = _appendView;
            final ControllerWidgetView newView = _newView;
            final Joystick2DirectionViewH viewH = _viewH;
            final Joystick2DirectionViewV viewV = _viewV;
            final boolean enoughSpace = _enoughSpace;
            changeDirectionAnimator = new AnimatorSet();
            AnimatorSet animatorSet = changeDirectionAnimator;
            animatorSet.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {
                    synchronized (changeDirectionLock) {
                        viewV.setWidgetTouchListener(null);
                        viewH.setWidgetTouchListener(null);
                    }
                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    synchronized (changeDirectionLock) {
                        changeDirectionAnimator = null;
                        mCellContainer.removeView(viewH, newView != viewH);
                        mCellContainer.removeView(viewV, newView != viewV);
                        mCellContainer.addViewToGrid(appendView, true);
                        if (enoughSpace) {
                            ControllerManager.removeWidgetConfigById(viewH.getWidgetConfig().id);
                            ControllerManager.removeWidgetConfigById(viewV.getWidgetConfig().id);
                            ControllerManager.updateWidgetConfig(appendView.getWidgetConfig(), false);
                        }
                        appendView.setWidgetTouchListener(newWidgetListener());
                    }
                }

                @Override
                public void onAnimationCancel(Animator animation) {
                }

                @Override
                public void onAnimationRepeat(Animator animation) {
                }
            });
            animatorSet.playSequentially(animators);
            animatorSet.start();
        }
    }

    private void showMotionDialog() {
        ControllerWidgetView view = mCellContainer.findSelectWidget();
        if (view == null) {
            return;
        }
        ControllerMotionDialogFragment.newBuilder()
                .setWidgetType(view.getWidgetConfig().type)
                .setSelectedMotionId(view.getMotionId())
                .setMotionSelectListener(new IControllerMotionSelectListener() {
                    @Override
                    public void onMotionSelected(String motionId) {
                        ControllerWidgetView view = mCellContainer.findSelectWidget();
                        if (view != null) {
                            view.setWidgetName(ControllerManager.generateWidgetNameByMotionId(view.getWidgetConfig().type, motionId, view.getWidgetName()));
                            view.setMotionId(motionId);
                            ControllerManager.updateWidgetConfig(view.getWidgetConfig());
                        }
                    }
                })
                .build()
                .show(getChildFragmentManager(), "set_motion_id");
    }

    private void showSetRangeDialog() {
        ControllerWidgetView view = mCellContainer.findSelectWidget();
        if (view == null) {
            return;
        }
        ControllerRangeDialogFragment.newBuilder(getContext())
                .setMaxValue(view.getMaxRange())
                .setMinValue(view.getMinRange())
                .setRangeConfirmListener(new IControllerRangeConfirmListener() {
                    @Override
                    public void onRangeConfirmed(int minValue, int maxValue) {
                        ControllerWidgetView view = mCellContainer.findSelectWidget();
                        if (view != null) {
                            view.setValueRange(minValue, maxValue);
                            ControllerManager.updateWidgetConfig(view.getWidgetConfig());
                        }
                    }
                })
                .build()
                .show(getChildFragmentManager(), "set_range");
    }

    private void showSelectIconDialog() {
        ControllerWidgetView view = mCellContainer.findSelectWidget();
        if (view == null) {
            return;
        }
        ControllerIconDialogFragment.newBuilder(getContext())
                .setSelectedIndex(view.getWidgetIconIndex())
                .setIconSelectListener(new IControllerIconSelectListener() {
                    @Override
                    public void onIconSelected(int index) {
                        ControllerWidgetView view = mCellContainer.findSelectWidget();
                        if (view != null) {
                            view.setWidgetIconIndex(index);
                            ControllerManager.updateWidgetConfig(view.getWidgetConfig());
                        }
                    }
                })
                .build()
                .show(getChildFragmentManager(), "select_icon");
    }

    private void showRenameDialog() {
        ControllerWidgetView view = mCellContainer.findSelectWidget();
        if (view == null) {
            return;
        }
        String hint = view.getWidgetItem().getWidgetType().getRenameHint();
        PromptEditDialogFragment.newBuilder()
                .title(getString(R.string.project_controller_setting_rename_title))
                .message(view.getWidgetName())
                .hint(hint)
                .maxLength(20)
                .onConfirmClickListener(new PromptEditDialogFragment.OnConfirmClickListener() {
                    @Override
                    public boolean confirm(String newName) {
                        ControllerWidgetView view = mCellContainer.findSelectWidget();
                        if (view != null) {
                            if (!ControllerManager.isNameAvailable(view.getWidgetConfig().type, newName)) {
                                ToastHelper.toastShort(getString(R.string.project_save_duplicate_name));
                                return true;
                            }
                            view.setWidgetName(newName);
                            ControllerManager.updateWidgetConfig(view.getWidgetConfig());
                        }
                        return false;
                    }
                })
                .cancelable(false)
                .build()
                .show(getChildFragmentManager(), "rename");
    }

    private void showUsageDialog() {
        ControllerWidgetView view = mCellContainer.findSelectWidget();
        if (view == null) {
            return;
        }
        String usageMsg = view.getWidgetItem().getWidgetType().getUsage();
        PromptDialogFragment.newBuilder(getContext())
                .type(PromptDialogFragment.Type.NORMAL)
                .title(getString(R.string.project_controller_setting_usage_title))
                .message(usageMsg)
                .positiveButtonText(getString(R.string.project_controller_setting_confirm_btn))
                .showNegativeButton(false)
                .cancelable(true)
                .build()
                .show(getChildFragmentManager(), "usage");
    }

    @Override
    protected ControllerContracts.UI createUIView() {
        return new ControllerUI();
    }

    @Override
    protected ControllerContracts.Presenter createPresenter() {
        return new ControllerPresenter();
    }

    @Override
    public void onConnectStatusChanged(URoProduct product, URoConnectStatus connectStatus) {
        if (ControllerModeHolder.getControllerMode() != ControllerModeHolder.MODE_EXECUTION) {
            return;
        }
        if (connectStatus == URoConnectStatus.CONNECTED) {
            mBluetoothConnect.setVisibility(View.GONE);
            mBatteryIcon.setVisibility(View.VISIBLE);
            mBatteryIcon.setImageLevel(getBatteryLevel());
            BluetoothHelper.addCommand(BtInvocationFactory.boardSelfCheck(true));
        } else {
            mBluetoothConnect.setVisibility(View.VISIBLE);
            mBluetoothConnect.setVisibility(View.VISIBLE);
            mBatteryIcon.setVisibility(View.GONE);
        }
    }

    class ControllerUI extends ControllerContracts.UI {

        @Override
        public void showAllOperationBar() {
            float scaleX = (1.0f * (mCellContainer.getWidth() + mToolbarListArea.getWidth())) / mCellContainer.getWidth();
            float scaleY = (1.0f * (mCellContainer.getHeight() + mTitleArea.getHeight())) / mCellContainer.getHeight();
            ArrayList<Animator> animators = new ArrayList<>();
            animators.add(ObjectAnimator.ofFloat(mToolbarListArea, "translationX", -mToolbarListArea.getWidth(), 0));
            animators.add(ObjectAnimator.ofFloat(mTitleArea, "translationY", -mTitleArea.getHeight(), 0));
            animators.add(ObjectAnimator.ofFloat(mCellContainer, "translationX", -mToolbarListArea.getWidth() / 2, 0));
            animators.add(ObjectAnimator.ofFloat(mCellContainer, "translationY", -mTitleArea.getHeight() / 2, 0));
            animators.add(ObjectAnimator.ofFloat(mCellContainer, "scaleX", scaleX, 1.0f));
            animators.add(ObjectAnimator.ofFloat(mCellContainer, "scaleY", scaleY, 1.0f));
            AnimatorSet animatorSet = new AnimatorSet();
            animatorSet.setDuration(DEFAULT_ANIMATION_DURATION);
            animatorSet.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {
                    mExecutionExitBtn.setVisibility(View.GONE);
                    mExecutionEntryBtn.setVisibility(View.VISIBLE);
                    mTitleArea.setVisibility(View.VISIBLE);
                    mToolbarListArea.setVisibility(View.VISIBLE);
                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    mCellContainer.setTranslationX(0);
                    mCellContainer.setTranslationY(0);
                    mCellContainer.setScaleX(1.0f);
                    mCellContainer.setScaleY(1.0f);
                }

                @Override
                public void onAnimationCancel(Animator animation) {
                }

                @Override
                public void onAnimationRepeat(Animator animation) {
                }
            });
            animatorSet.playTogether(animators);
            animatorSet.start();
        }

        @Override
        public void hideAllOperationBar() {
            mCellContainer.cleanChildSelectFlag();
            mToolbarListAdapter.clearSelectedItem();
            float scaleX = (1.0f * (mCellContainer.getWidth() + mToolbarListArea.getWidth())) / mCellContainer.getWidth() * 1.0f;
            float scaleY = (1.0f * (mCellContainer.getHeight() + mTitleArea.getHeight())) / mCellContainer.getHeight() * 1.0f;
            ArrayList<Animator> animators = new ArrayList<>();
            if (mWidgetListArea.getVisibility() == View.VISIBLE) {
                animators.add(ObjectAnimator.ofFloat(mWidgetListArea, "translationX", 0, -ControllerConstValue.WIDGET_LIST_WIDTH_PIXEL));
            }
            if (mWidgetActionArea.getVisibility() == View.VISIBLE) {
                animators.add(ObjectAnimator.ofFloat(mWidgetActionArea, "translationY", 0, ControllerConstValue.WIDGET_ACTION_Y_PIXEL));
            }
            animators.add(ObjectAnimator.ofFloat(mToolbarListArea, "translationX", 0, -mToolbarListArea.getWidth()));
            animators.add(ObjectAnimator.ofFloat(mTitleArea, "translationY", 0, -mTitleArea.getHeight()));
            animators.add(ObjectAnimator.ofFloat(mCellContainer, "translationX", 0, -mToolbarListArea.getWidth() / 2));
            animators.add(ObjectAnimator.ofFloat(mCellContainer, "translationY", 0, -mTitleArea.getHeight() / 2));
            animators.add(ObjectAnimator.ofFloat(mCellContainer, "scaleX", 1.0f, scaleX));
            animators.add(ObjectAnimator.ofFloat(mCellContainer, "scaleY", 1.0f, scaleY));
            AnimatorSet animatorSet = new AnimatorSet();
            animatorSet.setDuration(DEFAULT_ANIMATION_DURATION);
            animatorSet.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {
                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    mTitleArea.setVisibility(View.GONE);
                    mToolbarListArea.setVisibility(View.GONE);
                    mWidgetListArea.setVisibility(View.GONE);
                    mWidgetActionArea.setVisibility(View.GONE);
                    mExecutionExitBtn.setVisibility(View.VISIBLE);
                    mExecutionEntryBtn.setVisibility(View.GONE);
                    mCellContainer.setTranslationX(0);
                    mCellContainer.setTranslationY(0);
                    mCellContainer.setScaleX(1.0f);
                    mCellContainer.setScaleY(1.0f);
                }

                @Override
                public void onAnimationCancel(Animator animation) {
                }

                @Override
                public void onAnimationRepeat(Animator animation) {
                }
            });
            animatorSet.playTogether(animators);
            animatorSet.start();
        }

        @Override
        public void showWidgetGroup(ToolbarType group) {
            mWidgetListAdapter.setSelectGroup(group);
            hideWidgetAction();
            if (mWidgetListArea.getVisibility() != View.VISIBLE) {
                mWidgetListArea.setVisibility(View.VISIBLE);
                int width = ControllerConstValue.WIDGET_LIST_WIDTH_PIXEL - ControllerConstValue.TOOLBAR_WIDTH_PIXEL + ControllerConstValue.BG_RADIUS_PIXEL;
                ObjectAnimator
                        .ofFloat(mWidgetListArea, "translationX", -width, 0)
                        .setDuration(DEFAULT_ANIMATION_DURATION)
                        .start();
            }
        }

        @Override
        public void hideWidgetGroup() {
            mToolbarListAdapter.clearSelectedItem();
            if (mWidgetListArea.getVisibility() == View.VISIBLE) {
                int width = ControllerConstValue.WIDGET_LIST_WIDTH_PIXEL - ControllerConstValue.TOOLBAR_WIDTH_PIXEL + ControllerConstValue.BG_RADIUS_PIXEL;
                ObjectAnimator objectAnimator = ObjectAnimator
                        .ofFloat(mWidgetListArea, "translationX", 0, -width)
                        .setDuration(DEFAULT_ANIMATION_DURATION);
                objectAnimator.addListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animation) {
                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        mWidgetListArea.setVisibility(View.GONE);
                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {
                    }

                    @Override
                    public void onAnimationRepeat(Animator animation) {
                    }
                });
                objectAnimator.start();
            }
        }

        @Override
        public void showWidgetAction(WidgetItem item) {
            ArrayList<WidgetProperty> properties = new ArrayList<>(Arrays.asList(item.getWidgetProperties()));
            mWidgetActionJoystick.setVisibility(properties.contains(WidgetProperty.PROPERTY_JOYSTICKSETTING) ? View.VISIBLE : View.GONE);
            mWidgetActionBlockly.setVisibility(properties.contains(WidgetProperty.PROPERTY_BLOCKLYCODE) ? View.VISIBLE : View.GONE);
            mWidgetActionMotion.setVisibility(properties.contains(WidgetProperty.PROPERTY_PERFORMACTION) ? View.VISIBLE : View.GONE);
            mWidgetActionRename.setVisibility(properties.contains(WidgetProperty.PROPERTY_RENAME) ? View.VISIBLE : View.GONE);
            mWidgetActionRotate.setVisibility(properties.contains(WidgetProperty.PROPERTY_SWITCHDIRECTION) ? View.VISIBLE : View.GONE);
            mWidgetActionRange.setVisibility(properties.contains(WidgetProperty.PROPERTY_RANGESETTING) ? View.VISIBLE : View.GONE);
            mWidgetActionIcon.setVisibility(properties.contains(WidgetProperty.PROPERTY_SETTINGSICON) ? View.VISIBLE : View.GONE);
            mWidgetActionUsage.setVisibility(properties.contains(WidgetProperty.PROPERTY_USAGE) ? View.VISIBLE : View.GONE);
            hideWidgetGroup();
            if (mWidgetActionArea.getVisibility() != View.VISIBLE) {
                mWidgetActionArea.setVisibility(View.VISIBLE);
                ObjectAnimator
                        .ofFloat(mWidgetActionArea, "translationY", ControllerConstValue.WIDGET_ACTION_Y_PIXEL, 0)
                        .setDuration(DEFAULT_ANIMATION_DURATION)
                        .start();
            }
        }

        @Override
        public void hideWidgetAction() {
            mCellContainer.cleanChildSelectFlag();
            if (mWidgetActionArea.getVisibility() == View.VISIBLE) {
                ObjectAnimator objectAnimator = ObjectAnimator
                        .ofFloat(mWidgetActionArea, "translationY", 0, ControllerConstValue.WIDGET_ACTION_Y_PIXEL)
                        .setDuration(DEFAULT_ANIMATION_DURATION);
                objectAnimator.addListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animation) {
                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        mWidgetActionArea.setVisibility(View.GONE);
                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {
                    }

                    @Override
                    public void onAnimationRepeat(Animator animation) {
                    }
                });
                objectAnimator.start();
            }
        }
    }

    private ControllerWidgetListener newWidgetListener() {
        return new ControllerWidgetListener();
    }

    private class ControllerWidgetListener implements IControllerWidgetListener {
        @Override
        public void onWidgetClick(View v) {
            ControllerWidgetView view = (ControllerWidgetView) v;
            if (view.isSelected()) {
                getUIView().hideWidgetAction();
            } else {
                WidgetItem item = view.getWidgetItem();
                mCellContainer.cleanChildSelectFlag();
                view.setSelectedFlag(true);
                getUIView().showWidgetAction(item);
            }
        }

        @Override
        public void onWidgetMove(View v) {
            ControllerWidgetView view = (ControllerWidgetView) v;
            WidgetItem item = view.getWidgetItem();
            if (startDrag(DragItemInfo.DRAG_FROM_CONTAINER, item, view)) {
                mCellContainer.removeView(view);
                mCellContainer.addView(view, false);
                view.setVisibility(View.INVISIBLE);
            }
        }

        @Override
        public String getBlocklyContent(String id, String name) {
            return ControllerManager.findControllerFileContent(id, name);
        }
    }

    private class DragItemInfo {
        private static final int DRAG_FROM_TOOLBAR = 1;
        private static final int DRAG_FROM_CONTAINER = 2;
        private boolean outOfView = false;
        private boolean hasDropped = false;
        private final int dragFrom;
        private final WidgetItem widgetItem;
        private final View view;
        private final ControllerWidgetView srcView;
        private ControllerWidgetView dragLayerView;
        @Keep
        private WidgetDragShadowBuilder dragShadow;
        private Point coordinate = null;

        public DragItemInfo(int dragFrom, WidgetItem widgetItem, View view, ControllerWidgetView srcView) {
            this.dragFrom = dragFrom;
            this.widgetItem = widgetItem;
            this.view = view;
            this.srcView = srcView;
        }
    }

    private class WidgetDragShadowBuilder extends View.DragShadowBuilder {

        public WidgetDragShadowBuilder(View view) {
            super(view);
        }

        @Override
        public void onProvideShadowMetrics(Point outShadowSize, Point outShadowTouchPoint) {
            final View view = getView();
            if (view != null) {
                outShadowSize.set(view.getWidth(), view.getHeight());
                outShadowTouchPoint.set(view.getWidth() / 2, view.getHeight() / 2);
            }
        }

        @Override
        public void onDrawShadow(Canvas canvas) {
            // do nothing, draw with onDrag event
//            final View view = getView();
//            if (view != null) {
//                if(PlatformUtil.isChromebookDevice()) {
//                    canvas.drawColor(0, PorterDuff.Mode.CLEAR);
//                } else {
//                    view.draw(canvas);
//                }
//            }
        }

    }

    private final static FragmentManager.FragmentLifecycleCallbacks controllerDialogCb = new FragmentManager.FragmentLifecycleCallbacks() {
        @Override
        public void onFragmentAttached(@NonNull FragmentManager fm, @NonNull Fragment f, @NonNull Context context) {
            if (!(f instanceof PromptDialogFragment)) {
                return;
            }
            RcLogUtils.e("showDialog -> controller pause");
            ControllerManager.pause();
        }

        @Override
        public void onFragmentDetached(@NonNull FragmentManager fm, @NonNull Fragment f) {
            if (!(f instanceof PromptDialogFragment)) {
                return;
            }
            RcLogUtils.e("hideDialog -> controller resume");
            ControllerManager.resume();
        }
    };

}
