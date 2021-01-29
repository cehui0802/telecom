package com.telecom.ecloudframework.security.core.dao;

import com.telecom.ecloudframework.base.dao.BaseDao;
import com.telecom.ecloudframework.security.core.model.MenuRole;
import org.apache.ibatis.annotations.Param;
import org.mybatis.spring.annotation.MapperScan;

/**
 * <pre>
 * 描述：角色菜单关联 DAO接口
 * </pre>
 *
 * @author 谢石
 * @date 2020-9-23
 */
@MapperScan
public interface MenuRoleDao extends BaseDao<String, MenuRole> {

    /**
     * 删除
     *
     * @param systemId
     * @param roleId
     * @param menuId
     * @author 谢石
     * @date 2020-7-15 16:32
     */
    void removeByInfo(@Param("systemId") String systemId, @Param("roleId") String roleId, @Param("menuId") String menuId);

    /**
     * 部分更新
     *
     * @param menuRole
     * @return
     * @author 谢石
     * @date 2020-8-3
     */
    int updateByPrimaryKeySelective(MenuRole menuRole);
}
