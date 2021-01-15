/**
 * @2018 UBTECH Robotics Corp. All rights reserved (C)
 **/
package com.ubtedu.ukit.common.view;


import android.text.Spanned;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author qinicy
 * @Date 2019/1/25
 **/
public class BlackCharsInputFilter extends EmojiInputFilter {

    private List<CharSequence> mCharSequenceList;
    public BlackCharsInputFilter(int max) {
        super(max);
        mCharSequenceList = new ArrayList<>();
    }

    public BlackCharsInputFilter addFilterChar(CharSequence c){
        mCharSequenceList.add(c);
        return this;
    }
    @Override
    public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
        CharSequence superCharSequence = super.filter(source,start,end,dest,dstart,dend);
        if (superCharSequence != null){
          return superCharSequence;
        }
        for(CharSequence sequence : mCharSequenceList){
            if (sequence.equals(source)){
                return "";
            }
        }
        return super.filter(source,start,end,dest,dstart,dend);
    }
}
