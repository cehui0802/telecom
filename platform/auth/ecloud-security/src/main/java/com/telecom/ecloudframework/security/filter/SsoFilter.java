package com.telecom.ecloudframework.security.filter;

import com.telecom.ecloudframework.base.api.constant.BaseStatusCode;
import com.telecom.ecloudframework.base.api.exception.BusinessException;
import com.telecom.ecloudframework.base.api.response.impl.ResultMsg;
import com.telecom.ecloudframework.base.core.util.AppUtil;
import com.telecom.ecloudframework.base.core.util.RequestContext;
import com.telecom.ecloudframework.org.api.model.ISSOUser;
import com.telecom.ecloudframework.org.api.model.IUser;
import com.telecom.ecloudframework.org.api.service.SSOService;
import com.telecom.ecloudframework.org.api.service.UserService;
import com.telecom.ecloudframework.security.IngoreChecker;
import com.telecom.ecloudframework.security.config.SsoConfig;
import com.telecom.ecloudframework.security.login.model.LoginUser;
import com.alibaba.fastjson.JSON;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.session.SessionInformation;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

/**
 * <pre>
 * 描述： sso拦截器
 * </pre>
 *
 * @author 谢石
 * @date 2020-8-1
 */
public class SsoFilter extends IngoreChecker implements Filter {

    private Logger logger = LoggerFactory.getLogger(SsoFilter.class);

    @Autowired
    SsoConfig ssoConfig;
    @Autowired
    private UserService userService;
    @Autowired(required = false)
    private SessionRegistry sessionRegistry;

    /**
     * 构造函数
     */
    public SsoFilter(){
        ssoConfig = AppUtil.getBean(SsoConfig.class);
        userService = AppUtil.getBean(UserService.class);
        sessionRegistry = AppUtil.getBean(SessionRegistry.class);
    }

    /**
     * 创建session
     *
     * @param sessionId
     * @param userName
     * @param ifInit    是否初始化创建session
     * @author 谢石
     * @date 2020-7-23
     */
    private void creatSession(String sessionId, String userName, boolean ifInit) {
        SecurityContext securityContext = SecurityContextHolder.getContext();
        if (null == securityContext.getAuthentication() || ifInit) {
            creatUserSession(sessionId, userName);
        }
        if (null != sessionRegistry) {
            SessionInformation sessionInformation = sessionRegistry.getSessionInformation(sessionId);
            if (sessionInformation == null || ifInit) {
                LoginUser loginUser = creatUserSession(sessionId, userName);
                sessionRegistry.registerNewSession(sessionId, loginUser);
            } else {
                //会话续期
                sessionRegistry.refreshLastRequest(sessionId);
            }
        }
    }

    /**
     * 创建用户session
     *
     * @param sessionId
     * @param userName
     * @return
     * @author 谢石
     * @date 2020-8-5
     */
    private LoginUser creatUserSession(String sessionId, String userName) {
        HttpServletRequest request = RequestContext.getHttpServletRequest();
        HttpServletResponse response = RequestContext.getHttpServletResponse();
        IUser defaultUser = userService.getUserByAccount(userName);
        if (defaultUser == null) {
            throw new BusinessException(String.format("用户：[%s]不存在", userName));
        }
        LoginUser loginUser = new LoginUser(defaultUser);
        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(loginUser, null, loginUser.getAuthorities());
        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        SecurityContextHolder.getContext().setAuthentication(authentication);
        return loginUser;
    }

    /**
     * 获取指定类型的SSO服务的实现类
     *
     * @param type
     * @return
     * @author 谢石
     * @date 2020-7-31
     */
    public static SSOService getService(String type) {
        Map<String, SSOService> map = AppUtil.getImplInstance(SSOService.class);
        for (Map.Entry<String, SSOService> entry : map.entrySet()) {
            if (entry.getValue().getType().equals(type)) {
                return entry.getValue();
            }
        }
        throw new BusinessException(String.format("找不到类型[%s]的SSO服务的实现类", type));
    }

    private void writeResponseNoAccess(HttpServletResponse response, String sessionId) {

        try {
            ResultMsg resultMsg = new ResultMsg<>(BaseStatusCode.TIMEOUT, "认证失败");
            response.getWriter().print(JSON.toJSONString(resultMsg));
        } catch (Exception e) {
            logger.error("writeResponse(sessionId:{})异常", sessionId, e);
        }

    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse resp = (HttpServletResponse) response;
        String url = req.getServletPath();
        if (!isIngores(url)) {
            //获取用户信息 和 openapi 不拦截
            if (null != ssoConfig.getEnable() && ssoConfig.getEnable()
                    && !("/privilege/loadUserByUsername".equals(url)
                    || url.startsWith("/openapi/"))) {
                boolean ifInit = false;
                if ("/org/sso/init".equals(url)) {
                    ifInit = true;
                }
                SSOService ssoService = getService(ssoConfig.getSsoType());
                String sessionId = req.getSession().getId();
                //获取SSO用户信息
                ISSOUser ssoUser = ssoService.getSSOUser(req, resp);
                //用户为空则认证失败
                if (ssoUser == null) {
                    writeResponseNoAccess(resp, sessionId);
                    return;
                }
                //更新session信息
                creatSession(sessionId, ssoUser.getAccount(), ifInit);
            }
        }
        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {

    }
}
