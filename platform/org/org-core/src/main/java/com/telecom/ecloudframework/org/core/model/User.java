package com.telecom.ecloudframework.org.core.model;

import com.telecom.ecloudframework.base.core.model.BaseModel;
import com.telecom.ecloudframework.org.api.constant.UserTypeConstant;
import com.telecom.ecloudframework.org.api.model.IUser;
import com.telecom.ecloudframework.org.api.model.IUserRole;
import org.hibernate.validator.constraints.NotBlank;

import java.util.List;


/**
 * <pre>
 * 描述：用户表 实体对象
 * </pre>
 *
 * @author
 */
public class User extends BaseModel implements IUser {
    private static final long serialVersionUID = -700694295167942753L;
    /**
     * 姓名
     */
    @NotBlank(message = "用户姓名不能为空")
    protected String fullname;

    /**
     * 账号
     */
    @NotBlank(message = "用户账户不能为空")
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
     * openId
     */
    protected String openid;

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
    protected String from = "system";

    /**
     * 0:禁用，1正常
     */
    protected Integer status;
    /**
     * 排序
     */
    protected Integer sn;
    /**
     * 座机号码
     */
    protected String telephone;
    /**
     * 激活状态 0 未激活 1激活
     */
    protected Integer activeStatus;
    /**
     * 密级 0无、1秘密、2机密、3机密增强、4绝密
     */
    protected Integer secretLevel;

    /**
     * 用户关系
     */
    protected List<OrgRelation> orgRelationList;
    /**
     * 用户类型 {@linkplain UserTypeConstant}
     */
    protected String type;
    /**
     * 管理员机构id列表
     */
    protected List<String> managerGroupIdList;
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

    @Override
    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    @Override
    public String getFullname() {
        return this.fullname;
    }

    @Override
    public void setAccount(String account) {
        this.account = account;
    }

    @Override
    public String getAccount() {
        return this.account;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public String getPassword() {
        return this.password;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public String getEmail() {
        return this.email;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    @Override
    public String getMobile() {
        return this.mobile;
    }

    public void setWeixin(String weixin) {
        this.weixin = weixin;
    }

    @Override
    public String getWeixin() {
        return this.weixin;
    }

    @Override
    public void setCreateTime(java.util.Date createTime) {
        this.createTime = createTime;
    }

    @Override
    public java.util.Date getCreateTime() {
        return this.createTime;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getAddress() {
        return this.address;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    @Override
    public String getPhoto() {
        return this.photo;
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
    public List<IUserRole> getRoles() {
        return null;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getSex() {
        return this.sex;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getFrom() {
        return this.from;
    }

    @Override
    public String getOpenid() {
        return openid;
    }

    public void setOpenid(String openid) {
        this.openid = openid;
    }

    public List<OrgRelation> getOrgRelationList() {
        return orgRelationList;
    }

    public void setOrgRelationList(List<OrgRelation> orgRelationList) {
        this.orgRelationList = orgRelationList;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    @Override
    public Integer getStatus() {
        return this.status;
    }

    @Override
    public String getUserId() {
        return this.id;
    }

    @Override
    public void setUserId(String userId) {
        this.id = userId;
    }

    @Override
    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public Integer getActiveStatus() {
        return activeStatus;
    }

    public void setActiveStatus(Integer activeStatus) {
        this.activeStatus = activeStatus;
    }

    public Integer getSecretLevel() {
        return secretLevel;
    }

    public void setSecretLevel(Integer secretLevel) {
        this.secretLevel = secretLevel;
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

    @Override
    public String getOrgId() {
        return orgId;
    }

    public void setOrgId(String orgId) {
        this.orgId = orgId;
    }

    @Override
    public String getOrgName() {
        return orgName;
    }

    public void setOrgName(String orgName) {
        this.orgName = orgName;
    }

    @Override
    public String getOrgCode() {
        return orgCode;
    }

    public void setOrgCode(String orgCode) {
        this.orgCode = orgCode;
    }

    @Override
    public String getPostId() {
        return postId;
    }

    public void setPostId(String postId) {
        this.postId = postId;
    }

    @Override
    public String getPostName() {
        return postName;
    }

    public void setPostName(String postName) {
        this.postName = postName;
    }

    public void setManagerGroupIdList(List<String> managerGroupIdList) {
        this.managerGroupIdList = managerGroupIdList;
    }

    @Override
    public String getPostCode() {
        return postCode;
    }

    public void setPostCode(String postCode) {
        this.postCode = postCode;
    }

    @Override
    public String toString() {
        return "User{" +
                "fullname='" + fullname + '\'' +
                ", account='" + account + '\'' +
                ", password='" + password + '\'' +
                ", email='" + email + '\'' +
                ", mobile='" + mobile + '\'' +
                ", weixin='" + weixin + '\'' +
                ", openid='" + openid + '\'' +
                ", createTime=" + createTime +
                ", address='" + address + '\'' +
                ", photo='" + photo + '\'' +
                ", sex='" + sex + '\'' +
                ", from='" + from + '\'' +
                ", status=" + status +
                ", sn=" + sn +
                ", telephone='" + telephone + '\'' +
                ", activeStatus=" + activeStatus +
                ", orgRelationList=" + orgRelationList +
                ", id='" + id + '\'' +
                ", createTime=" + createTime +
                ", createBy='" + createBy + '\'' +
                ", updateTime=" + updateTime +
                ", updateBy='" + updateBy + '\'' +
                ", version=" + version +
                ", delete=" + delete +
                '}';
    }
}