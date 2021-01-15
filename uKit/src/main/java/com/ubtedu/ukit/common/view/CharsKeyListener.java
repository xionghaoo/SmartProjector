/**
 * @2018 UBTECH Robotics Corp. All rights reserved (C)
 **/
package com.ubtedu.ukit.common.view;

import android.text.method.NumberKeyListener;

import androidx.annotation.NonNull;

/**
 * @Author qinicy
 * @Date 2019/2/27
 **/
public class CharsKeyListener extends NumberKeyListener {
    private int mInputType;
    private String mAcceptChars;

    public CharsKeyListener(int inputType, String acceptChars) {
        mInputType = inputType;
        mAcceptChars = acceptChars;
    }

    @NonNull
    @Override
    protected char[] getAcceptedChars() {
        if (mAcceptChars == null) {
            return new char[]{};
        }

        char[] mAccepted = new char[mAcceptChars.length()];
        mAcceptChars.getChars(0, mAcceptChars.length(), mAccepted, 0);
        return mAccepted;
    }

    @Override
    public int getInputType() {
        return mInputType;
    }
}
