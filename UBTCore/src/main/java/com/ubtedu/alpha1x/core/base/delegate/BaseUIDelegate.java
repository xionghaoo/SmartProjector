package com.ubtedu.alpha1x.core.base.delegate;

import android.content.Context;
import android.content.DialogInterface;
import android.view.View;
import android.widget.Toast;

import com.ubtedu.alpha1x.core.base.widget.ILoadingController;
import com.ubtedu.alpha1x.core.base.widget.IToastView;
import com.ubtedu.alpha1x.core.mvp.UIDelegate;

import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

/**
 * Created by qinicy on 2017/5/2.
 */

public class BaseUIDelegate implements UIDelegate {


    private Context mContext;
    private ILoadingController mLoadingController;


    private IToastView mToastView;
    private View.OnClickListener mSafeOnClickListener;
    private View.OnClickListener mNormalOnClickListener;
    public int SAFE_CLICK_INTERNAL_TIME = 400;


    private ClickEventDelegate mClickEventDelegate;

    public BaseUIDelegate(Context context) {
        this.mContext = context;

    }

    @Override
    public void resume() {

    }

    @Override
    public void pause() {

    }

    @Override
    public void destroy() {
    }

    @Override
    public void setClickEventDelegate(ClickEventDelegate delegate) {
        this.mClickEventDelegate = delegate;
    }

    @Override
    public void visible(boolean flag, View view) {
        if (flag) view.setVisibility(View.VISIBLE);
    }

    @Override
    public void gone(boolean flag, View view) {
        if (flag) view.setVisibility(View.GONE);
    }

    @Override
    public void inVisible(View view) {
        view.setVisibility(View.INVISIBLE);
    }

    @Override
    public void toastShort(String msg) {
        if (mToastView != null) {
            mToastView.setText(msg);
            mToastView.setDuration(Toast.LENGTH_SHORT);
            mToastView.show();
        }

    }

    @Override
    public void toastLong(String msg) {
        if (mToastView != null) {
            mToastView.setText(msg);
            mToastView.setDuration(Toast.LENGTH_LONG);
            mToastView.show();
        }
    }

    @Override
    public void showLoading(boolean cancelAble) {
        if (mLoadingController != null) {
            mLoadingController.setLabel(null).setCancellable(cancelAble).show();
        }
    }

    @Override
    public void showLoading(boolean cancelAble, String message) {
        if (mLoadingController != null) {
            if (!mLoadingController.isShowing()) {
                mLoadingController.setLabel(message).setCancellable(cancelAble).show();
            } else {
                mLoadingController.setLabel(message);
            }
        }
    }

    @Override
    public void hideLoading() {
        if (mLoadingController != null)
            mLoadingController.dismiss();
    }

    @Override
    public boolean isLoadingShowing() {
        if (mLoadingController != null)
            return mLoadingController.isShowing();
        else
            return false;
    }

    @Override
    public void cancel() {
        if (mToastView != null) {
            mToastView.cancel();
        }
    }

    @Override
    public Context getContext() {
        return mContext;
    }

    /**
     * 防止快速多次点击重复
     * {@link #onClick(View, boolean)}
     * {@link #SAFE_CLICK_INTERNAL_TIME}
     *
     * @param view The target view
     */
    @Override
    public final void bindSafeClickListener(View view) {
        view.setOnClickListener(getSafeClickListener());
    }

    @Override
    public final View.OnClickListener getSafeClickListener() {
        if (mSafeOnClickListener == null) {
            mSafeOnClickListener = new View.OnClickListener() {
                public ObservableEmitter<View> mPreventRepeatedEmitter;

                @Override
                public void onClick(final View clickView) {
                    if (mPreventRepeatedEmitter == null || mPreventRepeatedEmitter.isDisposed()) {
                        Observable
                                .create(new ObservableOnSubscribe<View>() {
                                    @Override
                                    public void subscribe(ObservableEmitter<View> emitter) throws Exception {
                                        mPreventRepeatedEmitter = emitter;
                                        //处理首次点击
                                        emitter.onNext(clickView);
                                    }
                                })
                                .throttleFirst(SAFE_CLICK_INTERNAL_TIME, TimeUnit.MILLISECONDS)
                                .subscribe(new Observer<View>() {
                                    @Override
                                    public void onSubscribe(Disposable d) {

                                    }

                                    @Override
                                    public void onNext(View next) {
                                        try {
                                            BaseUIDelegate.this.onClick(next, true);
                                        } catch (final Exception e) {
                                            next.post(new Runnable() {
                                                @Override
                                                public void run() {
                                                    throw e;
                                                }
                                            });
                                        }
                                    }

                                    @Override
                                    public void onError(Throwable e) {

                                    }

                                    @Override
                                    public void onComplete() {

                                    }
                                });
                    } else {
                        mPreventRepeatedEmitter.onNext(clickView);
                    }
                }
            };
        }

        return mSafeOnClickListener;
    }

    @Override
    public final void bindClickListener(View view) {
        view.setOnClickListener(getNormalClickListener());
    }

    @Override
    public final View.OnClickListener getNormalClickListener() {
        if (mNormalOnClickListener == null) {
            mNormalOnClickListener = new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    BaseUIDelegate.this.onClick(view, false);
                }
            };
        }
        return mNormalOnClickListener;
    }

    @Override
    public void onClick(View v, boolean isSafeClick) {
        if (mClickEventDelegate != null) {
            mClickEventDelegate.onClick(v, isSafeClick);
        }
    }


    @Override
    public void setLoadingController(ILoadingController loadingController) {
        mLoadingController = loadingController;
    }

    @Override
    public void setLoadingCancelListener(DialogInterface.OnCancelListener listener) {
        if (mLoadingController != null) {
            mLoadingController.setCancelListener(listener);
        }
    }

    public ILoadingController getLoadingController() {
        return mLoadingController;
    }

    @Override
    public void setToastView(IToastView toastView) {
        mToastView = toastView;
    }

    public IToastView getToastView() {
        return mToastView;
    }
}
