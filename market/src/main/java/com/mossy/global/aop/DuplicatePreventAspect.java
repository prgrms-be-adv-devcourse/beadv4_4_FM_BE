package com.mossy.global.aop;

import com.mossy.exception.DomainException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class DuplicatePreventAspect {

    private final StringRedisTemplate redisTemplate;

    @Around("@annotation(preventDuplicate)")
    public Object prevent(ProceedingJoinPoint joinPoint, PreventDuplicate preventDuplicate) throws Throwable {
        String key = buildRedisKey(joinPoint, preventDuplicate.keyPrefix());

        Boolean isFirstRequest = redisTemplate.opsForValue()
                .setIfAbsent(key, "1", preventDuplicate.ttlSeconds(), TimeUnit.SECONDS);

        if (Boolean.FALSE.equals(isFirstRequest)) {
            log.warn("중복 요청 감지 - key: {}", key);
            throw new DomainException(preventDuplicate.errorCode());
        }

        return joinPoint.proceed();
    }

    private String buildRedisKey(ProceedingJoinPoint joinPoint, String keyPrefix) {
        Object[] args = joinPoint.getArgs();

        if (args == null || args.length == 0) {
            return keyPrefix;
        }

        String parameterKey = Stream.of(args)
                .filter(Objects::nonNull)
                .filter(this::isPrimitiveOrString)
                .map(String::valueOf)
                .collect(Collectors.joining(":"));

        return parameterKey.isEmpty()
                ? keyPrefix
                : keyPrefix + ":" + parameterKey;
    }

    private boolean isPrimitiveOrString(Object arg) {
        Class<?> clazz = arg.getClass();
        return clazz.isPrimitive()
                || clazz == String.class
                || clazz == Long.class
                || clazz == Integer.class
                || clazz == Boolean.class
                || clazz == Double.class
                || clazz == Float.class;
    }
}
