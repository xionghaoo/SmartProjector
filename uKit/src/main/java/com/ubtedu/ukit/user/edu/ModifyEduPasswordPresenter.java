package com.ubtedu.ukit.user.edu;

import com.ubtedu.alpha1x.utils.MD5Util;
import com.ubtedu.base.net.manager.NetworkManager;
import com.ubtedu.base.net.rxretrofit.exception.RxException;
import com.ubtedu.base.net.rxretrofit.mode.ApiResult;
import com.ubtedu.base.net.rxretrofit.subscriber.RxSubscriber;
import com.ubtedu.ukit.R;
import com.ubtedu.ukit.common.exception.ExceptionHelper;
import com.ubtedu.ukit.user.UserManager;
import com.ubtedu.ukit.user.vo.UpdatePwdParam;
import com.ubtedu.ukit.user.vo.response.EmptyResponse;

import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;

/**
 * @Author qinicy
 * @Date 2019/11/15
 **/
public class ModifyEduPasswordPresenter extends ModifyEduPasswordContracts.Presenter {
    private final CompositeDisposable mDisposable = new CompositeDisposable();

    @Override
    public void updatePassword(String oldPwd, String newPwd) {

        if (!NetworkManager.getInstance().isNetworkConnected()) {
            getView().getUIDelegate().toastShort(getView().getContext().getString(R.string.global_network_unavailable));
            return;
        }
        getView().getUIDelegate().showLoading(false);
        UpdatePwdParam param = new UpdatePwdParam();
        param.oldPwd = MD5Util.encodeSha256(oldPwd);
        param.newPwd = MD5Util.encodeSha256(newPwd);
        UserManager.getInstance().updateEduPassword(param)
                .subscribe(new RxSubscriber<ApiResult<EmptyResponse>>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        mDisposable.add(d);
                    }

                    @Override
                    public void onNext(ApiResult<EmptyResponse> result) {
                        if (result.code == 0){
                            getView().onUpdatePasswordSuccess();
                        }else {
                            getView().getUIDelegate().toastShort(mContext.getString(R.string.account_edu_modify_password_common_error));
                        }
                    }

                    @Override
                    public void onComplete() {
                        getView().getUIDelegate().hideLoading();
                    }

                    @Override
                    public void onError(RxException e) {
                        getView().getUIDelegate().hideLoading();
                        boolean eat = ExceptionHelper.handleException(e);
                        if (!eat){
                            getView().getUIDelegate().toastShort(mContext.getString(R.string.account_edu_modify_password_common_error));
                        }
                    }
                });
    }

    @Override
    public void release() {
        super.release();
        mDisposable.clear();
    }
}
