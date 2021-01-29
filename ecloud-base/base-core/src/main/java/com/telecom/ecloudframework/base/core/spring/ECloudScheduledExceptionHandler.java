package com.telecom.ecloudframework.base.core.spring;

import java.lang.reflect.Method;

/**
 * ab调度异常处理器
 *
 * @author wacxhs
 */
public interface ECloudScheduledExceptionHandler {


    /**
     * 异常处理
     *
     * @param o         调用对象
     * @param method    调用方法
     * @param throwable 异常信息
     */
    void exception(Object o, Method method, Throwable throwable);

}
