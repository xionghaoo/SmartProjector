/**
 * @2018 UBTECH Robotics Corp. All rights reserved (C)
 **/
package com.ubtedu.ukit.common.image;

import com.steelkiwi.cropiwa.AspectRatio;

/**
 * @Author qinicy
 * @Date 2019/3/4
 **/
public class ImageCropConfigFactory {
    public static ImageCropConfig createProjectImageCropConfig() {
        ImageCropConfig config = new ImageCropConfig();
        config.ratio = new AspectRatio(1076, 812);
        config.isCalculateFromWidth = false;
        config.isDynamicCrop = true;
        config.cropScale = 0.75f;
        config.width = 480;
        config.height = 360;
        config.quality = 100;
        config.compressFormat = ImageCropConfig.COMPRESS_FORMAT_JPEG;
        config.isImageScaleEnabled = true;
        config.isImageTranslationEnabled = true;
        config.maxScale = 5.0f;
        config.minScale = 0.1f;

        return config;
    }

    public static ImageCropConfig createUserAvatarCropConfig() {
        ImageCropConfig config = new ImageCropConfig();
        config.ratio = new AspectRatio(812, 812);
        config.isCalculateFromWidth = false;
        config.isDynamicCrop = true;
        config.cropScale = 0.75f;
        config.width = 480;
        config.height = 480;
        config.quality = 100;
        config.compressFormat = ImageCropConfig.COMPRESS_FORMAT_JPEG;
        config.isImageScaleEnabled = true;
        config.isImageTranslationEnabled = true;
        config.maxScale = 5.0f;
        config.minScale = 0.1f;
        return config;
    }
}
