package com.ubtrobot.smartprojector.widgets.flippage;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;

import com.ubtrobot.smartprojector.R;

public class BitmapProvider {

    private int[] resIds = new int[] {
            R.drawable.p1_480,
            R.drawable.p2_480,
            R.drawable.p3_480,
            R.drawable.p4_480,
            R.drawable.p5_480,
            R.drawable.p6_480,
            R.drawable.p7_480,
            R.drawable.p8_480,
            R.drawable.p9_480,
    };


    /**
     * Load bitmap from resources randomly
     *
     * @return bitmap object
     */
    public Bitmap getBitmap(Context context, int page) {
        Bitmap b = BitmapFactory.decodeResource(context.getResources(), resIds[page]);
//        Matrix matrix = new Matrix();
//        matrix.postRotate(90);
//        Bitmap lb = Bitmap.createBitmap(b, 0, 0, b.getWidth(), b.getHeight(),
//                matrix, true);
//        b.recycle();
        return b;
    }
}
