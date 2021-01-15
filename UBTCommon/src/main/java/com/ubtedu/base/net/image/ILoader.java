package com.ubtedu.base.net.image;

import android.content.Context;
import android.graphics.Bitmap;
import android.widget.ImageView;

import com.ubtedu.base.common.FrameworkConfig;

import java.io.File;

/**
 * @Description: 图片加载接口
 * @author: <a href="http://xiaoyaoyou1212.360doc.com">DAWI</a>
 * @date: 17/5/2 15:04
 */
public interface ILoader {
    void init(Context context);

    void loadNet(ImageView target, String url, Options options);

    void loadResource(ImageView target, int resId, Options options);

    void loadAssets(ImageView target, String assetName, Options options);

    void loadFile(ImageView target, File file, Options options);

    void clearMemoryCache(Context context);

    void clearDiskCache(Context context);

    class Options {

        public static final int RES_NONE = -1;
        public static final int RESIZE_NONE = -1;
        public int loadingResId = RES_NONE;//加载中的资源id
        public int loadErrorResId = RES_NONE;//加载失败的资源id
        public int resizeWidth = RESIZE_NONE;
        public int resizeHeight = RESIZE_NONE;
        public int resizeWDimen = RES_NONE;
        public int resizeHDimen = RES_NONE;
        public Bitmap.Config config = Bitmap.Config.RGB_565;

        public static Options defaultOptions() {
            return new Options(FrameworkConfig.IL_LOADING_RES, FrameworkConfig.IL_ERROR_RES);
        }

        public Options() {
            this.loadingResId = RES_NONE;
            this.loadErrorResId = RES_NONE;
            this.resizeWidth = RESIZE_NONE;
            this.resizeHeight = RESIZE_NONE;
            this.resizeWDimen = RES_NONE;
            this.resizeHDimen = RES_NONE;
        }

        public Options(int loadingResId, int loadErrorResId) {
            this.loadingResId = loadingResId;
            this.loadErrorResId = loadErrorResId;
        }

        public void setResize(int resizeWidth, int resizeHeight) {
            this.resizeWidth = resizeWidth;
            this.resizeHeight = resizeHeight;
        }

        public void setResizeDimen(int resizeWDimen, int resizeHDimen) {
            this.resizeWDimen = resizeWDimen;
            this.resizeHDimen = resizeHDimen;
        }

        public void setConfig(Bitmap.Config config) {
            this.config = config;
        }
    }
}
