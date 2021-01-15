package com.ubtedu.base.net.image;

/**
 * @Description: 加载工厂，可定制图片加载框架
 * @author: qinicy
 * @date: 17/5/2 15:16
 */
public class LoaderFactory {
    private static ILoader loader;

    public static ILoader getLoader() {
        if (loader == null) {
            synchronized (LoaderFactory.class) {
                if (loader == null) {
                    loader = new GlideLoader();
//                    loader = new PicassoLoader();
                }
            }
        }
        return loader;
    }
}
