package com.telecom.ecloudframework.org.api.context;

import com.telecom.ecloudframework.org.api.model.IGroup;
import com.telecom.ecloudframework.org.api.model.IUser;

import java.util.List;
import java.util.Locale;

/**
 * 获取上下文对象数据。
 *
 * @author
 */
public interface ICurrentContext {

    /**
     * 当前岗位
     */
    String CURRENT_ORG = "current_org";
    /**
     * 当前用户
     */
    String CURRENT_USER = "current_user";

    /**
     * 获取当当前登录用户
     *
     * @return User
     */
    IUser getCurrentUser();

    /**
     * 获取当前执行人
     *
     * @return String
     */
    String getCurrentUserId();

    /**
     * 获取当前组织
     *
     * @return
     */
    IGroup getCurrentGroup();

    /**
     * 清理当前用户
     */
    void clearCurrentUser();

    /**
     * 设置当前用户。
     *
     * @param user
     */
    void setCurrentUser(IUser user);

    /**
     * 根据用户帐号设置上下文用户。
     *
     * @param account 帐号。
     */
    void setCurrentUserByAccount(String account);

    /**
     * 缓存当前当前组织。
     *
     * @param group
     */
    void cacheCurrentGroup(IGroup group);

    /**
     * 删除用户的Redis级别的所有缓存，需要在用户登录时，注销时调用。这样可以防止出现，当前用户信息更新后依然读取缓冲的情况
     *
     * @param userId
     */
    void clearUserRedisCache(String userId);

    /**
     * 获取当前Locale。
     *
     * @return Locale
     */
    Locale getLocale();

    /**
     * 设置上下文local。
     *
     * @param local
     */
    void setLocale(Locale local);

    /**
     * 清除上下文local。
     */
    void clearLocale();

    /**
     * 是否是管理员
     *
     * @param user
     * @return
     */
    boolean isAdmin(IUser user);

    /**
     * 获取当前权限
     *
     * @return
     */
    List<? extends IGroup> getCurrentRoles();

}
