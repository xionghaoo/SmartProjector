package com.ubtedu.ukit.common.cloud;

import android.content.Context;

import io.reactivex.Observable;

/**
 * @Author qinicy
 * @Date 2020-02-06
 **/
public interface ICloudStorage {
    void init(Context context);

    String generateFileUrl(String objectKey);

    Observable<CloudResult> upload(final String filePath);

    Observable<Integer> download(final String objectKey, final String path);

}
