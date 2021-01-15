/**
 * ©2018, UBTECH Robotics, Inc. All rights reserved (C)
 **/
package com.ubtedu.ukit.demo;


import androidx.lifecycle.MediatorLiveData;

import com.ubtedu.alpha1x.core.base.Lifecycle.vo.Resource;

/**
 * @Author qinicy
 * @Date 2018/9/4
 **/
public class DataRepository {
    public MediatorLiveData<Resource<User>> mUserLiveData;

    private long mLastRequestTime;

    private static class SingletonHolder {
        private final static DataRepository instance = new DataRepository();
    }

    public static DataRepository getInstance() {
        return DataRepository.SingletonHolder.instance;
    }

    public MediatorLiveData<Resource<User>> requestUserInfo(boolean force) {
        if (mUserLiveData == null) {
            mUserLiveData = new MediatorLiveData<>();
        }
        //假设有这个场景：大于十秒需要更新数据
        boolean needUpdate = System.currentTimeMillis() - mLastRequestTime > 10000;
        if (force || needUpdate) {
            Resource<User> loadingResource = Resource.loading(null);
            mUserLiveData.postValue(loadingResource);
            //模拟网络请求数据3秒
            new Thread() {
                @Override
                public void run() {
                    super.run();
                    try {
                        Thread.sleep(3000);
                        User user = new User();
                        user.name = "qinicy";
                        user.age = 20;
                        mUserLiveData.postValue(Resource.success(user));
                        mLastRequestTime = System.currentTimeMillis();
                    } catch (Exception e) {
                        Resource<User> errResource = Resource.error("网络终端", null, e);
                        mUserLiveData.postValue(errResource);
                        e.printStackTrace();
                    }
                }
            }.start();
        }
        return mUserLiveData;
    }
}
