package com.telecom.ecloudframework.org.api.model.dto;

import com.telecom.ecloudframework.org.api.model.IGroup;

/**
 * <pre>
 *  岗位对外post code postID 均为 【组织ID-组织ID】
 *  岗位选择框 postCODE 为 关系的ID
 *  对外提供岗位查询 CODE查询的时候为 关系ID 返回POST 对象ID已经被转换成 【组织ID-组织ID】
 *  岗位不支持ID 的查询
 *  当前用户的岗位ID 也为【组织ID-组织ID】
 * </pre>
 *
 * @author
 */
public class GroupDTO implements IGroup {
    private static final long serialVersionUID = -700694295167942753L;
    String identityType;
    String groupId;
    String groupName;
    String groupCode;
    Integer sn;
    String groupType;
    String parentId;
    String path;
    Integer groupLevel;
    /**
     * 组人数
     */
    Integer userNum;
    /**
     * 简称
     */
    protected String simple;

    public GroupDTO(IGroup group) {
        this.groupCode = group.getGroupCode();
        this.groupId = group.getGroupId();
        this.groupType = group.getGroupType();
        this.parentId = group.getParentId();
        this.groupName = group.getGroupName();
        this.groupLevel = group.getGroupLevel();
        this.path = group.getPath();
        this.sn = group.getSn();
        this.userNum = group.getUserNum();
        this.simple = group.getSimple();
    }

    public GroupDTO() {

    }

    public GroupDTO(String groupId, String groupName, String groupType) {
        super();
        this.groupId = groupId;
        this.groupName = groupName;
        this.groupType = groupType;
    }

    public String getIdentityType() {
        return identityType;
    }

    public void setIdentityType(String identityType) {
        this.identityType = identityType;
    }

    @Override
    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    @Override
    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    @Override
    public String getGroupCode() {
        return groupCode;
    }

    public void setGroupCode(String groupCode) {
        this.groupCode = groupCode;
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
        return groupType;
    }

    @Override
    public Integer getGroupLevel() {
        return groupLevel;
    }

    public void setGroupType(String groupType) {
        this.groupType = groupType;
    }

    @Override
    public String getParentId() {
        return parentId;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }

    @Override
    public String getPath() {
        return path;
    }

    public void setGroupLevel(Integer groupLevel) {
        this.groupLevel = groupLevel;
    }

    public void setPath(String path) {
        this.path = path;
    }

    @Override
    public Integer getUserNum() {
        return userNum;
    }

    public void setUserNum(Integer userNum) {
        this.userNum = userNum;
    }

    @Override
    public String getSimple() {
        return simple;
    }

    public void setSimple(String simple) {
        this.simple = simple;
    }
}
