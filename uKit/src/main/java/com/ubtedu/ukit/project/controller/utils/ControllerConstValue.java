/**
 * @2018 UBTECH Robotics Corp. All rights reserved (C)
 **/
package com.ubtedu.ukit.project.controller.utils;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Point;
import android.hardware.display.DisplayManager;
import android.view.Display;

import com.ubtedu.ukit.R;
import com.ubtedu.ukit.application.UKitApplication;
import com.ubtedu.ukit.project.controller.model.item.WidgetItem;

import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * @Author naOKi
 * @Date 2018/11/29
 **/
public class ControllerConstValue {

    public static final long DEFAULT_ANIMATION_DURATION = 150L;

    //一些控件的尺寸或距离，代码中经常用到，这里提前计算，以便快速使用，需要和UI布局的尺寸保持一致
    public static final int TOOLBAR_WIDTH_PIXEL;
    public static final int WIDGET_LIST_WIDTH_PIXEL;
    public static final int TITLE_HEIGHT_PIXEL;
    public static final int BG_RADIUS_PIXEL;
    public static final int WIDGET_ACTION_Y_PIXEL;

    //产品需求，遥控器宫格尺寸
    public static final int GRID_X_CELLS = 27;
    public static final int GRID_Y_CELLS = 16;

    public static final int JOYSTICK_SLIDER_SIZE;

    public static final int SCREEN_WIDTH;
    public static final int SCREEN_HEIGHT;

    public static final int CELL_WIDTH;
    public static final int CELL_HEIGHT;
    public static final int CELL_SIZE;

    public static final int COLOR_READER_RADIUS;

    public static final int COUNTDOWN_BORDER_SIZE;

    public static final String LUA_FILE_TEMPLATE;

    public static final int[] ICON_RES_ARRAY = {
            R.drawable.act_icon_0, R.drawable.act_icon_1, R.drawable.act_icon_2, R.drawable.act_icon_3,
            R.drawable.act_icon_4, R.drawable.act_icon_5, R.drawable.act_icon_6, R.drawable.act_icon_7,
            R.drawable.act_icon_8, R.drawable.act_icon_9, R.drawable.act_icon_10, R.drawable.act_icon_11,
            R.drawable.act_icon_12, R.drawable.act_icon_13, R.drawable.act_icon_14, R.drawable.act_icon_15,
            R.drawable.act_icon_16, R.drawable.act_icon_17, R.drawable.act_icon_18, R.drawable.act_icon_19,
            R.drawable.act_icon_20, R.drawable.act_icon_21, R.drawable.act_icon_22, R.drawable.act_icon_23,
            R.drawable.act_icon_24, R.drawable.act_icon_25, R.drawable.act_icon_26, R.drawable.act_icon_27,
            R.drawable.act_icon_28, R.drawable.act_icon_29, R.drawable.act_icon_30, R.drawable.act_icon_31,
            R.drawable.act_icon_32, R.drawable.act_icon_33, R.drawable.act_icon_34, R.drawable.act_icon_35,
            R.drawable.act_icon_36
    };

    static {
        Resources res = UKitApplication.getInstance().getResources();
        TOOLBAR_WIDTH_PIXEL = res.getDimensionPixelOffset(R.dimen.ubt_dimen_334px);
        TITLE_HEIGHT_PIXEL = res.getDimensionPixelOffset(R.dimen.ubt_dimen_128px);
        BG_RADIUS_PIXEL = res.getDimensionPixelOffset(R.dimen.ubt_dimen_35px);
        COLOR_READER_RADIUS = res.getDimensionPixelOffset(R.dimen.ubt_dimen_3px);
        COUNTDOWN_BORDER_SIZE = res.getDimensionPixelOffset(R.dimen.ubt_dimen_12px);
        JOYSTICK_SLIDER_SIZE = res.getDimensionPixelOffset(R.dimen.ubt_dimen_120px);
        WIDGET_ACTION_Y_PIXEL = res.getDimensionPixelOffset(R.dimen.ubt_dimen_172px) + res.getDimensionPixelOffset(R.dimen.ubt_dimen_58px);
        DisplayManager dm = (DisplayManager) UKitApplication.getInstance().getSystemService(Context.DISPLAY_SERVICE);
        if (dm != null) {
            Display display = dm.getDisplay(Display.DEFAULT_DISPLAY);
            Point screenSize = new Point();
            display.getRealSize(screenSize);
            SCREEN_WIDTH = screenSize.x;
            SCREEN_HEIGHT = screenSize.y;
        } else {
            SCREEN_WIDTH = res.getDisplayMetrics().widthPixels;
            SCREEN_HEIGHT = res.getDisplayMetrics().heightPixels;
        }
        CELL_WIDTH = (SCREEN_WIDTH - TOOLBAR_WIDTH_PIXEL) / GRID_X_CELLS;
        CELL_HEIGHT = (SCREEN_HEIGHT - TITLE_HEIGHT_PIXEL) / GRID_Y_CELLS;
        CELL_SIZE = Math.min(CELL_WIDTH, CELL_HEIGHT);
        int maxSpanH = WidgetItem.findMaxSpanH();
        if (maxSpanH > 0) {
            WIDGET_LIST_WIDTH_PIXEL = TOOLBAR_WIDTH_PIXEL + ((maxSpanH + 1) * CELL_WIDTH);
        } else {
            WIDGET_LIST_WIDTH_PIXEL = res.getDimensionPixelOffset(R.dimen.ubt_dimen_800px);
        }
        LUA_FILE_TEMPLATE = readFromAssets("lua/template.lua");
    }

    private static String readFromAssets(String path) {
        String result = null;
        try (InputStream is = UKitApplication.getInstance().getAssets().open(path);
             OutputStream os = new ByteArrayOutputStream()) {

            int readLen;
            byte[] buffer = new byte[8 * 1024];
            while ((readLen = is.read(buffer, 0, buffer.length)) != -1) {
                os.write(buffer, 0, readLen);
            }
            result = new String(((ByteArrayOutputStream) os).toByteArray(), "UTF-8");
        } catch (Exception e) {
            // ignore
        }
        return result;
    }

    private static void close(Closeable closeable) {
        try {
            closeable.close();
        } catch (Exception e) {
            // ignore
        }
    }

}
