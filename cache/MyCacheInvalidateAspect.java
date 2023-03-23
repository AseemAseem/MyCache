package com.irobotics.mall.cache;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

/**
 * 失效缓存切面
 *
 * @author chenaixin
 * @description
 * @date 2023-03-22
 */
@Slf4j
@Aspect
@ConditionalOnExpression(value = "'${mycache.enable}'.equalsIgnoreCase('true')")
@Component
public class MyCacheInvalidateAspect {
    @Resource(name = "mallPmsRedisTemplate")
    private RedisTemplate redisTemplate;

    @Around("@annotation(myCacheInvalidate)")
    public Object handle(ProceedingJoinPoint joinPoint, MyCacheInvalidate myCacheInvalidate) throws Throwable {
        String[] keys = myCacheInvalidate.keys();
        if (keys != null && keys.length > 0) {
            try {
                List<String> list = Arrays.asList(keys);
                Long delete = redisTemplate.delete(list);
                log.error("清除缓存{}条，共{}条，keys:{}", delete, list.size(), list);
            } catch (Exception e) {
                log.error("清除缓存失败，keys：" + keys);
            }
        }
        return joinPoint.proceed();
    }
}
