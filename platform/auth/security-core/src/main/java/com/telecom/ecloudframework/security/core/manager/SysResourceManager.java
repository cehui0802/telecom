package com.telecom.ecloudframework.security.core.manager;

import com.telecom.ecloudframework.base.manager.Manager;
import com.telecom.ecloudframework.security.core.model.SysResource;

import java.util.List;

/**
 * <pre>
 * 描述：子系统资源 处理接口
 * </pre>
 *
 * @author
 */
public interface SysResourceManager extends Manager<String, SysResource> {

    /**
     * 根据子系统ID获取实体列表。
     */
    List<SysResource> getBySystemId(String id);


    /**
     * 根据系统和角色ID获取资源。
     *
     * @param systemId
     * @param roleId
     * @return
     */
    List<SysResource> getBySystemAndRole(String systemId, String roleId);

    /**
     * 判断资源是否存在。
     *
     * @param resource
     * @return
     */
    boolean isExist(SysResource resource);

    /**
     * 根据资源id递归删除资源数据。
     *
     * @param resId
     */
    void removeByResId(String resId);

    /**
     * 根据系统id和用户id获取资源。
     *
     * @param systemId
     * @param userId
     * @return
     */
    List<SysResource> getBySystemAndUser(String systemId, String userId);

    /**
     * 保存资源排序
     *
     * @param resource
     * @author 谢石
     * @date 2020-7-14 17:02
     */
    void saveOrder(SysResource resource);

    /**
     * 获取角色资源列表
     *
     * @param roleId
     * @param systemId
     * @return
     * @author 谢石
     * @date 2020-7-15 15:07
     */
    List<String> getResResList(String roleId, String systemId);
}
