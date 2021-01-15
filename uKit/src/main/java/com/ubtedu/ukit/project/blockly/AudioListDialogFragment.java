/**
 * @2018 UBTECH Robotics Corp. All rights reserved (C)
 **/
package com.ubtedu.ukit.project.blockly;

import android.content.Context;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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
import com.ubtedu.alpha1x.ui.widgets.SwipeMenuLayout;
import com.ubtedu.alpha1x.utils.LogUtil;
import com.ubtedu.alpha1x.utils.TimeUtil;
import com.ubtedu.ukit.R;
import com.ubtedu.ukit.common.base.UKitBaseDialogFragment;
import com.ubtedu.ukit.common.dialog.PromptEditDialogFragment;
import com.ubtedu.ukit.common.view.FixedLottieAnimationView;
import com.ubtedu.ukit.common.view.UKitCharsInputFilter;
import com.ubtedu.ukit.project.blockly.model.AudioItem;
import com.ubtedu.ukit.project.blockly.model.HeaderAudioItem;
import com.ubtedu.ukit.project.bridge.api.ToastHelper;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * @Author qinicy
 * @Date 2019/4/17
 **/
public abstract class AudioListDialogFragment extends UKitBaseDialogFragment {
    private View mContentView;
    private RecyclerView mRecyclerView;
    private View mFailureCloseBtn;
    private Button mFailureReloadBtn;
    private View mListLayer;
    private View mLoadingLayer;
    private FixedLottieAnimationView mLoadingAnimation;
    private View mFailureLayer;
    private View mCloseBtn;
    private Button mStartBtn;
    private View mEmptyView;
    private ArrayList<AudioItem> mAudios;
    protected String mCurrentAudioId;
    private int mCurrentPosition;
    private int mCurrentPlayPosition;
    private ListAdapter mAdapter;
    private AudioListPresenter mPresenter;
    private AudioListUI mUi;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mUi = createUi();
        mPresenter = createPresenter(mUi);
        mPresenter.init();
    }

    protected abstract @NonNull AudioListUI createUi();
    protected abstract @NonNull AudioListPresenter createPresenter(AudioListUI ui);

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        setCancelable(false);
        View root = inflater.inflate(R.layout.dialog_fragment_audio_list, null);
        mRootView = root.findViewById(R.id.dialog_fragment_root_lyt);
        mContentView = root.findViewById(R.id.dialog_fragment_root_view);
        mEmptyView = root.findViewById(R.id.audio_list_empty_lyt);

        mCloseBtn = root.findViewById(R.id.audio_list_close_btn);
        bindSafeClickListener(mCloseBtn);
        mStartBtn = root.findViewById(R.id.audio_list_start_btn);
        bindSafeClickListener(mStartBtn);
        mRecyclerView = root.findViewById(R.id.audio_list_rcv);
        mAudios = new ArrayList<>();

        mFailureCloseBtn = root.findViewById(R.id.audio_list_failure_close_btn);
        bindSafeClickListener(mFailureCloseBtn);
        mFailureReloadBtn = root.findViewById(R.id.audio_list_failure_reload_btn);
        bindSafeClickListener(mFailureReloadBtn);
        mListLayer = root.findViewById(R.id.audio_list_layer);
        mLoadingLayer = root.findViewById(R.id.audio_loading_layer);
        mFailureLayer = root.findViewById(R.id.audio_failure_layer);
        mLoadingAnimation = root.findViewById(R.id.audio_record_lottie_view);

        List<AudioItem> items = mPresenter.loadAudioList();
        setAudioItems(items);

        mAdapter = new ListAdapter(getContext(), mAudios);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setOrientation(RecyclerView.VERTICAL);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.addItemDecoration(new SpaceItemDecoration(getContext().getResources().getDimensionPixelOffset(R.dimen.ubt_dimen_24px)));
        checkIfEmptyThenUpdateUI();
        return root;
    }

    protected void setAudioItems(Collection<AudioItem> items) {
        mAudios.clear();
        if(items != null) {
            mAudios.addAll(items);
        }
        mAudios.add(0, new HeaderAudioItem());
        if (mAudios.size() > 0) {
            Collections.sort(mAudios);
        }
        mCurrentPosition = getSelectItemPosition();
    }

    @Override
    protected void onVisibilityChangedToUser(boolean isVisibleToUser) {
        super.onVisibilityChangedToUser(isVisibleToUser);
        LogUtil.d("isVisibleToUser:" + isVisibleToUser);
        stopPlaying();
    }

    @Override
    public void onClick(View v, boolean isSafeClick) {
        super.onClick(v, isSafeClick);
        if (v == mCloseBtn || v == mFailureCloseBtn) {
            dismiss();
        }
        if (v == mStartBtn) {
            addNew();
        }
        if (v == mFailureReloadBtn) {
            mPresenter.loadAudioList();
        }
    }

    @Override
    public void dismiss() {
        super.dismiss();
        stopPlaying();
        mPresenter.release();
        if (mDismissListener != null) {
            AudioItem audio;
            if(mCurrentPosition < 0 || mAudios == null || mCurrentPosition >= mAudios.size()) {
                audio = null;
            } else {
                audio = mAudios.get(mCurrentPosition);
            }
            mDismissListener.onDismiss(audio);
            mDismissListener = null;
        }
    }

    private int getSelectItemPosition() {
        if (mCurrentAudioId != null) {
            for (int i = 0; i < mAudios.size(); i++) {
                AudioItem b = mAudios.get(i);
                if (mCurrentAudioId.equals(b.getId())) {
                    return i;
                }
            }
        }
        LogUtil.d("default position 1");
        return -1;
    }

    private void open(AudioItem audio) {
        if(!mPresenter.canDoAction(AudioListPresenter.CAN_DO_CHOICE)) {
            return;
        }
        if (mDismissListener != null) {
            mDismissListener.onDismiss(audio);
            mDismissListener = null;
        }
        dismiss();
    }

    protected void addNew() {
        if (!mPresenter.canDoAction(AudioListPresenter.CAN_DO_RECORD)) {
            return;
        }
        stopPlaying();
        if (mPresenter.canAddNew(mAudios)) {
            mUi.showAudioRecordUI();
        } else {
            mUi.showAudioRecordFullUI();
        }
    }

    private boolean isAudioNameExist(String name) {
        if (!TextUtils.isEmpty(name)) {
            List<AudioItem> files = new ArrayList<>(mAudios);
            for (AudioItem file : files) {
                if (name.equals(file.getName())) {
                    return true;
                }
            }
        }
        return false;
    }

    protected void showAudioSavePromptDialog(final AudioItem audio) {
        PromptEditDialogFragment.newBuilder().title(getString(R.string.audio_save_title))
                .hint(getString(R.string.audio_save_hint))
                .editInputFilters(getAudioNameInputFilters())
                .cancelable(false)
                .message(getDefaultAudioName())
                .allowMessageUnchanged(true)
                .autoShowSoftKeyboard(false)
                .negativeButtonText(getString(R.string.audio_save_negative))
                .positiveButtonText(getString(R.string.audio_save_positive))
                .onConfirmClickListener(new PromptEditDialogFragment.OnConfirmClickListener() {
                    @Override
                    public boolean confirm(String text) {
                        if(!mPresenter.canDoAction(AudioListPresenter.CAN_DO_RENAME)) {
                            return false;
                        }
                        if (checkNameLengthExceedLimit(text)){
                            ToastHelper.toastShort(getString(R.string.audio_record_rename_length_limit_exceeded));
                            return true;
                        }
                        if (!isAudioNameExist(text)) {
                            ToastHelper.toastShort(getString(R.string.audio_record_success));
                            String newName = text.trim();
                            mPresenter.rename(audio, newName);
                            updateList(audio);
                            return false;
                        } else {
                            ToastHelper.toastShort(getString(R.string.audio_rename_duplicate_name));
                            return true;
                        }
                    }
                })
                .onCancelClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mPresenter.delete(audio);
                    }
                })
                .build()
                .show(getFragmentManager(), "showAudioSavePromptDialog");
    }

    protected boolean checkNameLengthExceedLimit(String text){
        if (TextUtils.isEmpty(text)) {
            return false;
        }
        return text.length() > 20;
    }

    protected InputFilter[] getAudioNameInputFilters() {
        return new InputFilter[]{new UKitCharsInputFilter(20)};
    }

    protected void updateAudioList(ArrayList<AudioItem> list) {
        setAudioItems(list);
        if(mAdapter != null) {
            mAdapter.notifyDataSetChanged();
        }
        checkIfEmptyThenUpdateUI();
    }

    private void checkIfEmptyThenUpdateUI() {
        if (mAudios != null && mAudios.size() > 1) {
            mRecyclerView.setVisibility(View.VISIBLE);
            mEmptyView.setVisibility(View.GONE);
            mStartBtn.setVisibility(View.GONE);
        } else {
            mRecyclerView.setVisibility(View.GONE);
            mEmptyView.setVisibility(View.VISIBLE);
            mStartBtn.setVisibility(View.VISIBLE);
        }
    }

    private void updateList(AudioItem audio) {
        mAudios.add(1, audio);
        if (mCurrentPosition >= 1) {
            mCurrentPosition++;
        }
        mPresenter.save(audio);
        if(mAdapter != null) {
            mAdapter.notifyDataSetChanged();
        }
        checkIfEmptyThenUpdateUI();
    }

    private String getDefaultAudioName() {
        return mPresenter.getDefaultAudioName(mAudios);
    }

    private void delete(final int position) {
        if(!mPresenter.canDoAction(AudioListPresenter.CAN_DO_DELETE)) {
            return;
        }
        if (position == mCurrentPlayPosition) {
            mCurrentPlayPosition = -1;
            stopPlaying();
        }

        final AudioItem audioItem = mAudios.get(position);
        mPresenter.delete(audioItem);
        mAudios.remove(audioItem);

        if (position == mCurrentPosition) {
            mCurrentPosition = -1;
        }
        if (position < mCurrentPosition) {
            mCurrentPosition--;
        }
        if (position < mCurrentPlayPosition) {
            mCurrentPlayPosition--;
        }
        if(mAdapter != null) {
            mAdapter.notifyItemRemoved(position);
            mAdapter.notifyItemRangeChanged(position, mAdapter.getItemCount() - position);
        }
        checkIfEmptyThenUpdateUI();
    }

    private void rename(final int position) {
//        if(!mPresenter.canDoAction(AudioListPresenter.CAN_DO_RENAME)) {
//            return;
//        }
        stopPlaying();
        final AudioItem audio = mAudios.get(position);
        mContentView.setVisibility(View.GONE);
        PromptEditDialogFragment fragment = PromptEditDialogFragment.newBuilder()
                .cancelable(false)
                .title(getString(R.string.audio_rename_title))
                .message(audio.getName())
                .editInputFilters(getAudioNameInputFilters())
                .hint(getString(R.string.audio_rename_hint))
                .negativeButtonText(getString(R.string.audio_save_negative))
                .positiveButtonText(getString(R.string.audio_save_positive))
                .onConfirmClickListener(new PromptEditDialogFragment.OnConfirmClickListener() {
                    @Override
                    public boolean confirm(String text) {
                        if(!mPresenter.canDoAction(AudioListPresenter.CAN_DO_RENAME)) {
                            return false;
                        }
                        if (checkNameLengthExceedLimit(text)){
                            ToastHelper.toastShort(getString(R.string.audio_record_rename_length_limit_exceeded));
                            return true;
                        }
                        if (isAudioNameExist(text)) {
                            ToastHelper.toastShort(getString(R.string.project_save_duplicate_name));
                            return true;
                        } else {
                            mPresenter.rename(audio, text);
                            if(mAdapter != null && position != -1 && position < mAudios.size()) {
                                mAdapter.notifyItemChanged(position);
                            }
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


    View.OnClickListener mItemEditClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int position = (int) v.getTag();
            if (position >= 0 && position < mAudios.size()) {
                if (v.getId() == R.id.audio_list_item_content_lyt) {
                    mCurrentPosition = position;
                    open(mAudios.get(position));
                }
                if (v.getId() == R.id.audio_list_add_new_btn) {
                    addNew();
                }
                if (v.getId() == R.id.audio_list_item_rename_btn) {
                    rename(position);
                }
                if (v.getId() == R.id.audio_list_item_delete_btn) {
                    delete(position);
                }
                if (v.getId() == R.id.audio_list_item_play_btn) {
                    play(position);
                }
            }
        }
    };

    protected void changeUIStatus(AudioListUI.UIStatus status) {
        if(AudioListUI.UIStatus.LOADING.equals(status)) {
            mListLayer.setVisibility(View.GONE);
            mLoadingLayer.setVisibility(View.VISIBLE);
            mFailureLayer.setVisibility(View.GONE);
            mLoadingAnimation.cancelAnimation();
            mLoadingAnimation.playAnimation();
        } else if(AudioListUI.UIStatus.FAILURE.equals(status)) {
            mListLayer.setVisibility(View.GONE);
            mLoadingLayer.setVisibility(View.GONE);
            mFailureLayer.setVisibility(View.VISIBLE);
            mLoadingAnimation.pauseAnimation();
        } else if(AudioListUI.UIStatus.SUCCESS.equals(status)) {
            mListLayer.setVisibility(View.VISIBLE);
            mLoadingLayer.setVisibility(View.GONE);
            mFailureLayer.setVisibility(View.GONE);
            mLoadingAnimation.pauseAnimation();
        }
    }

    private void play(int position) {
        if (position == mCurrentPlayPosition) {
            if(!mPresenter.canDoAction(AudioListPresenter.CAN_DO_STOP)) {
                return;
            }
            stopPlay(false);
        } else {
            if(!mPresenter.canDoAction(AudioListPresenter.CAN_DO_PLAY)) {
                return;
            }
            doPlay(position);
        }
    }

    private void doPlay(int position) {
        AudioItem audio = mAudios.get(position);
        if(mAdapter != null && mCurrentPlayPosition != -1 && mCurrentPlayPosition < mAudios.size()) {
            mAdapter.notifyItemChanged(mCurrentPlayPosition);
        }
        mCurrentPlayPosition = position;
        if(mAdapter != null && mCurrentPlayPosition != -1 && mCurrentPlayPosition < mAudios.size()) {
            mAdapter.notifyItemChanged(mCurrentPlayPosition);
        }
        String tag = String.valueOf(position);
        AudioListPresenter.IAudioPlayListener mAudioPlayListener = new AudioListPresenter.IAudioPlayListenerV2(tag) {
            @Override
            public void onFinished(String tag) {
                boolean resetPlayPosition = TextUtils.equals(tag, String.valueOf(mCurrentPlayPosition));
                stopPlaying(resetPlayPosition);
            }

            @Override
            public void onError(String tag) {
                boolean resetPlayPosition = TextUtils.equals(tag, String.valueOf(mCurrentPlayPosition));
                stopPlaying(resetPlayPosition);
            }
        };
        mPresenter.play(audio, mAudioPlayListener);
    }

    private void stopPlay(boolean isBackground) {
        stopPlay(isBackground, true);
    }

    private void stopPlay(boolean isBackground, boolean resetPlayPosition) {
        int last = mCurrentPlayPosition;
        if(resetPlayPosition) {
            mCurrentPlayPosition = -1;
        }
        if(mAdapter != null && last != -1 && last < mAudios.size()) {
            mAdapter.notifyItemChanged(last);
        }
        mPresenter.stop(isBackground);
    }

    protected void stopPlaying() {
        stopPlay(true, true);
    }

    protected void stopPlaying(boolean resetPlayPosition) {
        stopPlay(true, resetPlayPosition);
    }

    private class ListAdapter extends CommonAdapter<AudioItem> {

        final static int TYPE_HEADER = 1;

        public ListAdapter(Context context, List<AudioItem> datas) {
            super(context, R.layout.item_audio_list, datas);
            addItemViewDelegate(TYPE_HEADER, new ItemViewDelegate<AudioItem>() {
                @Override
                public int getItemViewLayoutId() {
                    return R.layout.item_audio_list_header;
                }

                @Override
                public boolean isForViewType(AudioItem item, int position) {
                    return false;
                }

                @Override
                public void convert(ViewHolder holder, AudioItem projectFile, int position) {

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
        protected void convert(ViewHolder holder, AudioItem audio, int position) {
            if (position != 0) {
                SwipeMenuLayout swipeMenuLayout = holder.getView(R.id.audio_list_item_root_lyt);
                swipeMenuLayout.setIos(false);
                holder.setTag(R.id.audio_list_item_content_lyt, position);
                holder.setOnClickListener(R.id.audio_list_item_content_lyt, mItemEditClickListener);

                holder.setTag(R.id.audio_list_item_rename_btn, position);
                holder.setOnClickListener(R.id.audio_list_item_rename_btn, mItemEditClickListener);
                holder.setTag(R.id.audio_list_item_delete_btn, position);
                holder.setOnClickListener(R.id.audio_list_item_delete_btn, mItemEditClickListener);
                if (position == mCurrentPlayPosition) {
                    holder.setBackgroundRes(R.id.audio_list_item_play_btn, R.drawable.recoding_stop_btn_nor);
                } else {
                    holder.setBackgroundRes(R.id.audio_list_item_play_btn, R.drawable.recoding_play_btn_nor);
                }

                holder.setTag(R.id.audio_list_item_play_btn, position);
                holder.setOnClickListener(R.id.audio_list_item_play_btn, mItemEditClickListener);

                holder.setText(R.id.audio_list_item_duration_tv, TimeUtil.milliseconds2String(audio.getDuration()));


                TextView tv = holder.getView(R.id.audio_list_item_name_tv);
                tv.setText(audio.getName());
                holder.setVisible(R.id.audio_list_item_selected_view, position == mCurrentPosition);
            } else {
                holder.setTag(R.id.audio_list_add_new_btn, position);
                holder.setOnClickListener(R.id.audio_list_add_new_btn, mItemEditClickListener);
            }
        }
    }

}
