package com.ubtedu.base.net.image;

import android.content.Context;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestBuilder;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.request.RequestOptions;

import java.io.File;

/**
 * @Description: 使用Glide框架加载图片
 * @author: qinicy
 * @date: 17/5/2 15:16
 */
public class GlideLoader implements ILoader {
    @Override
    public void init(Context context) {

    }

    @Override
    public void loadNet(ImageView target, String url, Options options) {
        load(getRequestManager(target.getContext()).load(url), target, options);
    }

    @Override
    public void loadResource(ImageView target, int resId, Options options) {
        load(getRequestManager(target.getContext()).load(resId), target, options);
    }

    @Override
    public void loadAssets(ImageView target, String assetName, Options options) {
        load(getRequestManager(target.getContext()).load("file:///android_asset/" + assetName), target, options);
    }

    @Override
    public void loadFile(ImageView target, File file, Options options) {
        load(getRequestManager(target.getContext()).load(file), target, options);
    }

    @Override
    public void clearMemoryCache(Context context) {
        Glide.get(context).clearMemory();
    }

    @Override
    public void clearDiskCache(Context context) {
        Glide.get(context).clearDiskCache();
    }

    private RequestManager getRequestManager(Context context) {
        return Glide.with(context);
    }

    private void load(RequestBuilder request, ImageView target, Options options) {
        if (options == null) {
            options = Options.defaultOptions();
        }

        RequestOptions requestOptions = new RequestOptions();

        if (options.loadingResId != Options.RES_NONE) {
            requestOptions = requestOptions.placeholder(options.loadingResId);

        }
        if (options.loadErrorResId != Options.RES_NONE) {
            requestOptions = requestOptions.error(options.loadErrorResId);
        }

        request.apply(requestOptions).
                into(target);
    }
}
