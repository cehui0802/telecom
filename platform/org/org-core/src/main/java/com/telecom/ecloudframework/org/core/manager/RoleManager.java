package com.telecom.ecloudframework.org.core.manager;

import com.telecom.ecloudframework.base.manager.Manager;
import com.telecom.ecloudframework.org.core.model.Role;

import java.util.List;

/**
 * <pre>
 * 描述：角色管理 处理接口
 * </pre>
 *
 * @author 谢石
 * @date 2020-7-30
 */
public interface RoleManager extends Manager<String, Role> {

    /**
     * 通过别名获取角色
     *
     * @param alias
     * @return
     */
    Role getByAlias(String alias);

    /**
     * 判断角色是否存在。
     *
     * @param role
     * @return
     */
    boolean isRoleExist(Role role);

    /**
     * 根据用户ID获取角色列表
     *
     * @param userId
     * @return
     */
    List<Role> getByUserId(String userId);

    /**
     * 设置角色状态
     *
     * @param id
     * @param status
     * @author 谢石
     * @date 2020-7-29
     */
    void setStatus(String id, Integer status);
}
