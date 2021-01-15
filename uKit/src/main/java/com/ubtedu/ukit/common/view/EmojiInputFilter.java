/**
 * @2018 UBTECH Robotics Corp. All rights reserved (C)
 **/
package com.ubtedu.ukit.common.view;

import android.text.InputFilter;
import android.text.Spanned;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @Author qinicy
 * @Date 2019/1/25
 **/
public class EmojiInputFilter extends InputFilter.LengthFilter {
   private final static Pattern EMOJI_PATTERN = Pattern.compile("[\ud83c\udc00-\ud83c\udfff]|[\ud83d\udc00-\ud83d\udfff]|[\u2600-\u27ff]",
            Pattern.UNICODE_CASE | Pattern.CASE_INSENSITIVE);

    public EmojiInputFilter(int max) {
        super(max);
    }

    @Override
    public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
        CharSequence superCharSequence = super.filter(source,start,end,dest,dstart,dend);
        if (superCharSequence != null){
            return superCharSequence;
        }
        //判断一
        Matcher emojiMatcher = EMOJI_PATTERN.matcher(source);
        if (emojiMatcher.find()) {
            return "";
        }

        //判断二
        for (int i = start; i < end; i++) {
            int type = Character.getType(source.charAt(i));
            if (type == Character.SURROGATE || type == Character.OTHER_SYMBOL) {
                return "";
            }
        }
        return super.filter(source,start,end,dest,dstart,dend);
    }
}
