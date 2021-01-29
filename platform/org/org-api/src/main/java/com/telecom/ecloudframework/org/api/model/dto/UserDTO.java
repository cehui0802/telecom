package com.telecom.ecloudframework.org.api.model.dto;

import com.telecom.ecloudframework.org.api.constant.UserTypeConstant;
import com.telecom.ecloudframework.org.api.model.IUser;
import com.telecom.ecloudframework.org.api.model.IUserRole;

import java.util.Date;
import java.util.List;


/**
 * <pre>
 * 描述：用户表 实体对象
 * </pre>
 *
 * @author
 */
public class UserDTO implements IUser {
    private static final long serialVersionUID = -700694295167942753L;

    /**
     * 来源-system
     */
    public static final String FROM_SYSTEM = "system";
    /**
     * 来源-人力资源
     */
    public static final String FROM_HUMAN_RESOURCE = "humanResource";

    /**
     * id_
     */
    protected String id;

    /**
     * 姓名
     */
    protected String fullname;

    /**
     * 账号
     */
    protected String account;

    /**
     * 密码
     */
    protected String password;

    /**
     * 邮箱
     */
    protected String email;

    /**
     * 手机号码
     */
    protected String mobile;

    /**
     * 微信号
     */
    protected String weixin;

    /**
     * 座机号码
     */
    protected String telephone;

    /**
     * 创建时间
     */
    protected java.util.Date createTime;

    /**
     * 地址
     */
    protected String address;

    /**
     * 头像
     */
    protected String photo;

    /**
     * 性别：男，女，未知
     */
    protected String sex;

    /**
     * 来源
     */
    protected String from;

    /**
     * 0:禁用，1正常
     */
    protected Integer status;
    /**
     * openid
     */
    protected String openid;
    /**
     * 排序
     */
    protected Integer sn;

    /**
     * 组织ID
     */
    protected String orgId;
    /**
     * 组织名称
     */
    protected String orgName;
    /**
     * 组织code
     */
    protected String orgCode;

    /**
     * 岗位id
     */
    protected String postId;

    /**
     * 岗位名称
     */
    protected String postName;
    /**
     * 岗位code
     */
    protected String postCode;

    /**
     * 用户是否是首次登录（1：是 0：否）
     */
    protected String fristLogin;

    /**
     * 角色列表
     */
    protected List<IUserRole> roles;

    /**
     * 用户关系
     */
    protected List<RelationDTO> orgRelationList;
    /**
     * 用户类型 {@linkplain UserTypeConstant}
     */
    protected String type;
    /**
     * 管理员机构id列表
     */
    protected List<String> managerGroupIdList;

    public UserDTO() {
        from = FROM_SYSTEM;
    }

    /**
     * 来自人事管理
     *
     * @return
     */
    public UserDTO fromHumanResource() {
        from = FROM_HUMAN_RESOURCE;
        return this;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return this.id;
    }

    @Override
    public String getUserId() {
        return id;
    }

    @Override
    public void setUserId(String userId) {
        this.id = userId;
    }

    @Override
    public String getFullname() {
        return fullname;
    }

    @Override
    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    @Override
    public String getAccount() {
        return account;
    }

    @Override
    public void setAccount(String account) {
        this.account = account;
    }

    @Override
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    @Override
    public String getWeixin() {
        return weixin;
    }

    public void setWeixin(String weixin) {
        this.weixin = weixin;
    }

    @Override
    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    @Override
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

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    @Override
    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    @Override
    public String getOpenid() {
        return openid;
    }

    public void setOpenid(String openid) {
        this.openid = openid;
    }

    @Override
    public Integer getSn() {
        return sn;
    }

    @Override
    public void setSn(Integer sn) {
        this.sn = sn;
    }

    @Override
    public String getOrgId() {
        return orgId;
    }

    public void setOrgId(String orgId) {
        this.orgId = orgId;
    }

    @Override
    public String getPostId() {
        return postId;
    }

    public void setPostId(String postId) {
        this.postId = postId;
    }

    @Override
    public List<IUserRole> getRoles() {
        return roles;
    }

    public void setRoles(List<IUserRole> roles) {
        this.roles = roles;
    }

    public List<RelationDTO> getOrgRelationList() {
        return orgRelationList;
    }

    public void setOrgRelationList(List<RelationDTO> orgRelationList) {
        this.orgRelationList = orgRelationList;
    }

    @Override
    public String getOrgName() {
        return orgName;
    }

    public void setOrgName(String orgName) {
        this.orgName = orgName;
    }

    @Override
    public String getPostName() {
        return postName;
    }

    public void setPostName(String postName) {
        this.postName = postName;
    }


    @Override
    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public List<String> getManagerGroupIdList() {
        return managerGroupIdList;
    }

    public void setManagerGroupIdList(List<String> managerGroupIdList) {
        this.managerGroupIdList = managerGroupIdList;
    }

    public String getFristLogin() {
        return fristLogin;
    }

    public void setFristLogin(String fristLogin) {
        this.fristLogin = fristLogin;
    }

    @Override
    public String getOrgCode() {
        return orgCode;
    }

    public void setOrgCode(String orgCode) {
        this.orgCode = orgCode;
    }

    @Override
    public String getPostCode() {
        return postCode;
    }

    public void setPostCode(String postCode) {
        this.postCode = postCode;
    }
}