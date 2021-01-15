package com.ubtedu.ukit.user.vo;

import android.os.Build;

import com.google.gson.annotations.SerializedName;
import com.ubtedu.alpha1x.utils.MD5Util;

/**
 * 登录API封装. POST
 *
 * @author Bright. Create on 2017/5/8.
 */
public class UserLogin extends UserBase {

    /**
     * 手机型号
     */
    @SerializedName("mobitype")
    private String mobitype;

    /**
     * 手机系统
     */
    @SerializedName("mobios")
    private String mobios;
    @SerializedName("type")
    private int mAccountType;
    @SerializedName("locale")
    private int mLocale;
//    /**
//     * 消息推送
//     */
//    @SerializedName("mDeviceToken")
//    private String mDeviceToken;

    /**
     * @param phone  手机号
     * @param passwd 密码，传入明文密码
     */
    public UserLogin(String phone, String passwd, int type, int locale) {
        super.setPhone(phone);
        this.mobitype = Build.MODEL;
        this.mobios = Build.VERSION.RELEASE;
        this.mAccountType = type;
        this.mLocale = locale;
        try {
            this.mcode = MD5Util.encodeByMD5(passwd);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String getMobitype() {
        return mobitype;
    }

    public void setMobitype(String mobitype) {
        this.mobitype = mobitype;
    }

    public String getMobios() {
        return mobios;
    }

    public void setMobios(String mobios) {
        this.mobios = mobios;
    }

//    public String generateDeviceToken() {
//        return mDeviceToken;
//    }
//
//    public void setDeviceToken(String mDeviceToken) {
//        this.mDeviceToken = mDeviceToken;
//    }
}
