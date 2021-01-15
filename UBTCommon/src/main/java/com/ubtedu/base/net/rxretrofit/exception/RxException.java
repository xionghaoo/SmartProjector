package com.ubtedu.base.net.rxretrofit.exception;

import android.net.ParseException;
import android.util.Log;

import com.google.gson.JsonParseException;
import com.ubtedu.base.net.rxretrofit.mode.ApiCode;
import com.ubtedu.base.net.rxretrofit.mode.ApiResult;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

import okhttp3.ResponseBody;
import retrofit2.HttpException;

import static android.content.ContentValues.TAG;


/**
 * @Description: API异常统一管理
 * @author: qinicy
 * @date: 17/5/417:59
 */
public class RxException extends IOException {

    private int code;
    private String message;


    public RxException(int code, String message) {
        super(new Throwable(message));
        this.code = code;
        this.message = message;
    }

    public RxException(Throwable throwable, int code) {
        super(throwable);
        this.code = code;
        this.message = throwable.getMessage();
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public RxException setMessage(String message) {
        this.message = message;
        return this;
    }

    public String getDisplayMessage() {
        return message + "(code:" + code + ")";
    }

    public static boolean isSuccess(ApiResult apiResult) {
        if (apiResult == null) {
            return false;
        }
        return apiResult.code == ApiCode.Response.HTTP_SUCCESS || ignoreSomeIssue(apiResult.code);
    }

    private static boolean ignoreSomeIssue(int code) {
        switch (code) {
            case ApiCode.Response.TIMESTAMP_ERROR://时间戳过期
            case ApiCode.Response.ACCESS_TOKEN_EXPIRED://AccessToken错误或已过期
            case ApiCode.Response.REFRESH_TOKEN_EXPIRED://RefreshToken错误或已过期
            case ApiCode.Response.OTHER_PHONE_LOGIN: //帐号在其它手机已登录
            case ApiCode.Response.SIGN_ERROR://签名错误
                return true;
            default:
                return false;
        }
    }

    public static RxException handleException(Throwable e) {
        Log.d(TAG, "handleException e:" + e);
        RxException ex;
        if (e instanceof RxException) {
            return (RxException) e;
        } else if (e instanceof HttpException) {
            HttpException httpException = (HttpException) e;
            ex = new RxException(e, ApiCode.Request.HTTP_ERROR);
            ResponseBody body = httpException.response().errorBody();
            try {
                String content = body.string();
                Log.d("'handleException:'", content);
                JSONObject jsonObject = new JSONObject(content);
                if (jsonObject.has("code")) {
                    ex.code = jsonObject.getInt("code");
                }
                if (jsonObject.has("status")) {
                    ex.code = jsonObject.getInt("status");
                }
                ex.message = content;
            } catch (IOException e1) {
                e1.printStackTrace();
            } catch (JSONException e1) {
                e1.printStackTrace();
            }
            switch (httpException.code()) {
                case ApiCode.Http.UNAUTHORIZED:
                    break;
                case ApiCode.Http.FORBIDDEN:
                    break;
                case ApiCode.Http.NOT_FOUND:
                case ApiCode.Http.REQUEST_TIMEOUT:
                case ApiCode.Http.GATEWAY_TIMEOUT:
                case ApiCode.Http.INTERNAL_SERVER_ERROR:
                case ApiCode.Http.BAD_GATEWAY:
                case ApiCode.Http.SERVICE_UNAVAILABLE:
                default:
                    ex.message = "NETWORK_ERROR";
                    break;
            }
            return ex;
        } else if (e instanceof JsonParseException || e instanceof JSONException || e instanceof ParseException) {
            ex = new RxException(e, ApiCode.Request.PARSE_ERROR);
            ex.message = "PARSE_ERROR";
            return ex;
        } else if (e instanceof ConnectException || e instanceof UnknownHostException) {
            ex = new RxException(e, ApiCode.Request.NETWORK_ERROR);
            ex.message = "NETWORK_ERROR";
            return ex;
        } else if (e instanceof javax.net.ssl.SSLHandshakeException) {
            ex = new RxException(e, ApiCode.Request.SSL_ERROR);
            ex.message = "SSL_ERROR";
            return ex;
        } else if (e instanceof SocketTimeoutException) {
            ex = new RxException(e, ApiCode.Request.TIMEOUT_ERROR);
            ex.message = "TIMEOUT_ERROR";
            return ex;
        } else {
            ex = new RxException(e, ApiCode.Request.UNKNOWN);
            ex.message = "UNKNOWN";
            return ex;
        }
    }

}
