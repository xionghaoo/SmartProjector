package com.ubtedu.ukit.bluetooth.ota;

import android.app.IntentService;
import android.content.Intent;
import android.text.TextUtils;

import androidx.annotation.Nullable;

import com.ubtedu.ukit.application.UKitApplication;
import com.ubtedu.ukit.common.files.FileHelper;
import com.ubtedu.ukit.common.utils.AssetCopier;

import java.io.File;

/**
 * @Author naOKi
 * @Date 2019/01/21
 **/
public class OtaExtractInternalService extends IntentService {

    public OtaExtractInternalService() {
        super(OtaExtractInternalService.class.getSimpleName());
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        extractFromAsset("ota");
    }

    private static boolean extractFromAsset(String path) {
        if(TextUtils.isEmpty(path)) {
            return false;
        }
        try {
            File destFile = new File(FileHelper.getOtaPath());
            AssetCopier copier = new AssetCopier(UKitApplication.getInstance());
            copier.copyDirectory(path, destFile);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
