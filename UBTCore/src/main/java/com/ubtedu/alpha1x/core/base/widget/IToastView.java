package com.ubtedu.alpha1x.core.base.widget;

public interface IToastView {
    void setText(String message);
    void setGravity(int gravity, int xOffset, int yOffset);
    void setDuration(int duration);
    void show();
    void cancel();
}
