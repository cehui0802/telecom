package com.telecom.ecloudframework.security.filter;

import com.telecom.ecloudframework.security.IngoreChecker;
import org.springframework.security.web.util.matcher.RequestMatcher;

import javax.servlet.http.HttpServletRequest;

public class SecurityRequestCsrfMatcher extends IngoreChecker implements RequestMatcher {

    @Override
    public boolean matches(HttpServletRequest request) {

        boolean isIngoreUrl = isIngores(request.getServletPath());

        if (isIngoreUrl) return true;

        return false;
    }

}
