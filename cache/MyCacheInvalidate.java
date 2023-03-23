package com.irobotics.mall.cache;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 失效缓存
 *
 * @author chenaixin
 * @description
 * @date 2023-03-22
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface MyCacheInvalidate {
    // del key
    String[] keys();
}