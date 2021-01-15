/**
 * @2018 UBTECH Robotics Corp. All rights reserved (C)
 **/
package com.ubtedu.ukit.common.exception;

import android.content.Context;
import android.text.TextUtils;

import com.ubtedu.alpha1x.utils.LogUtil;
import com.ubtedu.alpha1x.utils.SharedPreferenceUtils;
import com.ubtedu.base.net.rxretrofit.exception.RxException;
import com.ubtedu.base.net.rxretrofit.mode.ApiCode;
import com.ubtedu.ukit.R;
import com.ubtedu.ukit.common.dialog.PromptDialogHelper;
import com.ubtedu.ukit.project.bridge.api.ToastHelper;
import com.ubtedu.ukit.user.UserConsts;
import com.ubtedu.ukit.user.UserManager;
import com.ubtedu.ukit.user.vo.UserInfo;

/**
 * @Author qinicy
 * @Date 2018/11/6
 **/
public class ExceptionHelper {
    private static Context sApplicationContext;
    private static boolean isInited;

    public static void init(Context context) {
        if (!isInited) {
            isInited = true;
            sApplicationContext = context.getApplicationContext();
        }
    }

    public static boolean handleException(RxException e) {
        return handleException(e.getCode(), e.getMessage());
    }

    /**
     * @param code
     */
    public static boolean handleException(int code, String defaultMessage) {

        String toastText = "";
        switch (code) {
            case ResultCode.ERROR_PARAMS:
            case ApiCode.Http.INTERNAL_SERVER_ERROR:
                // 交给末端处理
                return false;

            case ResultCode.EDU_ACCOUNT_PASSWD_ERROR:
                //5001,校园账号登录密码错误
                toastText = sApplicationContext.getString(R.string.account_login_edu_error_password);
                break;
            case ResultCode.EDU_ACCOUNT_FROZEN:
                //5002,用户被冻结，请联系管理员
                toastText = sApplicationContext.getString(R.string.account_edu_frozen);
                break;
            case ResultCode.ACCOUNT_PASSWD_ERROR:
                //用户或密码错误
                toastText = sApplicationContext.getString(R.string.account_login_fail_case_password_invalid);
                break;

            case ResultCode.EDU_ACCOUNT_MODIFY_ORIGIN_PASSWD_ERROR:
                toastText = sApplicationContext.getString(R.string.account_edu_modify_password_invild_origin_password);
                break;
            case ResultCode.CAPTCHA_ERROR:
                //验证码错误
                toastText = sApplicationContext.getString(R.string.account_register_verify_code_incorrect);
                break;
            case ResultCode.ACCOUNT_ERROR:
                //账号错误
                toastText = sApplicationContext.getString(R.string.account_error_account);
                break;
            case ResultCode.EMAIL_FORMAT_ERROR:
                //邮箱格式不正确
                toastText = sApplicationContext.getString(R.string.account_error_invalid_email_format);
                break;
            case ResultCode.PHONE_FORMAT_ERROR:
                //手机号码格式不正确
                toastText = sApplicationContext.getString(R.string.account_error_invalid_phone_format);
                break;
            case ResultCode.PASSWD_ERROR:
                //	(2014,"密码错误"),
                toastText = sApplicationContext.getString(R.string.account_error_password);
                break;
            case ResultCode.ACCOUNT_MSG_OVER_COUNT:
                //	(2024,"同一帐号一小时内最多只能发送3次短信"),
                toastText = sApplicationContext.getString(R.string.account_verify_send_limit_minute);
                break;
            case ResultCode.ACCOUNT_MSG_OVER_COUNT_PER_HOUR:
                //	(2024,"同一帐号一小时内最多只能发3次短信"),
                toastText = sApplicationContext.getString(R.string.account_verify_send_limit_hour);
                break;
            case ResultCode.ACCOUNT_MSG_OVER_COUNT_24:
                //	(2025,"同一帐号24小时内最多只能发送10次短信"),
                toastText = sApplicationContext.getString(R.string.account_error_account_captcha_over_count_24);
                break;
            case ResultCode.CAPTCHA_OVERDUE:
                //	(2026,"验证码已过期"),
                toastText = sApplicationContext.getString(R.string.account_error_pcaptcha_overdue);
                break;
            case ResultCode.EMAIL_CAPTCHA_OVER_COUNT:
                //	(2027,"一个邮箱一分钟内只能发送一次邮件"),
                toastText = sApplicationContext.getString(R.string.account_error_email_captcha_over_count);
                break;
            case ResultCode.PASSWORD_RESET_SAME:
                //	(2029,"新密码与旧密码不能相同"),
                toastText = sApplicationContext.getString(R.string.account_new_pwd_no_change);
                break;

            case ApiCode.Request.SSL_ERROR:
            case ApiCode.Request.NETWORK_ERROR:
            case ApiCode.Request.HTTP_ERROR:

                if (!TextUtils.isEmpty(defaultMessage) && defaultMessage.toLowerCase().contains("unauthorized")) {
                    handleLoginTokenExpired();
                } else {
                    toastText = sApplicationContext.getString(R.string.global_network_unavailable);
                }
                break;
            case ApiCode.Request.TIMEOUT_ERROR:
                toastText = sApplicationContext.getString(R.string.network_timeout);
                break;
            case ApiCode.Request.PARSE_ERROR:
                toastText = sApplicationContext.getString(R.string.parse_result_data_fail);
                break;
            case ResultCode.SERVER_LOGIN_TOKEN_EXPIRED:
                handleLoginTokenExpired();
                break;
            case ResultCode.SERVER_FORBIDDEN:
                toastText = sApplicationContext.getString(R.string.server_forbidden);
                break;
            case ResultCode.SERVER_PAGE_NOT_FOUND:
                toastText = sApplicationContext.getString(R.string.server_not_found);
                break;
            case ResultCode.SERVER_NO_RESULT:
                toastText = sApplicationContext.getString(R.string.server_no_result);
                break;

            default:
                return false;
        }
        if (!TextUtils.isEmpty(toastText)) {
            ToastHelper.toastShort(toastText);
        }
        return true;
    }

    public static boolean isTokenExpired(RxException e) {
        if (e != null) {
            return e.getCode() == ResultCode.SERVER_LOGIN_TOKEN_EXPIRED ||
                    !TextUtils.isEmpty(e.getMessage()) && e.getMessage().toLowerCase().contains("unauthorized");
        }
        return false;
    }

    private static void handleLoginTokenExpired() {
        LogUtil.d("TokenExpired");
//        EventBus.getDefault().post(new LoginOverdueEvent());
        UserInfo user = UserManager.getInstance().getCurrentUser();
        if (user != null) {
            user.setLogin(false);
            SharedPreferenceUtils.getInstance(sApplicationContext).setValue(UserConsts.SP_CURRENT_LOGIN_USER, user);
        }
        PromptDialogHelper.showLoginOverdueDialog();
    }



}
