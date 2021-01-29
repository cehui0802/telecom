package com.telecom.ecloudframework.security.authentication.api;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;

/**
 * 多来源登录认证
 *
 * @author wacxhs
 */
public interface MultipleFromAuthentication {

    /**
     * 获取来源
     *
     * @return 来源
     */
    String getFrom();

    /**
     * 鉴权认证
     *
     * @param userDetails    登录用户详情
     * @param authentication 用户密码鉴权串
     * @throws AuthenticationException 鉴权异常
     */
    void authentication(UserDetails userDetails, UsernamePasswordAuthenticationToken authentication) throws AuthenticationException;
}
