package com.ubtedu.ukit.user.gdpr;


import com.ubtedu.alpha1x.utils.AppUtil;
import com.ubtedu.base.net.manager.NetworkManager;
import com.ubtedu.base.net.rxretrofit.exception.RxException;
import com.ubtedu.base.net.rxretrofit.mode.ApiResult;
import com.ubtedu.base.net.rxretrofit.subscriber.RxSubscriber;
import com.ubtedu.ukit.R;
import com.ubtedu.ukit.application.PrivacyInfo;
import com.ubtedu.ukit.user.UserManager;
import com.ubtedu.ukit.user.vo.GdprUserPactInfo;

import java.util.List;

import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;

public class GdprPactPresenter extends GdprPactContract.Presenter {
    private final CompositeDisposable mDisposables;

    public GdprPactPresenter() {
        mDisposables = new CompositeDisposable();
    }

    @Override
    public void checkPack(String account, @GdprUserPactInfo.PactType int type) {
        if (!NetworkManager.getInstance().isNetworkConnected()) {
            getView().getUIDelegate().toastShort(getView().getContext().getString(R.string.global_network_unavailable));
            return;
        }
        String lang = "CN";
        if (!AppUtil.isSimpleChinese(AppUtil.getAppLanguage(mContext))) {
            lang = "en";
        }
        getView().getUIDelegate().showLoading(false);
        UserManager.getInstance().pactInfo(PrivacyInfo.PRODUCT_ID, type,lang, ""+PrivacyInfo.getUBTAppId(), PrivacyInfo.getUBTSign())
                .subscribe(new RxSubscriber<ApiResult<List<GdprUserPactInfo>>>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        mDisposables.add(d);
                    }

                    @Override
                    public void onNext(ApiResult<List<GdprUserPactInfo>> result) {
                        if (result != null && result.code == 200) {
                            List<GdprUserPactInfo> gdprUserPactInfoList = result.data;

                        }
                    }

                    @Override
                    public void onComplete() {
                        getView().getUIDelegate().hideLoading();
                    }

                    @Override
                    public void onError(RxException e) {
                        getView().getUIDelegate().hideLoading();
                    }
                });
    }

    @Override
    public void release() {
        super.release();
        mDisposables.clear();
    }
}
