package com.ubtedu.ukit.user.vo;

import android.os.Build;

import com.google.gson.annotations.SerializedName;
import com.ubtedu.alpha1x.utils.MD5Util;

/**
 * 用户注册API封装. POST
 *
 * @author Bright. Create on 2017/5/8.
 */
public class UserRegister extends UserBase {

//    /**
//     * 友盟消息推送设备号
//     */
//    @SerializedName("mDeviceToken")
//    private String mDeviceToken;
    /**
     * 用户密码
     */
    @SerializedName("userPass")
    private String userPass;
    /**
     * 手机型号
     */
    @SerializedName("mobitype")
    private String mobitype;
    /**
     * 手机系统版本
     */
    @SerializedName("mobios")
    private String mobios;
    @SerializedName("type")
    private int mAccountType;
    @SerializedName("locale")
    private int mLocale;


    /**
     * @param phone        手机号
     * @param passwd       用户密码, 传入明文密码，内部会进行MD5加密
     * @param securityCode 验证码
     */
    public UserRegister(String phone, String passwd, String devToken, String securityCode, int type, int locale) {
        super.setPhone(phone);
        this.deviceToken = devToken;
        this.mobitype = Build.MODEL;
        this.mobios = Build.VERSION.RELEASE;
        this.mAccountType = type;
        this.mLocale = locale;
        try {
            this.userPass = MD5Util.encodeByMD5(passwd);
            this.mcode = MD5Util.encodeByMD5("phone=" + phone + "&userpass=" + userPass + "&code=" + securityCode);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
//
//    public String generateDeviceToken() {
//        return mDeviceToken;
//    }
//
//    public void setDeviceToken(String mDeviceToken) {
//        this.mDeviceToken = mDeviceToken;
//    }

    public String getUserPass() {
        return userPass;
    }

    public void setUserPass(String userPass) {
        this.userPass = userPass;
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

    public int getAccountType() {
        return mAccountType;
    }

    public void setAccountType(int accountType) {
        mAccountType = accountType;
    }

    public int getLocale() {
        return mLocale;
    }

    public void setLocale(int locale) {
        mLocale = locale;
    }
}
