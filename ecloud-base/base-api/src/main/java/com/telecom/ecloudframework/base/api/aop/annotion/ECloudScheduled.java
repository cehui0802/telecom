package com.telecom.ecloudframework.base.api.aop.annotion;

import java.lang.annotation.*;

/**
 * 调度
 *
 * @author wacxhs
 */
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Target(ElementType.METHOD)
public @interface ECloudScheduled {

    /**
     * cron表达式
     *
     * @return cron表达式
     */
    String cron();
}
