/**
 * @2018 UBTECH Robotics Corp. All rights reserved (C)
 **/
package com.ubtedu.ukit.common.view;

import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextUtils;

/**
 * 两个英文字符 == 一个中文字符
 *
 * @Author qinicy
 * @Date 2018/12/12
 **/
public class EditTextInputFilter implements InputFilter {

    private int maxLength;//最大长度，ASCII码算一个，其它算两个

    /**
     * 输入英文的最大长度 。比如你想要限制40个汉字，80个英文字符，传入的值就是80
     * 使用方式：mEdit.setFilters(new InputFilter[]{filter});
     */
    public EditTextInputFilter(int maxLength) {
        this.maxLength = maxLength;
    }

    @Override
    public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
        if (TextUtils.isEmpty(source)) {
            return null;
        }

        int inputCount = 0;
        int destCount = 0;
        inputCount = getCurrentLength(source);

        if (dest.length() != 0) {
            destCount = getCurrentLength(dest);
        }

        if (destCount >= maxLength) {
            return "";
        } else {

            int count = inputCount + destCount;
            if (dest.length() == 0) {
                if (count <= maxLength)
                    return null;
                else
                    return sub(source, maxLength);
            }
            if (count > maxLength) {
                //int min = count - maxLength;
                int maxSubLength = maxLength - destCount;
                return sub(source, maxSubLength);
            }
        }
        return null;
    }

    private CharSequence sub(CharSequence sq, int subLength) {
        int needLength = 0;
        int length = 0;
        for (int i = 0; i < sq.length(); i++) {
            if (sq.charAt(i) < 128)
                length += 1;
            else
                length += 2;
            ++needLength;
            if (subLength <= length) {
                return sq.subSequence(0, needLength);
            }
        }
        return sq;
    }

    public int getCurrentLength(CharSequence s) {
        int length = 0;
        if (s == null)
            return length;
        else {
            for (int i = 0; i < s.length(); i++) {
                if (s.charAt(i) < 128)
                    length += 1;
                else
                    length += 2;
            }
        }
        return length;
    }
}
