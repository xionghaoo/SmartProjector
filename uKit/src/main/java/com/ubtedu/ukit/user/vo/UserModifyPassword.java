package com.ubtedu.ukit.user.vo;

import com.google.gson.annotations.SerializedName;
import com.ubtedu.alpha1x.utils.MD5Util;

/**
 * 用于修改密码、忘记密码并重置情况. POST
 *
 * @author Bright. Create on 2017/5/8.
 */
public class UserModifyPassword extends UserBase {

    /**
     * 用户ID，登录后返回的
     */
    @SerializedName("userId")
    private String userId;

    /**
     * 旧密码，MD5加密
     */
    @SerializedName("oldpwd")
    private String oldPasswd;

    /**
     * 新密码，MD5加密
     */
    @SerializedName("userPass")
    private String newPasswd;

    @SerializedName("locale")
    private int mLocale;

    /**
     * @param userId    用户id，登录时获取
     * @param oldPasswd 旧密码，传入明文密码，内部进行MD5加密
     * @param newPasswd 新密码，传入明文密码，内部进行MD5加密
     * @param token     会话ID，登录时获取
     */
    public UserModifyPassword(String userId, String oldPasswd, String newPasswd, String token, int locale) {
        this.userId = userId;
        this.mLocale = locale;
        try {
            this.oldPasswd = MD5Util.encodeByMD5(oldPasswd);
            this.newPasswd = MD5Util.encodeByMD5(newPasswd);
            this.mcode = MD5Util.encodeByMD5("userId=" + userId + "&token=" + token);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String getUserId() {
        return userId;
    }

    public void setUserID(String userId) {
        this.userId = userId;
    }

    public String getOldPasswd() {
        return oldPasswd;
    }

    public void setOldPasswd(String oldPasswd) {
        this.oldPasswd = oldPasswd;
    }

    public String getNewPasswd() {
        return newPasswd;
    }

    public void setNewPasswd(String newPasswd) {
        this.newPasswd = newPasswd;
    }

}
