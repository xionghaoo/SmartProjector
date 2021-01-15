/**
 * ©2018, UBTECH Robotics, Inc. All rights reserved (C)
 **/
package com.ubtedu.ukit.demo;

import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;

import com.ubtedu.alpha1x.core.base.Lifecycle.vo.Resource;
import com.ubtedu.alpha1x.core.base.Lifecycle.vo.Status;

/**
 * @Author qinicy
 * @Date 2018/9/4
 **/
public class DemoPresenter extends DemoContracts.Presenter {
    private DemoViewModel mViewModel;
    private LiveData<Resource<User>> mUserResource;

    @Override
    public void onCreate() {
        super.onCreate();
        mViewModel = createViewModel(DemoViewModel.class);
        mUserResource = mViewModel.getUserResource(false);
        mUserResource.observe(mLifecycleOwner, new Observer<Resource<User>>() {
            @Override
            public void onChanged(@Nullable Resource<User> resource) {
                if (resource == null) {
                    return;
                }
                if (resource.status == Status.SUCCESS) {
                    getView().onUpdateUserInfo(resource.data);
                } else if (resource.status == Status.LOADING) {
                    getView().onLoading("loading...");
                } else if (resource.status == Status.ERROR) {
                    getView().onLoading("数据请求失败");
                }
            }
        });
    }

    @Override
    public void requestUserInfo() {
        mViewModel.getUserResource(true);
    }
}
