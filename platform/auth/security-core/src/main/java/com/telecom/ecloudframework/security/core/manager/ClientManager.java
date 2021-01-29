package com.telecom.ecloudframework.security.core.manager;

import com.telecom.ecloudframework.base.manager.Manager;
import com.telecom.ecloudframework.security.core.model.Client;

/**
 * <pre>
 * 描述：客户端 处理接口
 * </pre>
 *
 * @author 谢石
 * @date 2020-8-3
 */
public interface ClientManager extends Manager<String, Client> {
    /**
     * 重新生成秘钥
     *
     * @param id
     * @author 谢石
     * @date 2020-8-7
     */
    void updateSecretKey(String id);
}
