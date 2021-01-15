/**
 * ©2018, UBTECH Robotics, Inc. All rights reserved (C)
 **/
package com.ubtedu.ukit.demo;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;

import com.ubtedu.alpha1x.core.base.Lifecycle.viewmodel.BaseViewModel;
import com.ubtedu.alpha1x.core.base.Lifecycle.vo.Resource;

/**
 *@Author qinicy
 *@Date 2018/9/4
 **/
public class DemoViewModel extends BaseViewModel {

    private DataRepository mRepository;
    private MediatorLiveData<Resource<User>> mUserResource;
    public DemoViewModel(DataRepository repository) {
        mRepository = repository;
    }
    public LiveData<Resource<User>> getUserResource(boolean force) {
        mUserResource = mRepository.requestUserInfo(force);
        return mUserResource;
    }

}
