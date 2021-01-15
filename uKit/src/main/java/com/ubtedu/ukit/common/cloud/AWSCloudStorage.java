package com.ubtedu.ukit.common.cloud;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;

import com.amazonaws.auth.BasicSessionCredentials;
import com.amazonaws.mobile.client.AWSMobileClient;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferListener;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferObserver;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferService;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferState;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility;
import com.amazonaws.regions.Region;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.AmazonS3Exception;
import com.ubtedu.alpha1x.utils.LogUtil;
import com.ubtedu.base.net.rxretrofit.exception.RxException;
import com.ubtedu.base.net.rxretrofit.subscriber.SimpleRxSubscriber;
import com.ubtedu.ukit.common.exception.ResultCode;
import com.ubtedu.ukit.common.files.FileHelper;

import java.io.File;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.ObservableSource;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

/**
 * @Author qinicy
 * @Date 2020-02-06
 **/
public class AWSCloudStorage extends AbstractCloudStorage {
    private final static int SESSION_VALID_DURATION = 3600;
    private String mDomain;
    private Object mCreateFileLock = new Object();
    private TransferUtility mTransferUtility;
    private Context mApplicationContext;
    private long mLastSessionTime;


    @Override
    public void init(Context context) {
        mApplicationContext = context.getApplicationContext();
        initCloudClient().subscribe(new SimpleRxSubscriber<>());
    }

    public static void startService(Activity activity) {
        activity.getApplication().startService(new Intent(activity.getApplicationContext(), TransferService.class));
    }

    private Observable<Boolean> initCloudClient() {
        LogUtil.i("");
        return Observable.create((ObservableOnSubscribe<Boolean>) emitter -> {
            UploadSignature signature = requestUploadSignatureInfo(3, 1, SESSION_VALID_DURATION);

            if (signature != null &&
                    !TextUtils.isEmpty(signature.bucket) &&
                    !TextUtils.isEmpty(signature.domain) &&
                    !TextUtils.isEmpty(signature.region) &&
                    !TextUtils.isEmpty(signature.accessKeyId) &&
                    !TextUtils.isEmpty(signature.accessKeySecret) &&
                    !TextUtils.isEmpty(signature.securityToken)) {
                mDomain = signature.domain;
                BasicSessionCredentials sessionCredentials = new BasicSessionCredentials(
                        signature.accessKeyId,
                        signature.accessKeySecret,
                        signature.securityToken);
                Region region = Region.getRegion(signature.region);
                AmazonS3Client client = new AmazonS3Client(sessionCredentials, region);

                mTransferUtility = TransferUtility.builder()
                        .context(mApplicationContext)
                        .awsConfiguration(AWSMobileClient.getInstance().getConfiguration())
                        .s3Client(client)
                        .defaultBucket(signature.bucket)
                        .build();
                mLastSessionTime = System.currentTimeMillis();
                if (!emitter.isDisposed()) {
                    emitter.onNext(true);
                    emitter.onComplete();
                }

            } else {
                if (!emitter.isDisposed()) {
                    emitter.onError(new Exception("获取UploadSignature失败"));
                }
            }
        })
                .subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());

    }

    @Override
    public String generateFileUrl(String objectKey) {
        return mDomain + "/" + objectKey;
    }

    @Override
    public Observable<CloudResult> upload(String filePath) {
        if (filePath == null) {
            return Observable.error(new RxException(ResultCode.OSS_UPDATE_FAIL_CASE_INVALID_FILE, "Upload fail,filePath = null!"));

        }
        if (!new File(filePath).exists()) {
            return Observable.error(new RxException(ResultCode.OSS_UPDATE_FAIL_CASE_INVALID_FILE, "Upload fail,File is not exists!======" + filePath + "====="));
        }

        return handleUpload(filePath).retryWhen(createRetryFunc());
    }

    private Function<Observable<Throwable>, ObservableSource<?>> createRetryFunc() {
        return new Function<Observable<Throwable>, ObservableSource<?>>() {
            int retryTimes = 0;
            int maxRetryTimes = 2;

            @Override
            public ObservableSource<?> apply(Observable<Throwable> throwableObservable) throws Exception {

                return throwableObservable.flatMap((Function<Throwable, ObservableSource<?>>) ex -> {
                    if (ex instanceof AmazonS3Exception) {
                        AmazonS3Exception e = (AmazonS3Exception) ex;
                        //token过期
                        if (retryTimes++ < maxRetryTimes && "InvalidToken".equals(e.getErrorCode()) && 400 == e.getStatusCode()) {
                            LogUtil.e("=======InvalidToken=====retryWhen==");
                            mLastSessionTime = 0;
                            return Observable.timer(100, TimeUnit.MICROSECONDS);
                        }
                    }
                    return Observable.error(ex);
                });
            }
        };
    }

    private Observable<CloudResult> handleUpload(String filePath) {

        Observable<CloudResult> observable = Observable
                .create((ObservableOnSubscribe<CloudResult>) emitter -> {
                    String suffix = FileHelper.getSuffix(filePath);
                    String objectKey = createOssObjectKey(suffix);
                    realDoUpload(emitter, objectKey, filePath);
                })
                .subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());

        return Observable.just(0).flatMap((Function<Integer, ObservableSource<CloudResult>>) integer -> {
            if (mTransferUtility == null || isSessionExpiration()) {
                return initCloudClient().flatMap((Function<Boolean, ObservableSource<CloudResult>>) aBoolean -> observable);
            } else {
                return observable;
            }
        });

    }

    private boolean isSessionExpiration() {
        //由于服务端不一定是按照SESSION_VALID_DURATION来觉得token是否过期，因此设置短一些就失效
        return System.currentTimeMillis() - mLastSessionTime >= SESSION_VALID_DURATION * 1000 / 2;
    }

    private void realDoUpload(final ObservableEmitter<CloudResult> emitter, final String objectKey, final String filePath) {

        final CloudResult result = new CloudResult();
        result.objectKey = objectKey;

        TransferObserver uploadObserver = mTransferUtility.upload(objectKey, new File(filePath));

        // Attach a listener to the observer to get state update and progress notifications
        uploadObserver.setTransferListener(new TransferListener() {

            @Override
            public void onStateChanged(int id, TransferState state) {
                LogUtil.d("id:" + id + " state:" + state.name());
                if (TransferState.COMPLETED == state) {
                    result.isComplete = true;
                    if (!emitter.isDisposed()) {
                        emitter.onNext(result);
                        emitter.onComplete();
                    }

                }
            }

            @Override
            public void onProgressChanged(int id, long currenSize, long totalSize) {
                if (totalSize != 0 && currenSize != totalSize) {
                    int progress = (int) ((float) currenSize / (float) totalSize * 100f);
                    if (progress - result.progress > 10) {
                        result.progress = progress;
                        emitter.onNext(result);
                    }
                }
                LogUtil.d("ID:" + id + " bytesCurrent: " + currenSize
                        + " bytesTotal: " + totalSize + " " + result.progress + "%");
            }

            @Override
            public void onError(int id, Exception ex) {
                // Handle errors
                LogUtil.d("id=" + id + "  ex:" + ex.getMessage());
                if (!emitter.isDisposed()) {
                    emitter.onError(ex);
                }
            }

        });
    }

    @Override
    public Observable<Integer> download(String objectKey, String path) {
        LogUtil.d("doDownload:" + path);
        LogUtil.d("objectKey:" + objectKey);
        LogUtil.d("objectKey to url:" + generateFileUrl(objectKey));

        Observable<Integer> observable = Observable.create((ObservableOnSubscribe<Integer>) emitter -> {
            emitter.onNext(0);

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
            }

            TransferObserver downloadObserver = mTransferUtility.download(objectKey, file);

            // Attach a listener to the observer to get state update and progress notifications
            downloadObserver.setTransferListener(new TransferListener() {

                int preProgress = 0;

                @Override
                public void onStateChanged(int id, TransferState state) {
                    if (TransferState.COMPLETED == state) {
                        if (!emitter.isDisposed()) {
                            emitter.onNext(100);
                            emitter.onComplete();
                        }
                    }
                }

                @Override
                public void onProgressChanged(int id, long currentSize, long totalSize) {
                    int progress = 0;
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
                            emitter.onNext(progress);
                        }
                    }

                    LogUtil.d("   ID:" + id + "   bytesCurrent: " + currentSize + "   bytesTotal: " + totalSize + " " + progress + "%");
                }

                @Override
                public void onError(int id, Exception ex) {
                    // Handle errors
                    if (!emitter.isDisposed()) {
                        emitter.onError(ex);
                    }
                    ex.printStackTrace();
                }

            });

            // If you prefer to poll for the data, instead of attaching a
            // listener, check for the state and progress in the observer.
            if (TransferState.COMPLETED == downloadObserver.getState()) {
                // Handle a completed upload.
            }

            Log.d("Your Activity", "Bytes Transferred: " + downloadObserver.getBytesTransferred());
            Log.d("Your Activity", "Bytes Total: " + downloadObserver.getBytesTotal());

        })
                .subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread());
        return Observable.just(0)
                .flatMap((Function<Integer, ObservableSource<Integer>>) integer -> {
                    if (mTransferUtility == null || isSessionExpiration()) {
                        return initCloudClient().flatMap((Function<Boolean, ObservableSource<Integer>>) aBoolean -> observable);
                    } else {
                        return observable;
                    }
                })
                .retryWhen(createRetryFunc());

    }
}
