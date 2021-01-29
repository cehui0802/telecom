package com.telecom.ecloudframework.security.login.context;


import java.util.List;
import java.util.Locale;

import javax.annotation.Resource;

import com.telecom.ecloudframework.base.api.constant.StringConstants;
import com.telecom.ecloudframework.sys.util.SysPropertyUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import com.telecom.ecloudframework.base.core.cache.ICache;
import com.telecom.ecloudframework.base.core.util.AppUtil;
import com.telecom.ecloudframework.base.core.util.CookieUitl;
import com.telecom.ecloudframework.base.core.util.StringUtil;
import com.telecom.ecloudframework.org.api.constant.GroupTypeConstant;
import com.telecom.ecloudframework.org.api.context.ICurrentContext;
import com.telecom.ecloudframework.org.api.model.IGroup;
import com.telecom.ecloudframework.org.api.model.IUser;
import com.telecom.ecloudframework.org.api.service.GroupService;
import com.telecom.ecloudframework.org.api.service.UserService;


public class LoginContext implements ICurrentContext {
    private static final Logger log = LoggerFactory.getLogger(LoginContext.class);
    /**
     * 存放当前用户登录的语言环境
     */
    private static ThreadLocal<Locale> currentLocale = new ThreadLocal<Locale>();
    /**
     * 当前上下文用户
     */
    private static ThreadLocal<IUser> currentUser = new ThreadLocal<IUser>();


    @Resource
    GroupService groupService;
    @Resource
    UserService userService;

    /**
     * 获取当前语言环境
     *
     * @return
     */
    @Override
    public Locale getLocale() {
        if (currentLocale.get() != null) {
            return currentLocale.get();
        }
        setLocale(new Locale("zh", "CN"));
        return currentLocale.get();
    }

    /**
     * 返回当前用户ID
     *
     * @return String
     */
    @Override
    public String getCurrentUserId() {
        IUser user = getCurrentUser();
        if (user == null) {
            return null;
        }
        return user.getUserId();
    }

    @Override
    public IUser getCurrentUser() {
        if (currentUser.get() != null) {
            return currentUser.get();
        }

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && !"anonymousUser".equals(auth.getPrincipal())) {
            return (IUser) auth.getPrincipal();
        }
        return null;
    }


    @Override
    public IGroup getCurrentGroup() {
        //从缓存中取
        ICache<IGroup> iCache = AppUtil.getBean(ICache.class);
        String userKey = ICurrentContext.CURRENT_ORG.concat(getCurrentUserId());

        IGroup currentGroup = iCache.getByKey(userKey);
        // 缓存中存在组织,
        if (currentGroup != null) {
            return currentGroup;
        }

        // cookie 中存一份标识
        String cookieCurrrentOrgId = CookieUitl.getValueByName("currentOrg");
        if (StringUtil.isNotEmpty(cookieCurrrentOrgId) && cookieCurrrentOrgId.startsWith(userKey)) {
            return getCurrentOrgByCookiey(cookieCurrrentOrgId, userKey);
        }

        //获取当前人的主部门
        IGroup group = groupService.getMainGroup(getCurrentUserId());
        if (group != null) {
            cacheCurrentGroup(group);
        }
        return group;
    }

    private IGroup getCurrentOrgByCookiey(String cookieCurrrentOrgId, String userKey) {
        List<? extends IGroup> groupList = groupService.getGroupsByGroupTypeUserId(GroupTypeConstant.ORG.key(), getCurrentUserId());
        if (CollectionUtil.isEmpty(groupList)) {
            return null;
        }
        String orgId = cookieCurrrentOrgId.replace(userKey, StringConstants.EMPTY);
        for (IGroup group : groupList) {
            if (group.getGroupId().equals(orgId)) {
                cacheCurrentGroup(group);
                return group;
            }
        }

        cacheCurrentGroup(groupList.get(0));
        return groupList.get(0);
    }


    @Override
    public void clearUserRedisCache(String userId) {
        // 清楚当前组织的缓存
        AppUtil.getBean(ICache.class).delByKey(ICurrentContext.CURRENT_ORG.concat(userId));
        // 有其他的一并加入到这里,如当前分公司。当前租户信息等
    }

    /**
     * 将当前组织放到线程变量和缓存中
     */
    @Override
    public void cacheCurrentGroup(IGroup group) {
        //将当前人和组织放到缓存中。
        String userId = getCurrentUserId();
        ICache<IGroup> iCache = AppUtil.getBean(ICache.class);
        String userKey = ICurrentContext.CURRENT_ORG.concat(userId);
        iCache.add(userKey, group);
    }

    /**
     * 清理线程中的用户变量，以及他的岗位信息
     */
    @Override
    public void clearCurrentUser() {
        currentUser.remove();
    }

    @Override
    public void setCurrentUser(IUser user) {
        currentUser.set(user);
    }

    @Override
    public void clearLocale() {
        currentLocale.remove();
    }

    @Override
    public void setLocale(Locale local) {
        currentLocale.set(local);
    }

    @Override
    public void setCurrentUserByAccount(String account) {
        if (StringUtil.isEmpty(account)) {
            throw new RuntimeException("输入帐号为空!");
        }
        IUser user = userService.getUserByAccount(account);
        if (user == null) {
            throw new RuntimeException("系统中没有帐号[" + account + "]对应的用户");
        }
        currentUser.set(user);
    }

    @Override
    public boolean isAdmin(IUser user) {
        String tmp = SysPropertyUtil.getByAlias("admin.account", "admin");
        return StrUtil.equals(tmp, user.getAccount());
    }

    @Override
    public List<? extends IGroup> getCurrentRoles() {
        return groupService.getGroupsByGroupTypeUserId(GroupTypeConstant.ROLE.key(), getCurrentUserId());
    }

}