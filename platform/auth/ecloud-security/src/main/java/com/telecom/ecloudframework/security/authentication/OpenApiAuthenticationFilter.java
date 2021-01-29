package com.telecom.ecloudframework.security.authentication;

import com.telecom.ecloudframework.base.api.constant.BaseStatusCode;
import com.telecom.ecloudframework.base.api.response.impl.ResultMsg;
import com.telecom.ecloudframework.security.authentication.api.OpenApiService;
import com.alibaba.fastjson.JSON;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author
 */
public class OpenApiAuthenticationFilter extends OncePerRequestFilter {
    @Autowired
    OpenApiService openApiService;


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws ServletException, IOException {

        //系统间访问包括G1的访问全部走这里，如果需要session的地方需要把用户名当作参数传递
        if (request.getServletPath().startsWith("/openapi/")) {
            String clientId = request.getHeader(openApiService.getClientHeader());
            String token = request.getHeader(openApiService.getContextHeader());
            if (openApiService.validation(request.getServletPath(), clientId, token)) {
                chain.doFilter(request, response);
            } else {
                String msg = "OpenApi校验失败";
                ResultMsg<Boolean> resultMsg = new ResultMsg<>(BaseStatusCode.PARAM_ILLEGAL, msg);
                resultMsg.setData(Boolean.FALSE);
                response.setContentType("application/json");
                response.getWriter().print(JSON.toJSONString(resultMsg));
            }
        } else {
            chain.doFilter(request, response);
        }
    }
}
