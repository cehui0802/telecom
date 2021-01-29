package com.telecom.ecloudframework.org.api.service;

import com.telecom.ecloudframework.org.api.model.ISSOUser;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author
 */
public interface SSOService {

    /**
     * 获取sso 用户信息
     *
     * @param request
     * @param response
     * @return
     */
    ISSOUser getSSOUser(HttpServletRequest request, HttpServletResponse response);

    /**
     * 注销sso token
     *
     * @param request
     */
    void invalidSSOToken(HttpServletRequest request, HttpServletResponse response);

    /**
     * 获取服务类型
     */
    String getType();

}
