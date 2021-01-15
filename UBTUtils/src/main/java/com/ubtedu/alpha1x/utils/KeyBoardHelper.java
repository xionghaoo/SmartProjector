package com.ubtedu.alpha1x.utils;

import android.app.Activity;
import android.content.Context;
import android.graphics.Rect;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;

/**
 * Created by qinicy on 2017/8/16.
 */

public class KeyBoardHelper {
    private Activity activity;
    private OnKeyBoardStatusChangeListener onKeyBoardStatusChangeListener;
    private int screenHeight;
    // 空白高度 = 屏幕高度 - 当前 Activity 的可见区域的高度
    // 当 blankHeight 不为 0 即为软键盘高度。
    private int blankHeight = 0;

    public KeyBoardHelper(Activity activity) {
        this.activity = activity;
        screenHeight = activity.getResources().getDisplayMetrics().heightPixels;
        activity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

    }

    public void onCreate() {
        View content = activity.findViewById(android.R.id.content);
        content.getViewTreeObserver().addOnGlobalLayoutListener(onGlobalLayoutListener);
    }

    public void onDestory() {
        View content = activity.findViewById(android.R.id.content);
        content.getViewTreeObserver().removeOnGlobalLayoutListener(onGlobalLayoutListener);
    }

    private ViewTreeObserver.OnGlobalLayoutListener onGlobalLayoutListener = new ViewTreeObserver.OnGlobalLayoutListener() {

        @Override
        public void onGlobalLayout() {
            Rect rect = new Rect();
            activity.getWindow().getDecorView().getWindowVisibleDisplayFrame(rect);
            int newBlankheight = screenHeight - rect.bottom;
            if (newBlankheight != blankHeight) {
                if (newBlankheight == 0) {
                    // keyboard close
                    if (onKeyBoardStatusChangeListener != null) {
                        onKeyBoardStatusChangeListener.OnKeyBoardClose(blankHeight);
                    }
                } else {

                    // keyboard pop
                    if (onKeyBoardStatusChangeListener != null) {
                        onKeyBoardStatusChangeListener.OnKeyBoardPop(newBlankheight);
                    }
                }
            }
            blankHeight = newBlankheight;
        }
    };

    public void setOnKeyBoardStatusChangeListener(
            OnKeyBoardStatusChangeListener onKeyBoardStatusChangeListener) {
        this.onKeyBoardStatusChangeListener = onKeyBoardStatusChangeListener;
    }

    public interface OnKeyBoardStatusChangeListener {

        void OnKeyBoardPop(int keyBoardheight);

        void OnKeyBoardClose(int oldKeyBoardheight);
    }

    public static void hideSoftKeyBoard(View view) {
        if (view != null){
            InputMethodManager imm =
                    (InputMethodManager) view.getContext().getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }

    }

    public static void openSoftKeyboard(Context context) {
        if (context != null){
            InputMethodManager imm = (InputMethodManager) context.getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.toggleSoftInput(InputMethodManager.SHOW_IMPLICIT, InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

}
