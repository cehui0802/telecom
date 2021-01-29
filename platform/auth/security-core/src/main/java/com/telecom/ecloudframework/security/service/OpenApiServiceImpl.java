package com.telecom.ecloudframework.security.service;

import com.telecom.ecloudframework.base.api.exception.BusinessException;
import com.telecom.ecloudframework.base.core.util.StringUtil;
import com.telecom.ecloudframework.org.api.model.system.IClient;
import com.telecom.ecloudframework.security.authentication.api.OpenApiService;
import com.telecom.ecloudframework.security.core.manager.ClientManager;
import com.telecom.ecloudframework.security.core.model.Client;
import com.telecom.ecloudframework.security.core.model.ClientContext;
import cn.hutool.crypto.symmetric.SymmetricAlgorithm;
import cn.hutool.crypto.symmetric.SymmetricCrypto;
import com.alibaba.fastjson.JSON;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;


/**
 * @author
 */
@Service
public class OpenApiServiceImpl implements OpenApiService {
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Value("${ecloud.openapi.header.clientid:ecloud-openapi-clientid}")
    String clientIdHeader;
    @Value("${ecloud.openapi.header.context:ecloud-openapi-context}")
    String contextHeader;
    @Value("${ecloud.openapi.token.expiretime:60000}")
    long tokenExpireTime;

    @Resource
    ClientManager clientManager;

    @Override
    public String getClientHeader() {
        return clientIdHeader;
    }

    @Override
    public String getContextHeader() {
        return contextHeader;
    }

    @Override
    public IClient parserToken(String clientId, String token) {
        if (StringUtil.isNotEmpty(token)) {
            Client client = clientManager.get(clientId);
            if (null == client) {
                throw new BusinessException(String.format("客户端[%s]不存在", clientId));
            }

            SymmetricCrypto crypto = new SymmetricCrypto(SymmetricAlgorithm.DES, client.getSecretKey().getBytes());
            ClientContext context;
            try {
                context = JSON.parseObject(crypto.decryptStr(token), ClientContext.class);
            } catch (Exception e) {
                String sMsg = String.format("客户端[%s]密文[%s]解析失败", clientId, token);
                logger.error(sMsg, e);
                throw new BusinessException(sMsg, e.fillInStackTrace());
            }
            if (!clientId.equals(context.getClientId())) {
                throw new BusinessException(String.format("客户端[%s]密文有误", clientId));
            }

            if (context.getCreateTime() == 0 || System.currentTimeMillis() > context.getCreateTime() + tokenExpireTime) {
                throw new BusinessException(String.format("token超时，当前时间[%s],token[%s]", System.currentTimeMillis(), context.getCreateTime()));
            }

            return client;
        }
        return null;
    }

    @Override
    public boolean validation(String url, String clientId, String token) {
        IClient iClient = parserToken(clientId, token);
        if (iClient != null && null != iClient.getAuthority()) {
            for (String authority : iClient.getAuthority()) {
                if (url.equalsIgnoreCase(authority)) {
                    return true;
                }
            }
        }
        return false;
    }
}
