package com.ubtedu.alpha1x.utils;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Shader;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

/**
 * @author Bright. Create on 2017/6/27.
 */
public class BitmapUtil {

    /**
     * 将给定bitmap图片添加圆形边框，并裁剪为圆形图像
     *
     * @param bitmap     原图像
     * @param boundColor 边框颜色
     * @param boundWidth 边框宽度
     * @return 返回带圆框的圆形图像
     */
    public static Bitmap getRoundBoundBitmap(Bitmap bitmap, float w, float h, int boundColor, int boundWidth) {
        return getRoundBoundBitmap(bitmap, w, h, boundColor, true, boundWidth);
    }

    /**
     * 将给定bitmap裁剪为圆形图像
     */
    public static Bitmap getRoundBitmap(Bitmap bitmap, float w, float h) {
        return getRoundBoundBitmap(bitmap, w, h, 0xff00f7ff, false, 3);
    }

    /**
     * 根据图片的路径得到图片资源(压缩后)
     * 如果targetW或者targetH为0就自动压缩
     *
     * @param path
     * @param
     * @return 压缩后的图片
     */
    public static Bitmap getBitmapFromImagePath(String path, int targetW, int targetH) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, options);
        int height = options.outHeight;
        int width = options.outWidth;
        int inSampleSize = calculateInSampleSize(options);
        options.inSampleSize = inSampleSize;
        options.inJustDecodeBounds = false;
        Bitmap src = BitmapFactory.decodeFile(path, options);

        if (src == null) {
            return null;
        }
        Bitmap bitmap = null;

        if (targetH == 0 || targetW == 0) {
            bitmap = Bitmap.createScaledBitmap(src, width / inSampleSize, height / inSampleSize, false);
        } else {
            bitmap = Bitmap.createScaledBitmap(src, targetW, targetH, false);
        }

        if (src != bitmap) {
            src.recycle();
        }

        return bitmap;
    }

    /**
     * 将给定bitmap裁剪为圆形图像
     *
     * @param bitmap      原图像
     * @param boundColor  边框颜色
     * @param isNeedBound 是否需要边框
     * @param boundWidth  边框宽度
     * @return 返回带圆框的圆形图像
     */
    private static Bitmap getRoundBoundBitmap(Bitmap bitmap, float w, float h, int boundColor, boolean isNeedBound, int boundWidth) {
        Bitmap targetBg = Bitmap.createBitmap((int) w, (int) h, Bitmap.Config.ARGB_8888);
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(boundColor);
        paint.setStrokeWidth(boundWidth);
        // 画图像
        paint.setStyle(Paint.Style.FILL_AND_STROKE);
        Canvas canvas = new Canvas(targetBg);
        canvas.drawBitmap(bitmap, null,
                new Rect(0, 0, (int) w, (int) h), paint);

        if (isNeedBound) {
            // 画外框
            paint.setStyle(Paint.Style.STROKE);
            canvas.drawCircle(w / 2,
                    h / 2,
                    Math.min(w, h) / 2 - 1,
                    paint);
        }

        Bitmap target = Bitmap.createBitmap((int) w, (int) h, Bitmap.Config.ARGB_8888);
        Canvas boundCanvas = new Canvas(target);
        // 取圆形图像
        paint.setStyle(Paint.Style.FILL_AND_STROKE);
        paint.setShader(new BitmapShader(targetBg, Shader.TileMode.REPEAT, Shader.TileMode.REPEAT));
        boundCanvas.drawCircle(w / 2,
                h / 2,
                Math.min(w, h) / 2,
                paint);
        if (!targetBg.isRecycled()) {
            targetBg.recycle();
        }
        return target;
    }

    /**
     * 计算压缩比
     *
     * @param options
     * @return
     */
    public static int calculateInSampleSize(BitmapFactory.Options options) {
        int height = options.outHeight;
        int width = options.outWidth;

        int min = height > width ? width : height;
        int inSampleSize = min / 400;

        if (inSampleSize == 0)

            return 1;

        return inSampleSize;
    }

    private static int calculateInSampleSize(BitmapFactory.Options options,
                                             int reqWidth, int reqHeight) {
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;
        if (height > reqHeight || width > reqWidth) {
            final int halfHeight = height / 2;
            final int halfWidth = width / 2;
            while ((halfHeight / inSampleSize) > reqHeight
                    && (halfWidth / inSampleSize) > reqWidth) {
                inSampleSize *= 2;
            }
        }
        return inSampleSize;
    }

    // 如果是放大图片，filter决定是否平滑，如果是缩小图片，filter无影响
    private static Bitmap createScaleBitmap(Bitmap src, int dstWidth,
                                            int dstHeight) {
        Bitmap dst = Bitmap.createScaledBitmap(src, dstWidth, dstHeight, false);
        if (src != dst) { // 如果没有缩放，那么不回收
            src.recycle(); // 释放Bitmap的native像素数组
        }
        return dst;
    }

    // 从Resources中加载图片
    public static Bitmap decodeSampledBitmapFromResource(Resources res,
                                                         int resId, int reqWidth, int reqHeight) {
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(res, resId, options); // 读取图片长款
        options.inSampleSize = calculateInSampleSize(options, reqWidth,
                reqHeight); // 计算inSampleSize
        options.inJustDecodeBounds = false;
        Bitmap src = BitmapFactory.decodeResource(res, resId, options); // 载入一个稍大的缩略图
        return createScaleBitmap(src, reqWidth, reqHeight); // 进一步得到目标大小的缩略图
    }

    // 从sd卡上加载图片
    public static Bitmap decodeSampledBitmapFromFd(String pathName,
                                                   int reqWidth, int reqHeight) {
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(pathName, options);
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);
        options.inJustDecodeBounds = false;
        Bitmap src = BitmapFactory.decodeFile(pathName, options);
        return createScaleBitmap(src, reqWidth, reqHeight);
    }

    /**
     * bitmap转JPEG图片，返回图片路径
     *
     * @param context
     * @param bitmap
     * @param quality 0-100
     * @return
     */
    public static String bitmapToJPEG(Context context, Bitmap bitmap, int quality, String fileName) {

        File dirFile = context.getFilesDir();
        if (!dirFile.exists()) {
            dirFile.mkdir();
        }
        try {
            File myCaptureFile = new File(dirFile, fileName + ".jpg");
            BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(myCaptureFile));
            bitmap.compress(Bitmap.CompressFormat.JPEG, quality, bos);
            bos.flush();
            bos.close();
            return myCaptureFile.getAbsolutePath();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * bitmap转JPEG图片，返回图片路径
     *
     * @param context
     * @param bitmap
     * @param quality 0-100
     * @return
     */
    public static String bitmapToJPEG(Context context, Bitmap bitmap, int quality, String directory, String fileName) {

        File dirFile = new File(directory);
        if (!dirFile.exists()) {
            dirFile.mkdirs();
        }
        try {
            File myCaptureFile = new File(dirFile, fileName + ".jpg");
            if (!myCaptureFile.exists()){
                myCaptureFile.createNewFile();
            }
            BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(myCaptureFile));
            bitmap.compress(Bitmap.CompressFormat.JPEG, quality, bos);
            bos.flush();
            bos.close();
            return myCaptureFile.getAbsolutePath();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 以最省内存的方式读取本地资源的图片.decodeStream最直接调用JNI>>nativeDecodeAsset()来完成decode
     * ，无需再使用java层的createBitmap，从而节省了java层的空间
     *
     * @param context
     * @param resId
     * @return
     */
    public static Bitmap readBitmap(Context context, int resId, int inSampleSize) {
        BitmapFactory.Options opt = new BitmapFactory.Options();
        opt.inPreferredConfig = Bitmap.Config.ARGB_8888;
        opt.inPurgeable = true;
        opt.inInputShareable = true;
        opt.inSampleSize = inSampleSize;
        InputStream is = context.getResources().openRawResource(resId);
        return BitmapFactory.decodeStream(is, null, opt);
    }

}
