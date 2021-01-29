package com.telecom.ecloudframework.security.forbidden;

import com.telecom.ecloudframework.base.core.util.StringUtil;
import com.alibaba.fastjson.JSONObject;
import com.telecom.ecloudframework.base.api.constant.BaseStatusCode;
import com.telecom.ecloudframework.base.api.response.impl.ResultMsg;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 超时访问
 *
 * @author jeff
 */
public class DefualtAuthenticationEntryPoint implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        response.setCharacterEncoding("UTF-8");

        // 登录超时记录下当前redirectURL
        String referer = httpRequest.getHeader("Referer");
        String Url = getBackUrl(request);
        if(StringUtil.isNotEmpty(referer) && StringUtil.isNotEmpty( httpRequest.getContentType())) {
            request.getSession().setAttribute("SSO_redirectUrl",referer );
        }

        ResultMsg resultMsg = new ResultMsg(BaseStatusCode.TIMEOUT,"登录访问超时！");
        response.getWriter().print(JSONObject.toJSONString(resultMsg));
        return;
    }

    private String getBackUrl(HttpServletRequest request) {

        StringBuffer strBackUrl = new StringBuffer("http://").append( request.getServerName()) //服务器地址

                .append( ":" )

                .append( request.getServerPort() ) //端口号

                .append( request.getContextPath() )     //项目名称

                .append( request.getServletPath() ) ;

        if(StringUtil.isNotEmpty(request.getQueryString())) {
            strBackUrl.append( "?" ).append( (request.getQueryString()));
        }



        return strBackUrl.toString();
    }



}