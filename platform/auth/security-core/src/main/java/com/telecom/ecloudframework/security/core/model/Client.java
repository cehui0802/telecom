package com.telecom.ecloudframework.security.core.model;

import com.telecom.ecloudframework.base.api.model.IDModel;
import com.telecom.ecloudframework.org.api.model.system.IClient;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * <pre>
 * 描述：客户端实体
 * </pre>
 *
 * @author 谢石
 * @date 2020-8-3
 */
public class Client implements IClient, IDModel {
    /**
     * 唯一标识
     */
    private String id;
    /**
     * 名称
     */
    private String name;
    /**
     * 私钥
     */
    private String secretKey;
    /**
     * 权限地址
     */
    private String sAuthority;
    /**
     * 权限地址集合
     */
    private List<String> authority;

    @Override
    public String getId() {
        return id;
    }

    @Override
    public void setId(String id) {
        this.id = id;
    }

    @Override
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String getSecretKey() {
        return secretKey;
    }

    public void setSecretKey(String secretKey) {
        this.secretKey = secretKey;
    }

    public String getsAuthority() {
        return sAuthority;
    }

    public void setsAuthority(String sAuthority) {
        this.sAuthority = sAuthority;
    }

    @Override
    public List<String> getAuthority() {
        if (authority == null) {
            if (StringUtils.isNotEmpty(sAuthority)) {
                String[] arrAuthority = sAuthority.split(",");
                authority = new ArrayList<>();
                Collections.addAll(authority, arrAuthority);
            }
        }
        return authority;
    }

    public void setAuthority(List<String> authority) {
        this.authority = authority;
    }
}
