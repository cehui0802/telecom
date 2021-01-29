package com.telecom.ecloudframework.sys.util;

import java.util.Locale;

import com.telecom.ecloudframework.base.core.util.StringUtil;
import com.telecom.ecloudframework.org.api.context.ICurrentContext;
import com.telecom.ecloudframework.org.api.model.IGroup;
import com.telecom.ecloudframework.org.api.model.IUser;


/**
 * 获取上下文数据对象的工具类。
 * <pre>
 *
 * </pre>
 */
public class ContextUtil {

    private static ContextUtil contextUtil;

    private ICurrentContext currentContext;

    public void setCurrentContext(ICurrentContext _currentContext) {
        contextUtil = this;
        contextUtil.currentContext = _currentContext;
    }

    /**
     * 获取当前执行人
     *
     * @return User
     * @throws
     * @since 1.0.0
     */
    public static IUser getCurrentUser() {
        return contextUtil.currentContext.getCurrentUser();
    }

    public static String getCurrentUserId() {
        return contextUtil.currentContext.getCurrentUserId();
    }
    
    public static String getCurrentUserName() {
    	IUser currentUser = getCurrentUser();
    	if(currentUser!=null) {
    		return currentUser.getFullname();
    	}
    	return null;
    }
    
    public static String getCurrentUserAccount() {
    	IUser currentUser = getCurrentUser();
    	if(currentUser!=null) {
    		return currentUser.getAccount();
    	}
    	return null;
    }

    /**
     * 获取当前组织
     */
    public static IGroup getCurrentGroup() {
        return contextUtil.currentContext.getCurrentGroup();
    }

    /**
     * 获取当前组织Id。组织为空则返回空字符串
     */
    public static String getCurrentGroupId() {
        IGroup iGroup = getCurrentGroup();
        if (iGroup != null) {
            return iGroup.getGroupId();
        } else {
            return "";
        }
    }
    
    /**
     	* 获取当前组织名称。组织为空则返回空字符串
     */
    public static String getCurrentGroupName() {
    	IGroup iGroup = getCurrentGroup();
    	if (iGroup != null) {
    		return iGroup.getGroupName();
    	} else {
    		return "";
    	}
    }

    /**
     * 获取当前Locale。
     *
     * @return Locale
     * @throws
     * @since 1.0.0
     */
    public static Locale getLocale() {
        return contextUtil.currentContext.getLocale();
    }

    /**
     * 清除当前执行人。
     * void
     *
     * @throws
     * @since 1.0.0
     */
    public static void clearCurrentUser() {
        contextUtil.currentContext.clearCurrentUser();

    }

    /**
     * 设置当前执行人。
     *
     * @param user void
     * @throws
     * @since 1.0.0
     */
    public static void setCurrentUser(IUser user) {
        contextUtil.currentContext.setCurrentUser(user);
    }


    public static void setCurrentUserByAccount(String account) {
        contextUtil.currentContext.setCurrentUserByAccount(account);
    }


    /**
     * 设置当前组织(岗位)。
     *
     * @param group void
     * @throws
     * @since 1.0.0
     */
    public static void setCurrentOrg(IGroup group) {
        contextUtil.currentContext.cacheCurrentGroup(group);
    }
    
    /**
     * 用来清楚用户的缓存，建议在登录、注销时调用
     * 目前存储了 当前组织信息
     * @param userId
     */
    public static void clearUserRedisCache(String userId) {
    	if(StringUtil.isEmpty(userId)) {
    		userId = getCurrentUserId();
    	}
        contextUtil.currentContext.clearUserRedisCache(userId);
    }

    /**
     * 设置Locale。
     *
     * @param locale void
     * @throws
     * @since 1.0.0
     */
    public static void setLocale(Locale locale) {
        contextUtil.currentContext.setLocale(locale);
    }

    /**
     * 清除Local。
     * void
     *
     * @throws
     * @since 1.0.0
     */
    public static void cleanLocale() {
        contextUtil.currentContext.clearLocale();
    }

    public static void clearAll() {
        cleanLocale();
        clearCurrentUser();
    }
    
    public static boolean isAdmin(IUser user) {
    	 return contextUtil.currentContext.isAdmin(user);
    	 
    }
    
    public static boolean currentUserIsAdmin() {
    	IUser user = getCurrentUser();
    	return isAdmin(user);
  }
}
