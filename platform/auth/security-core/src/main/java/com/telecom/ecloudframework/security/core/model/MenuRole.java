package com.telecom.ecloudframework.security.core.model;

import com.telecom.ecloudframework.base.api.model.IDModel;


/**
 * <pre>
 * 描述：角色菜单关联 实体对象
 * </pre>
 *
 * @author 谢石
 * @date 2020-7-15 15:48
 */
public class MenuRole implements IDModel {

    /**
     * 主键
     */
    protected String id;

    /**
     * 系统ID
     */
    protected String systemId;

    /**
     * 菜单ID
     */
    protected String MenuId;

    /**
     * 角色ID
     */
    protected String roleId;

    @Override
    public String getId() {
        return id;
    }

    @Override
    public void setId(String id) {
        this.id = id;
    }

    public String getSystemId() {
        return systemId;
    }

    public void setSystemId(String systemId) {
        this.systemId = systemId;
    }

    public String getMenuId() {
        return MenuId;
    }

    public void setMenuId(String menuId) {
        MenuId = menuId;
    }

    public String getRoleId() {
        return roleId;
    }

    public void setRoleId(String roleId) {
        this.roleId = roleId;
    }
}