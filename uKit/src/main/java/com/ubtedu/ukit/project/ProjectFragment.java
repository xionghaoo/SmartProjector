/**
 * @2018 UBTECH Robotics Corp. All rights reserved (C)
 **/
package com.ubtedu.ukit.project;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.LottieAnimationView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.transition.DrawableCrossFadeFactory;
import com.ubtedu.alpha1x.core.base.fragment.OnDialogFragmentDismissListener;
import com.ubtedu.alpha1x.ui.recyclerview.CommonAdapter;
import com.ubtedu.alpha1x.ui.recyclerview.base.ViewHolder;
import com.ubtedu.alpha1x.utils.GsonUtil;
import com.ubtedu.alpha1x.utils.LogUtil;
import com.ubtedu.base.net.rxretrofit.subscriber.SimpleRxSubscriber;
import com.ubtedu.ukit.R;
import com.ubtedu.ukit.common.analysis.Events;
import com.ubtedu.ukit.common.analysis.UBTReporter;
import com.ubtedu.ukit.common.base.UKitBaseFragment;
import com.ubtedu.ukit.common.dialog.PromptDialogFragment;
import com.ubtedu.ukit.home.HomeActivity;
import com.ubtedu.ukit.project.vo.Project;
import com.ubtedu.ukit.user.UserManager;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.ubtedu.ukit.common.analysis.Events.Ids.app_project_create_btn_click;

/**
 * @author qinicy
 * @date 2018/11/26
 */
public class ProjectFragment extends UKitBaseFragment<ProjectContracts.Presenter, ProjectContracts.UI> {
    private static final long HIDE_SYNC_BUTTON_DELAY_TIME = 3000;
    private RecyclerView mProjectsRcv;
    private ProjectAdapter mProjectAdapter;
    private List<Project> mProjects;

    private View mEmptyLyt;
    private View mAddProject;
    private int mCurrentEditPosition = -1;
    private LottieAnimationView mLoadingLyt;
    private ViewGroup mProjectRootLyt;
    private String mCurrentUserId;
    private Button mSyncBtn;
    private boolean isSyncing;
    private boolean isSyncSuccess;
    private PopupWindow mPopupWindow;
    private Runnable mHideSyncButtonTask;
    private boolean mIsVisibleToUser;
    private TextView mTipTv;
    private long mLastPopupWindowDismissTime;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mProjects = new ArrayList<>();
    }

    @SuppressLint("ClickableViewAccessibility")
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View contentView = inflater.inflate(R.layout.fragment_project, null);

        mSyncBtn = contentView.findViewById(R.id.project_sync_btn);
        bindSafeClickListener(mSyncBtn);
        mLoadingLyt = contentView.findViewById(R.id.project_lottie_loading_view);
        mEmptyLyt = contentView.findViewById(R.id.project_empty_lyt);
        mAddProject = contentView.findViewById(R.id.project_add_btn);
        bindSafeClickListener(mAddProject);
        mProjectRootLyt = contentView.findViewById(R.id.project_root_lyt);
        bindClickListener(mProjectRootLyt);

        mProjectsRcv = contentView.findViewById(R.id.project_list_rcv);
        bindClickListener(mProjectsRcv);
        mProjectAdapter = new ProjectAdapter(getContext(), R.layout.item_project, mProjects);
        GridLayoutManager layoutManager = new GridLayoutManager(getContext(), 2);
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        mProjectsRcv.setLayoutManager(layoutManager);
        mProjectsRcv.setAdapter(mProjectAdapter);
        mProjectsRcv.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    cancelItemEditLayout();
                }
                return false;
            }
        });
        mAddProject.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (MotionEvent.ACTION_DOWN == event.getAction()) {
                    mAddProject.setAlpha(0.5f);
                } else if (MotionEvent.ACTION_UP == event.getAction() || MotionEvent.ACTION_CANCEL == event.getAction()) {
                    mAddProject.setAlpha(1.0f);
                }
                return false;
            }
        });

        return contentView;
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getPresenter().syncProjects();
    }

    @Override
    public void onResume() {
        super.onResume();
        LogUtil.d("");
    }

    @Override
    public void onVisibilityChangedToUser(boolean isVisibleToUser) {
        super.onVisibilityChangedToUser(isVisibleToUser);
        mIsVisibleToUser = isVisibleToUser;

    }

    private void updateSyncButtonVisibility() {
        LogUtil.d("isGuest():" + UserManager.getInstance().isGuest());
        boolean visible = !UserManager.getInstance().isGuest();
        setSyncButtonVisibility(visible);
    }

    private void setSyncButtonVisibility(final boolean visible) {
        LogUtil.d("visible:" + visible);
        if (mSyncBtn == null) {
            return;
        }
        boolean currentVisible = mSyncBtn.getVisibility() == View.VISIBLE;
        if (currentVisible != visible) {
            //设置GONE时，也需要取消动画，要不然按钮还会显示。
            mSyncBtn.setVisibility(visible ? View.VISIBLE : View.GONE);
            if (!visible) {
                mSyncBtn.clearAnimation();
            }
        }
    }

    private void showSyncTip(boolean show, String message) {

        if (show && mIsVisibleToUser) {
            if (mPopupWindow == null || mTipTv == null) {
                View view = LayoutInflater.from(getContext()).inflate(R.layout.popup_sync_fail_tip, null, false);
                mTipTv = view.findViewById(R.id.project_sync_fail_tip_tv);
                mPopupWindow = new PopupWindow(getContext());
                mPopupWindow.setContentView(view);
                mPopupWindow.setWidth(getResources().getDimensionPixelOffset(R.dimen.ubt_dimen_576px));
                mPopupWindow.setHeight(getResources().getDimensionPixelOffset(R.dimen.ubt_dimen_178px));

                mPopupWindow.setBackgroundDrawable(null);
                mPopupWindow.setFocusable(false);
                mPopupWindow.setOutsideTouchable(true);
                mPopupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
                    @Override
                    public void onDismiss() {
                        mLastPopupWindowDismissTime =System.currentTimeMillis();
                    }
                });
            }
            if (message != null) {
                mTipTv.setText(message);
            }
            int height = 0;
            int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
            if (resourceId > 0) {
                height = getResources().getDimensionPixelSize(resourceId);
            }
            DisplayMetrics dm = context.getResources().getDisplayMetrics();
            mPopupWindow.showAtLocation(mSyncBtn, Gravity.RIGHT | Gravity.TOP, dm.widthPixels -mSyncBtn.getRight() - height, mSyncBtn.getBottom());
        } else {
            if (mPopupWindow != null && mPopupWindow.isShowing()) {
                mPopupWindow.dismiss();
            }
        }

    }

    @Override
    public void onClick(View v, boolean isSafeClick) {
        super.onClick(v, isSafeClick);
        if (v == mAddProject) {
            openProject(null);
            UBTReporter.onEvent(app_project_create_btn_click, null);
        }
        if (v == mProjectsRcv || v == mProjectRootLyt) {
            cancelItemEditLayout();
        }
        if (v == mSyncBtn) {
            if (System.currentTimeMillis()- mLastPopupWindowDismissTime <200){
                return;
            }
            if (!isSyncing && !isSyncSuccess) {
                showSyncTip(true, null);
            }
        }
    }

    private void cancelItemEditLayout() {
        if (mCurrentEditPosition > -1) {
            mProjectAdapter.notifyItemChanged(mCurrentEditPosition);
            mCurrentEditPosition = -1;
        }
    }

    private void updateContentLayout() {
        if (mProjects != null && mProjects.size() > 1) {
            mProjectsRcv.setVisibility(View.VISIBLE);
            mEmptyLyt.setVisibility(View.GONE);
        } else {
            mProjectsRcv.setVisibility(View.GONE);
            mEmptyLyt.setVisibility(View.VISIBLE);
        }
    }

    View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int position = (int) v.getTag();

            if (position == 0) {
                openProject(null);
                UBTReporter.onEvent(app_project_create_btn_click, null);
            } else {
                Project project = mProjects.get(position);
                if (!project.isCompat()) {
                    ProjectCompat.showProjectCompatDialog(getContext(), getFragmentManager());
                    return;
                }
                openProject(project);
                Map<String, String> args = new HashMap<>(1);
                args.put("projectId", project.projectId);
                args.put("projectName", project.projectName);
                UBTReporter.onEvent(Events.Ids.app_project_open_btn_click, args);
            }
        }
    };

    View.OnClickListener mEditOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            final int position = (int) v.getTag();
            if (position >= 0 && position < mProjects.size()) {
                int id = v.getId();
                int lastPosition = mCurrentEditPosition;
                mCurrentEditPosition = position;
                Project project = mProjects.get(mCurrentEditPosition);
                if (id == R.id.project_item_edit_btn) {
                    if (!project.isCompat()) {
                        ProjectCompat.showProjectCompatDialog(getContext(), getFragmentManager());
                        return;
                    }
                    mProjectAdapter.notifyItemChanged(lastPosition);
                    mProjectAdapter.notifyItemChanged(mCurrentEditPosition);

                    //数据采集
                    Map<String, String> args = new HashMap<>(1);
                    args.put("projectId", project.projectId);
                    args.put("projectName", project.projectName);
                    UBTReporter.onEvent(Events.Ids.app_project_edit_btn_click, args);
                }
                if (id == R.id.project_item_edit_lyt) {
                    mCurrentEditPosition = -1;
                    mProjectAdapter.notifyItemChanged(lastPosition);
                }
                if (id == R.id.project_item_edit_delete_lyt) {
                    mCurrentEditPosition = -1;
                    mProjectAdapter.notifyItemChanged(lastPosition);
                    deleteProject(position);

                    Map<String, String> args = new HashMap<>(1);
                    args.put("projectId", project.projectId);
                    args.put("projectName", project.projectName);
                    UBTReporter.onEvent(Events.Ids.app_project_delete_btn_click, args);
                }
                if (id == R.id.project_item_edit_modify_lyt) {
                    mCurrentEditPosition = -1;
                    mProjectAdapter.notifyItemChanged(lastPosition);
                    String imgPath = TextUtils.isEmpty(project.imgPath) ? project.imgUrl : project.imgPath;
                    if (!isAdded()) return;
                    ProjectEditDialogFragment dialogFragment = ProjectEditDialogFragment.newInstance(getString(R.string.project_edit_title), project.projectName, imgPath);
                    dialogFragment.setCancelable(false);
                    dialogFragment.setDismissListener(new OnDialogFragmentDismissListener() {
                        @Override
                        public void onDismiss(Object... value) {
                            if (value != null && value.length == 3) {
                                boolean isPositive = (boolean) value[0];
                                if (isPositive) {
                                    String projectName = (String) value[1];
                                    String imagePath = (String) value[2];
                                    doUpdate(position, projectName, imagePath);
                                }
                            }
                        }
                    });
                    dialogFragment.show(getFragmentManager(), "ProjectEditDialogFragment");

                    Map<String, String> args = new HashMap<>(1);
                    args.put("projectId", project.projectId);
                    args.put("projectName", project.projectName);
                    UBTReporter.onEvent(Events.Ids.app_project_rename_btn_click, args);
                }
            }

        }
    };


    private void deleteProject(final int position) {
        PromptDialogFragment.newBuilder(getContext())
                .type(PromptDialogFragment.Type.NORMAL)
                .title(getString(R.string.project_delete_dialog_title))
                .message(getString(R.string.project_delete_dialog_message))
                .onPositiveClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        doDelete(position);
                    }
                })
                .build()
                .show(getFragmentManager(), "delete project dialog");
    }

    private void doDelete(final int position) {
        if (!UserManager.getInstance().isGuest()) {
            getUIView().onSyncStart(true);
        }

        String projectId = mProjects.get(position).projectId;
        mProjects.remove(position);
        mProjectAdapter.notifyItemRemoved(position);
        mProjectAdapter.notifyItemRangeChanged(position, mProjects.size() - position);
        updateContentLayout();
        UserDataSynchronizer.getInstance().deleteProject(projectId)
                .subscribe(new SimpleRxSubscriber<Boolean>() {
                    @Override
                    public void onNext(Boolean success) {
                        super.onNext(success);
                        if (success) {
                            UserDataSynchronizer.getInstance().sync(true).subscribe(new SimpleRxSubscriber<>());
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
                        LogUtil.e("");
                        e.printStackTrace();
                        if (getUIView() != null) {
                            getUIView().onSyncEnd(true, false, null);
                        }
                    }
                });
    }

    private void doRename(final int position, final String newName) {
        final Project project = mProjects.get(position);
        getPresenter().renameProject(project, newName).subscribe(new SimpleRxSubscriber<Project>() {
            @Override
            public void onError(Throwable e) {
                super.onError(e);
                LogUtil.e(e.getMessage());
                e.printStackTrace();
            }

            @Override
            public void onComplete() {
                super.onComplete();
                project.projectName = newName;
                mProjectAdapter.notifyItemChanged(position);
                getUIDelegate().hideLoading();
            }
        });
    }

    private void doUpdate(final int position, final String newName, final String newImagePath) {
        final Project project = mProjects.get(position);
        getPresenter().updateProject(project, newName, newImagePath).subscribe(new SimpleRxSubscriber<Project>() {
            @Override
            public void onError(Throwable e) {
                super.onError(e);
                LogUtil.e(e.getMessage());
                e.printStackTrace();
            }

            @Override
            public void onComplete() {
                super.onComplete();
                project.projectName = newName;
                project.imgPath = newImagePath;
                mProjectAdapter.notifyItemChanged(position);
                getUIDelegate().hideLoading();
            }
        });
    }

    private void openProject(Project project) {
        if (getActivity() != null && getActivity() instanceof HomeActivity) {
            HomeActivity activity = (HomeActivity) getActivity();
            activity.openProject(project);
            //隐藏编辑界面
            int lastPosition = mCurrentEditPosition;
            mCurrentEditPosition = -1;
            mProjectAdapter.notifyItemChanged(lastPosition);
        }
    }

    class ProjectAdapter extends CommonAdapter<Project> {
//        private RequestOptions mRequestOptions;
        private DrawableCrossFadeFactory mDrawableCrossFadeFactory;

        ProjectAdapter(Context context, int layoutId, List<Project> datas) {
            super(context, layoutId, datas);
            mDrawableCrossFadeFactory = new DrawableCrossFadeFactory.Builder(300).setCrossFadeEnabled(true).build();
//            mRequestOptions = new RequestOptions()
//                    .transforms(new CenterCrop(), new RoundedCorners(getResources().getDimensionPixelOffset(R.dimen.ubt_dimen_30px)))
//                    .timeout(20000)
//                    .error(R.drawable.img_nonetwork);
        }

        @Override
        protected void convert(ViewHolder holder, Project project, int position) {
            holder.itemView.setTag(position);
            holder.itemView.setOnClickListener(mOnClickListener);

            holder.getView(R.id.project_item_edit_btn).setTag(position);
            holder.getView(R.id.project_item_edit_delete_lyt).setTag(position);
            holder.getView(R.id.project_item_edit_modify_lyt).setTag(position);
            holder.getView(R.id.project_item_edit_lyt).setTag(position);
            holder.getView(R.id.project_item_edit_btn).setOnClickListener(mEditOnClickListener);
            holder.getView(R.id.project_item_edit_delete_lyt).setOnClickListener(mEditOnClickListener);
            holder.getView(R.id.project_item_edit_modify_lyt).setOnClickListener(mEditOnClickListener);
            holder.getView(R.id.project_item_edit_lyt).setOnClickListener(mEditOnClickListener);


            holder.setVisible(R.id.project_item_edit_lyt, position == mCurrentEditPosition);
            if (position == 0) {
                holder.setBackgroundRes(R.id.project_item_content_lyt, R.drawable.myprogram_bg_add);
                holder.setVisible(R.id.project_item_project_lyt, false);
                holder.setVisible(R.id.project_item_create_tv, true);
            } else {

                int backgroundPosition = (position - 1) % 5;
                int backgroundId = R.drawable.myprogram_bg_yellow;
                switch (backgroundPosition) {
                    case 0:
                        backgroundId = R.drawable.myprogram_bg_red;
                        break;
                    case 1:
                        backgroundId = R.drawable.myprogram_bg_yellow;
                        break;
                    case 2:
                        backgroundId = R.drawable.myprogram_bg_blue;
                        break;
                    case 3:
                        backgroundId = R.drawable.myprogram_bg_purple;
                        break;
                    case 4:
                        backgroundId = R.drawable.myprogram_bg_green;
                        break;
                }

                holder.setBackgroundRes(R.id.project_item_bg_view, backgroundId);

                holder.setBackgroundColor(R.id.project_item_content_lyt, Color.TRANSPARENT);
                holder.setVisible(R.id.project_item_project_lyt, true);
                holder.setVisible(R.id.project_item_create_tv, false);
                holder.setText(R.id.project_item_name_tv, project.projectName);
                LogUtil.d("imgPath:" + project.imgPath + " \nimgUrl:" + project.imgUrl);
                if (!TextUtils.isEmpty(project.imgPath) && new File(project.imgPath).exists()) {

                    Glide.with(holder.itemView)
                            .load(project.imgPath)
                            .transition(DrawableTransitionOptions.with(mDrawableCrossFadeFactory))
//                            .apply(mRequestOptions)
                            .into((ImageView) holder.getView(R.id.project_item_image_iv));
                } else if (!TextUtils.isEmpty(project.imgUrl)) {
                    Glide.with(holder.itemView)
                            .load(project.imgUrl)
                            .transition(DrawableTransitionOptions.with(mDrawableCrossFadeFactory))
//                            .apply(mRequestOptions)
                            .into((ImageView) holder.getView(R.id.project_item_image_iv));
                } else {
                    Glide.with(holder.itemView)
                            .load(R.drawable.myprogram_img_default_map)
                            .transition(DrawableTransitionOptions.with(mDrawableCrossFadeFactory))
//                            .apply(mRequestOptions)
                            .into((ImageView) holder.getView(R.id.project_item_image_iv));
                }
            }
        }
    }

    private void delayHideSyncButton() {
        if (mHideSyncButtonTask == null) {
            mHideSyncButtonTask = new Runnable() {

                @Override
                public void run() {
                    if (isSyncSuccess) {
                        setSyncButtonVisibility(false);
                    }
                }
            };
        }
        mSyncBtn.removeCallbacks(mHideSyncButtonTask);
        mSyncBtn.postDelayed(mHideSyncButtonTask, HIDE_SYNC_BUTTON_DELAY_TIME);
    }


    @Override
    public void onDestroyView() {
        mSyncBtn.removeCallbacks(mHideSyncButtonTask);
        super.onDestroyView();
    }

    @Override
    protected ProjectContracts.Presenter createPresenter() {
        return new ProjectPresenter(new Workspace());
    }

    @Override
    protected ProjectContracts.UI createUIView() {
        return new ProjectUI();
    }

    private void updateSyncStatusUI(int syncEvent, boolean isOnlineSync, boolean isSyncSuccess, String error) {
        boolean isGuest = UserManager.getInstance().isGuest();
        setSyncButtonVisibility(!isGuest);
        showSyncTip(false, null);
        if (!isGuest && isOnlineSync) {
            if (syncEvent == DataSyncEvent.SYNC_EVENT_SYNC_START) {
                mSyncBtn.setBackgroundResource(R.drawable.bluetooth_upload_icon);
                mSyncBtn.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.anim_sync));
            } else if (syncEvent == DataSyncEvent.SYNC_EVENT_SYNC_END) {
                mSyncBtn.clearAnimation();
                if (isSyncSuccess) {
                    mSyncBtn.setBackgroundResource(R.drawable.bluetooth_upgrade_icon);
                    delayHideSyncButton();
                } else {
                    showSyncTip(true, error);
                    mSyncBtn.setBackgroundResource(R.drawable.bluetooth_abnormal_icon);
                }
            } else if (syncEvent == DataSyncEvent.SYNC_EVENT_SYNC_CANCEL) {
                mSyncBtn.clearAnimation();
                mSyncBtn.setBackgroundResource(R.drawable.bluetooth_abnormal_icon);
            }
        }
    }

    class ProjectUI extends ProjectContracts.UI {
        @Override
        public void updateProjects(List<Project> projects) {
            if (projects != null) {
                String json = GsonUtil.get().toJson(projects);
                LogUtil.d("projects:" + json);
                mProjects.clear();
                mProjects.add(new Project());
                mProjects.addAll(projects);
                mProjectAdapter.notifyDataSetChanged();
                updateContentLayout();
            }
        }

        @Override
        public void dismissLoadingView() {
            if (mLoadingLyt.getVisibility() != View.GONE) {
                mLoadingLyt.setVisibility(View.GONE);
                mLoadingLyt.cancelAnimation();
            }
        }

        @Override
        public void onSyncStart(boolean isOnlineSync) {
            LogUtil.d("project sync isOnlineSync:" + isOnlineSync);
            isSyncing = true;

            updateSyncStatusUI(DataSyncEvent.SYNC_EVENT_SYNC_START, isOnlineSync, false, null);
            String userId = UserManager.getInstance().getLoginUserId();
            if (userId != null && !userId.equals(mCurrentUserId)) {
                mProjectsRcv.setVisibility(View.GONE);
                mEmptyLyt.setVisibility(View.GONE);
                mLoadingLyt.setVisibility(View.VISIBLE);
                mLoadingLyt.playAnimation();
                mCurrentUserId = userId;
            }
        }

        @Override
        public void onSyncEnd(boolean isOnlineSync, boolean success, String errorMessage) {
            LogUtil.d("project sync isOnlineSync:" + isOnlineSync + "  success:" + success);
            isSyncing = false;
            mLoadingLyt.setVisibility(View.GONE);
            mLoadingLyt.cancelAnimation();
            updateContentLayout();
            isSyncSuccess = success;

            updateSyncStatusUI(DataSyncEvent.SYNC_EVENT_SYNC_END, isOnlineSync, success, errorMessage);
        }

        @Override
        public void onSyncCancel(boolean isOnlineSync) {
            LogUtil.d("isOnlineSync:" + isOnlineSync);
            updateSyncStatusUI(DataSyncEvent.SYNC_EVENT_SYNC_CANCEL, isOnlineSync, false, null);
        }
    }

}