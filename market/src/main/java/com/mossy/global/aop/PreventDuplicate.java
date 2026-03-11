package com.mossy.global.aop;

import com.mossy.exception.ErrorCode;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface PreventDuplicate {
    String keyPrefix();

    ErrorCode errorCode();

    int ttlSeconds() default 3;
}
