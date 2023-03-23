package com.irobotics.mall.cache;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 缓存
 *
 * @author chenaixin
 * @description
 * @date 2023-03-22
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface MyCache {
    // 类似于 hset key field value 即： hset service+module+method  参数json  返回值
    // 微服务
    String service();

    // 模块
    String module();

    // 方法
    String method();

    // 默认失效时间,单位：秒
    long expireTime() default 10 * 60;
}