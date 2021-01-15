package com.ubtedu.ukit.user.vo;

import com.google.gson.annotations.SerializedName;
import com.ubtedu.alpha1x.utils.MD5Util;
import com.ubtedu.ukit.application.UKitApplication;
import com.ubtedu.ukit.common.utils.VersionUtil;
import com.ubtedu.ukit.common.vo.SerializablePOJO;

/**
 * @author Bright. Create on 2017/5/8.
 */
public class UserBase extends SerializablePOJO {

    @SerializedName("phone")
    protected String phone;

    @SerializedName("version")
    protected String version;

    @SerializedName("mcode")
    protected String mcode;

    @SerializedName("mDeviceToken")
    protected String deviceToken;

    public UserBase() {
//        version = VersionUtil.generateServerVersionName();
//        deviceToken = UKitApplication.getInstance().generateDeviceToken();
//        try {
//            mcode = MD5Util.encodeByMD5("phone=" + phone + "&version=" + version);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    /**
     * 获取MD5加密值
     */
    public String getMCode() {
        return mcode;
    }

    public void setMCode(String mcode) {
        this.mcode = mcode;
    }

    public String getDeviceToken() {
        return deviceToken;
    }

    public void setDeviceToken(String deviceToken) {
        this.deviceToken = deviceToken;
    }
}
