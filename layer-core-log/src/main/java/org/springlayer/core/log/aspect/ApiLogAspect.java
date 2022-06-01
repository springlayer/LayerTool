package org.springlayer.core.log.aspect;

import org.springlayer.core.log.annotation.ApiLog;
import org.springlayer.core.log.publisher.ApiLogPublisher;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.context.annotation.Configuration;

/**
 * @author zhihou
 * @date 2022/4/23 14:59
 * @description
 */
@Slf4j
@Aspect
@Configuration
public class ApiLogAspect {

    @Around("@annotation(apiLog)")
    public Object around(ProceedingJoinPoint point, ApiLog apiLog) throws Throwable {
        // 执行方法
        Object result = point.proceed();
        // 类名+方法名
        String methodName = point.getTarget().getClass().getName() + "." + point.getSignature().getName();
        // 记录日志
        ApiLogPublisher.publishEvent(apiLog, methodName);
        return result;
    }
}