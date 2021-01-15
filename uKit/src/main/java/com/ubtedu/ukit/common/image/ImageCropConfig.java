/**
 * @2018 UBTECH Robotics Corp. All rights reserved (C)
 **/
package com.ubtedu.ukit.common.image;

import com.steelkiwi.cropiwa.AspectRatio;

import java.io.Serializable;

/**
 * @Author qinicy
 * @Date 2019/3/4
 **/
public class ImageCropConfig implements Serializable {
    public static final int COMPRESS_FORMAT_JPEG = 0;
    public static final int COMPRESS_FORMAT_PNG = 1;
    public AspectRatio ratio;
    public boolean isCalculateFromWidth;
    public boolean isDynamicCrop;
    public int width;
    public int height;
    public int quality;
    public int compressFormat;
    public float cropScale;
    public boolean isImageScaleEnabled;
    public boolean isImageTranslationEnabled;
    public float maxScale;
    public float minScale;


}
