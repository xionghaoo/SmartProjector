package com.ubtedu.ukit.common.analysis;

import java.util.HashMap;
import java.util.Map;

public class PageNames {

    public static final String MAIN = "main";

    /**
     * key:页面的class，value:页面名称
     */
    private static final Map<Class,String> PAGE_MAP;
    static {
        PAGE_MAP = new HashMap<>();
    }
    public static String getPageName(Class clazz){
        return PAGE_MAP.get(clazz);
    }
}
