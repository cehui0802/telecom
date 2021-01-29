package com.telecom.ecloudframework.org.api.model;

import com.telecom.ecloudframework.org.api.constant.GroupTypeConstant;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;


/**
 * 描述：抽象用户组类型
 *
 * @author
 */
@ApiModel(description = "组定义，含org,role,post 组类型详见 GroupTypeConstant.java")
public interface IGroup extends Serializable {

    /**
     * 组织ID
     *
     * @return
     */
    @ApiModelProperty("组ID")
    String getGroupId();

    /**
     * 组织名称
     *
     * @return
     */
    @ApiModelProperty("组名字")
    String getGroupName();

    /**
     * 组织编码
     *
     * @return
     */
    @ApiModelProperty("组CODE")
    String getGroupCode();

    /**
     * 组织类型。
     * {@linkplain GroupTypeConstant}
     *
     * @return
     */
    @ApiModelProperty("组类型：org,role,post")
    String getGroupType();

    /**
     * 组级别
     *
     * @return
     */
    @ApiModelProperty("组级别")
    Integer getGroupLevel();

    /**
     * 上级ID
     *
     * @return
     */
    @ApiModelProperty("树形组 parentId")
    String getParentId();

    /**
     * 排序
     *
     * @return
     */
    @ApiModelProperty("排序sn")
    Integer getSn();

    /**
     * 路径
     *
     * @return
     */
    @ApiModelProperty("路径")
    String getPath();

    /**
     * 用户人数
     *
     * @return
     */
    @ApiModelProperty("用户人数")
    Integer getUserNum();
    /**
     * 简称
     *
     * @return
     */
    @ApiModelProperty("简称")
    String getSimple();
}
