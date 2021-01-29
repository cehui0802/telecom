package com.telecom.ecloudframework.security.core.dao;

import com.telecom.ecloudframework.base.dao.BaseDao;
import com.telecom.ecloudframework.security.core.model.Menu;
import org.apache.ibatis.annotations.Param;
import org.mybatis.spring.annotation.MapperScan;

import java.util.List;

/**
 * <pre>
 * 描述：菜单 DAO接口
 * </pre>
 *
 * @author 谢石
 * @date 2020-7-13 13:53
 */
@MapperScan
public interface MenuDao extends BaseDao<String, Menu> {
    /**
     * 根据Code取定义对象。
     *
     * @param code
     * @param systemId
     * @param excludeId
     * @return
     */
    Menu getByCode(@Param("code") String code, @Param("systemId") String systemId, @Param("excludeId") String excludeId);

    /**
     * 部分更新
     *
     * @param menu
     * @return
     * @author 谢石
     * @date 2020-7-14 17:05
     */
    int updateByPrimaryKeySelective(Menu menu);

    /**
     * 获取角色菜单列表
     *
     * @param roleId
     * @param systemId
     * @return
     * @author 谢石
     * @date 2020-7-15 15:07
     */
    List<String> getRoleMenuList(@Param("roleId") String roleId, @Param("systemId") String systemId);
}
