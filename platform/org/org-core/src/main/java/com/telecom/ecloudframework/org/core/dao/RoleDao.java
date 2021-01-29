package com.telecom.ecloudframework.org.core.dao;

import com.telecom.ecloudframework.base.dao.BaseDao;
import com.telecom.ecloudframework.org.core.model.Role;

import java.util.List;

import org.mybatis.spring.annotation.MapperScan;

/**
 * 描述：角色管理 DAO接口
 *
 * @author 谢石
 * @date 2020-7-30
 */
@MapperScan
public interface RoleDao extends BaseDao<String, Role> {
    /**
     * 根据别名获取角色
     *
     * @param alias
     * @return
     * @author 谢石
     * @date 2020-7-30
     */
    Role getByAlias(String alias);

    /**
     * 判断角色系统中是否存在。
     *
     * @param role
     * @return
     */
    Integer isRoleExist(Role role);

    /**
     * 用过用户ID 获取角色
     *
     * @param userId
     * @return
     */
    List<Role> getByUserId(String userId);

    /**
     * 部分更新
     *
     * @param role
     * @return
     * @author 谢石
     * @date 2020-7-14 17:05
     */
    void updateByPrimaryKeySelective(Role role);
}
