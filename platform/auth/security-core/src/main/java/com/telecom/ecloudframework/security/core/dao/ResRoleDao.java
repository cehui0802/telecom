package com.telecom.ecloudframework.security.core.dao;

import com.telecom.ecloudframework.base.dao.BaseDao;
import com.telecom.ecloudframework.security.core.model.ResRole;
import org.apache.ibatis.annotations.Param;
import org.mybatis.spring.annotation.MapperScan;

import java.util.List;

/**
 * <pre>
 * 描述：角色资源分配 DAO接口
 * </pre>
 *
 * @author 谢石
 * @date 2020-9-23
 */
@MapperScan
public interface ResRoleDao extends BaseDao<String, ResRole> {
    /**
     * 根据系统和角色删除资源
     *
     * @param roleId
     * @param systemId
     * @author 谢石
     * @date 2020-9-23
     */
    void removeByRoleAndSystem(@Param("roleId") String roleId, @Param("systemId") String systemId);

    /**
     * 获取资源和角色的映射关系
     *
     * @return
     */
    List<ResRole> getAllResRole();

    /**
     * 删除
     *
     * @param systemId
     * @param roleId
     * @param resId
     * @author 谢石
     * @date 2020-7-15 16:32
     */
    void removeByInfo(@Param("systemId") String systemId, @Param("roleId") String roleId, @Param("resId") String resId);

    /**
     * 部分更新
     *
     * @param resRole
     * @return
     * @author 谢石
     * @date 2020-8-3
     */
    int updateByPrimaryKeySelective(ResRole resRole);
}
