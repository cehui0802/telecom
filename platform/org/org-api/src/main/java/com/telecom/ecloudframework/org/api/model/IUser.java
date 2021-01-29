package com.telecom.ecloudframework.org.api.model;

import com.telecom.ecloudframework.org.api.constant.UserTypeConstant;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;
import java.util.List;

/**
 * 用户实体接口
 *
 * @author 谢石
 * @date 2020-9-10
 */
@ApiModel(description = "用户信息")
public interface IUser extends Serializable {

    /**
     * 用户标识Id
     *
     * @return String
     */
    @ApiModelProperty("用户ID")
    String getUserId();

    /**
     * 设置用户id
     *
     * @param userId
     */
    void setUserId(String userId);

    /**
     * 用户姓名
     *
     * @return String
     */
    @ApiModelProperty("用户名")
    String getFullname();

    /**
     * 设置用户名
     *
     * @param fullName
     */
    void setFullname(String fullName);

    /**
     * 用户账号
     *
     * @return String
     */
    @ApiModelProperty("账户")
    String getAccount();

    /**
     * 设置账号
     *
     * @param account
     */
    void setAccount(String account);

    /**
     * 获取密码
     *
     * @return String
     */
    @ApiModelProperty("密码")
    String getPassword();

    /**
     * 邮件。
     *
     * @return String
     */
    @ApiModelProperty("Email")
    String getEmail();

    /**
     * 手机。
     *
     * @return String
     */
    @ApiModelProperty("手机号")
    String getMobile();

    /**
     * 微信号
     *
     * @return
     */
    @ApiModelProperty("微信号")
    String getWeixin();

    /**
     * openID
     *
     * @return
     */
    @ApiModelProperty("openID")
    String getOpenid();

    /**
     * 座机号
     *
     * @return
     */
    @ApiModelProperty("座机号")
    String getTelephone();

    /**
     * 是否启用
     *
     * @return
     */
    @ApiModelProperty("是否启用 0/1")
    Integer getStatus();

    /**
     * 照片
     *
     * @return
     */
    @ApiModelProperty("照片")
    String getPhoto();

    /**
     * 排序
     *
     * @return
     */
    @ApiModelProperty("用户sn")
    Integer getSn();

    /**
     * 设置排序
     *
     * @param sn
     */
    void setSn(Integer sn);

    /**
     * 获取角色列表
     *
     * @return
     */
    @ApiModelProperty("角色列表")
    List<? extends IUserRole> getRoles();

    /**
     * 用户类型 {@linkplain UserTypeConstant}
     *
     * @return String
     */
    @ApiModelProperty("用户类型")
    String getType();

    /**
     * 管理员机构id列表
     *
     * @return
     */
    @ApiModelProperty("管理员机构id列表")
    List<String> getManagerGroupIdList();

    /**
     * 用户主岗位id
     *
     * @return
     */
    @ApiModelProperty("主岗位id")
    String getPostId();

    /**
     * 用户主岗位名称
     *
     * @return
     */
    @ApiModelProperty("主岗位名称")
    String getPostName();
    /**
     * 用户主岗位编号
     *
     * @return
     */
    @ApiModelProperty("主岗位编号")
    String getPostCode();

    /**
     * 用户主机构id
     *
     * @return
     */
    @ApiModelProperty("主机构id")
    String getOrgId();

    /**
     * 用户主机构名称
     *
     * @return
     */
    @ApiModelProperty("主机构名称")
    String getOrgName();

    /**
     * 用户主机构名称
     *
     * @return
     */
    @ApiModelProperty("主机构code")
    String getOrgCode();
}
