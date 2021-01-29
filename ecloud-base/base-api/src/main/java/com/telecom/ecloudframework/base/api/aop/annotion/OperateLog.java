package com.telecom.ecloudframework.base.api.aop.annotion;

import com.telecom.ecloudframework.base.api.handler.OperateLogHandler;

import java.lang.annotation.*;

/**
 * 操作日志注解，记录操作日志信息
 *
 * @author guolihao
 * @date 2020/9/18 10:50
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface OperateLog {

    /**
     * 是否写入返回结果
     */
    boolean writeResponse() default true;

    /**
     * 是否写入请求参数
     */
    boolean writeRequest() default true;

    /**
     * 特殊接口处理，通过handler返回值来保存配置项
     */
    Class<? extends OperateLogHandler> handler() default OperateLogHandler.class;
}
