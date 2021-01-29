package com.telecom.ecloudframework.security.core.manager;

import com.telecom.ecloudframework.base.manager.Manager;
import com.telecom.ecloudframework.security.core.model.Subsystem;

import java.util.List;

/**
 * 子系统定义 处理接口
 *
 * @author 谢石
 * @date 2020-7-29
 */
public interface SubsystemManager extends Manager<String, Subsystem> {

    /**
     * 判断别名是否存在。
     *
     * @param subsystem
     * @return
     */
    boolean isExist(Subsystem subsystem);

    /**
     * 获取可用的子系统。
     *
     * @return
     */
    List<Subsystem> getList();

    /**
     * 获取默认子系统。
     * 1.获取用户有权限的系统，如果没有权限则返回空。
     * 2.如果权限子系统，判断是否有默认的子系统，有则返回。
     * 3.否则取第一个。
     *
     * @param userId
     * @return
     * @author 谢石
     * @date 2020-7-30
     */
    Subsystem getDefaultSystem(String userId);

    /**
     * 设置默认子系统。
     * 1.如果是默认的则取消。
     * 2.非默认则设置默认。
     *
     * @param systemId
     */
    void setDefaultSystem(String systemId);

    /**
     * 获取当前用户子系统集合
     *
     * @return
     * @author 谢石
     * @date 2020-7-30
     */
    List<Subsystem> getCuurentUserSystem();

    /**
     * 根据别名获取子系统
     *
     * @param systemAlias
     * @return
     * @author 谢石
     * @date 2020-7-30
     */
    Subsystem getByAlias(String systemAlias);

    /**
     * 设置子系统状态
     *
     * @param id
     * @param status
     * @author 谢石
     * @date 2020-7-29
     */
    void setStatus(String id, Integer status);
}
