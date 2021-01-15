package com.ubtedu.base.gson;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 在把Model转换成Gson时，忽略掉使用该注解的字段。
 * 需要与{@link GExclusionStrategy}配合使用。
 * Created by qinicy on 2017/6/22.
 */

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface Exclude {

    /**
     * If {@code false}, the field marked with this annotation is written out in the JSON while
     * serializing. If {@code true}, the field marked with this annotation is skipped from the
     * serialized output. Defaults to {@code false}.
     * @return
     */
    boolean serialize() default false;

    /**
     * If {@code false}, the field marked with this annotation is deserialized from the JSON.
     * If {@code true}, the field marked with this annotation is skipped during deserialization.
     * Defaults to {@code false}.
     */
    boolean deserialize() default false;
}
