package com.telecom.ecloudframework.security.core.dao;

import com.telecom.ecloudframework.base.dao.BaseDao;
import com.telecom.ecloudframework.security.core.model.SysResource;
import org.apache.ibatis.annotations.Param;
import org.mybatis.spring.annotation.MapperScan;

import java.util.List;

/**
 * <pre>
 * 描述：子系统资源 DAO接口
 * </pre>
 *
 * @author
 */
@MapperScan
public interface SysResourceDao extends BaseDao<String, SysResource> {
    /**
     * 根据子系统ID取定义对象。
     *
     * @param systemId
     * @return
     */
    List<SysResource> getBySystemId(String systemId);

    /**
     * 根据角色和系统id获取资源。
     *
     * @param systemId
     * @param roleId
     * @return
     */
    List<SysResource> getBySystemAndRole(@Param("systemId") String systemId, @Param("roleId") String roleId);

    /**
     * 判断资源是否存在。
     *
     * @param resource
     * @return
     */
    Integer isExist(SysResource resource);

    /**
     * 根据父ID获取下级节点。
     *
     * @param parentId
     * @return
     */
    List<SysResource> getByParentId(String parentId);

    /**
     * 根据系统id和用户id获取资源列表。
     *
     * @param systemId 系统id
     * @param userId   用户id
     * @return
     */
    List<SysResource> getBySystemAndUser(@Param("systemId") String systemId, @Param("userId") String userId);

    /**
     * 根据Code取定义对象。
     *
     * @param code
     * @param excludeId
     * @return
     */
    SysResource getByCode(@Param("code") String code, @Param("excludeId") String excludeId);

    /**
     * 部分更新
     *
     * @param resource
     * @return
     * @author 谢石
     * @date 2020-7-14 17:05
     */
    void updateByPrimaryKeySelective(SysResource resource);

    /**
     * 获取角色资源列表
     *
     * @param roleId
     * @param systemId
     * @return
     * @author 谢石
     * @date 2020-7-15 15:07
     */
    List<String> getResResList(@Param("roleId") String roleId, @Param("systemId") String systemId);
}
