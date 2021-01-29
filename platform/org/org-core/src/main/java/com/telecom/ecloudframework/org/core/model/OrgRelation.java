package com.telecom.ecloudframework.org.core.model;

import com.telecom.ecloudframework.base.core.model.BaseModel;
import com.telecom.ecloudframework.org.api.constant.RelationTypeConstant;
import com.telecom.ecloudframework.org.api.model.IRelation;

import java.util.Date;


/**
 * 用户组织关系 实体对象
 *
 * @author Jeff
 * @email for_office@qq.com
 * @time 2018-12-16 01:07:59
 */
public class OrgRelation extends BaseModel implements IRelation {
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
     * 状态 0禁用 1启动
     */
    protected Integer status;

    /**
     * 类型：{@linkplain RelationTypeConstant}
     */
    protected String type;
    /**
     * 关系是否包含下级
     */
    protected String hasChild;

    /**
     * 前端字段
     */
    protected String groupName;
    protected String userName;
    protected String userAccount;
    protected String roleName;
    protected String roleAlias;
    protected String photo;
    protected String sex;
    protected String isMasters;
    /**
     * 手机
     */
    protected String mobile;
    /**
     * 用户排序
     */
    protected String sn;
    /**
     * 旧组id
     */
    protected String oldGroupId;
    /**
     * 角色ID
     */
    protected String roleId;
    /**
     * 岗位id
     */
    protected String postId;
    /**
     * 岗位名称
     */
    protected String postName;
    /**
     * 外部单位id
     */
    protected String unitId;
    /**
     * 外部单位名称
     */
    protected String unitName;
    /**
     * 用户状态
     */
    protected Integer userStatus;
    /**
     * 用户激活状态
     */
    protected Integer userActiveStatus;
    /**
     * 用户创建时间
     */
    protected Date userCreateTime;
    /**
     * 上级机构名称
     */
    protected String parentOrgName;


    public OrgRelation() {

    }

    public OrgRelation(String groupId, String userId, String type) {
        super();
        this.groupId = groupId;
        this.userId = userId;
        this.type = type;
    }

    public String getUnitId() {
        return unitId;
    }

    public void setUnitId(String unitId) {
        this.unitId = unitId;
    }

    public String getUnitName() {
        return unitName;
    }

    public void setUnitName(String unitName) {
        this.unitName = unitName;
    }

    @Override
    public String getGroupId() {
        return this.groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }


    public void setUserId(String userId) {
        this.userId = userId;
    }

    @Override
    public String getUserId() {
        return this.userId;
    }


    public void setIsMaster(Integer isMaster) {
        this.isMaster = isMaster;
    }

    @Override
    public Integer getIsMaster() {
        return this.isMaster;
    }


    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getUserName() {
        return userName;
    }

    public Integer getStatus() {
        return status;
    }

    public String getRoleAlias() {
        return roleAlias;
    }


    public void setRoleAlias(String roleAlias) {
        this.roleAlias = roleAlias;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    public void setRoleId(String roleId) {
        this.roleId = roleId;
    }

    public String getRoleId() {
        return this.roleId;
    }


    public void setType(String type) {
        this.type = type;
    }

    @Override
    public String getType() {
        return this.type;
    }

    public String getUserAccount() {
        return userAccount;
    }

    public void setUserAccount(String userAccount) {
        this.userAccount = userAccount;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getSn() {
        return sn;
    }

    public void setSn(String sn) {
        this.sn = sn;
    }

    public String getIsMasters() {
        return isMasters;
    }

    public void setIsMasters(String isMasters) {
        this.isMasters = isMasters;
    }

    public String getOldGroupId() {
        return oldGroupId;
    }

    public void setOldGroupId(String oldGroupId) {
        this.oldGroupId = oldGroupId;
    }

    public String getPostId() {
        return postId;
    }

    public void setPostId(String postId) {
        this.postId = postId;
    }

    public String getPostName() {
        return postName;
    }

    public void setPostName(String postName) {
        this.postName = postName;
    }

    public Integer getUserStatus() {
        return userStatus;
    }

    public void setUserStatus(Integer userStatus) {
        this.userStatus = userStatus;
    }

    public Integer getUserActiveStatus() {
        return userActiveStatus;
    }

    public void setUserActiveStatus(Integer userActiveStatus) {
        this.userActiveStatus = userActiveStatus;
    }

    public Date getUserCreateTime() {
        return userCreateTime;
    }

    public void setUserCreateTime(Date userCreateTime) {
        this.userCreateTime = userCreateTime;
    }

    @Override
    public String getHasChild() {
        return hasChild;
    }

    public void setHasChild(String hasChild) {
        this.hasChild = hasChild;
    }

    public String getParentOrgName() {
        return parentOrgName;
    }

    public void setParentOrgName(String parentOrgName) {
        this.parentOrgName = parentOrgName;
    }
}