/**
 * @2018 UBTECH Robotics Corp. All rights reserved (C)
 **/
package com.ubtedu.ukit.project.blockly;

import android.net.Uri;

import com.ubtedu.alpha1x.utils.LogUtil;
import com.ubtedu.alpha1x.utils.SharedPreferenceUtils;
import com.ubtedu.alpha1x.utils.StringUtil;
import com.ubtedu.alpha1x.utils.ZipUtil2;
import com.ubtedu.base.net.rxretrofit.exception.RxException;
import com.ubtedu.base.net.rxretrofit.subscriber.SimpleRxSubscriber;
import com.ubtedu.ukit.application.UKitApplication;
import com.ubtedu.ukit.common.files.FileHelper;
import com.ubtedu.ukit.common.ota.RxOtaHelper;
import com.ubtedu.ukit.common.thread.ThreadPoolUtil;
import com.ubtedu.ukit.common.utils.AssetCopier;
import com.ubtedu.ukit.common.utils.ResourceUtils;

import java.io.File;
import java.io.IOException;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.ObservableSource;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

/**
 * @author binghua.qin
 * @date 2019/02/21
 */
public class BlocklyPresenter extends BlocklyContracts.Presenter {

    private final static String TAG = "BLOCKLY-WEBVIEW";

    private final static String BLOCKLY_ASSET_INDEX = "file:///android_asset/blockly/index.html";
    public final static String BLOCKLY_ASSET_VERSION = "blocklyVersion";
    public final static String BLOCKLY_ASSET_ZIP = "blockly.zip";
    public final static String SDCARD_BLOCKLY_VERSION = FileHelper.join(FileHelper.getPublicBlocklyPath(), "version");

    private SimpleRxSubscriber<String> mObserver;
    private CompositeDisposable mCompositeDisposable;

    private final static Object mInitLock = new Object();

    public BlocklyPresenter() {
        mCompositeDisposable = new CompositeDisposable();
    }

    @Override
    public void loadBlockly() {
        ThreadPoolUtil.execute(new Runnable() {
            @Override
            public void run() {
                loadBlocklyBackground();
            }
        });
    }

    private void loadBlocklyBackground() {
        final long loadBlocklyStartTime = System.currentTimeMillis();
        LogUtil.d(TAG, "初始化Blockly开始 ---> " + loadBlocklyStartTime);
        if (mObserver == null) {
            mObserver = new SimpleRxSubscriber<String>() {
                @Override
                public void onSubscribe(Disposable d) {
                    super.onSubscribe(d);
                    mCompositeDisposable.add(d);
                }

                @Override
                public void onNext(String s) {
                    super.onNext(s);

                    if (getView() != null) {
                        String url = Uri.fromFile(new File(s)).toString();

                        getView().loadUrl(appendUrlTimestamp(url));
                    }
                    LogUtil.d(TAG, "初始化Blockly成功 ---> " + (System.currentTimeMillis() - loadBlocklyStartTime) + "ms");
                }

                @Override
                public void onError(RxException e) {
                    super.onError(e);
                    e.printStackTrace();
                    if (getView() != null) {
                        getView().loadUrl(appendUrlTimestamp(BLOCKLY_ASSET_INDEX));
                    }
                    LogUtil.d(TAG, "初始化Blockly失败 ---> " + (System.currentTimeMillis() - loadBlocklyStartTime) + "ms");
                }
            };
        }
        initBlockly()
                .subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(mObserver);
    }

    @Override
    public void release() {
        super.release();
        mCompositeDisposable.clear();
    }

    @Override
    public Observable<String> initBlockly() {
        synchronized (mInitLock) {
            //使用同步锁mInitLock锁住initBlockly，copyAssetsBlockly和copyOtaBlockly，只要有一个正在解压的任务在进行，另一个initBlockly任务就会处于等待状态
            //如果index.html文件存在，需要检查是否最新版本
            final String blocklyVersion = SharedPreferenceUtils.getInstance(mContext).getStringValue(RxOtaHelper.BLOCKLY_VERSION_CODE);
            final String blocklyMd5 = SharedPreferenceUtils.getInstance(mContext).getStringValue(RxOtaHelper.BLOCKLY_VERSION_FILE_MD5);
            final String blocklyOtaZipFile = SharedPreferenceUtils.getInstance(mContext).getStringValue(RxOtaHelper.BLOCKLY_VERSION_FILE);
            boolean isBlocklyFileExist = new File(RxOtaHelper.BLOCKLY_INDEX).exists();
            boolean isShouldUnzip = false;
            if (isBlocklyFileExist) {
                if (blocklyVersion != null) {
                    String verifyFile = getVerifyFilePath(blocklyVersion, blocklyMd5);
                    //在解压Blockly的时候，需要在Blockly目录里写入verifyFile，所以可以通过该方式判断当前的Blockly需不需要升级。
                    if (!new File(verifyFile).exists()) {
                        isShouldUnzip = blocklyOtaZipFile != null && new File(blocklyOtaZipFile).exists();
                    }
                }

            } else {
                if (blocklyVersion != null) {
                    isShouldUnzip = blocklyOtaZipFile != null && new File(blocklyOtaZipFile).exists();
                }
            }

            if (isShouldUnzip) {
                LogUtil.d(TAG, "解压缩Blockly ---> Ota更新");
                return unzipOtaBlockly(blocklyVersion, blocklyMd5, blocklyOtaZipFile)
                        .flatMap(new Function<String, ObservableSource<String>>() {
                            @Override
                            public ObservableSource<String> apply(String s) throws Exception {
                                return compareVersionShouldUseAssetsBlockly(blocklyVersion, blocklyMd5);
                            }
                        });
            } else {
                if (isBlocklyFileExist) {
                    LogUtil.d(TAG, "无需解压处理");
                    return compareVersionShouldUseAssetsBlockly(blocklyVersion, blocklyMd5);
                } else {
                    LogUtil.d(TAG, "解压缩Blockly ---> 应用内置");
                    return unzipAssetsBlockly();
                }
            }
        }
    }

    private Observable<String> compareVersionShouldUseAssetsBlockly(final String otaVersion, final String md5) {
        LogUtil.d(TAG, "compareVersionShouldUseAssetsBlockly");
        return Observable
                .create(new ObservableOnSubscribe<Boolean>() {
                    @Override
                    public void subscribe(ObservableEmitter<Boolean> emitter) throws Exception {
                        int assetsBlocklyVersion = readAssetsBlocklyVersionCode();
                        int sdcardBlocklyVersion = readSdcardBlocklyVersionCode();
                        LogUtil.d(TAG, "assetsBlocklyVersion:" + assetsBlocklyVersion + " sdcardBlocklyVersion:" + sdcardBlocklyVersion);
                        if (!emitter.isDisposed()) {
                            emitter.onNext(assetsBlocklyVersion > sdcardBlocklyVersion);
                            emitter.onComplete();
                        }

                    }
                })
                .flatMap(new Function<Boolean, ObservableSource<String>>() {
                    @Override
                    public ObservableSource<String> apply(Boolean shouldUseAssetsBlockly) throws Exception {
                        LogUtil.d(TAG, "shouldUseAssetsBlockly:" + shouldUseAssetsBlockly);
                        if (shouldUseAssetsBlockly) {
                            return unzipAssetsBlockly()
                                    .map(new Function<String, String>() {
                                        @Override
                                        public String apply(String s) throws Exception {
                                            //如果ota的版本比assets的版本低，使用assets覆盖掉后，需要记录ota的版本，避免重复执行该步骤
                                            FileHelper.createFile(FileHelper.getPublicBlocklyPath(), createVerifyFileName(otaVersion, md5), null);
                                            FileHelper.createFile(FileHelper.getPublicBlocklyPath(), "otaVersionLessThanAssetsVersion", null);
                                            return s;
                                        }
                                    });
                        } else {
                            return Observable.just(RxOtaHelper.BLOCKLY_INDEX);
                        }
                    }
                })
                .subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * verifyFile由版本和MD组成
     */
    private String getVerifyFilePath(String version, String md5) {
        return FileHelper.join(FileHelper.getPublicBlocklyPath(), createVerifyFileName(version, md5));
    }

    private String createVerifyFileName(String version, String md5) {
        return version + "_" + md5;
    }

    private int readAssetsBlocklyVersionCode() {
        return versionName2Code(ResourceUtils.readAssets2String(UKitApplication.getInstance(), BLOCKLY_ASSET_VERSION, "utf-8"));
    }

    private int readSdcardBlocklyVersionCode() {
        return versionName2Code(FileHelper.readTxtFile(SDCARD_BLOCKLY_VERSION));
    }

    private int versionName2Code(String version) {

        return StringUtil.getNumberFromString(version);
    }

    /**
     * 从assets目录copy app内置的blockly解压到sd卡
     */
    private Observable<String> unzipAssetsBlockly() {
        LogUtil.i(TAG, "start unzip assets blockly");
        return Observable
                .create(new ObservableOnSubscribe<String>() {
                    @Override
                    public void subscribe(ObservableEmitter<String> emitter) throws Exception {
                        doUnzipAssetsBlockly(emitter);
                    }
                })
                .subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());

    }

    private void doUnzipAssetsBlockly(ObservableEmitter<String> emitter) {
        synchronized (mInitLock) {
            //使用同步锁mInitLock锁住initBlockly，copyAssetsBlockly和copyOtaBlockly，只要有一个正在解压的任务在进行，另一个initBlockly任务就会处于等待状态
            AssetCopier copier = new AssetCopier(mContext);
            File destZipFile = new File(FileHelper.join(FileHelper.getCacheRootPath(), BLOCKLY_ASSET_ZIP));

            if (destZipFile.exists()) {
                FileHelper.removeDir(destZipFile);
            }
            try {
                long time = System.currentTimeMillis();
                copier.copyFile(BLOCKLY_ASSET_ZIP, destZipFile);
                File blocklyDir = new File(FileHelper.getPublicPath());
                boolean success = ZipUtil2.unzipFile(destZipFile, blocklyDir);
                LogUtil.i(TAG, "end unzip assets blockly:" + success + "  time:" + (System.currentTimeMillis() - time));
                if (!emitter.isDisposed()) {
                    if (success) {
                        emitter.onNext(RxOtaHelper.BLOCKLY_INDEX);
                        emitter.onComplete();
                    } else {
                        emitter.onError(new IOException("unzip blockly fail~"));
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
                if (!emitter.isDisposed()) {
                    emitter.onError(e);
                }
            }
        }
    }

    /**
     * 按照unzipAssetsBlockly的实现写了unzipOtaBlockly
     */
    private Observable<String> unzipOtaBlockly(final String version, final String md5, final String zipPath) {
        return Observable
                .create(new ObservableOnSubscribe<String>() {
                    @Override
                    public void subscribe(ObservableEmitter<String> emitter) {
                        copyOtaBlockly(emitter, version, md5, zipPath);
                    }
                })
                .subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 按照copyAssetsBlockly和unzipBlockly的实现写了copyOtaBlockly
     */
    private void copyOtaBlockly(ObservableEmitter<String> emitter, final String version, final String md5, final String zipPath) {
        synchronized (mInitLock) {
            //使用同步锁mInitLock锁住initBlockly，copyAssetsBlockly和copyOtaBlockly，只要有一个正在解压的任务在进行，另一个initBlockly任务就会处于等待状态
            File destFile = new File(FileHelper.getPublicBlocklyPath());
            if (destFile.exists()) {
                FileHelper.removeDir(destFile);
            }
            boolean success = ZipUtil2.unzipFile(zipPath, FileHelper.getPublicPath());
            if (success) {
                LogUtil.i(TAG, "end unzip ota blockly:success");
                FileHelper.createFile(FileHelper.getPublicBlocklyPath(), createVerifyFileName(version, md5), null);
            }
            if (!emitter.isDisposed()) {
                if (success) {
                    emitter.onNext(RxOtaHelper.BLOCKLY_INDEX);
                    emitter.onComplete();
                } else {
                    emitter.onError(new Throwable("Zip action fail!"));
                }
            }
        }
    }


    private String appendUrlTimestamp(String url) {
        return url + "?timestamp=" + System.currentTimeMillis();
    }
}
