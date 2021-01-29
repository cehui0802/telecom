package com.telecom.ecloudframework.org.api.model.dto;

import com.telecom.ecloudframework.org.api.constant.RelationTypeConstant;
import com.telecom.ecloudframework.org.api.model.IRelation;


/**
 * <pre>
 * 描述：用户关系 实体对象
 * </pre>
 *
 * @author
 */
public class RelationDTO implements IRelation {
    private static final long serialVersionUID = -700694295167942753L;
    /**
     * 组ID
     */
    protected String groupId;
    /**
     * 用户ID
     */
    protected String userId;
    /**
     * 是否主机构 0:否，1：是
     */
    protected Integer isMaster;
    /**
     * 类型：{@linkplain RelationTypeConstant}
     */
    protected String type;
    /**
     * 关系是否包含下级
     */
    protected String hasChild;

    public RelationDTO() {

    }

    public RelationDTO(String groupId, String userId, String type) {
        this.groupId = groupId;
        this.userId = userId;
        this.type = type;
    }

    @Override
    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    @Override
    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    @Override
    public Integer getIsMaster() {
        return isMaster;
    }

    public void setIsMaster(Integer isMaster) {
        this.isMaster = isMaster;
    }

    @Override
    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public String getHasChild() {
        return hasChild;
    }

    public void setHasChild(String hasChild) {
        this.hasChild = hasChild;
    }
}