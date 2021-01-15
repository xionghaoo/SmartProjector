package com.ubtedu.alpha1x.utils;

import android.text.TextUtils;

import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author qinicy
 * @data 2017/11/9
 */

public class StringUtil {
    public final static String RegexIP = "^(25[0-5]|2[0-4][0-9]|1{1}[0-9]{2}|[1-9]{1}[0-9]{1}|[1-9])\\.(25[0-5]|2[0-4][0-9]|1{1}[0-9]{2}|[1-9]{1}[0-9]{1}|[1-9]|0)\\.(25[0-5]|2[0-4][0-9]|1{1}[0-9]{2}|[1-9]{1}[0-9]{1}|[1-9]|0)\\.(25[0-5]|2[0-4][0-9]|1{1}[0-9]{2}|[1-9]{1}[0-9]{1}|[0-9])$";
    public final static String RegexPort = "^6553[0-5]|655[0-2][0-9]|65[0-4][0-9]{2}|6[0-4][0-9]{3}|[1-5][0-9]{4}|[1-9][0-9]{0,3}$";
    public final static String RegexAllChinese = "^[\\u4e00-\\u9fa5]*$";
    public final static String RegexPhoneNumber = "^(((13[0-9])|(15[^4,\\D])|(18[0,5-9]))\\d{8})|((\\d{3,4}-)?\\d{7,8}(-\\d{1,4})?)$";
    public final static String RegexEmail = "w+([-+.]w+)*@w+([-.]w+)*.w+([-.]w+)*";

    public static boolean validateRegex(String string, String regex) {
        if (string == null) {
            return false;
        }
        return string.matches(regex);
    }

    public static boolean isEmail(String email) {
        if (TextUtils.isEmpty(email)) {
            return false;
        }
        String pattern = "^[0-9a-zA-Z-_+%\\.]+@([A-Za-z0-9-]+\\.)+[A-Za-z]{2,4}$";
        return Pattern.matches(pattern, email);
    }

    public static boolean isNumeric(String str) {
        if (str != null) {
            Pattern pattern = Pattern.compile("[0-9]*");
            Matcher isNum = pattern.matcher(str);
            if (!isNum.matches()) {
                return false;
            }
        }

        return true;
    }

    public static int getNumberFromString(String s) {
        if (!TextUtils.isEmpty(s)) {
            String regEx = "[^0-9]";
            Pattern p = Pattern.compile(regEx);
            Matcher m = p.matcher(s);
            String numStr = m.replaceAll("").trim();
            try {
                return Integer.parseInt(numStr);
            } catch (NumberFormatException e) {
                LogUtil.d("versionName2Code:empty s");
            }
        }
        return 0;
    }

    public static boolean containRepeatChar(String str) {
        if (str == null || str.isEmpty()) {
            return false;
        }
        char[] elements = str.toCharArray();
        for (char e : elements) {
            if (str.indexOf(e) != str.lastIndexOf(e)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 检测是否有emoji表情
     */
    public static boolean containsEmoji(String source) {
        int len = source.length();
        for (int i = 0; i < len; i++) {
            char codePoint = source.charAt(i);
            if (!isEmojiCharacter(codePoint)) {
                // 如果不能匹配,则该字符是Emoji表情
                return true;
            }
        }
        return false;
    }

    public static boolean isEmojiCharacter(char codePoint) {
        return (codePoint == 0x0) || (codePoint == 0x9) || (codePoint == 0xA) ||
                (codePoint == 0xD) || ((codePoint >= 0x20) && (codePoint <= 0xD7FF)) ||
                ((codePoint >= 0xE000) && (codePoint <= 0xFFFD)) || ((codePoint >= 0x10000)
                && (codePoint <= 0x10FFFF));
    }

    public static String getRandomChar(int length) {            //生成随机字符串
        char[] chr = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
                'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z',
                'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z'};
        Random random = new Random();
        StringBuilder buffer = new StringBuilder();
        for (int i = 0; i < length; i++) {
            buffer.append(chr[random.nextInt(chr.length - 1)]);
        }
        return buffer.toString();
    }

}
