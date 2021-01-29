package com.telecom.ecloudframework.security.login;

import com.telecom.ecloudframework.base.core.cache.ICache;
import com.telecom.ecloudframework.base.core.jwt.JWTService;
import com.telecom.ecloudframework.org.api.model.IUser;
import com.telecom.ecloudframework.org.api.model.IUserRole;
import com.telecom.ecloudframework.org.api.service.UserService;
import com.telecom.ecloudframework.security.constans.PlatformConsts;
import com.telecom.ecloudframework.security.login.model.LoginUser;
import com.telecom.ecloudframework.sys.util.ContextUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * 实现UserDetailsService 接口获取UserDetails 接口实例对象。
 *
 * @author
 */
public class UserDetailsServiceImpl implements UserDetailsService {
    public static final String LOGIN_USER_CACHE_KEY = "ecloudframework:loginUser:";
    @Resource
    UserService userService;
    @Resource
    ICache<LoginUser> loginUserCache;
    @Autowired
    JWTService jwtService;

    /**
     * 更新缓存
     *
     * @param username
     * @throws UsernameNotFoundException
     */
    public void reloadUser(String username) throws UsernameNotFoundException {
        if (jwtService.getJwtEnabled()) {
            loginUserCache.delByKey(LOGIN_USER_CACHE_KEY.concat(username));
            loadUserByUsername(username);
        }
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        LoginUser loginUser;
        // 只有 jwt 模式才有必要缓存用户角色
        if (jwtService.getJwtEnabled()) {
            loginUser = loginUserCache.getByKey(LOGIN_USER_CACHE_KEY.concat(username)); //TODO 加上有效期

            if (loginUser != null) {
                return loginUser;
            }
        }

        IUser defaultUser = userService.getUserByAccount(username);

        if (defaultUser == null) {
            throw new UsernameNotFoundException("用户：" + username + "不存在");
        }

        loginUser = new LoginUser(defaultUser);

        //构建用户角色。
        List<? extends IUserRole> userRoleList = userService.getUserRole(loginUser.getUserId());
        Collection<GrantedAuthority> collection = new ArrayList<>();
        for (IUserRole userRole : userRoleList) {
            GrantedAuthority role = new SimpleGrantedAuthority(userRole.getAlias());
            collection.add(role);
        }
        if (ContextUtil.isAdmin(loginUser)) {
            collection.add(PlatformConsts.ROLE_GRANT_SUPER);
        }
        loginUser.setAuthorities(collection);
        if (jwtService.getJwtEnabled()) {
            loginUserCache.add(LOGIN_USER_CACHE_KEY.concat(username), loginUser);
        }
        return loginUser;
    }
}
