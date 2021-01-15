package com.ubtedu.ukit.project.controller.interfaces;

import android.view.View;

/**
 * @Author naOKi
 * @Date 2018/11/19
 **/
public interface IControllerWidgetListener {
    void onWidgetClick(View view);
    void onWidgetMove(View view);
    String getBlocklyContent(String id, String name);
}
