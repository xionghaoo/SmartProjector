/**
 * @2018 UBTECH Robotics Corp. All rights reserved (C)
 **/
package com.ubtedu.ukit.common.image;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;

import com.steelkiwi.cropiwa.CropIwaView;
import com.steelkiwi.cropiwa.config.CropIwaSaveConfig;
import com.ubtedu.alpha1x.utils.LogUtil;
import com.ubtedu.ukit.R;
import com.ubtedu.ukit.common.base.UKitBaseActivity;
import com.ubtedu.ukit.common.files.FileHelper;

import java.io.File;


/**
 * @Author qinicy
 * @Date 2018/11/16
 **/
public class ImageCropActivity extends UKitBaseActivity {
    public final static String IMAGE_PATH_KEY = "image_path";
    public final static String CROP_CONFIG_KEY = "crop_ratio";
    public static final int REQUEST_CODE_CROP_IMAGE = 998;
    public static final float CROP_BASE_WIDTH = 480f;
    private View mBackBtn;
    private View mSubmitBtn;
    private CropIwaView mImageView;
    private ImageCropConfig mCropConfig;

    public static void open(Activity context, Uri imagePath, ImageCropConfig config) {
        if (context != null && imagePath != null) {
            Intent intent = new Intent(context, ImageCropActivity.class);
            intent.putExtra(IMAGE_PATH_KEY, imagePath);
            intent.putExtra(CROP_CONFIG_KEY, config);
            context.startActivityForResult(intent, REQUEST_CODE_CROP_IMAGE);
        }
    }


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_crop);

        mCropConfig = (ImageCropConfig) getIntent().getSerializableExtra(CROP_CONFIG_KEY);
        if (mCropConfig == null) {
            mCropConfig = ImageCropConfigFactory.createProjectImageCropConfig();
        }
        Uri imageUri = getIntent().getParcelableExtra(IMAGE_PATH_KEY);
        if (imageUri == null) {
            finish();
            return;
        }

        mBackBtn = findViewById(R.id.image_crop_back_btn);
        mSubmitBtn = findViewById(R.id.image_crop_submit_btn);
        bindSafeClickListener(mBackBtn);
        bindSafeClickListener(mSubmitBtn);

        mImageView = findViewById(R.id.image_crop_iv);

        mImageView.setImageUri(imageUri);
        mImageView.configureImage()
                .setImageScaleEnabled(mCropConfig.isImageScaleEnabled)
                .setImageTranslationEnabled(mCropConfig.isImageTranslationEnabled)
                .setMaxScale(mCropConfig.maxScale)
                .setMinScale(mCropConfig.minScale)
                .apply();

        mImageView.configureOverlay()
                .setAspectRatio(mCropConfig.ratio)
                .setCropScale(mCropConfig.cropScale)
                .setCalculateFromWidth(mCropConfig.isCalculateFromWidth)
                .setDynamicCrop(mCropConfig.isDynamicCrop)
                .setCornerStrokeWidth(getResources().getDimensionPixelOffset(R.dimen.ubt_dimen_12px))
                .setBorderStrokeWidth(getResources().getDimensionPixelOffset(R.dimen.ubt_dimen_6px))
                .setGridColor(Color.parseColor("#cccccc"))
                .setBorderColor(Color.parseColor("#cccccc"))
                .setCornerColor(Color.parseColor("#cccccc"))
                .apply();

    }


    private void onSubmit() {
        final String imagePath = FileHelper.getImageCacheDirectoryPath() + File.separator + ImageChooseActivity.IMAGE_CAPTURE_NAME + "-" + System.currentTimeMillis() + ".jpg";
        File file = new File(imagePath);
        if (!file.exists()) {
            try {
                if (!file.getParentFile().exists()) {
                    file.getParentFile().mkdirs();
                }
                boolean success = file.createNewFile();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        mImageView.crop(new CropIwaSaveConfig.Builder()
                .saveToFile(Uri.fromFile(new File(imagePath)))
                .setCompressFormat(mCropConfig.compressFormat == ImageCropConfig.COMPRESS_FORMAT_JPEG ? Bitmap.CompressFormat.JPEG : Bitmap.CompressFormat.PNG)
                .setSize(mCropConfig.width, mCropConfig.height) //Optional. If not specified, SRC dimensions will be used
                .setQuality(mCropConfig.quality) //Hint for lossy compression formats
                .build());
        mImageView.setCropSaveCompleteListener(new CropIwaView.CropSaveCompleteListener() {
            @Override
            public void onCroppedRegionSaved(Uri bitmapUri) {
                LogUtil.d(bitmapUri.toString());
                Intent data = new Intent();

                data.putExtra(IMAGE_PATH_KEY, imagePath);
                setResult(RESULT_OK, data);
                finish();
            }
        });
        mImageView.setErrorListener(new CropIwaView.ErrorListener() {
            @Override
            public void onError(Throwable e) {
                LogUtil.d("");
                e.printStackTrace();
                finish();
            }
        });

    }


    @Override
    public void onClick(View v, boolean isSafeClick) {
        super.onClick(v, isSafeClick);
        if (v == mBackBtn) {
            finish();
        }
        if (v == mSubmitBtn) {
            onSubmit();
        }
    }
}
