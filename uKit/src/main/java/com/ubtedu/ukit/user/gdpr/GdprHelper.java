/**
 * @2018 UBTECH Robotics Corp. All rights reserved (C)
 **/
package com.ubtedu.ukit.user.gdpr;

import android.content.Context;

import com.ubtedu.alpha1x.utils.GsonUtil;
import com.ubtedu.alpha1x.utils.LogUtil;
import com.ubtedu.alpha1x.utils.SharedPreferenceUtils;
import com.ubtedu.base.net.rxretrofit.exception.RxException;
import com.ubtedu.base.net.rxretrofit.mode.ApiResult;
import com.ubtedu.base.net.rxretrofit.subscriber.SimpleRxSubscriber;
import com.ubtedu.ukit.R;
import com.ubtedu.ukit.application.PrivacyInfo;
import com.ubtedu.ukit.common.exception.ExceptionHelper;
import com.ubtedu.ukit.common.exception.ResultCode;
import com.ubtedu.ukit.menu.region.RegionHelper;
import com.ubtedu.ukit.user.UserConsts;
import com.ubtedu.ukit.user.UserManager;
import com.ubtedu.ukit.user.vo.GdprUserPactInfo;
import com.ubtedu.ukit.user.vo.UserInfo;
import com.ubtedu.ukit.user.vo.response.EmptyResponse;

import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;

/**
 * @Author qinicy
 * @Date 2019/4/4
 **/
public class GdprHelper {
    private Context mContext;
    private GdprContracts mContracts;
    private CompositeDisposable mDisposables;
    private List<GdprUserPactInfo> mGdprPacts;
    private UserInfo mUser;

    public GdprHelper(Context context, GdprContracts contracts) {
        mContext = context;
        mContracts = contracts;
        mDisposables = new CompositeDisposable();
    }


    public void setContracts(GdprContracts contracts) {
        mContracts = contracts;
    }

    public void initGdprPacts() {
        requestGprPact().subscribe(new SimpleRxSubscriber<>());
    }


    public void showGdprPacts(@GdprUserPactInfo.PactType final int type) {
        getUbtPactAsync().subscribe(new SimpleRxSubscriber<List<GdprUserPactInfo>>() {
            @Override
            public void onSubscribe(Disposable d) {
                super.onSubscribe(d);
                mDisposables.add(d);
            }

            @Override
            public void onError(RxException e) {
                super.onError(e);
                LogUtil.d("toShowReadOnlyGdprPact fail:" + e.getDisplayMessage());
                ExceptionHelper.handleException(e);
            }

            @Override
            public void onNext(List<GdprUserPactInfo> gdprUserPactInfos) {
                super.onNext(gdprUserPactInfos);
                if (gdprUserPactInfos == null || gdprUserPactInfos.isEmpty()) {
                    return;
                }
                if (mContracts != null) {
                    if (type == GdprUserPactInfo.GDPR_TYPE_ALL) {
                        mContracts.showInteractiveGdprPacts(gdprUserPactInfos);
                    } else {
                        for (int i = 0; i < 2; i++) {
                            if (type == gdprUserPactInfos.get(i).type) {
                                mContracts.showReadOnlyGdprPactDialog(gdprUserPactInfos.get(i));
                            }
                        }
                    }

                }
            }
        });
    }

    public void saveGdprPact() {
        if (mUser != null) {
            if (mGdprPacts == null) {
                String json = SharedPreferenceUtils.getInstance(mContext).getStringValue(UserConsts.SP_GDPR_LASTEST_UBT_PACTS);
                if (json != null) {
                    mGdprPacts = GsonUtil.get().toObjectList(json, GdprUserPactInfo.class);
                }
            }

            if (mGdprPacts != null) {

                if (mContracts != null) {
                    mContracts.showLoading(true);
                }

                UserManager.getInstance().saveUserPactInfo(mGdprPacts, mUser.getToken())
                        .subscribe(new SimpleRxSubscriber<ApiResult<EmptyResponse>>() {
                            @Override
                            public void onError(RxException e) {
                                super.onError(e);

                                if (mContracts != null) {
                                    mContracts.hideLoading();
                                    boolean eat = ExceptionHelper.handleException(e);
                                    if (!eat) {
                                        mContracts.toastShort(mContext.getString(R.string.account_login_fail_case_try_again));
                                    }
                                }

                            }

                            @Override
                            public void onSubscribe(Disposable d) {
                                super.onSubscribe(d);
                                mDisposables.add(d);
                            }

                            @Override
                            public void onNext(ApiResult<EmptyResponse> result) {
                                super.onNext(result);
                                if (mContracts != null) {
                                    mContracts.hideLoading();
                                    mContracts.onLoginAcceptGdprPact(true);
                                }
                            }
                        });
            }
        }

    }


    private boolean isCurrentLanguageGpdrPact() {
        if (mGdprPacts == null || mGdprPacts.size() == 0) {
            return false;
        }
        String lang = mGdprPacts.get(0).lang;
        String currentLang = RegionHelper.getAPIRegionLanguage();
        return currentLang != null && currentLang.equalsIgnoreCase(lang);
    }

    public Observable<List<GdprUserPactInfo>> getUbtPactAsync() {

        if (mGdprPacts == null) {
            String json = SharedPreferenceUtils.getInstance(mContext).getStringValue(UserConsts.SP_GDPR_LASTEST_UBT_PACTS);
            if (json != null) {
                mGdprPacts = GsonUtil.get().toObjectList(json, GdprUserPactInfo.class);
            }
        }
        //如果条款版本的语言和当前系统不一致，则需要清空
        if (!isCurrentLanguageGpdrPact()) {
            mGdprPacts = null;
        }

        if (mGdprPacts != null) {
            return Observable.just(mGdprPacts);
        } else {
            return requestGprPact();
        }
    }

    public List<GdprUserPactInfo> getGdprPacts() {
        return mGdprPacts;
    }

    private void showLoading() {
        if (mContracts != null) {
            mContracts.showLoading(true);
        }
    }

    private void hideLoading() {
        if (mContracts != null) {
            mContracts.hideLoading();
        }
    }

    public Observable<List<GdprUserPactInfo>> requestGprPact() {
        String lang = getCurrentLanguage();
        return UserManager.getInstance().pactInfo(PrivacyInfo.PRODUCT_ID, GdprUserPactInfo.GDPR_TYPE_ALL, lang,
                String.valueOf(PrivacyInfo.getUBTAppId()), PrivacyInfo.getUBTSign())
                .doOnSubscribe(new Consumer<Disposable>() {
                                   @Override
                                   public void accept(Disposable disposable) throws Exception {
                                       showLoading();
                                       mDisposables.add(disposable);
                                   }
                               }
                )

                .flatMap(new Function<ApiResult<List<GdprUserPactInfo>>, ObservableSource<List<GdprUserPactInfo>>>() {
                    @Override
                    public ObservableSource<List<GdprUserPactInfo>> apply(ApiResult<List<GdprUserPactInfo>> result) throws Exception {
                        if (result != null && result.code == 200) {
                            if (result.data != null) {
                                mGdprPacts = result.data;
                                SharedPreferenceUtils.getInstance(mContext).setValue(UserConsts.SP_GDPR_LASTEST_UBT_PACTS, mGdprPacts);
                                return Observable.just(mGdprPacts);
                            }
                        }
                        return Observable.error(new RxException(ResultCode.SERVER_NO_RESULT, "No GDPR pact..."));
                    }
                })
                .doOnComplete(new Action() {
                    @Override
                    public void run() throws Exception {
                        hideLoading();
                    }
                })
                .doOnError(new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        hideLoading();
                    }
                });

    }


    private String getCurrentLanguage() {
        return RegionHelper.getAPIRegionLanguage();
    }

    public Observable<Boolean> checkUserPact(UserInfo user) {
        this.mUser = user;
        return getUbtPactAsync().flatMap(new Function<List<GdprUserPactInfo>, ObservableSource<Boolean>>() {
            @Override
            public ObservableSource<Boolean> apply(final List<GdprUserPactInfo> latestPacts) {
                return requestUserPactThenCheck(latestPacts);
            }
        });

    }

    private Observable<Boolean> requestUserPactThenCheck(final List<GdprUserPactInfo> latestPacts) {
        if (latestPacts.size() == 0) {
            return Observable.just(true);
        }
        return UserManager.getInstance().getUserPactInfo(PrivacyInfo.PRODUCT_ID,
                mUser.getUserID(), String.valueOf(PrivacyInfo.getUBTAppId()), PrivacyInfo.getUBTSign())
                .doOnSubscribe(new Consumer<Disposable>() {
                    @Override
                    public void accept(Disposable disposable) throws Exception {
                        showLoading();
                        mDisposables.add(disposable);
                    }
                })
                .doOnComplete(new Action() {
                    @Override
                    public void run() throws Exception {
                        hideLoading();
                    }
                })
                .map(new Function<ApiResult<List<GdprUserPactInfo>>, Boolean>() {
                    @Override
                    public Boolean apply(ApiResult<List<GdprUserPactInfo>> result) {
                        if (result != null && result.code == 200) {
                            String json = GsonUtil.get().toJson(result.data);
                            List<GdprUserPactInfo> userPacts = GsonUtil.get().toObjectList(json, GdprUserPactInfo.class);
                            if (userPacts != null) {
                                for (int i = 0; i < userPacts.size(); i++) {
                                    for (int j = 0; j < latestPacts.size(); j++) {
                                        if (latestPacts.get(j).version != null && latestPacts.get(j).version.equals(userPacts.get(i).version)) {
                                            return true;
                                        }
                                    }
                                }
                            }
                        }
                        return false;
                    }
                });
    }


    public void release() {
        if (mDisposables != null) {
            mDisposables.clear();
        }
    }

}
