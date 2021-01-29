package com.telecom.ecloudframework.security.login.logout;

import com.telecom.ecloudframework.base.api.constant.BaseStatusCode;
import com.telecom.ecloudframework.base.api.exception.BusinessException;
import com.telecom.ecloudframework.base.api.response.impl.ResultMsg;
import com.telecom.ecloudframework.base.core.cache.ICache;
import com.telecom.ecloudframework.base.core.jwt.JWTService;
import com.telecom.ecloudframework.base.core.util.AppUtil;
import com.telecom.ecloudframework.base.core.util.CookieUitl;
import com.telecom.ecloudframework.base.core.util.StringUtil;
import com.telecom.ecloudframework.org.api.context.ICurrentContext;
import com.telecom.ecloudframework.org.api.service.SSOService;
import com.telecom.ecloudframework.security.config.SsoConfig;
import com.telecom.ecloudframework.security.login.UserDetailsServiceImpl;
import com.telecom.ecloudframework.security.login.model.LoginUser;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

/**
 * <pre>
 * 描述：spring security 退出成功执行方法
 * </pre>
 *
 * @author 谢石
 * @date 2020-8-4
 */
public class DefualtLogoutSuccessHandler implements LogoutSuccessHandler {

    @Override
    public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {
        JWTService jwtService = AppUtil.getBean(JWTService.class);
        ICache icache = AppUtil.getBean(ICache.class);
        response.setCharacterEncoding("UTF-8");

        if (null != authentication) {
            LoginUser user = (LoginUser) authentication.getPrincipal();
            if (null != icache) {
                //删除组织缓存
                icache.delByKey(ICurrentContext.CURRENT_ORG.concat(user.getId()));
            }
            if (null != jwtService) {
                //设置过期
                if (jwtService.getJwtEnabled()) {
                    String authHeader = request.getHeader(jwtService.getJwtHeader());
                    if (StringUtil.isEmpty(authHeader)) {
                        authHeader = CookieUitl.getValueByName(jwtService.getJwtHeader(), request);
                    }
                    if (authHeader != null && authHeader.startsWith(jwtService.getJwtTokenHead())) {
                        String authToken = authHeader.substring(jwtService.getJwtTokenHead().length());
                        jwtService.logoutRedisToken(authToken);
                    }

                    if (null != icache) {
                        //删除 UserDetial
                        icache.delByKey(UserDetailsServiceImpl.LOGIN_USER_CACHE_KEY.concat(user.getAccount()));
                    }
                    CookieUitl.delCookie(jwtService.getJwtHeader(), request, response);
                }
            }
            //清理sso信息
            SsoConfig ssoConfig = AppUtil.getBean(SsoConfig.class);
            SSOService ssoService = null;
            if (null != ssoConfig && StringUtils.isNotEmpty(ssoConfig.getSsoType())) {
                ssoService = getService(ssoConfig.getSsoType());
            }
            if (null != ssoService) {
                ssoService.invalidSSOToken(request, response);
            }
        }


        ResultMsg resultMsg = new ResultMsg(BaseStatusCode.SUCCESS, "退出登录成功");
        response.getWriter().print(JSONObject.toJSONString(resultMsg));
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
