package com.telecom.ecloudframework.security.authentication.api;

import com.telecom.ecloudframework.org.api.model.system.IClient;

/**
 * @author
 */
public interface OpenApiService {
    /**
     * 获取客户端头
     *
     * @return
     */
    String getClientHeader();

    /**
     * 获取密文头
     *
     * @return
     */
    String getContextHeader();

    /**
     * 解密token
     *
     * @param clientId
     * @param token
     * @return
     */
    IClient parserToken(String clientId, String token);

    /**
     * 校验连接
     *
     * @param url
     * @param clientId
     * @param token
     * @return
     */
    boolean validation(String url, String clientId, String token);
}
