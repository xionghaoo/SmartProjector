//package com.ubtedu.base.net.image;
//
//import android.content.Context;
//import android.graphics.Bitmap;
//import android.net.Uri;
//import android.widget.ImageView;
//
//import com.squareup.picasso.Picasso;
//import com.squareup.picasso.RequestCreator;
//
//import java.io.File;
//
//public class PicassoLoader implements ILoader {
//    @Override
//    public void init(Context context) {
//
//    }
//
//    @Override
//    public void loadNet(ImageView target, String url, Options options) {
//        load(getPicasso(target.getContext()).load(url), target, options);
//    }
//
//    @Override
//    public void loadResource(ImageView target, int resId, Options options) {
//        load(getPicasso(target.getContext()).load(resId), target, options);
//    }
//
//    @Override
//    public void loadAssets(ImageView target, String assetName, Options options) {
//        load(getPicasso(target.getContext()).load("file:///android_asset/" + assetName), target, options);
//    }
//
//    @Override
//    public void loadFile(ImageView target, File file, Options options) {
//        load(getPicasso(target.getContext()).load(file), target, options);
//    }
//
//    @Override
//    public void clearMemoryCache(Context context) {
//        // Picasso不支持
//    }
//
//    public void clearMemoryCache(Context context, File file) {
//        getPicasso(context).invalidate(file);
//    }
//
//    public void clearMemoryCache(Context context, String path) {
//        getPicasso(context).invalidate(path);
//    }
//
//    public void clearMemoryCache(Context context, Uri uri) {
//        getPicasso(context).invalidate(uri);
//    }
//
//    @Override
//    public void clearDiskCache(Context context) {
//        // Picasso不支持
//    }
//
//    public Picasso getPicasso(Context context) {
//        return Picasso.with(context);
//    }
//
//    private void load(RequestCreator requestCreator, ImageView target, Options options) {
//        if (options == null) options = Options.defaultOptions();
//
//        if (options.loadingResId != Options.RES_NONE) {
//            requestCreator.placeholder(options.loadingResId);
//        }
//        if (options.loadErrorResId != Options.RES_NONE) {
//            requestCreator.error(options.loadErrorResId);
//        }
//        if (options.resizeWidth != Options.RESIZE_NONE && options.resizeHeight != Options.RESIZE_NONE) {
//            requestCreator.resize(options.resizeWidth, options.resizeHeight);
//        }
//        requestCreator.config(options.config);
//        requestCreator.into(target);
//    }
//}
