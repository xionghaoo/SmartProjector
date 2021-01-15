package com.ubtedu.ukit.user.vo;

import android.text.TextUtils;

import com.google.gson.annotations.SerializedName;
import com.ubtedu.base.gson.Exclude;
import com.ubtedu.ukit.common.vo.SerializablePOJO;

/**
 * 用户信息
 *
 * @author Bright. Create on 2017/5/8.
 */
public class UserInfo extends SerializablePOJO {
    private static final long serialVersionUID = 5622476213633844142L;
    public static final int ACCOUNT_TYPE_PHONE = 0;
    public static final int ACCOUNT_TYPE_EMAIL = 1;

    private boolean isGuest;
    @SerializedName("account")
    private String account;

    @SerializedName("countryCode")
    private String countryCode;

    @SerializedName("countryName")
    private String countryName;

    @SerializedName("nickName")
    private String nickName;

    @SerializedName("userImage")
    private String userImage;

    // 与account相同
    @SerializedName("userName")
    private String userName;

    @SerializedName("emailVerify")
    private int emailVerify;

    @SerializedName("pwdCreateType")
    private int pwdCreateType;

    @SerializedName("userId")
    private int userId = 0;

    @SerializedName("userBirthday")
    private String userBirthday;

    @SerializedName("userEmail")
    private String userEmail;

    @SerializedName("userPhone")
    private String userPhone;

    @SerializedName("userExtraEmail")
    private String userExtraEmail;

    @SerializedName("userExtraPhone")
    private String userExtraPhone;

    @Exclude(serialize = true)
    private String token;

    /**
     * 用户是否已登录
     */
    @Exclude(serialize = true)
    private boolean isLogin;


    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public boolean isGuest() {
        return isGuest;
    }

    public void setGuest(boolean guest) {
        isGuest = guest;
    }

    public String getAccount() {
        if (!TextUtils.isEmpty(account)) {
            return account;
        }
        return userName;
    }

    public void setAccount(String account) {
        this.account = account;
        this.userName = account;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public String getCountryName() {
        return countryName;
    }

    public void setCountryName(String countryName) {
        this.countryName = countryName;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getUserImage() {
        return userImage;
    }

    public void setUserImage(String userImage) {
        this.userImage = userImage;
    }

    public String getUserName() {
        if (!TextUtils.isEmpty(userName)) {
            return userName;
        }
        return account;
    }

    public void setUserName(String userName) {
        this.userName = userName;
        this.account = userName;
    }


    public int getEmailVerify() {
        return emailVerify;
    }

    public void setEmailVerify(int emailVerify) {
        this.emailVerify = emailVerify;
    }

    public int getPwdCreateType() {
        return pwdCreateType;
    }

    public void setPwdCreateType(int pwdCreateType) {
        this.pwdCreateType = pwdCreateType;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getUserID() {
        return String.valueOf(userId);
    }

    public String getUserBirthday() {
        return userBirthday;
    }

    public void setUserBirthday(String userBirthday) {
        this.userBirthday = userBirthday;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getUserPhone() {
        return userPhone;
    }

    public void setUserPhone(String userPhone) {
        this.userPhone = userPhone;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public boolean isLogin() {
        return isLogin;
    }

    public void setLogin(boolean login) {
        isLogin = login;
    }
}
