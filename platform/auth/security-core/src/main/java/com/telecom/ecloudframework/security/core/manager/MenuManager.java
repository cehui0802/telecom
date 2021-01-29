package com.telecom.ecloudframework.security.core.manager;

import com.telecom.ecloudframework.base.manager.Manager;
import com.telecom.ecloudframework.security.core.model.Menu;

import java.util.List;

/**
 * <pre>
 * 描述：菜单 处理接口
 * </pre>
 *
 * @author 谢石
 * @date 2020-7-13 13:53
 */
public interface MenuManager extends Manager<String, Menu> {
    /**
     * 保存菜单排序
     *
     * @param menu
     * @author 谢石
     * @date 2020-7-14 17:02
     */
    void saveOrder(Menu menu);

    /**
     * 获取角色菜单列表
     *
     * @param roleId
     * @param systemId
     * @return
     * @author 谢石
     * @date 2020-7-15 15:07
     */
    List<String> getRoleMenuList(String roleId, String systemId);
}
