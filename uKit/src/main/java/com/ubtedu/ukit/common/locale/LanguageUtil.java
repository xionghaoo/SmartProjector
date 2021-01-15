package com.ubtedu.ukit.common.locale;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.util.DisplayMetrics;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.ubtedu.ukit.R;
import com.ubtedu.ukit.menu.settings.Settings;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.lang.reflect.Type;
import java.nio.CharBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * 多语言切换的帮助类
 * http://blog.csdn.net/finddreams
 */
public class LanguageUtil {

    private static final String TAG = "MultiLanguageUtil";
    private Context mContext;

    private LanguageUtil() {
    }

    private static class SingletonHolder {
        private final static LanguageUtil instance = new LanguageUtil();
    }

    public static LanguageUtil getInstance() {
        if (SingletonHolder.instance.mContext == null) {
            throw new IllegalStateException("You must be init LanguageUtil first");
        }
        return LanguageUtil.SingletonHolder.instance;
    }


    public static void init(Context mContext) {
        SingletonHolder.instance.mContext = mContext;
    }


    /**
     * 设置语言
     */
    public void updateConfiguration() {
        Locale targetLocale = getLanguageLocale();
        Configuration configuration = mContext.getResources().getConfiguration();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            configuration.setLocale(targetLocale);
        } else {
            configuration.locale = targetLocale;
        }
        Resources resources = mContext.getResources();
        DisplayMetrics dm = resources.getDisplayMetrics();
        resources.updateConfiguration(configuration, dm);//语言更换生效的代码!
    }


    private Locale getLanguageLocale() {
        return Settings.getLocale();
    }


    public static Context attachBaseContext(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            return createConfigurationResources(context);
        } else {
            LanguageUtil.getInstance().updateConfiguration();
            return context;
        }
    }

    @TargetApi(Build.VERSION_CODES.N)
    private static Context createConfigurationResources(Context context) {
        Resources resources = context.getResources();
        Configuration configuration = resources.getConfiguration();
        Locale locale = getInstance().getLanguageLocale();
        configuration.setLocale(locale);
        return context.createConfigurationContext(configuration);
    }

    /**
     * 根据语言设置读取国家名翻译
     *
     * @param context
     * @return 国家名和区域码
     * @throws IOException
     */
    public static List<UBTLocale> getI18NCountryListTranslate(Context context) {

        CharBuffer buffer = CharBuffer.allocate(50 * 1024);
        int length = 0;
        try (InputStream input = context.getResources().openRawResource(R.raw.diallingcode);
             Reader reader = new InputStreamReader(input)) {
            length = reader.read(buffer);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (length != 0) {
            buffer.flip();
        }

        Type collectionType = new TypeToken<ArrayList<UBTLocale>>() {
        }.getType();
        List<UBTLocale> nsLocaleList = new Gson().fromJson(buffer.toString(), collectionType);
        return nsLocaleList;
    }
}
