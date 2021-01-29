package com.telecom.ecloudframework.base.api.handler;

/**
 * 日志记录：特殊接口参数处理，返回日志配置信息
 *
 * @author guolihao
 * @date 2020/11/11 20:21
 */
public interface OperateLogHandler {

    /**
     * 特殊接口处理，通过handler返回值来保存配置项
     *
     * @return Object
     * @author guolihao
     * @date 2020/11/11 20:34
     */
    Object getLogConfig(Object... object);
}
