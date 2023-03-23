package com.irobotics.mall.cache;

import com.alibaba.fastjson.JSONObject;
import jakarta.annotation.Resource;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.CodeSignature;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

/***
 * 失效缓存
 *
 * @author chenaixin
 * @description
 * @date 2023-03-22
 */
@Aspect
@ConditionalOnExpression(value = "'${mycache.enable}'.equals('true')")
@Component
public class MyCacheAspect {
    @Resource(name = "mallPmsRedisTemplate")
    private RedisTemplate redisTemplate;

    @Around("@annotation(myCache)")
    public Object cacheableJson(ProceedingJoinPoint joinPoint, MyCache myCache) throws Throwable {
        String key = myCache.service() + myCache.module() + myCache.method();
        String hashKey = getHashKey(joinPoint);
        //尝试从缓存中获取数据
        String str = String.valueOf(redisTemplate.opsForHash().get(key, hashKey));
        //如果缓存中存在数据，则直接返回
        if (str != null && !str.equalsIgnoreCase("null")) {
            MethodSignature signature = (MethodSignature) joinPoint.getSignature();
            Class returnType = signature.getReturnType();
            Object result = JSONObject.parseObject(str, returnType);
            return result;
        }

        //如果缓存中不存在数据，则执行方法并将结果存储到缓存中
        Object result = joinPoint.proceed();

        String jsonStr = JSONObject.toJSONStringWithDateFormat(result, "millis");
        redisTemplate.opsForHash().put(key, hashKey, jsonStr);
        long expireTime = myCache.expireTime();
        redisTemplate.expire(key, Duration.ofSeconds(expireTime));

        return result;
    }

    /**
     * 构建Redis Hash结构的键
     *
     * @param joinPoint
     * @return
     */
    private String getHashKey(ProceedingJoinPoint joinPoint) {
        Map<String, Object> map = new HashMap<>();
        Object[] values = joinPoint.getArgs();
        String[] names = ((CodeSignature) joinPoint.getSignature()).getParameterNames();
        for (int i = 0; i < names.length; i++) {
            map.put(names[i], values[i]);
        }
        return JSONObject.toJSONString(map);
    }
}