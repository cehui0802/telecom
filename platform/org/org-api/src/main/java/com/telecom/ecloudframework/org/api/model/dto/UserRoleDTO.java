package com.telecom.ecloudframework.org.api.model.dto;

import com.telecom.ecloudframework.org.api.model.IUserRole;

/**
 * @author
 */
public class UserRoleDTO implements IUserRole {
    /**
     * 角色id
     */
    protected String roleId;

    /**
     * 用户id
     */
    protected String userId;

    /**
     * 用户名
     */
    protected String fullname;
    /**
     * 角色名称
     */
    protected String roleName;
    /**
     * 角色别名
     */
    protected String alias;
    /**
     * 账号
     */
    protected String account;


    /**
     * 构造函数
     *
     * @param roleId
     * @param roleName
     * @param alias
     */
    public UserRoleDTO(String roleId, String roleName, String alias) {
        this.roleId = roleId;
        this.roleName = roleName;
        this.alias = alias;
    }

    public UserRoleDTO(String roleId, String userId, String fullname, String roleName) {
        super();
        this.roleId = roleId;
        this.userId = userId;
        this.fullname = fullname;
        this.roleName = roleName;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    @Override
    public String getAlias() {
        return this.alias;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    @Override
    public String getFullname() {
        return this.fullname;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    @Override
    public String getRoleName() {
        return this.roleName;
    }

    public void setRoleId(String roleId) {
        this.roleId = roleId;
    }

    @Override
    public String getRoleId() {
        return this.roleId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    @Override
    public String getUserId() {
        return this.userId;
    }

    @Override
    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }
}
