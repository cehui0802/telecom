package com.telecom.ecloudframework.security.constans;

import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.access.SecurityConfig;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

/**
 * 平台常量类
 *
 * @author
 */
public class PlatformConsts {

    private final static String ROLE_SUPER = "ROLE_SUPER";//超级
    private final static String ROLE_PUBLIC = "ROLE_PUBLIC";//公共角色
    private final static String ROLE_ANONYMOUS = "ROLE_ANONYMOUS";//匿名级

    public final static GrantedAuthority ROLE_GRANT_SUPER = new SimpleGrantedAuthority(ROLE_SUPER);
    public final static ConfigAttribute ROLE_CONFIG_PUBLIC = new SecurityConfig(ROLE_PUBLIC);
    public final static ConfigAttribute ROLE_CONFIG_ANONYMOUS = new SecurityConfig(ROLE_ANONYMOUS);
    /**
     * 系统资源缓存key
     */
    public final static String CACHE_KEY_URL_ROLE_MAPPING = "ecloudframework:sys:resoucesUrlRoleMapping:";
}
