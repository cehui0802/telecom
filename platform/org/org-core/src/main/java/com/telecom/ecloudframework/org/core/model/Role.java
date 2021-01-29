package com.telecom.ecloudframework.org.core.model;

import com.telecom.ecloudframework.base.core.model.BaseModel;
import com.telecom.ecloudframework.org.api.constant.GroupTypeConstant;
import com.telecom.ecloudframework.org.api.model.IGroup;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.util.Map;


/**
 * <pre>
 * 描述：角色管理 实体对象
 * </pre>
 *
 * @author 谢石
 * @date 2020-7-30
 */
public class Role extends BaseModel implements IGroup {
    private static final long serialVersionUID = -700694295167942753L;
    /**
     * 角色名称
     */
    protected String name;

    /**
     * 角色别名
     */
    protected String alias;

    /**
     * 0：禁用，1：启用
     */
    protected Integer enabled;

    /**
     * 角色描述
     */
    protected String description;

    private Integer sn;
    /**
     * 机构id
     */
    protected String orgId;
    /*------------------------------前端字段------------------------------*/
    /**
     * 组人数
     */
    protected Integer userNum;

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public String getAlias() {
        return this.alias;
    }

    public void setEnabled(Integer enabled) {
        this.enabled = enabled;
    }

    public Integer getEnabled() {
        return this.enabled;
    }

    @Override
    public String getGroupId() {
        return this.id;
    }

    @Override
    public String getGroupCode() {
        return this.alias;
    }

    @Override
    public Integer getSn() {
        return sn;
    }

    public void setSn(Integer sn) {
        this.sn = sn;
    }

    @Override
    public String getGroupType() {
        return GroupTypeConstant.ROLE.key();
    }

    @Override
    public Integer getGroupLevel() {
        return null;
    }

    @Override
    public String getParentId() {
        return "";
    }

    @Override
    public String getPath() {
        return this.name;
    }

    public Map<String, Object> getParams() {
        return null;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String getGroupName() {
        return this.name;
    }

    public String getOrgId() {
        return orgId;
    }

    public void setOrgId(String orgId) {
        this.orgId = orgId;
    }

    @Override
    public Integer getUserNum() {
        return userNum;
    }

    @Override
    public String getSimple() {
        return null;
    }

    public void setUserNum(Integer userNum) {
        this.userNum = userNum;
    }

    /**
     * @see Object#toString()
     */
    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("id", this.id)
                .append("name", this.name)
                .append("alias", this.alias)
                .append("enabled", this.enabled)
                .toString();
    }
}