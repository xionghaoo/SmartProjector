/**
 * @2018 UBTECH Robotics Corp. All rights reserved (C)
 **/
package com.ubtedu.ukit.menu.account;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.FutureTarget;
import com.ubtedu.ukit.R;
import com.ubtedu.ukit.common.files.FileHelper;
import com.ubtedu.ukit.user.UserManager;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * @author binghua.qin
 * @date 2019/02/26
 */
public class UserAvatarPresenter extends UserAvatarContracts.Presenter {

    private Bitmap mBitmap;
    private FutureTarget<Bitmap> mFutureTarget;

    public UserAvatarPresenter() {

    }

    @Override
    public void save() {
        if (mBitmap != null) {
            String imagePath = bitmapToImageFile(mBitmap);
            sendAvatarToGallely(imagePath);
            getView().getUIDelegate().toastShort(mContext.getString(R.string.account_avatar_save_success));
        }else {
            getView().getUIDelegate().toastShort(mContext.getString(R.string.account_avatar_save_fail));
        }
    }

    private String bitmapToImageFile(Bitmap bitmap) {
        String directory = FileHelper.getImageCacheDirectoryPath();
        String fileName = "UserAvatar-" + UserManager.getInstance().getLoginUserId() + "-" + System.currentTimeMillis() + ".png";

        File dirFile = new File(directory);
        if (!dirFile.exists()) {
            dirFile.mkdirs();
        }
        try {
            File myCaptureFile = new File(dirFile, fileName);
            BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(myCaptureFile));
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, bos);
            bos.flush();
            bos.close();
            return myCaptureFile.getAbsolutePath();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Observable<Bitmap> loadAvatar(final String url) {
        return Observable.create(new ObservableOnSubscribe<Bitmap>() {
            @Override
            public void subscribe(ObservableEmitter<Bitmap> emitter) throws Exception {

                if (url != null) {
                    mFutureTarget = Glide.with(mContext)
                            .asBitmap()
                            .load(url)
                            .submit();
                } else {
                    mFutureTarget = Glide.with(mContext)
                            .asBitmap()
                            .load(R.drawable.meun_account_portrait_pic2)
                            .submit();
                }
                mBitmap = mFutureTarget.get();
                Glide.with(mContext).clear(mFutureTarget);
                if (!emitter.isDisposed()) {
                    if (mBitmap != null) {
                        emitter.onNext(mBitmap);
                        emitter.onComplete();
                    } else {
                        emitter.onError(new Exception("Load user avatar fail"));
                    }
                }
            }
        })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    private void sendAvatarToGallely(String imagePath) {
        String nickname = UserManager.getInstance().getLoginUserNickname();
        if (imagePath != null && new File(imagePath).exists()) {
            String uriString = null;
            try {
                uriString = MediaStore.Images.Media.insertImage(mContext.getContentResolver(), imagePath, nickname, nickname);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            if (uriString != null) {
//                File file1 = new File(getRealPathFromURI(Uri.parse(uriString), mContext));
                updatePhotoMedia(Uri.parse(uriString), mContext);
            }
        }

    }


    //更新图库
    private static void updatePhotoMedia(Uri file, Context context) {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        intent.setData(file);
        context.sendBroadcast(intent);
    }

    //得到绝对地址
    private static String getRealPathFromURI(Uri contentUri, Context context) {
        String[] proj = {MediaStore.Images.Media.DATA};
        Cursor cursor = context.getContentResolver().query(contentUri, proj, null, null, null);
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        String fileStr = cursor.getString(column_index);
        cursor.close();
        return fileStr;
    }

    @Override
    public void release() {

        super.release();

        if (mBitmap != null) {
            mBitmap = null;
        }
    }
}
