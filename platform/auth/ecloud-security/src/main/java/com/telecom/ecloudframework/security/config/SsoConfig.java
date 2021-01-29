package com.telecom.ecloudframework.security.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

/**
 * <pre>
 * 描述：SSO配置实体
 * </pre>
 *
 * @author 谢石
 * @date 2020-7-30
 */
@Configuration
public class SsoConfig {
    /**
     * 是否启用sso
     */
    @Value("${ecloud.sso.enable:false}")
    private Boolean enable;
    /**
     * sso类型
     */
    @Value("${ecloud.sso.type:}")
    public String ssoType;

    public Boolean getEnable() {
        return enable;
    }

    public void setEnable(Boolean enable) {
        this.enable = enable;
    }

    public String getSsoType() {
        return ssoType;
    }

    public void setSsoType(String ssoType) {
        this.ssoType = ssoType;
    }
}