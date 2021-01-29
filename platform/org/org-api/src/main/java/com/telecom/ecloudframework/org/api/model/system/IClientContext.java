package com.telecom.ecloudframework.org.api.model.system;


/**
 * <pre>
 * 描述：客户端上下文内容接口
 * </pre>
 *
 * @author 谢石
 * @date 2020-8-10
 */
public interface IClientContext {

    /**
     * 客户端 id
     *
     * @return
     */
    String getClientId();

    /**
     * 创建时间
     *
     * @return
     */
    Long getCreateTime();

    /**
     * 用户账号
     *
     * @return
     */
    String getUserName();

}