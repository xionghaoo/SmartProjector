package com.ubtedu.base.gson;


import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;

import java.util.Collection;

/**
 * 需要配合注解{@link Exclude}使用
 * Created by qinicy on 2017/6/22.
 */

public class GExclusionStrategy implements ExclusionStrategy {

    private boolean mSerialization;
    private String mExcludeClassName;
    private boolean isExcludeListOrArray;

    /**
     * @param serialization 当{@link com.google.gson.GsonBuilder#addSerializationExclusionStrategy(ExclusionStrategy)}为true;
     *                      当{@link com.google.gson.GsonBuilder#addDeserializationExclusionStrategy(ExclusionStrategy)}为false;
     */
    public GExclusionStrategy(boolean serialization) {
        mSerialization = serialization;
    }

    /**
     * 排除对应Class定义的Field，可以用来排除父类的Field。
     *
     * @param declaringClass 排除该Class定义的Field
     */
    public void excludeDeclaringClass(String declaringClass) {
            this.mExcludeClassName = declaringClass;
    }

    /**
     * 排除数组或者List
     *
     * @param exclude
     */
    public void excludeListOrArray(boolean exclude) {
        isExcludeListOrArray = exclude;
    }

    @Override
    public boolean shouldSkipField(FieldAttributes f) {


        Class clazz = f.getDeclaringClass();
        Class fieldClass = f.getDeclaredClass();

        boolean clazzExclude =  clazz.getName().contains(mExcludeClassName);
        boolean arrayExclude = isExcludeListOrArray && fieldClass.isArray();
        boolean listExclude = isExcludeListOrArray && Collection.class.isAssignableFrom(fieldClass);

        Exclude exclude = f.getAnnotation(Exclude.class);
        if (mSerialization) {
            return (exclude != null && exclude.serialize()) || clazzExclude || arrayExclude || listExclude;
        } else {
            return (exclude != null && exclude.deserialize()) || clazzExclude || arrayExclude || listExclude;
        }
    }

    @Override
    public boolean shouldSkipClass(Class<?> clazz) {
        return false;
    }
}
