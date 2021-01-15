/**
 * @2018 UBTECH Robotics Corp. All rights reserved (C)
 **/
package com.ubtedu.ukit.menu.account;

import com.ubtedu.alpha1x.utils.GsonUtil;
import com.ubtedu.alpha1x.utils.LogUtil;
import com.ubtedu.alpha1x.utils.SharedPreferenceUtils;
import com.ubtedu.base.net.manager.NetworkManager;
import com.ubtedu.base.net.rxretrofit.exception.RxException;
import com.ubtedu.base.net.rxretrofit.mode.ApiResult;
import com.ubtedu.base.net.rxretrofit.subscriber.RxSubscriber;
import com.ubtedu.ukit.R;
import com.ubtedu.ukit.common.exception.ExceptionHelper;
import com.ubtedu.ukit.common.exception.ResultCode;
import com.ubtedu.ukit.common.cloud.CloudStorageManager;
import com.ubtedu.ukit.common.cloud.CloudResult;
import com.ubtedu.ukit.user.UserConsts;
import com.ubtedu.ukit.user.UserManager;
import com.ubtedu.ukit.user.vo.UpdateUserInfo;
import com.ubtedu.ukit.user.vo.UserInfo;
import com.ubtedu.ukit.user.vo.response.UserInfoResponse;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

/**
 * @author qinicy
 * @date 2018/12/12
 */
public class MenuAccountPresenter extends MenuAccountContracts.Presenter {

    private CompositeDisposable mDisposables;

    public MenuAccountPresenter() {
        mDisposables = new CompositeDisposable();
    }

    @Override
    public void rename(String newName) {
        // 上传文件后，将response的url保存到用户对象的userImage中
        UserInfo user = UserManager.getInstance().getCurrentUser();
        if (user == null) {
            return;
        }

        UserInfo copyUser = user.deepClone();
        copyUser.setNickName(newName);
        copyUser.setCountryCode(user.getCountryCode());
        copyUser.setCountryName(user.getCountryName());
        copyUser.setUserName(user.getUserName());

        UpdateUserInfo updateUserInfo = new UpdateUserInfo(copyUser);

        UserManager.getInstance().modifyUserInfo(updateUserInfo, copyUser.getToken())
                .subscribe(new RxSubscriber<ApiResult<UserInfoResponse>>() {

                    @Override
                    public void onError(RxException e) {
                        getView().getUIDelegate().hideLoading();
                        e.printStackTrace();
                        ExceptionHelper.handleException(e.getCode(), e.getMessage());
                    }

                    @Override
                    public void onSubscribe(@NonNull Disposable d) {
                        mDisposables.add(d);
                    }

                    @Override
                    public void onNext(@NonNull ApiResult<UserInfoResponse> result) {
                        int code = result.sourceRawData.code;
                        if (code == 0) {
                            LogUtil.d(result.sourceRawData.message);
                            UserInfo user = UserManager.getInstance().getCurrentUser();
                            UserInfoResponse newUser = result.sourceRawData;

                            if (user == null || newUser == null) {
                                LogUtil.e("user == null || newUser ==null");
                                return;
                            }
                            user.setNickName(newUser.getNickName());
                            SharedPreferenceUtils.getInstance(getView().getContext()).setValue(UserConsts.SP_CURRENT_LOGIN_USER, user);
                            getView().onRenameResult(true);

                        } else {
                            LogUtil.e("更新用户信息失败：" + result.sourceRawData.message);
                            ExceptionHelper.handleException(result.sourceRawData.code, result.sourceRawData.message);
                        }

                    }

                    @Override
                    public void onComplete() {
                        getView().getUIDelegate().hideLoading();
                    }
                });
    }

    @Override
    public void updateUserInfo(final String avatarPath) {


        if (!NetworkManager.getInstance().isNetworkConnected()) {
            getView().getUIDelegate().toastShort(getView().getContext().getString(R.string.global_network_unavailable));
            return;
        }

        getView().getUIDelegate().showLoading(false);

        doUploadAvatar(avatarPath)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .flatMap(new Function<CloudResult, ObservableSource<ApiResult<UserInfoResponse>>>() {
                    @Override
                    public ObservableSource<ApiResult<UserInfoResponse>> apply(CloudResult cloudResult) throws Exception {
//                         上传文件后，将response的url保存到用户对象的userImage中
                        UserInfo user = UserManager.getInstance().getCurrentUser();
                        if (user == null) {
                            return Observable.error(new RxException(ResultCode.SERVER_LOGIN_TOKEN_EXPIRED, "更新用户信息失败，用户没登录！"));
                        }
                        String json = GsonUtil.get().toJson(user);
                        UserInfo copyUser = GsonUtil.get().toObject(json, UserInfo.class);
                        copyUser.setNickName(user.getNickName());
                        copyUser.setCountryCode(user.getCountryCode());
                        copyUser.setCountryName(user.getCountryName());
                        copyUser.setUserName(user.getUserName());
                        copyUser.setUserImage(CloudStorageManager.getInstance().generateFileUrl(cloudResult.objectKey));

                        UpdateUserInfo updateUserInfo = new UpdateUserInfo(copyUser);

                        return UserManager.getInstance().modifyUserInfo(updateUserInfo, copyUser.getToken());
                    }
                })
                .subscribe(new RxSubscriber<ApiResult<UserInfoResponse>>() {
                    @Override
                    public void onError(RxException e) {
                        getView().getUIDelegate().hideLoading();
                        e.printStackTrace();
                        LogUtil.d("upload user avatar fail:" + e.getDisplayMessage());
                        boolean eat = ExceptionHelper.handleException(e.getCode(), e.getMessage());
                        if (!eat) {
                            getView().getUIDelegate().toastShort(mContext.getString(R.string.account_avatar_modify_fail));
                        }
                    }

                    @Override
                    public void onSubscribe(@NonNull Disposable d) {
                        mDisposables.add(d);
                    }

                    @Override
                    public void onNext(@NonNull ApiResult<UserInfoResponse> result) {
                        int code = result.sourceRawData.code;
                        if (code == 0) {
                            UserInfo user = UserManager.getInstance().getCurrentUser();
                            UserInfoResponse newUser = result.sourceRawData;
                            if (user == null || newUser == null) {
                                LogUtil.e("user == null || newUser ==null");
                                return;
                            }
                            user.setNickName(newUser.getNickName());
                            user.setUserImage(newUser.getUserImage());
                            SharedPreferenceUtils.getInstance(getView().getContext()).setValue(UserConsts.SP_CURRENT_LOGIN_USER, user);
                            getView().onModifyAvatarSuccess(avatarPath);

                            getView().getUIDelegate().toastShort(mContext.getString(R.string.account_avatar_modify_success));

                        } else {
                            LogUtil.e("更新用户信息失败：" + result.sourceRawData.message);
                            ExceptionHelper.handleException(result.sourceRawData.code, result.sourceRawData.message);
                        }
                    }

                    @Override
                    public void onComplete() {
                        getView().getUIDelegate().hideLoading();
                    }
                });

    }


    private Observable<CloudResult> doUploadAvatar(String path) {
        return CloudStorageManager.getInstance().upload(path).takeLast(1);
    }

    @Override
    public void release() {
        super.release();
        mDisposables.clear();
    }


}
