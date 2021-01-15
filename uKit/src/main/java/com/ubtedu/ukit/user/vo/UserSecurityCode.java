package com.ubtedu.ukit.user.vo;

import com.ubtedu.alpha1x.utils.MD5Util;

/**
 * 验证码API封装. POST
 *
 * @author Bright. Create on 2017/5/9.
 */
public class UserSecurityCode extends UserBase {

    public UserSecurityCode(String phone, String securityCode) {
        super.setPhone(phone);
        try {
            this.mcode = MD5Util.encodeByMD5("phone=" + phone + "&code=" + securityCode);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
