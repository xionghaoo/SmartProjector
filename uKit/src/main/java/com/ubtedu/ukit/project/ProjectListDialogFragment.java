/**
 * @2018 UBTECH Robotics Corp. All rights reserved (C)
 **/
package com.ubtedu.ukit.project;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ubtedu.alpha1x.core.base.fragment.OnDialogFragmentDismissListener;
import com.ubtedu.alpha1x.ui.recyclerview.CommonAdapter;
import com.ubtedu.alpha1x.ui.recyclerview.base.ItemViewDelegate;
import com.ubtedu.alpha1x.ui.recyclerview.base.ViewHolder;
import com.ubtedu.alpha1x.ui.recyclerview.item.SpaceItemDecoration;
import com.ubtedu.alpha1x.utils.LogUtil;
import com.ubtedu.base.net.rxretrofit.subscriber.SimpleRxSubscriber;
import com.ubtedu.ukit.R;
import com.ubtedu.ukit.common.base.UKitBaseDialogFragment;
import com.ubtedu.ukit.common.dialog.PromptEditDialogFragment;
import com.ubtedu.ukit.common.files.FileHelper;
import com.ubtedu.ukit.project.bridge.api.ToastHelper;
import com.ubtedu.ukit.project.host.ProjectHostFragment;
import com.ubtedu.ukit.project.vo.Blockly;
import com.ubtedu.ukit.project.vo.Controller;
import com.ubtedu.ukit.project.vo.DataWrapper;
import com.ubtedu.ukit.project.vo.Doodle;
import com.ubtedu.ukit.project.vo.Motion;
import com.ubtedu.ukit.project.vo.Project;
import com.ubtedu.ukit.project.vo.ProjectFile;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;


/**
 * @Author qinicy
 * @Date 2018/12/1
 **/
public class ProjectListDialogFragment extends UKitBaseDialogFragment {
    private final static int[] LIST_TITTLES = new int[]{
            R.string.project_sub_list_blockly,
            R.string.project_sub_list_motion,
            R.string.project_sub_list_controller,
            R.string.project_sub_list_doodle
    };
    private final static int[] RENAME_HINTS = new int[]{
            R.string.project_sub_list_blockly_rename_hint,
            R.string.project_sub_list_motion_rename_hint,
            R.string.project_sub_list_controller_rename_hint,
            R.string.project_sub_list_doodle_rename_hint
    };

    private final static boolean[] MOTION_PLAY_BUTTON_VISIBLE = new boolean[]{
            false,
            true,
            false,
            false
    };
    private ProjectHostFragment mProjectHostFragment;
    private Project mProject;
    private int mListType;
    private TextView mTittleTv;
    private View mCloseBtn;
    private RecyclerView mRecyclerView;
    private ListAdapter mAdapter;

    private View mContentView;
    private List<ProjectFile> mDatas;
    private int mCurrentPosition;
    private String mCurrentSubProjectId;

    private long mLastPressedTime = -1;
    private MotionPlayer mMotionPlayer;

    public static ProjectListDialogFragment newInstance(@NonNull ProjectHostFragment host, int positionType, String currentSubProjectId, @NonNull Project project) {
        ProjectListDialogFragment fragment = new ProjectListDialogFragment();
        fragment.setCancelable(true);
        if (positionType != ProjectHostFragment.POSITION_BLOCKLY &&
                positionType != ProjectHostFragment.POSITION_MOTION &&
                positionType != ProjectHostFragment.POSITION_CONTROLLER &&
                positionType != ProjectHostFragment.POSITION_DOODLE) {
            positionType = ProjectHostFragment.POSITION_BLOCKLY;
        }
        fragment.mListType = positionType;
        fragment.mProject = project;
        fragment.mCurrentSubProjectId = currentSubProjectId;
        fragment.mProjectHostFragment = host;
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.dialog_fragment_project_sub_list, null);
        if (isCancelable()) {
            mRootView = root.findViewById(R.id.dialog_fragment_root_lyt);
        }
        mContentView = root.findViewById(R.id.dialog_fragment_root_view);
        mTittleTv = root.findViewById(R.id.project_sub_list_title_tv);

        mTittleTv.setText(LIST_TITTLES[mListType]);
        mCloseBtn = root.findViewById(R.id.project_sub_list_close_btn);
        bindClickListener(mCloseBtn);

        if (MOTION_PLAY_BUTTON_VISIBLE[mListType]) {
            mMotionPlayer = new MotionPlayer();
            mMotionPlayer.setListener(new MotionPlayer.OnMotionPlayStateChangeListener() {
                @Override
                public void onMotionPlayStateChanged() {
                    mAdapter.notifyDataSetChanged();
                }
            });
        }

        mRecyclerView = root.findViewById(R.id.project_sub_list_rcv);
        mDatas = new ArrayList<>();
        mDatas.add(new ProjectFile());
        List<ProjectFile> datas = initDatas();
        if (datas != null) {
            mDatas.addAll(datas);
            Collections.sort(mDatas, new Comparator<ProjectFile>() {
                @Override
                public int compare(ProjectFile o1, ProjectFile o2) {
                    long value = o1.createTime - o2.createTime;
                    if (value < 0) {
                        return 1;
                    } else if (value == 0) {
                        return 0;
                    } else {
                        return -1;
                    }
                }
            });
        }

        mCurrentPosition = getRecentlyItemPosition();
        mAdapter = new ListAdapter(getContext(), mDatas);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setOrientation(RecyclerView.VERTICAL);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.addItemDecoration(new SpaceItemDecoration(getContext().getResources().getDimensionPixelOffset(R.dimen.ubt_dimen_24px)));

        return root;
    }

    @Override
    public void onClick(View v, boolean isSafeClick) {
        super.onClick(v, isSafeClick);
        if (v == mCloseBtn) {
            stopMotion();
            dismiss();
        }
    }

    @Override
    public void onPause() {
        stopMotion();
        super.onPause();
    }

    private int getRecentlyItemPosition() {
        if (mDatas != null && mDatas.size() > 0 && mCurrentSubProjectId != null) {
            for (int i = 0; i < mDatas.size(); i++) {
                ProjectFile b = mDatas.get(i);
                if (mCurrentSubProjectId.equals(b.id)) {
                    return i;
                }
            }
        }
        LogUtil.d("default position 1");
        return 1;
    }

    private List<ProjectFile> initDatas() {
        if (mProject != null) {
            switch (mListType) {
                case ProjectHostFragment.POSITION_BLOCKLY:
                    if (mProject.blocklyList != null) {
                        ProjectFile[] projectFiles = new ProjectFile[mProject.blocklyList.size()];
                        mProject.blocklyList.toArray(projectFiles);
                        return Arrays.asList(projectFiles);
                    }
                    break;
                case ProjectHostFragment.POSITION_MOTION:
                    if (mProject.motionList != null) {
                        ProjectFile[] projectFiles = new ProjectFile[mProject.motionList.size()];
                        mProject.motionList.toArray(projectFiles);
                        return Arrays.asList(projectFiles);
                    }
                    break;
                case ProjectHostFragment.POSITION_CONTROLLER:
                    if (mProject.controllerList != null) {
                        ProjectFile[] projectFiles = new ProjectFile[mProject.controllerList.size()];
                        mProject.controllerList.toArray(projectFiles);
                        return Arrays.asList(projectFiles);
                    }
                    break;
                case ProjectHostFragment.POSITION_DOODLE:
                    if (mProject.doodleList != null) {
                        ProjectFile[] projectFiles = new ProjectFile[mProject.doodleList.size()];
                        mProject.doodleList.toArray(projectFiles);
                        return Arrays.asList(projectFiles);
                    }
                    break;
            }
        }
        return null;
    }


    private void open(ProjectFile projectFile) {
        if (mProjectHostFragment != null) {
            mProjectHostFragment.openSubProject(projectFile, true);
        }
        dismiss();
    }

    private void addNew() {
        if (mProjectHostFragment != null) {
            mProjectHostFragment.addNewSubProject();
        }
        dismiss();
    }

    private void delete(final int position) {
        final ProjectFile projectFile = mDatas.get(position);

        if (mProjectHostFragment != null) {
            mProjectHostFragment.getCurrentProjectFile()
                    .subscribe(new SimpleRxSubscriber<DataWrapper>() {
                        @Override
                        public void onNext(DataWrapper wrapper) {
                            super.onNext(wrapper);
                            ProjectFile current = null;
                            if (wrapper.data != null) {
                                current = (ProjectFile) wrapper.data;
                            }
                            if (current != null && current.id.equals(projectFile.id) && isAdded()) {
                                getUIDelegate().toastShort(getString(R.string.project_delete_fail_case_editting));
                                return;
                            }
                            File file = getProjectFile(projectFile);
                            if (file != null && file.exists() && file.isDirectory()) {
                                FileHelper.removeDir(file);
                            }
                            mDatas.remove(projectFile);
                            mProjectHostFragment.removeProjectFile(projectFile);
                            if (position < mCurrentPosition) {
                                mCurrentPosition--;
                            }
                            if (isAdded()) {
                                mAdapter.notifyItemRemoved(position);
                                mAdapter.notifyItemRangeChanged(position, mDatas.size() - position);
                            }
                        }
                    });
        }
    }

    private String generateDuplicationDefaultName(String sourceProjectName) {
        String duplicationName = "";
        int index = 0;
        do {
            index++;
            duplicationName = getString(R.string.project_edit_duplication, sourceProjectName) + (index==1?"":index+"");
        } while (projectFileNameExist(duplicationName));
        return duplicationName;
    }

    private void copy(final int position) {
        final ProjectFile projectFile = mDatas.get(position);
        mContentView.setVisibility(View.GONE);
        PromptEditDialogFragment fragment = PromptEditDialogFragment.newBuilder()
                .cancelable(false)
                .title(getString(R.string.project_edit_save_as))
                .message(generateDuplicationDefaultName(projectFile.name))
                .maxLength(20)
                .allowMessageUnchanged(true)
                .hint(getString(RENAME_HINTS[mListType]))
                .onConfirmClickListener(new PromptEditDialogFragment.OnConfirmClickListener() {
                    @Override
                    public boolean confirm(String text) {
                        if (projectFileNameExist(text)) {
                            ToastHelper.toastShort(getString(R.string.project_save_duplicate_name));
                            return true;
                        } else {
                            if (mProjectHostFragment != null) {
                                mProjectHostFragment.addSubProjectDuplication(projectFile, text);
                            }
                            ProjectListDialogFragment.this.dismiss();
                        }
                        return false;
                    }
                })
                .build();
        fragment.setDismissListener(new OnDialogFragmentDismissListener() {
            @Override
            public void onDismiss(Object... value) {
                mContentView.setVisibility(View.VISIBLE);
            }
        });
        fragment.show(getFragmentManager(), "PromptEditDialogFragment-projectFile");
    }

    private void rename(final int position) {
        final ProjectFile projectFile = mDatas.get(position);
        mContentView.setVisibility(View.GONE);
        PromptEditDialogFragment fragment = PromptEditDialogFragment.newBuilder()
                .cancelable(false)
                .title(getString(R.string.project_edit_rename))
                .message(projectFile.name)
                .maxLength(20)
                .hint(getString(RENAME_HINTS[mListType]))
                .onConfirmClickListener(new PromptEditDialogFragment.OnConfirmClickListener() {
                    @Override
                    public boolean confirm(String text) {
                        if (projectFileNameExist(text)) {
                            ToastHelper.toastShort(getString(R.string.project_save_duplicate_name));
                            return true;
                        } else {

                            mProjectHostFragment.renameProjectFile(projectFile, text);
                            mAdapter.notifyItemChanged(position);
                        }
                        return false;
                    }
                })
                .build();
        fragment.setDismissListener(new OnDialogFragmentDismissListener() {
            @Override
            public void onDismiss(Object... value) {
                mContentView.setVisibility(View.VISIBLE);
            }
        });
        fragment.show(getFragmentManager(), "PromptEditDialogFragment-projectFile");
    }

    private void playMotion(final int position) {
        if (mMotionPlayer != null) {
            if (System.currentTimeMillis() - mLastPressedTime < 200L) {
                return;
            }
            mLastPressedTime = System.currentTimeMillis();

            String motionId = mDatas.get(position).id;
            if (TextUtils.equals(mMotionPlayer.getPlayMotionId(), motionId)) {
                mMotionPlayer.stopMotion();
            } else {
                Motion motion = mProject.getMotion(motionId);
                mMotionPlayer.playMotion(motion);
            }
        }
    }

    private void stopMotion() {
        if (mMotionPlayer != null) {
            mMotionPlayer.stopMotion();
        }
    }

    private boolean projectFileNameExist(String name) {
        if (!TextUtils.isEmpty(name)) {
            List<ProjectFile> files = new ArrayList<>(mDatas);
            for (ProjectFile file : files) {
                if (name.equals(file.name)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 获取projectFile的File对象
     *
     * @param projectFile
     * @return
     */
    private File getProjectFile(ProjectFile projectFile) {
        String path = "";
        if (projectFile instanceof Blockly) {
            path = Workspace.BLOCKLY_DIR;

        }
        if (projectFile instanceof Motion) {
            path = Workspace.MOTION_DIR;
        }
        if (projectFile instanceof Controller) {
            path = Workspace.CONTROLLER_DIR;
        }
        if (projectFile instanceof Doodle) {
            path = Workspace.DOODLE_DIR;
        }
        path += File.separator + projectFile.id;
        File file = new File(path);
        if (file.exists() && file.isDirectory()) {
            return file;
        }
        return null;
    }

    View.OnClickListener mItemClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            stopMotion();
            int position = (int) v.getTag();
            if (position >= 0 && position < mDatas.size()) {
                mCurrentPosition = position;
                open(mDatas.get(position));
            }
        }
    };
    View.OnClickListener mItemEditClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int position = (int) v.getTag();
            if (position >= 0 && position < mDatas.size()) {
                if (v.getId() == R.id.project_sub_list_add_new_btn) {
                    stopMotion();
                    addNew();
                }
                if (v.getId() == R.id.project_sub_list_item_rename_btn) {
                    stopMotion();
                    rename(position);
                }
                if (v.getId() == R.id.project_sub_list_item_delete_btn) {
                    if (mMotionPlayer != null && mMotionPlayer.getPlayMotionId() != null && mMotionPlayer.getPlayMotionId().equals(mDatas.get(position).id)) {
                        stopMotion();
                    }
                    delete(position);
                }
                if (v.getId() == R.id.project_sub_list_item_copy_btn) {
                    stopMotion();
                    copy(position);
                }
                if (v.getId() == R.id.project_sub_list_item_play_btn) {
                    playMotion(position);
                }
            }
        }
    };

    class ListAdapter extends CommonAdapter<ProjectFile> {

        final static int TYPE_HEADER = 1;

        public ListAdapter(Context context, List<ProjectFile> datas) {
            super(context, R.layout.item_project_sub_list, datas);
            addItemViewDelegate(TYPE_HEADER, new ItemViewDelegate<ProjectFile>() {
                @Override
                public int getItemViewLayoutId() {
                    return R.layout.item_project_sub_list_header;
                }

                @Override
                public boolean isForViewType(ProjectFile item, int position) {
                    return false;
                }

                @Override
                public void convert(ViewHolder holder, ProjectFile projectFile, int position) {

                }
            });
        }

        @Override
        public int getItemViewType(int position) {
            if (position == 0) {
                return TYPE_HEADER;
            }
            return super.getItemViewType(position);
        }

        @Override
        protected void convert(ViewHolder holder, ProjectFile projectFile, int position) {
            if (position != 0) {
                holder.setTag(R.id.project_sub_list_item_content_lyt, position);
                holder.setOnClickListener(R.id.project_sub_list_item_content_lyt, mItemClickListener);

                holder.setTag(R.id.project_sub_list_item_copy_btn, position);
                holder.setOnClickListener(R.id.project_sub_list_item_copy_btn, mItemEditClickListener);
                holder.setTag(R.id.project_sub_list_item_rename_btn, position);
                holder.setOnClickListener(R.id.project_sub_list_item_rename_btn, mItemEditClickListener);
                holder.setTag(R.id.project_sub_list_item_delete_btn, position);
                holder.setOnClickListener(R.id.project_sub_list_item_delete_btn, mItemEditClickListener);
                TextView tv = holder.getView(R.id.project_sub_list_item_name_tv);
                tv.setText(projectFile.name);
                holder.setVisible(R.id.project_sub_list_item_selected_view, position == mCurrentPosition);

                holder.setTag(R.id.project_sub_list_item_play_btn, position);
                holder.setOnClickListener(R.id.project_sub_list_item_play_btn, mItemEditClickListener);
                holder.setVisible(R.id.project_sub_list_item_play_btn, MOTION_PLAY_BUTTON_VISIBLE[mListType]);
                holder.setVisible(R.id.project_sub_list_item_time_tv, MOTION_PLAY_BUTTON_VISIBLE[mListType]);

                if (MOTION_PLAY_BUTTON_VISIBLE[mListType]) {
                    String playMotionId = mMotionPlayer != null ? mMotionPlayer.getPlayMotionId() : null;
                    boolean isPlaying = TextUtils.equals(projectFile.id, playMotionId);
                    holder.setBackgroundRes(R.id.project_sub_list_item_play_btn, isPlaying ? R.drawable.popup_list_stop_btn_nor : R.drawable.popup_list_play_btn_nor);
                    TextView timeTv = holder.getView(R.id.project_sub_list_item_time_tv);
                    timeTv.setText(formatTime(mProject.getMotion(projectFile.id).totaltime));
                    timeTv.setSelected(isPlaying);
                }

            } else {
                holder.setTag(R.id.project_sub_list_add_new_btn, position);
                holder.setOnClickListener(R.id.project_sub_list_add_new_btn, mItemEditClickListener);
            }
        }

        private String formatTime(long time) {
            if (time != 0) {
                time = Math.max((time + 999) / 1000, 1);
            }
            return String.format(Locale.US, "%02d:%02d", time / 60, time % 60);
        }

    }

}
