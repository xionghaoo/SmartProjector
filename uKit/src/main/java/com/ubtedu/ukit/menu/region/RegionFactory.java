/**
 * @2018 UBTECH Robotics Corp. All rights reserved (C)
 **/
package com.ubtedu.ukit.menu.region;

import android.content.Context;

import com.ubtedu.ukit.R;

import java.util.Locale;

/**
 * @Author qinicy
 * @Date 2019/3/5
 **/
public class RegionFactory {
    private Context mContext;

    public RegionFactory(Context context) {
        mContext = context;
    }

    public RegionInfo createCN() {
        RegionInfo cn = new RegionInfo();

        cn.name = RegionInfo.REGION_CN;
        cn.locale = Locale.CHINA;

        cn.displayName = mContext.getString(R.string.menu_tab_region_cn);
        cn.backgroundResourceId = R.drawable.meun_region_cn_btn;

        cn.unityTag = RegionInfo.TAG_CN;
        cn.unityLanguage = RegionInfo.LANGUAGE_CN;
        return cn;
    }

    public RegionInfo createNA() {
        RegionInfo na = new RegionInfo();
        na.name = RegionInfo.REGION_NA;
        na.locale = Locale.US;

        na.displayName = mContext.getString(R.string.menu_tab_region_na);
        na.backgroundResourceId = R.drawable.meun_region_en_btn;

        na.unityTag = RegionInfo.TAG_NA;
        na.unityLanguage = RegionInfo.LANGUAGE_NA;
        return na;
    }

    public RegionInfo createGL() {
        RegionInfo gl = new RegionInfo();
        gl.name = RegionInfo.REGION_GL;
        gl.locale = new Locale("und");//需要区分Locale.US，文案有部分差异

        gl.displayName = mContext.getString(R.string.menu_tab_region_en_global);
        gl.backgroundResourceId = R.drawable.meun_region_earth_btn;

        gl.unityTag = RegionInfo.TAG_GL;
        gl.unityLanguage = RegionInfo.LANGUAGE_GL;
        return gl;
    }

    public RegionInfo createPL() {
        RegionInfo gl = new RegionInfo();
        gl.name = RegionInfo.REGION_PL;
        gl.locale = new Locale("pl", "PL");

        gl.displayName = mContext.getString(R.string.menu_tab_region_pl);
        gl.backgroundResourceId = R.drawable.meun_language_poland_btn;

        gl.unityTag = RegionInfo.TAG_PL;
        gl.unityLanguage = RegionInfo.LANGUAGE_PL;
        return gl;
    }

    public RegionInfo createKR() {
        RegionInfo gl = new RegionInfo();
        gl.name = RegionInfo.REGION_KR;
        gl.locale =Locale.KOREA;

        gl.displayName = mContext.getString(R.string.menu_tab_region_kr);
        gl.backgroundResourceId = R.drawable.meun_language_korea_btn;

        gl.unityTag = RegionInfo.TAG_KR;
        gl.unityLanguage = RegionInfo.LANGUAGE_KR;
        return gl;
    }

    public RegionInfo createVN() {
        RegionInfo gl = new RegionInfo();
        gl.name = RegionInfo.REGION_VN;
        gl.locale =new Locale("vi", "VN");;

        gl.displayName = mContext.getString(R.string.menu_tab_region_vn);
        gl.backgroundResourceId = R.drawable.meun_language_yuenan_btn;

        gl.unityTag = RegionInfo.TAG_VN;
        gl.unityLanguage = RegionInfo.LANGUAGE_VN;
        return gl;
    }

    public RegionInfo createBD() {
        RegionInfo gl = new RegionInfo();
        gl.name = RegionInfo.REGION_BD;
        gl.locale =new Locale("bn", "BD");;

        gl.displayName = mContext.getString(R.string.menu_tab_region_bd);
        gl.backgroundResourceId = R.drawable.meun_language_mengjiala_btn;

        gl.unityTag = RegionInfo.TAG_BD;
        gl.unityLanguage = RegionInfo.LANGUAGE_BD;
        return gl;
    }
}
