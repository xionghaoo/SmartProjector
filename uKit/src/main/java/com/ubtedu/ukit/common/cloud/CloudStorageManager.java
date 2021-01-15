/**
 * @2018 UBTECH Robotics Corp. All rights reserved (C)
 **/
package com.ubtedu.ukit.common.cloud;


import android.content.Context;

import com.ubtedu.ukit.common.flavor.Channel;
import com.ubtedu.ukit.common.flavor.Flavor;

import io.reactivex.Observable;


/**
 * @Author qinicy
 * @Date 2018/11/19
 **/
public class CloudStorageManager {

    private ICloudStorage mCloudStorage;
    private CloudStorageManager() {

    }

    private static class SingletonHolder {
        private final static CloudStorageManager instance = new CloudStorageManager();
    }

    public static CloudStorageManager getInstance() {
        return CloudStorageManager.SingletonHolder.instance;
    }

    public void init(Context context){
        if (Channel.NA.getId() == Flavor.getChannel().getId()){
            mCloudStorage = new AWSCloudStorage();
        }else {
            mCloudStorage = new AliCloudStorage();
        }
        mCloudStorage.init(context);
    }

    public  String generateFileUrl(String key){
        return mCloudStorage.generateFileUrl(key);
    }
    public Observable<CloudResult> upload(final String filePath){
        return mCloudStorage.upload(filePath);
    }

    public Observable<Integer> download(final String objectKey, final String path){
        return mCloudStorage.download(objectKey,path);
    }

}

