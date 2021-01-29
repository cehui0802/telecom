package com.telecom.ecloudframework.security.core.model;

import com.telecom.ecloudframework.org.api.model.system.IClientContext;

/**
 * <pre>
 * 描述：客户端上下文内容实体
 * </pre>
 *
 * @author 谢石
 * @date 2020-8-10
 */
public class ClientContext implements IClientContext {
    /**
     * 客户端id
     */
    private String clientId;
    /**
     * 创建时间
     */
    private Long createTime;
    /**
     * 用户账号
     */
    private String userName;

    @Override
    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    @Override
    public Long getCreateTime() {
        return createTime;
    }

    public ClientContext setCreateTime(Long createTime) {
        this.createTime = createTime;
        return this;
    }

    @Override
    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }
}
