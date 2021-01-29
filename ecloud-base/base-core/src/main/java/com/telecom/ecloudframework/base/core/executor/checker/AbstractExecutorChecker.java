package com.telecom.ecloudframework.base.core.executor.checker;

import com.telecom.ecloudframework.base.api.executor.checker.ExecutorChecker;

/**
 * 执行器的校验者的抽象类
 *
 * @author aschs
 */
public abstract class AbstractExecutorChecker implements ExecutorChecker {
    /**
     * <pre>
     * 校验器的key
     * 默认是类名
     * </pre>
     *
     * @return
     */
    @Override
    public String getKey() {
        return this.getClass().getSimpleName();
    }
}
