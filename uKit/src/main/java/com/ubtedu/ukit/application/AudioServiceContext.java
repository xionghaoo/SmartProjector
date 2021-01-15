package com.ubtedu.ukit.application;

import android.content.Context;
import android.content.ContextWrapper;

public class AudioServiceContext extends ContextWrapper {

    public AudioServiceContext(Context base) {
        super(base);
    }

    public static ContextWrapper getContext(Context base) {
        return new AudioServiceContext(base);
    }

    @Override
    public Object getSystemService(String name) {
        if (Context.AUDIO_SERVICE.equals(name)) {
            return getApplicationContext().getSystemService(name);
        }
        return super.getSystemService(name);
    }
}