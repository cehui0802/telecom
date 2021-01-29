package com.telecom.ecloudframework.security.rest.controller;

import com.telecom.ecloudframework.base.api.aop.annotion.CatchErr;
import com.telecom.ecloudframework.base.api.aop.annotion.OperateLog;
import com.telecom.ecloudframework.base.api.exception.BusinessException;
import com.telecom.ecloudframework.base.api.exception.BusinessMessage;
import com.telecom.ecloudframework.base.api.response.impl.ResultMsg;
import com.telecom.ecloudframework.base.core.cache.ICache;
import com.telecom.ecloudframework.base.core.jwt.JWTService;
import com.telecom.ecloudframework.base.core.util.AppUtil;
import com.telecom.ecloudframework.base.core.util.RequestContext;
import com.telecom.ecloudframework.base.core.util.StringUtil;
import com.telecom.ecloudframework.base.rest.ControllerTools;
import com.telecom.ecloudframework.base.rest.util.RequestUtil;
import com.telecom.ecloudframework.org.api.model.IUser;
import com.telecom.ecloudframework.org.api.service.SSOService;
import com.telecom.ecloudframework.org.api.service.UserService;
import com.telecom.ecloudframework.security.config.SsoConfig;
import com.telecom.ecloudframework.security.constant.PlatFormStatusCode;
import com.telecom.ecloudframework.security.factory.LoginServiceFactory;
import com.telecom.ecloudframework.security.login.SecurityUtil;
import com.telecom.ecloudframework.security.login.model.LoginUser;
import com.telecom.ecloudframework.security.service.ILoginService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.session.SessionInformation;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.security.web.authentication.session.NullAuthenticatedSessionStrategy;
import org.springframework.security.web.authentication.session.SessionAuthenticationStrategy;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

/**
 * <pre>
 * 描述：登陆服务接口
 * </pre>
 *
 * @author 谢石
 * @date 2020-7-29
 */
@Api(description = "登陆服务接口")
@RestController
public class LoginController extends ControllerTools {
    SessionAuthenticationStrategy sessionStrategy = new NullAuthenticatedSessionStrategy();
    @Resource
    JWTService jwtService;
    @Resource
    UserService userService;
    @Resource
    ICache iCache;
    @Autowired(required = false)
    private SessionRegistry sessionRegistry;
    @Autowired
    private SsoConfig ssoConfig;

    @RequestMapping(value = "/org/login/valid", method = {RequestMethod.POST, RequestMethod.GET})
    @CatchErr
    @OperateLog
    @ApiOperation(value = "用户登录", notes = "登录鉴权")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "form", dataType = "String", name = "account", value = "账号"),
            @ApiImplicitParam(paramType = "form", dataType = "String", name = "password", value = "密码"),
            @ApiImplicitParam(paramType = "form", dataType = "String", name = "type", value = "登录方式，account:账号，email:邮箱，phone:手机号"),
            @ApiImplicitParam(paramType = "form", dataType = "String", name = "audience", value = "接收方，pc/mobile/android"),
            @ApiImplicitParam(paramType = "form", dataType = "String", name = "subSystemCode", value = "登录方式-子系统编号")})
    public ResultMsg login(HttpServletRequest request, HttpServletResponse response) {
        String account = RequestUtil.getString(request, "account");
        String password = RequestUtil.getString(request, "password");
        String audience = RequestUtil.getString(request, "audience", "pc");
        String subSystemCode = RequestUtil.getString(request, "subSystemCode");
        if (account.endsWith("-gOffice")) {
            account = account.substring(0, account.lastIndexOf("-"));
        }
        if (StringUtil.isEmpty(account)) {
            throw new BusinessMessage("账户不能为空", PlatFormStatusCode.LOGIN_ERROR);
        }
        if (StringUtil.isEmpty(password)) {
            throw new BusinessMessage("密码不能为空", PlatFormStatusCode.LOGIN_ERROR);
        }

        try {
            ILoginService loginService = null;
            if (StringUtils.isNotEmpty(subSystemCode)) {
                loginService = LoginServiceFactory.buildLoginService(subSystemCode);
            }
            if (loginService != null) {
                account = loginService.beforeLogin(request, response);
            }
            // 用security 登录机制处理下
            Authentication auth = SecurityUtil.login(request, account, password, false);

            //写入session的
            sessionStrategy.onAuthentication(auth, request, response);
            //执行记住密码动作。
            SecurityUtil.writeRememberMeCookie(request, response, account, password);
            wiriteToken(request, response);
            //jwt 模式 支持cookie模式和token调用形式
            if (jwtService.getJwtEnabled()) {

                String token = jwtService.generateToken(account, audience);
                //直接写入 cookie ,把cookie当做session来用
                wiriteJwtToken2Cookie(request, response, token);
                return getSuccessResult(token, "登录成功！");
            }
            if (loginService != null) {
                loginService.afterLogin(request, response);
            }
            return getSuccessResult("登录成功！");

        } catch (BadCredentialsException e) {
            throw new BusinessMessage("账号或密码错误", PlatFormStatusCode.LOGIN_ERROR);
        } catch (DisabledException e) {
            throw new BusinessMessage("帐号已禁用", PlatFormStatusCode.LOGIN_ERROR);
        } catch (LockedException e) {
            throw new BusinessMessage("帐号已锁定", PlatFormStatusCode.LOGIN_ERROR);
        } catch (AccountExpiredException e) {
            throw new BusinessMessage("帐号已过期", PlatFormStatusCode.LOGIN_ERROR);
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new BusinessException(PlatFormStatusCode.LOGIN_ERROR, ex);
        }
    }

    protected static final String REQUEST_ATTRIBUTE_NAME = "_csrf";

    private void wiriteToken(HttpServletRequest request, HttpServletResponse response) {
        CsrfToken token = (CsrfToken) request.getAttribute(REQUEST_ATTRIBUTE_NAME);

        if (token != null) {
            Cookie cookie = new Cookie("XSRF-TOKEN", token.getToken());
            cookie.setPath(request.getContextPath());
            response.addCookie(cookie);
        }
    }

    /**
     * 类似session的形式将 token 写入 cookie 设值
     *
     * @param request
     * @param response
     * @param token
     */
    private void wiriteJwtToken2Cookie(HttpServletRequest request, HttpServletResponse response, String token) {
        Cookie cookie = new Cookie(jwtService.getJwtHeader(), jwtService.getJwtTokenHead() + token);
        String contextPath = "/";
        cookie.setPath(contextPath);
        response.addCookie(cookie);
    }

    /**
     * 类似session的形式将 token 写入 header 设值
     *
     * @param request
     * @param response
     * @param token
     */
    private void wiriteJwtToken2Header(HttpServletRequest request, HttpServletResponse response, String token) {
        response.addHeader(jwtService.getJwtHeader(), jwtService.getJwtTokenHead() + token);
    }

    /**
     * 创建session
     *
     * @param sessionId
     * @param username
     * @return
     * @author 谢石
     * @date 2020-7-23
     */
    @PostMapping(value = "/org/sso/refreshSession4SSO")
    public ResultMsg<String> refreshSession4SSO(@RequestParam(value = "sessionId", required = false) String sessionId,
                                                @RequestParam(value = "username") String username) {
        HttpServletRequest request = RequestContext.getHttpServletRequest();
        HttpServletResponse response = RequestContext.getHttpServletResponse();
        SecurityContext securityContext = SecurityContextHolder.getContext();
        String sessionIdTemp = "";
        if (null == securityContext.getAuthentication() || "anonymousUser".equals(securityContext.getAuthentication().getPrincipal())) {
            creatUserSession(username);
            sessionId = sessionIdTemp = request.getSession().getId();

        }
        if (null != sessionRegistry) {
            SessionInformation sessionInformation = sessionRegistry.getSessionInformation(sessionId);
            if (sessionInformation == null) {
                IUser defaultUser = userService.getUserByAccount(username);
                if (defaultUser == null) {
                    return getWarnResult("用户：" + username + "不存在");
                }
                LoginUser loginUser = new LoginUser(defaultUser);
                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(loginUser, null, loginUser.getAuthorities());
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authentication);
                sessionRegistry.registerNewSession(sessionId, loginUser);
            } else {
                //会话续期
                sessionRegistry.refreshLastRequest(sessionId);
            }
        }
        return getSuccessResult(sessionIdTemp);
    }

    /**
     * 创建用户session
     *
     * @param userName
     * @return
     * @author 谢石
     * @date 2020-8-5
     */
    private LoginUser creatUserSession(String userName) {
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
     * 清理session
     *
     * @param sessionId
     * @return
     * @author 谢石
     * @date 2020-7-23
     */
    @PostMapping(value = "/org/sso/cleanSession4SSO")
    public ResultMsg<String> cleanSession4SSO(@RequestParam(value = "sessionId") String sessionId) {
        if (null != sessionRegistry) {
            sessionRegistry.removeSessionInformation(sessionId);
        }
        SecurityContextHolder.clearContext();
        return getSuccessResult("清理成功");
    }

    /**
     * 获取当前sessionId
     *
     * @return
     * @author 谢石
     * @date 2020-7-27
     */
    @PostMapping(value = "/org/sso/init")
    public ResultMsg<String> getSessionId(HttpServletRequest request, HttpServletResponse response) {
        String sessionId = request.getSession().getId();
        return getSuccessResult(sessionId);
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
}