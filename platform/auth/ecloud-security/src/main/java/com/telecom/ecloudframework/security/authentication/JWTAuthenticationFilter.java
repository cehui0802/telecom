package com.telecom.ecloudframework.security.authentication;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.telecom.ecloudframework.base.rest.util.RequestUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import com.telecom.ecloudframework.base.core.jwt.JWTService;
import com.telecom.ecloudframework.base.core.util.CookieUitl;
import com.telecom.ecloudframework.base.core.util.StringUtil;

/**
 * @author who
 */
public class JWTAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private JWTService jwtService;
    @Autowired
    private UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws ServletException, IOException {
        if (!jwtService.getJwtEnabled()) {
            chain.doFilter(request, response);
            return;
        }

        String authHeader = request.getHeader(jwtService.getJwtHeader());
        //支持 session的形式
        if (StringUtil.isEmpty(authHeader)) {
            //尝试用小写header方式取
            authHeader = request.getHeader(jwtService.getJwtHeader().toLowerCase());
            if (StringUtil.isEmpty(authHeader)) {
                //尝试从cookie中取
                authHeader = CookieUitl.getValueByName(jwtService.getJwtHeader(), request);
                if (StringUtil.isEmpty(authHeader)) {
                    //尝试从参数中获取
                    authHeader = RequestUtil.getString(request, jwtService.getJwtParam());
                }
            }
        }

        String tokenHead = this.jwtService.getJwtTokenHead();

        if (authHeader != null && authHeader.startsWith(tokenHead)) {
            String authToken = authHeader.substring(tokenHead.length());

            String username = jwtService.getValidSubjectFromRedisToken(authToken);
            if (StringUtil.isNotEmpty(username)) {
                //从缓存中获取，获取失败则通过实现接口的方法获取用户
                UserDetails userDetails = userDetailsService.loadUserByUsername(username);

                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                logger.debug("authenticated user " + username + ", setting security context");
                SecurityContextHolder.getContext().setAuthentication(authentication);
            } else {
                SecurityContextHolder.getContext().setAuthentication(null);
            }
        } else {
            logger.info("无权限访问" + request.getRequestURI());
        }
        chain.doFilter(request, response);
    }
}
