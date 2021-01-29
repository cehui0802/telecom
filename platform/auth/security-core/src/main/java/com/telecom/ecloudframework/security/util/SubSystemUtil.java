package com.telecom.ecloudframework.security.util;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.telecom.ecloudframework.base.core.util.CookieUitl;
import com.telecom.ecloudframework.base.core.util.StringUtil;

public class SubSystemUtil {

    /**
     * 获取上下文系统ID
     *
     * @param req
     * @return
     */
    public static String getSystemId(HttpServletRequest req) {
        String systemAlias = CookieUitl.getValueByName("system", req);
        if (StringUtil.isEmpty(systemAlias)) return "ecloudframework";
        return systemAlias;
    }

    /**
     * 设置系统id。
     *
     * @param req
     * @param response
     * @param systemAlias
     */
    public static void setSystemId(HttpServletRequest req, HttpServletResponse response, String systemAlias) {
        CookieUitl.addCookie("system", systemAlias,CookieUitl.cookie_no_expire);
    }
}
