package com.ubtedu.ukit.common.ota;

import android.text.TextUtils;

import com.ubtedu.alpha1x.utils.LogUtil;
import com.ubtedu.alpha1x.utils.MD5Util;
import com.ubtedu.alpha1x.utils.SharedPreferenceUtils;
import com.ubtedu.ukit.BuildConfig;
import com.ubtedu.ukit.application.AppConfigs;
import com.ubtedu.ukit.application.ServerConfig;
import com.ubtedu.ukit.application.UKitApplication;
import com.ubtedu.ukit.common.files.FileHelper;
import com.ubtedu.ukit.common.http.UKitHeadersInterceptor;
import com.ubtedu.ukit.common.utils.VersionUtil;
import com.ubtedu.ukit.menu.settings.Settings;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.ObservableSource;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;

public class RxOtaHelper {
    public static final String MAIN_BOARD_VERSION_CODE = "main_board_version_code";
    public static final String MAIN_BOARD_VERSION_FILE = "main_board_version_file";
    public static final String MAIN_BOARD_VERSION_FILE_MD5 = "main_board_version_file_md5";
    public static final String MAIN_BOARD_VERSION_URL = "main_board_version_url";
    public static final String MAIN_BOARD_VERSION_FILE_SIZE = "main_board_version_file_size";

    public static final String STEERING_VERSION_CODE = "Steering_version_code";
    public static final String STEERING_VERSION_FILE = "Steering_version_file";
    public static final String STEERING_VERSION_FILE_MD5 = "Steering_version_file_md5";
    public static final String STEERING_VERSION_URL = "Steering_version_url";
    public static final String STEERING_VERSION_FILE_SIZE = "Steering_version_file_size";

    public static final String BLOCKLY_VERSION_CODE = "blockly_version_code";
    public static final String BLOCKLY_VERSION_FILE = "blockly_version_file";
    public static final String BLOCKLY_VERSION_FILE_MD5 = "blockly_version_file_md5";

    public static final String APP_VERSION_CODE = "app_version_code";
    //////////////// 传感器升级程序 ////////////////////////ini
    /**
     * 暂时有8种传感器，如果每种传感器配置两个字段，则需要16个，所以利用
     */
    public static final String COMPONENT_VERSION = "Version";
    public static final String COMPONENT_FILE_PATH = "FilePath";
    public static final String COMPONENT_FILE_MD5 = "FileMd5";
    public static final String COMPONENT_FILE_URL = "FileUrl";
    public static final String COMPONENT_FILE_SIZE = "FileSize";

    public final static String BLOCKLY_INDEX = FileHelper.getPublicBlocklyPath() + File.separator + "index.html";
    private static final String OTA_URL = "%s/v1/upgrade-rest/component/version?productName=%s&projectCode=%s&componentNames=%s&platformName=%s&appVersion=%s";

    private static OkHttpClient okHttpClient;


    private static final String productName = "uKitEdu";
    private static final String platform = "Android";
    private static final String appVersion = VersionUtil.generateOtaVersionName();
    private static SharedPreferenceUtils sPreferenceUtils;

    static {
        initOkHttpClient();
        sPreferenceUtils = SharedPreferenceUtils.getInstance(UKitApplication.getInstance());
        checkOtaConfig();
    }

    private static void checkOtaConfig() {
        if (sPreferenceUtils == null) {
            return;
        }
        String appVersionCode = String.valueOf(BuildConfig.VERSION_CODE);
        String savedAppVersionCode = sPreferenceUtils.getStringValue(RxOtaHelper.APP_VERSION_CODE);
        if (!TextUtils.equals(appVersionCode, savedAppVersionCode)) {
            //写入新的AppVersion信息并移除所有已保存的配置信息
            sPreferenceUtils.setValue(APP_VERSION_CODE, appVersionCode);
            clearOtaUpgradeInfo(OtaComponent.Mainboard.getName());
            clearOtaUpgradeInfo(OtaComponent.Mainboard2.getName());
            clearOtaUpgradeInfo(OtaComponent.MainboardVB.getName());
            clearOtaUpgradeInfo(OtaComponent.Servo.getName());
            clearOtaUpgradeInfo(OtaComponent.Blockly.getName());
            for (OtaComponent otaComponent : OtaComponent.values()) {
                clearOtaUpgradeInfo(otaComponent.getName());
            }
        }
    }

    private static void initOkHttpClient() {
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        builder.connectTimeout(10, TimeUnit.SECONDS);
        builder.readTimeout(20, TimeUnit.SECONDS);
        builder.writeTimeout(20, TimeUnit.SECONDS);
        builder.retryOnConnectionFailure(true);
        if (AppConfigs.HTTP_LOG_ENABLE) {
            HttpLoggingInterceptor.Level level = HttpLoggingInterceptor.Level.BODY;
            //新建log拦截器
            HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor(new HttpLoggingInterceptor.Logger() {
                @Override
                public void log(String message) {
                    LogUtil.i("RxClient", message);
                }
            });
            loggingInterceptor.setLevel(level);
            builder.addInterceptor(loggingInterceptor);
        }

        builder.addInterceptor(new UKitHeadersInterceptor());
        okHttpClient = builder.build();
    }

    private static String stringJoin(OtaComponent... components) {
        StringBuilder sb = new StringBuilder();
        if (components != null && components.length > 0) {
            boolean first = true;
            for (OtaComponent component : components) {
                if (first) {
                    first = false;
                } else {
                    sb.append(",");
                }
                sb.append(component.getName());
            }
        }
        return sb.toString();
    }


    public static Observable<OtaResponse> checkAllComponentsUpgrade() {
        OtaComponent[] allComponents = OtaComponent.values();
        return checkComponentUpgrade(allComponents);
    }

    public static Observable<OtaResponse> checkAllComponentsUpgradeWithoutBlockly() {
        OtaComponent[] allComponents = OtaComponent.values();
        OtaComponent[] otaComponents = new OtaComponent[allComponents.length - 1];
        int i = 0;
        for (OtaComponent component : OtaComponent.values()) {
            if (component != OtaComponent.Blockly) {
                otaComponents[i++] = component;
            }
        }

        return checkComponentUpgrade(otaComponents);
    }


    private static String getOtaProjectCode() {
        return Settings.getRegion().name;
    }

    private static void requestOtaInfo(String componentStr, final ObservableEmitter<String> emitter) {
        LogUtil.d("componentStr:" + componentStr);
        String projectCode = getOtaProjectCode();
        String requestUrl = String.format(
                OTA_URL,
                //需要动态获取，虽然目前不区分国内外域名
                ServerConfig.getOtaServer(),
                productName,
                projectCode,
                componentStr,
                platform,
                appVersion);

        Request request = new Request.Builder()
                .get()
                .url(requestUrl)
                .build();
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                // ignore
                LogUtil.e(e.getMessage());
                if (!emitter.isDisposed()) {
                    emitter.onError(e);
                }
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (!emitter.isDisposed()) {
                    emitter.onNext(response.body().string());
                    emitter.onComplete();
                }
            }
        });
    }

    /**
     * OTA升级检测
     *
     * @param components 需要检测更新的部件
     */
    public static Observable<OtaResponse> checkComponentUpgrade(OtaComponent... components) {
        if (components == null || components.length == 0) {
            return Observable.error(new Exception("components == null || components.length == 0"));
        }
        final String componentStr = stringJoin(components);

        return Observable
                .create(new ObservableOnSubscribe<String>() {
                    @Override
                    public void subscribe(ObservableEmitter<String> emitter) throws Exception {
                        requestOtaInfo(componentStr, emitter);
                    }
                })
                .map(new Function<String, List<UpgradeInfo>>() {
                    @Override
                    public List<UpgradeInfo> apply(String s) throws Exception {
                        List<UpgradeInfo> responses = new ArrayList<>();
                        JSONArray jsonArray = new JSONArray(s);
                        if (jsonArray.length() > 0) {
                            UpgradeInfo blockly = null;
                            for (int i = 0; i < jsonArray.length(); i++) {
                                UpgradeInfo upgradeInfo = new UpgradeInfo();
                                JSONObject jsonObject = jsonArray.getJSONObject(i);
                                upgradeInfo.componentName = jsonObject.getString("componentName");
                                upgradeInfo.versionName = jsonObject.getString("versionName");
                                upgradeInfo.packageUrl = jsonObject.getString("packageUrl");
                                upgradeInfo.packageMd5 = jsonObject.getString("packageMd5");
                                upgradeInfo.isForced = jsonObject.getBoolean("isForced");
                                if (OtaComponent.Blockly.getName().equals(upgradeInfo.componentName)) {
                                    blockly = upgradeInfo;
                                } else {
                                    responses.add(upgradeInfo);
                                }
                            }
                            if (blockly != null) {
                                responses.add(blockly);
                            }
                        }
                        clearOtaUpgradeInfos(responses);
                        return responses;
                    }
                }).flatMap(new Function<List<UpgradeInfo>, ObservableSource<OtaResponse>>() {
                    @Override
                    public ObservableSource<OtaResponse> apply(List<UpgradeInfo> upgradeInfos) throws Exception {
                        return downloadComponentFirmwares(upgradeInfos);
                    }
                })

                .flatMap(new Function<OtaResponse, ObservableSource<OtaResponse>>() {
                    @Override
                    public ObservableSource<OtaResponse> apply(OtaResponse response) throws Exception {
                        return recordOtaUpgradeInfos(response);
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());

    }

    private static void clearOtaUpgradeInfos(List<UpgradeInfo> upgradeInfos) {
        Set<String> componentNames = new HashSet<>();
        for (OtaComponent component : OtaComponent.values()) {
            componentNames.add(component.getName());
        }
        if (upgradeInfos != null) {
            for (UpgradeInfo upgradeInfo : upgradeInfos) {
                componentNames.remove(upgradeInfo.componentName);
            }
            for (String name : componentNames) {
                clearOtaUpgradeInfo(name);
            }
        }
    }

    private static void clearOtaUpgradeInfo(String name) {
        if (OtaComponent.Blockly.getName().equals(name)) {
            sPreferenceUtils.removeKey(BLOCKLY_VERSION_FILE);
            sPreferenceUtils.removeKey(BLOCKLY_VERSION_CODE);
            //需要也记录MD5,避免OTA配置一样的版本号
            sPreferenceUtils.removeKey(BLOCKLY_VERSION_FILE_MD5);
        } else if (OtaComponent.Mainboard.getName().equals(name)) {
            sPreferenceUtils.removeKey(MAIN_BOARD_VERSION_FILE);
            sPreferenceUtils.removeKey(MAIN_BOARD_VERSION_CODE);
            sPreferenceUtils.removeKey(MAIN_BOARD_VERSION_URL);
            sPreferenceUtils.removeKey(MAIN_BOARD_VERSION_FILE_MD5);
            sPreferenceUtils.removeKey(MAIN_BOARD_VERSION_FILE_SIZE);
        } else if (OtaComponent.Mainboard2.getName().equals(name) || OtaComponent.MainboardVB.getName().equals(name)) {
            sPreferenceUtils.removeKey(name + MAIN_BOARD_VERSION_FILE);
            sPreferenceUtils.removeKey(name + MAIN_BOARD_VERSION_CODE);
            sPreferenceUtils.removeKey(name + MAIN_BOARD_VERSION_URL);
            sPreferenceUtils.removeKey(name + MAIN_BOARD_VERSION_FILE_MD5);
            sPreferenceUtils.removeKey(name + MAIN_BOARD_VERSION_FILE_SIZE);
        } else if (OtaComponent.Infrared.getName().equals(name)
                || OtaComponent.Touch.getName().equals(name)
                || OtaComponent.Gyroscope.getName().equals(name)
                || OtaComponent.Light.getName().equals(name)
                || OtaComponent.Speaker.getName().equals(name)
                || OtaComponent.LEDBox.getName().equals(name)) {
            // 传感器升级
            sPreferenceUtils.removeKey(name + COMPONENT_FILE_PATH);
            sPreferenceUtils.removeKey(name + COMPONENT_VERSION);
            sPreferenceUtils.removeKey(name + COMPONENT_FILE_URL);
            sPreferenceUtils.removeKey(name + COMPONENT_FILE_MD5);
            sPreferenceUtils.removeKey(name + COMPONENT_FILE_SIZE);
        } else if (OtaComponent.Servo.getName().equals(name)) {
            // 舵机升级
            sPreferenceUtils.removeKey(STEERING_VERSION_FILE);
            sPreferenceUtils.removeKey(STEERING_VERSION_CODE);
            sPreferenceUtils.removeKey(STEERING_VERSION_URL);
            sPreferenceUtils.removeKey(STEERING_VERSION_FILE_MD5);
            sPreferenceUtils.removeKey(STEERING_VERSION_FILE_SIZE);
        }
    }

    private static Observable<OtaResponse> recordOtaUpgradeInfos(final OtaResponse response) {
        if (response.isSuccess) {
            if (OtaComponent.Mainboard.equals(response.component)) {
                // 主板升级
                // 保存版本信息
                sPreferenceUtils.setValue(MAIN_BOARD_VERSION_FILE, response.filePath);
                sPreferenceUtils.setValue(MAIN_BOARD_VERSION_CODE, response.version);
                sPreferenceUtils.setValue(MAIN_BOARD_VERSION_URL, response.url);
                sPreferenceUtils.setValue(MAIN_BOARD_VERSION_FILE_MD5, response.md5);
                sPreferenceUtils.setValue(MAIN_BOARD_VERSION_FILE_SIZE, response.size);
            } else if (OtaComponent.Mainboard2.equals(response.component) || OtaComponent.MainboardVB.equals(response.component)) {
                sPreferenceUtils.setValue(response.component.getName() + MAIN_BOARD_VERSION_FILE, response.filePath);
                sPreferenceUtils.setValue(response.component.getName() + MAIN_BOARD_VERSION_CODE, response.version);
                sPreferenceUtils.setValue(response.component.getName() + MAIN_BOARD_VERSION_URL, response.url);
                sPreferenceUtils.setValue(response.component.getName() + MAIN_BOARD_VERSION_FILE_MD5, response.md5);
                sPreferenceUtils.setValue(response.component.getName() + MAIN_BOARD_VERSION_FILE_SIZE, response.size);
            } else if (OtaComponent.Servo.equals(response.component)) {
                // 舵机升级
                sPreferenceUtils.setValue(STEERING_VERSION_FILE, response.filePath);
                sPreferenceUtils.setValue(STEERING_VERSION_CODE, response.version);
                sPreferenceUtils.setValue(STEERING_VERSION_URL, response.url);
                sPreferenceUtils.setValue(STEERING_VERSION_FILE_MD5, response.md5);
                sPreferenceUtils.setValue(STEERING_VERSION_FILE_SIZE, response.size);
            } else if (OtaComponent.Infrared.equals(response.component)
                    || OtaComponent.Touch.equals(response.component)
                    || OtaComponent.Gyroscope.equals(response.component)
                    || OtaComponent.Light.equals(response.component)
                    || OtaComponent.Speaker.equals(response.component)
                    || OtaComponent.LEDBox.equals(response.component)) {
                // 传感器升级
                sPreferenceUtils.setValue(response.component.getName() + COMPONENT_FILE_PATH, response.filePath);
                sPreferenceUtils.setValue(response.component.getName() + COMPONENT_VERSION, response.version);
                sPreferenceUtils.setValue(response.component.getName() + COMPONENT_FILE_URL, response.url);
                sPreferenceUtils.setValue(response.component.getName() + COMPONENT_FILE_MD5, response.md5);
                sPreferenceUtils.setValue(response.component.getName() + COMPONENT_FILE_SIZE, response.size);
            } else if (OtaComponent.Blockly.equals(response.component)) {
                LogUtil.i("blockly filePath:" + response.filePath);
                // Blockly升级
                sPreferenceUtils.setValue(BLOCKLY_VERSION_FILE, response.filePath);
                sPreferenceUtils.setValue(BLOCKLY_VERSION_CODE, response.version);
                //需要也记录MD5,避免OTA配置一样的版本号
                sPreferenceUtils.setValue(BLOCKLY_VERSION_FILE_MD5, response.md5);
            }

        }
        return Observable.just(response);
    }

    private static Observable<OtaResponse> downloadComponentFirmwares(final List<UpgradeInfo> upgradeInfos) {
        return Observable
                .create(new ObservableOnSubscribe<OtaResponse>() {
                    @Override
                    public void subscribe(ObservableEmitter<OtaResponse> emitter) throws Exception {
                        OtaEmitter<OtaResponse> otaEmitter = new OtaEmitter<>(upgradeInfos.size(), emitter);
                        if (upgradeInfos.size() > 0) {
                            for (UpgradeInfo info : upgradeInfos) {
                                downloadComponentFirmware(info, otaEmitter);
                            }
                        } else {
                            //如果是空的，需要发一条数据
                            OtaResponse response = new OtaResponse(false);
                            otaEmitter.onNext(response);
                            otaEmitter.onComplete();
                        }

                    }
                });
    }

    /**
     * 下载机器人部件更新的过程，首先检查本地是否已经下载过了，如果已下载，则不会再次下载
     */
    private static void downloadComponentFirmware(final UpgradeInfo upgradeInfo, final OtaEmitter<OtaResponse> emitter) {
        final OtaComponent comp = OtaComponent.valueOf(upgradeInfo.componentName);
        String name = upgradeInfo.packageUrl.substring(upgradeInfo.packageUrl.lastIndexOf("/") + 1);
        String componentPath = FileHelper.join(FileHelper.getOtaPath(), name);
        final File saveFile = new File(componentPath);
        final OtaResponse response = new OtaResponse(true);
        response.component = comp;
        response.version = upgradeInfo.versionName;
        response.filePath = saveFile.getAbsolutePath();
        response.isForced = upgradeInfo.isForced;
        response.md5 = upgradeInfo.packageMd5;
        response.url = upgradeInfo.packageUrl;
        if (checkFile(saveFile, upgradeInfo.packageMd5)) {
            // 如果文件存在，且MD5校验一致，则不用下载，直接回调结果
            response.size = (int)saveFile.length();
            emitter.onNext(response);
            return;
        }
        Request request = new Request.Builder().get().url(upgradeInfo.packageUrl).build();
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                // ignore
                LogUtil.e(e.getMessage());
                response.isSuccess = false;
                emitter.onNext(response);
            }

            @Override
            public void onResponse(Call call, Response r) throws IOException {
                InputStream is = r.body().byteStream();
                if (saveFile(is, saveFile) && checkFile(saveFile, upgradeInfo.packageMd5)) {
                    // 文件下载成功，且MD5校验一致，则回调结果
                    response.isSuccess = true;
                    response.size = (int)saveFile.length();
                } else {
                    response.isSuccess = false;
                }
                emitter.onNext(response);
                r.close();
            }
        });
    }

    /**
     * 检查文件与MD5是否一致
     *
     * @param saveFile  需要比较的文件
     * @param md5Digest 期望的MD5值
     * @return
     */
    private static boolean checkFile(File saveFile, String md5Digest) {
        try {
            return !TextUtils.isEmpty(md5Digest) && saveFile.exists() && md5Digest.equals(MD5Util.encodeFileMd5(saveFile.getAbsolutePath()));
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 将输入流内容保存为
     *
     * @param inputStream 输入流
     * @param saveFile    保存文件名
     * @return
     * @throws Exception
     */
    private static boolean saveFile(InputStream inputStream, File saveFile) {
        boolean result = false;
        if (!saveFile.getParentFile().exists()) {
            saveFile.getParentFile().mkdirs();
        }
        byte[] buffer = new byte[8 * 1024];
        int readLen;
        try (FileOutputStream fos = new FileOutputStream(saveFile)) {

            while ((readLen = inputStream.read(buffer, 0, buffer.length)) != -1) {
                fos.write(buffer, 0, readLen);
            }
            fos.flush();
            result = true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }
}
