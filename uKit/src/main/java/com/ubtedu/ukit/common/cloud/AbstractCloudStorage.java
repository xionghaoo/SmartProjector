package com.ubtedu.ukit.common.cloud;

import com.ubtedu.alpha1x.utils.GsonUtil;
import com.ubtedu.alpha1x.utils.LogUtil;
import com.ubtedu.ukit.common.http.RxClientFactory;
import com.ubtedu.ukit.common.utils.UuidUtil;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Response;

/**
 * @Author qinicy
 * @Date 2020-02-06
 **/
public abstract class AbstractCloudStorage implements ICloudStorage {
    protected static final String PROJECT_NAME = "edu/uKit2.0";

    /**
     * 同步请求云存储参数
     * @param cloudType
     * @param storageType
     * @param validTime
     * @return
     */
    protected UploadSignature requestUploadSignatureInfo(int cloudType,int storageType,int validTime) {
        Call<UploadSignature> call = RxClientFactory.getFileService().getUploadSignature(
                PROJECT_NAME, cloudType, storageType, validTime);
        UploadSignature signature = null;
        try {
            Response<UploadSignature> response = call.execute();
            if (response.isSuccessful() && response.code() == 200) {
                signature = response.body();
                LogUtil.d("UploadSignature:" + GsonUtil.get().toJson(signature));
                return signature;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
    protected String createOssObjectKey(String suffix) {
        return PROJECT_NAME + "/" + UuidUtil.createUUID() + suffix;
    }
}
