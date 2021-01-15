/**
 * @2018 UBTECH Robotics Corp. All rights reserved (C)
 **/
package com.ubtedu.ukit.project;

import android.annotation.SuppressLint;

import androidx.lifecycle.MediatorLiveData;

import com.ubtedu.alpha1x.core.base.Lifecycle.viewmodel.BaseViewModel;
import com.ubtedu.alpha1x.utils.LogUtil;
import com.ubtedu.base.net.rxretrofit.exception.RxException;
import com.ubtedu.base.net.rxretrofit.subscriber.SimpleRxSubscriber;
import com.ubtedu.ukit.project.vo.Project;

import java.util.List;


/**
 * @Author qinicy
 * @Date 2018/11/26
 **/
public class UserDataViewModel extends BaseViewModel {
    private MediatorLiveData<List<Project>> mProjectsData;
    private MediatorLiveData<DataSyncEvent> mSyncEventData;

    public UserDataViewModel() {
        mProjectsData = new MediatorLiveData<>();
        mSyncEventData = new MediatorLiveData<>();
    }

    @SuppressLint("CheckResult")
    public void initData() {
        UserDataSynchronizer.getInstance().getSynchronizerObservable()
                .subscribe(new SimpleRxSubscriber<DataSyncEvent>() {
                    @Override
                    public void onNext(DataSyncEvent event) {
                        super.onNext(event);
                        if (event != null) {
                            LogUtil.d("DataSyncEvent:" + event.getStringEvent());
                            mSyncEventData.postValue(event);
                            if ((event.event == DataSyncEvent.SYNC_EVENT_SYNCING ||
                                    event.event == DataSyncEvent.SYNC_EVENT_SYNC_END) &&
                                    event.projects != null) {
                                mProjectsData.postValue(event.projects);
                            }
                        }
                    }

                    @Override
                    public void onError(RxException e) {
                        super.onError(e);
                    }
                });
    }

    public MediatorLiveData<DataSyncEvent> getSyncEventData() {
        return mSyncEventData;
    }

    public MediatorLiveData<List<Project>> getProjectsData() {
        return mProjectsData;
    }

}
