/**
 * @2018 UBTECH Robotics Corp. All rights reserved (C)
 **/
package com.ubtedu.ukit.common.view;

import android.text.InputType;
import android.text.method.DigitsKeyListener;
import android.text.method.KeyListener;

/**
 * @Author qinicy
 * @Date 2019/2/27
 **/
public class KeyFactory {
    private static final String PHONE_DIGITS = "0123456789";

    private static final String EMAIL_DIGITS = "0123456789" +
            "qwertyuiopasdfghjklzxcvbnm" +
            "QWERTYUIOPASDFGHJKLZXCVBNM" +
            "!#$%&'\"*+-/\\=?^_`|<>{}[]()~·,.:;@€£¥";

    private static final String EDU_ACCOUNT_DIGITS = EMAIL_DIGITS;
    private static final String PASSWORD_DIGITS = EMAIL_DIGITS;

    public static KeyListener createPhoneDigitsKeyListener() {
        return DigitsKeyListener.getInstance(PHONE_DIGITS);
    }

    public static KeyListener createEmailDigitsKeyListener() {
        return new CharsKeyListener(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS, EMAIL_DIGITS);
    }
    public static KeyListener createEduAccountDigitsKeyListener() {
        return new CharsKeyListener(InputType.TYPE_CLASS_TEXT, EDU_ACCOUNT_DIGITS);
    }
    public static KeyListener createPasswordDigitsKeyListener() {
        return new CharsKeyListener(InputType.TYPE_TEXT_VARIATION_PASSWORD, PASSWORD_DIGITS);
    }

    public static KeyListener createPasswordVisibleDigitsKeyListener() {
        return new CharsKeyListener(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD, PASSWORD_DIGITS);
    }

}
