package com.telecom.ecloudframework.security.core.model;

import com.telecom.ecloudframework.base.core.model.BaseModel;

import java.util.Set;


/**
 * <pre>
 * 描述：菜单类
 * </pre>
 *
 * @author 谢石
 * @date 2020-7-13 13:23
 */
public class Menu extends BaseModel {

    /**
     * 系统ID
     */
    protected String systemId;
    /**
     * 名称
     */
    protected String name;
    /**
     * 别名
     */
    protected String code;
    /**
     * 类型
     */
    protected String type;
    /**
     * 图标
     */
    protected String icon;
    /**
     * 图标颜色
     */
    protected String iconColor;
    /**
     * 父节点
     */
    protected String parentId;
    /**
     * 菜单url连接或者路由
     */
    protected String url;
    /**
     * 排序
     */
    protected Integer sn;
    /**
     * 是否可用 1可用 0不可用
     */
    protected Integer enable;
    /*------------------------------前端字段------------------------------*/
    /**
     * 角色集合
     */
    private Set<String> roles;


    public String getSystemId() {
        return systemId;
    }

    public void setSystemId(String systemId) {
        this.systemId = systemId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getIconColor() {
        return iconColor;
    }

    public void setIconColor(String iconColor) {
        this.iconColor = iconColor;
    }

    public String getParentId() {
        return parentId;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Integer getSn() {
        return sn;
    }

    public void setSn(Integer sn) {
        this.sn = sn;
    }

    public Integer getEnable() {
        return enable;
    }

    public void setEnable(Integer enable) {
        this.enable = enable;
    }

    public Set<String> getRoles() {
        return roles;
    }

    public void setRoles(Set<String> roles) {
        this.roles = roles;
    }
}