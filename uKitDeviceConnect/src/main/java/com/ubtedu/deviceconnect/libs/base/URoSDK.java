package com.ubtedu.deviceconnect.libs.base;

import android.content.Context;

import com.ubtedu.deviceconnect.libs.base.product.URoProductType;
import com.ubtedu.deviceconnect.libs.utils.URoLogUtils;

/**
 * @Author naOKi
 * @Date 2019/08/15
 **/
public class URoSDK {

    private Context context;

    private boolean hasInit = false;

    private boolean debug = false;

    private static final String SDK_VERSION = "1.0.0.beta";
    private static final int SDK_VERSION_INT = 1;

    static final class SingleHolder {
        static URoSDK INSTANCE = null;
    }

    private URoSDK(){}

    public static URoSDK getInstance() {
        synchronized (SingleHolder.class) {
            if(SingleHolder.INSTANCE == null) {
                SingleHolder.INSTANCE = new URoSDK();
            }
            return SingleHolder.INSTANCE;
        }
    }

    public void init(Context context) {
        try {
            URoLogUtils.e("===== URoSDK begin init =====");
            initInternal(context);
            this.context = context;
            hasInit = true;
            URoLogUtils.e("===== URoSDK init SUCCESS =====");
        } catch (Throwable e) {
            URoLogUtils.e(e);
            URoLogUtils.e("===== URoSDK init FAILURE =====");
        } finally {
            URoLogUtils.e("===== URoSDK end init  =====");
        }
    }

    public void debug(boolean enabled) {
        debug = enabled;
    }

    public boolean isDebug() {
        return debug;
    }

    public void initInternal(Context context) throws Throwable {
        if(context == null) {
            throw new NullPointerException("context cannot be null");
        }
        URoLogUtils.e("===== SDK_VERSION: %s(%d) =====", SDK_VERSION, SDK_VERSION_INT);
        this.context = context.getApplicationContext();
    }

    public boolean hasInit() {
        return hasInit;
    }

    public Context getContext() {
        return context;
    }

    public static URoProductType[] getSupportProducts() {
        return new URoProductType[] {
            URoProductType.UKIT_LEGACY,
            URoProductType.UKIT_SMART
        };
    }

    public static int getSdkVersionInt() {
        return SDK_VERSION_INT;
    }

    public static String getSdkVersionString() {
        return SDK_VERSION;
    }
}
