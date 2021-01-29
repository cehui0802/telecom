package com.telecom.ecloudframework.security.service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 描述：用户与组服务接口
 * <pre>
 * </pre>
 *
 * @author
 */
public interface ILoginService {
    /**
     * 登录前
     *
     * @param request
     * @param response
     * @return
     * @author 谢石
     * @date 2020-12-3
     */
    String beforeLogin(HttpServletRequest request, HttpServletResponse response);

    /**
     * 登录后
     *
     * @param request
     * @param response
     * @author 谢石
     * @date 2020-12-3
     */
    void afterLogin(HttpServletRequest request, HttpServletResponse response);

    /**
     * 获取登录类型
     *
     * @return
     * @author 谢石
     * @date 2020-12-3
     */
    String getType();
}
