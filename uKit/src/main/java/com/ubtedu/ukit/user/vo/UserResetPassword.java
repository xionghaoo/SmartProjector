package com.ubtedu.ukit.user.vo;

import com.google.gson.annotations.SerializedName;
import com.ubtedu.alpha1x.utils.MD5Util;

/**
 * 重置密码API接口. POST
 *
 * @author Bright. Create on 2017/5/9.
 */
public class UserResetPassword extends UserBase {


    @SerializedName("userPass")
    private String userPass;
    @SerializedName("type")
    private int mAccountType;
    @SerializedName("locale")
    private int mLocale;

    /**
     * @param phone       手机号
     * @param code        验证码
     * @param newPassword 新密码, 传入明文密码，内部会进行MD5加密
     */
    public UserResetPassword(String phone, String code, String newPassword, int type, int locale) {
        super.setPhone(phone);
        this.mAccountType = type;
        this.mLocale = locale;
        try {
            this.userPass = MD5Util.encodeByMD5(newPassword);
            this.mcode = MD5Util.encodeByMD5("phone=" + phone + "&code=" + code);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String getNewPassword() {
        return userPass;
    }

    public void setNewPassword(String newPassword) {
        this.userPass = newPassword;
    }

}
