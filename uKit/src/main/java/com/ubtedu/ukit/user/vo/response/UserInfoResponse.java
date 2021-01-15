package com.ubtedu.ukit.user.vo.response;

import android.text.TextUtils;

import androidx.annotation.IntDef;

import com.google.gson.annotations.SerializedName;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * 用户信息
 *
 * @author Bright. Create on 2017/5/8.
 */
public class UserInfoResponse extends EmptyResponse {
    public static final int GENDER_DEFAULT = 0;
    public static final int GENDER_MALE = 1;
    public static final int GENDER_FEMALE = 2;
    public static final int ACCOUNT_TYPE_PHONE = 1;
    public static final int ACCOUNT_TYPE_EMAIL = 2;

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

    @GenderValue
    @SerializedName("userGender")
    private int userGender;

    @SerializedName("emailVerify")
    private int emailVerify;

    @SerializedName("pwdCreateType")
    private int pwdCreateType;

    @SerializedName("userId")
    private int userId;

    @SerializedName("userBirthday")
    private String userBirthday;

    @SerializedName("userEmail")
    private String userEmail;

    @SerializedName("userPhone")
    private String userPhone;

    @IntDef({GENDER_MALE, GENDER_FEMALE, GENDER_DEFAULT})
    @Retention(RetentionPolicy.SOURCE)
    public @interface GenderValue {
    }

    public String getAccount() {
        if (!TextUtils.isEmpty(account)) {
            return userName;
        }
        return account;
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
            return account;
        }
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
        this.account = userName;
    }

    public int getUserGender() {
        return userGender;
    }

    public void setUserGender(int userGender) {
        this.userGender = userGender;
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
}
