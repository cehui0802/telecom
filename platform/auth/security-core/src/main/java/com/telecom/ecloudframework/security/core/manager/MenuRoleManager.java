package com.telecom.ecloudframework.security.core.manager;

import com.telecom.ecloudframework.base.manager.Manager;
import com.telecom.ecloudframework.security.core.model.MenuRole;

/**
 * <pre>
 * 描述：角色菜单关联 处理接口
 * </pre>
 *
 * @author 谢石
 * @date 2020-7-15 15:51
 */
public interface MenuRoleManager extends Manager<String, MenuRole> {

    /**
     * 删除角色菜单关联
     *
     * @param systemId
     * @param roleId
     * @param menuId
     * @author 谢石
     * @date 2020-7-15 16:31
     */
    void remove(String systemId, String roleId, String menuId);
}
