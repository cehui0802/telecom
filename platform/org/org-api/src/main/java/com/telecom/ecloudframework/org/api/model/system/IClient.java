package com.telecom.ecloudframework.org.api.model.system;


import java.util.List;

public interface IClient {

    /**
     * client id
     *
     * @return
     */
    String getId();

    /**
     * client name
     *
     * @return
     */
    String getName();

    /**
     * 密钥
     *
     * @return
     */
    String getSecretKey();

    /**
     * 授权请求路径
     *
     * @return
     */
    List<String> getAuthority();

}