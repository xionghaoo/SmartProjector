/**
 * @2018 UBTECH Robotics Corp. All rights reserved (C)
 **/
package com.ubtedu.ukit.menu.feedback;

import com.ubtedu.alpha1x.utils.SharedPreferenceUtils;
import com.ubtedu.alpha1x.utils.StringUtil;
import com.ubtedu.base.net.manager.NetworkManager;
import com.ubtedu.base.net.rxretrofit.exception.RxException;
import com.ubtedu.base.net.rxretrofit.mode.ApiResult;
import com.ubtedu.base.net.rxretrofit.subscriber.SimpleRxSubscriber;
import com.ubtedu.ukit.R;
import com.ubtedu.ukit.application.PrivacyInfo;
import com.ubtedu.ukit.common.exception.ExceptionHelper;
import com.ubtedu.ukit.menu.MenuConstants;
import com.ubtedu.ukit.user.UserManager;
import com.ubtedu.ukit.user.vo.IdeaFeedBackBody;
import com.ubtedu.ukit.user.vo.response.EmptyResponse;

import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;

/**
 * @author qinicy
 * @date 2018/12/13
 */
public class MenuFeedbackPresenter extends MenuFeedbackContracts.Presenter {

    private static final long LIMIT_TIME = 60L * 1000;

    private CompositeDisposable mCompositeDisposable;

    public MenuFeedbackPresenter() {
        mCompositeDisposable = new CompositeDisposable();
    }

    @Override
    public void feedback(String email, String content) {
        boolean isEmail = StringUtil.isEmail(email);
        if (!isEmail) {
            getView().getUIDelegate().toastShort(mContext.getString(R.string.menu_tab_feedback_email_invalid));
            return;
        }

        long lastTime = SharedPreferenceUtils.getInstance(mContext).getLongValue(MenuConstants.SP_KEY_LAST_FEEDBACK_TIME, 0);
        if (System.currentTimeMillis() - lastTime < LIMIT_TIME) {
            getView().getUIDelegate().toastShort(mContext.getString(R.string.menu_tab_feedback_limit_times));
            return;
        }

        if (!NetworkManager.getInstance().isNetworkConnected()) {
            getView().getUIDelegate().toastShort(getView().getContext().getString(R.string.global_network_unavailable));
            return;
        }
        IdeaFeedBackBody body = new IdeaFeedBackBody();
        body.content = content;
        body.userEmail=email;
        if (!UserManager.getInstance().isGuest()) {
            body.userId = UserManager.getInstance().getLoginUserId();
        }
        getView().getUIDelegate().showLoading(true);
        UserManager.getInstance().pushFeedback(PrivacyInfo.PRODUCT_ID,body)
                .subscribe(new SimpleRxSubscriber<ApiResult<EmptyResponse>>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        mCompositeDisposable.add(d);
                    }

                    @Override
                    public void onNext(ApiResult<EmptyResponse> result) {
                        getView().getUIDelegate().toastShort(getView().getContext().getString(R.string.menu_tab_feedback_success));
                        getView().getUIDelegate().hideLoading();
                        getView().onFeedbackResult(true);
                        SharedPreferenceUtils.getInstance(mContext).setValue(MenuConstants.SP_KEY_LAST_FEEDBACK_TIME, System.currentTimeMillis());
                    }


                    @Override
                    public void onError(RxException e) {
                        getView().getUIDelegate().hideLoading();
                        getView().onFeedbackResult(false);
                        boolean eat = ExceptionHelper.handleException(e.getCode(), e.getMessage());
                        if (!eat){
                            getView().getUIDelegate().toastShort(mContext.getString(R.string.menu_tab_feedback_submit_fail));
                        }
                    }
                });
    }

    @Override
    public void release() {
        super.release();
        mCompositeDisposable.clear();
    }
}
