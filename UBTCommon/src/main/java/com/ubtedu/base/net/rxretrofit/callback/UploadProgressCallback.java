package com.ubtedu.base.net.rxretrofit.callback;

/**
 * @Description: 上传进度回调接口
 * @author: qinicy
 * @date: 17/5/6 15:03
 */
public interface UploadProgressCallback {
    void onProgress(long currentLength, long totalLength, float percent);
}
