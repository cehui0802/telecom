package com.telecom.ecloudframework.org.api.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;

@ApiModel(description = "SSO用户信息")
public interface ISSOUser extends Serializable {

    /**
     * 用户标识Id
     *
     * @return String
     */
    @ApiModelProperty("用户ID")
    String getUserId();

    void setUserId(String userId);

    /**
     * 用户姓名
     *
     * @return String
     */
    @ApiModelProperty("用户名")
    String getFullname();

    void setFullname(String fullmame);

    /**
     * 用户账号
     *
     * @return String
     */
    @ApiModelProperty("账户")
    String getAccount();

    void setAccount(String account);

}
