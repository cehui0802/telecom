package com.telecom.ecloudframework.org.api.model;

import io.swagger.annotations.ApiModel;

import java.io.Serializable;


/**
 * <pre>
 * 描述：抽象关系
 * </pre>
 *
 * @author 谢石
 * @date 2020-12-1
 */
@ApiModel(description = "关系定义")
public interface IRelation extends Serializable {

    /**
     * 获取组id
     *
     * @return
     */
    String getGroupId();

    /**
     * 获取用户id
     *
     * @return
     */
    String getUserId();

    /**
     * 获取主从标识
     *
     * @return
     */
    Integer getIsMaster();

    /**
     * 获取关系类型
     *
     * @return
     */
    String getType();

    /**
     * 获取自否含有子集
     *
     * @return
     */
    String getHasChild();

}
