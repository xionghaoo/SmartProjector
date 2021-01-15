/**
 * @2018 UBTECH Robotics Corp. All rights reserved (C)
 **/
package com.ubtedu.ukit.menu.account;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import com.ubtedu.alpha1x.utils.LogUtil;
import com.ubtedu.base.net.rxretrofit.exception.RxException;
import com.ubtedu.base.net.rxretrofit.subscriber.SimpleRxSubscriber;
import com.ubtedu.ukit.R;
import com.ubtedu.ukit.common.base.UKitBaseActivity;

/**
 * @author binghua.qin
 * @date 2019/02/26
 */
public class UserAvatarActivity extends UKitBaseActivity<UserAvatarContracts.Presenter, UserAvatarContracts.UI> {
    public final static String AVATAR_URL_KEY = "avatar_url_key";
    private Button mBackBtn;
    private Button mSaveBtn;
    private ImageView mAvatarIv;
    private String mAvatarUrl;

    public static void open(Context context, String url) {
        if (context == null) {
            return;
        }
        Intent intent = new Intent(context, UserAvatarActivity.class);
        intent.putExtra(AVATAR_URL_KEY, url);
        context.startActivity(intent);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAvatarUrl = getIntent().getStringExtra(AVATAR_URL_KEY);

        setContentView(R.layout.activity_user_avatar);
        mBackBtn = findViewById(R.id.user_avatar_back_btn);
        mSaveBtn = findViewById(R.id.user_avatar_save_btn);
        mSaveBtn.setAlpha(0.3f);
        mAvatarIv = findViewById(R.id.user_avatar_iv);
        bindSafeClickListener(mBackBtn);
        bindSafeClickListener(mSaveBtn);
        bindSafeClickListener(mAvatarIv);
        mAvatarIv.post(new Runnable() {
            @Override
            public void run() {
                LogUtil.d("mAvatarIv width:"+mAvatarIv.getWidth());
                LogUtil.d("mAvatarIv width:"+mAvatarIv.getHeight());
                ViewGroup.LayoutParams lp = mAvatarIv.getLayoutParams();
                lp.height = mAvatarIv.getHeight();
                lp.width = mAvatarIv.getHeight();
                mAvatarIv.setLayoutParams(lp);
                loadAvatar();
            }
        });

    }


    private void loadAvatar(){
        getUIDelegate().showLoading(true);
        getPresenter().loadAvatar(mAvatarUrl)
                .subscribe(new SimpleRxSubscriber<Bitmap>() {
                    @Override
                    public void onNext(Bitmap bitmap) {
                        super.onNext(bitmap);
                        LogUtil.d("user avatar bitmap getWidth:"+bitmap.getWidth());
                        LogUtil.d("user avatar bitmap getHeight:"+bitmap.getHeight());
                        getUIDelegate().hideLoading();
                        mAvatarIv.setImageBitmap(bitmap);
                        mSaveBtn.setEnabled(true);
                        mSaveBtn.setAlpha(1.0f);
                    }

                    @Override
                    public void onError(RxException e) {
                        super.onError(e);
                        mAvatarIv.setImageResource(R.drawable.account_avatar_picture_fail);
                        getUIDelegate().toastShort(getString(R.string.account_avatar_load_fail));
                        getUIDelegate().hideLoading();
                    }
                });
    }
    @Override
    public void onClick(View v, boolean isSafeClick) {
        super.onClick(v, isSafeClick);
        if (v == mBackBtn) {
            finish();
        }
        if (v == mSaveBtn) {
            getPresenter().save();
        }
    }


    @Override
    protected UserAvatarContracts.Presenter createPresenter() {
        return new UserAvatarPresenter();
    }

    @Override
    protected UserAvatarContracts.UI createUIView() {
        return new UserAvatarUI();
    }

    class UserAvatarUI extends UserAvatarContracts.UI {
    }


}