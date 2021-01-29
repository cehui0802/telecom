package com.telecom.ecloudframework.security.core.manager;

import com.telecom.ecloudframework.base.manager.Manager;
import com.telecom.ecloudframework.security.core.model.ResRole;

import java.util.List;
import java.util.Set;

/**
 * <pre>
 * 描述：角色资源分配 处理接口
 * </pre>
 */
public interface ResRoleManager extends Manager<String, ResRole> {

    /**
     * 分配角色资源。
     *
     * @param resIds
     * @param systemId
     * @param roleId
     */
    void assignResByRoleSys(String resIds, String systemId, String roleId);

    /**
     * 通过url 获取可访问的角色
     *
     * @param url
     * @return
     */
    Set<String> getAccessRoleByUrl(String url);

    /**
     * 删除角色资源关联
     *
     * @param systemId
     * @param roleId
     * @param resId
     * @author 谢石
     * @date 2020-7-15 16:31
     */
    void remove(String systemId, String roleId, String resId);

    /**
     * 分配资源
     *
     * @param param
     * @author 谢石
     * @date 2020-9-17
     */
    void assignResByRoleSys(List<ResRole> param);
}
