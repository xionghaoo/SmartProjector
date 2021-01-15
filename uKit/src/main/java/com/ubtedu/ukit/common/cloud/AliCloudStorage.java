/**
 * @2018 UBTECH Robotics Corp. All rights reserved (C)
 **/
package com.ubtedu.ukit.common.cloud;


import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.alibaba.sdk.android.oss.ClientException;
import com.alibaba.sdk.android.oss.OSSClient;
import com.alibaba.sdk.android.oss.ServiceException;
import com.alibaba.sdk.android.oss.callback.OSSCompletedCallback;
import com.alibaba.sdk.android.oss.callback.OSSProgressCallback;
import com.alibaba.sdk.android.oss.common.auth.OSSCredentialProvider;
import com.alibaba.sdk.android.oss.common.auth.OSSFederationCredentialProvider;
import com.alibaba.sdk.android.oss.common.auth.OSSFederationToken;
import com.alibaba.sdk.android.oss.model.GetObjectRequest;
import com.alibaba.sdk.android.oss.model.GetObjectResult;
import com.alibaba.sdk.android.oss.model.PutObjectRequest;
import com.alibaba.sdk.android.oss.model.PutObjectResult;
import com.ubtedu.alpha1x.utils.LogUtil;
import com.ubtedu.base.net.rxretrofit.exception.RxException;
import com.ubtedu.ukit.common.exception.ResultCode;
import com.ubtedu.ukit.common.files.FileHelper;
import com.ubtedu.ukit.common.thread.ThreadPoolUtil;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.schedulers.Schedulers;


/**
 * @Author qinicy
 * @Date 2018/11/19
 **/
public class AliCloudStorage extends AbstractCloudStorage {

    private String mBucket;
    private String mDomain;
    private static String DEFAULT_BUCKET = "ubtrobot-new";
    private static String DEFAULT_ENDPOINT = "oss-cn-shenzhen.aliyuncs.com";
    private boolean isInited;
    private Context mContext;
    private OSSClient mOssClient;
    private String mEndPoint;
    private Object mCreateFileLock = new Object();

    public void init(Context context) {
        if (context != null && !isInited) {
            this.mContext = context.getApplicationContext();
            initOssClient();
            isInited = true;
        }
    }


    /**
     * https://ubtrobot-new.oss-cn-shenzhen.aliyuncs.com/edu/uKit2.0/2515a2493d3c45498a487c4573dd335d.zip
     */
    @Override
    public String generateFileUrl(String objectKey) {
        //https://assets-new.ubtrobot.com,有CND加速
        if (mDomain != null) {
            return mDomain + "/" + objectKey;
        }
        //无CDN加速
        if (mOssClient != null) {
            String url = mOssClient.presignPublicObjectURL(mBucket, objectKey);
            if (url != null) {
                url = url.replace("http", "https");
                return url;
            }
        }

        return "https://" + DEFAULT_BUCKET + "." + DEFAULT_ENDPOINT + "/" + objectKey;
    }


    private void initOssClient() {
        ThreadPoolUtil.execute(new Runnable() {
            @Override
            public void run() {
                UploadSignature signature = requestUploadSignatureInfo(2,1,3600);
                if (signature == null) {
                    signature = new UploadSignature();
                    signature.bucket = DEFAULT_BUCKET;
                    signature.endPoint = DEFAULT_ENDPOINT;
                }
                if (TextUtils.isEmpty(signature.bucket)) {
                    signature.bucket = DEFAULT_BUCKET;
                }
                if (TextUtils.isEmpty(signature.endPoint)) {
                    signature.endPoint = DEFAULT_ENDPOINT;
                }
                mBucket = signature.bucket;
                mEndPoint = signature.endPoint;
                mDomain = signature.domain;
                mOssClient = new OSSClient(mContext, mEndPoint, mCredentialProvider);
            }
        });
    }

    private OSSCredentialProvider mCredentialProvider = new OSSFederationCredentialProvider() {
        @Override
        public OSSFederationToken getFederationToken() {
            UploadSignature signature = requestUploadSignatureInfo(2,1,3600);
            if (signature != null) {
                return new OSSFederationToken(
                        signature.accessKeyId,
                        signature.accessKeySecret,
                        signature.securityToken,
                        signature.expiration);
            }
            return null;
        }
    };


    @Override
    public Observable<CloudResult> upload(final String filePath) {

        return Observable
                .create(new ObservableOnSubscribe<CloudResult>() {
                    @Override
                    public void subscribe(ObservableEmitter<CloudResult> emitter) {
                        String suffix = FileHelper.getSuffix(filePath);
                        String objectKey = createOssObjectKey(suffix);
                        realDoUpload(emitter, objectKey, filePath);
                    }
                })
                .subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    @Override
    public Observable<Integer> download(final String objectKey, final String path) {
        LogUtil.d("doDownload:" + path);
        LogUtil.d("objectKey:" + objectKey);
        LogUtil.d("objectKey to url:" + generateFileUrl(objectKey));
        if (mOssClient == null) {
            return Observable.error(new Exception("OSS SDK not init"));
        }
        return Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(@NonNull final ObservableEmitter<Integer> emitter) {
                emitter.onNext(0);
                GetObjectRequest get = new GetObjectRequest(mBucket, objectKey);

                mOssClient.asyncGetObject(get, new OSSCompletedCallback<GetObjectRequest, GetObjectResult>() {

                    @Override
                    public void onSuccess(GetObjectRequest request, GetObjectResult objectResult) {
                        LogUtil.d("doDownload onSuccess:");

                        File file = new File(path);
                        if (!file.exists()) {
                            File parent = file.getParentFile();
                            synchronized (mCreateFileLock) {
                                if (parent != null && !parent.exists()) {
                                    boolean success = parent.mkdirs();
                                    if (!success) {
                                        if (!emitter.isDisposed()) {
                                            emitter.onError(new RxException(ResultCode.OSS_DOWN_WRITE_FILE_FAIL, "创建文件夹失败！"));
                                        }
                                        return;
                                    }
                                }
                            }
                            boolean success = false;
                            try {
                                success = file.createNewFile();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            if (!success) {
                                if (!emitter.isDisposed()) {
                                    emitter.onError(new RxException(ResultCode.OSS_DOWN_WRITE_FILE_FAIL, "创建文件失败！"));
                                }
                                return;
                            }
                        }
                        try (FileOutputStream stream = new FileOutputStream(file);
                             InputStream inputStream = objectResult.getObjectContent()) {

                            long totalSize = objectResult.getContentLength();
                            totalSize += totalSize * 0.1;
                            long currentSize = 0;

                            byte[] buffer = new byte[1024];
                            int len;
                            while ((len = inputStream.read(buffer)) != -1) {

                                // 处理下载的数据
                                stream.write(buffer, 0, len);

                                currentSize += len;
                                int progress = 0;
                                int preProgress = 0;
                                if (totalSize != 0) {
                                    if (currentSize <= totalSize) {
                                        progress = (int) ((float) currentSize / (float) totalSize * 100f);
                                    } else {
                                        progress = 95;
                                    }
                                }
                                if (progress - preProgress > 5 || progress < 5) {
                                    preProgress = progress;
                                    if (!emitter.isDisposed()) {
                                        emitter.onNext(preProgress);
                                    }
                                    LogUtil.d("doDownload progress:" + progress);
                                }
                            }
                            stream.flush();
                            stream.close();
                            Thread.sleep(200);
                            if (!emitter.isDisposed()) {
                                emitter.onNext(100);
                                emitter.onComplete();
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                            if (!emitter.isDisposed()) {
                                emitter.onError(new RxException(ResultCode.OSS_DOWN_WRITE_FILE_FAIL, "下载创建文件失败！"));
                            }
                        }

                    }

                    @Override
                    public void onFailure(GetObjectRequest request, ClientException clientExcepion, ServiceException serviceException) {
                        LogUtil.e("onFailure");
                        // 请求异常
                        if (clientExcepion != null) {
                            // 本地异常如网络异常等
                            clientExcepion.printStackTrace();
                            if (!emitter.isDisposed()) {
                                emitter.onError(clientExcepion);
                            }
                        }
                        if (serviceException != null) {
                            // 服务异常
                            if (!emitter.isDisposed()) {
                                emitter.onError(serviceException);
                            }
                            Log.e("ErrorCode", serviceException.getErrorCode());
                            Log.e("RequestId", serviceException.getRequestId());
                            Log.e("HostId", serviceException.getHostId());
                            Log.e("RawMessage", serviceException.getRawMessage());
                        }
                    }
                });
            }
        })
                .subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread());

    }



    private void realDoUpload(final ObservableEmitter<CloudResult> emitter, final String objectKey, final String filePath) {
        if (mOssClient == null) {
            emitter.onError(new Exception("OSS SDK not init"));
            return;
        }
        if (filePath == null) {
            emitter.onError(new RxException(ResultCode.OSS_UPDATE_FAIL_CASE_INVALID_FILE, "Upload fail,filePath = null!"));
            return;
        }
        if (!new File(filePath).exists()) {
            emitter.onError(new RxException(ResultCode.OSS_UPDATE_FAIL_CASE_INVALID_FILE, "Upload fail,File is not exists!======" + filePath + "====="));
            return;
        }
        if (TextUtils.isEmpty(objectKey)) {
            emitter.onError(new RxException(ResultCode.OSS_UPDATE_FAIL_CASE_INVALID_FILE, "Upload fail,objectKey is empty!"));
            return;
        }


        final CloudResult result = new CloudResult();
        result.objectKey = objectKey;
        // 构造上传请求
        final PutObjectRequest request = new PutObjectRequest(mBucket, objectKey, filePath);
        request.setProgressCallback(new OSSProgressCallback<PutObjectRequest>() {
            @Override
            public void onProgress(PutObjectRequest putObjectRequest, long currenSize, long totalSize) {
                if (totalSize != 0 && currenSize != totalSize) {
                    int progress = (int) ((float) currenSize / (float) totalSize * 100f);
                    if (progress - result.progress > 10) {
                        result.progress = progress;
                        emitter.onNext(result);
                    }
                }
            }
        });

        mOssClient.asyncPutObject(request, new OSSCompletedCallback<PutObjectRequest, PutObjectResult>() {
            @Override
            public void onSuccess(PutObjectRequest request, PutObjectResult r) {

                result.isComplete = true;
                if (!emitter.isDisposed()) {
                    emitter.onNext(result);
                    emitter.onComplete();
                }

                Log.d("PutObject", "UploadSuccess");
                Log.d("ETag", r.getETag());
                Log.d("RequestId", r.getRequestId());
                Log.d("objectKey", result.objectKey);
            }


            @Override
            public void onFailure(PutObjectRequest request, ClientException clientExcepion, ServiceException serviceException) {
                // 请求异常
                if (clientExcepion != null) {
                    // 本地异常如网络异常等
                    if (!emitter.isDisposed()) {
                        emitter.onError(clientExcepion);
                    }
                    clientExcepion.printStackTrace();
                } else if (serviceException != null) {
                    // 服务异常
                    if (!emitter.isDisposed()) {
                        emitter.onError(serviceException);
                    }
                    Log.e("ErrorCode", serviceException.getErrorCode());
                    Log.e("RequestId", serviceException.getRequestId());
                    Log.e("HostId", serviceException.getHostId());
                    Log.e("RawMessage", serviceException.getRawMessage());
                }
            }
        });
    }
}

