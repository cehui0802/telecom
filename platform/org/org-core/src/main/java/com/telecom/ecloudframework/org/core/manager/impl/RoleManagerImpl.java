package com.telecom.ecloudframework.org.core.manager.impl;

import java.util.Collections;
import java.util.List;

import javax.annotation.Resource;

import com.telecom.ecloudframework.sys.util.ContextUtil;
import com.telecom.ecloudframework.org.core.dao.RoleDao;
import com.telecom.ecloudframework.org.core.manager.RoleManager;
import com.telecom.ecloudframework.org.core.model.Role;
import org.springframework.stereotype.Service;

import com.telecom.ecloudframework.base.core.util.StringUtil;
import com.telecom.ecloudframework.base.manager.impl.BaseManager;

/**
 * <pre>
 * 描述：角色管理 处理实现类
 * </pre>
 *
 * @author 谢石
 * @date 2020-7-30
 */
@Service
public class RoleManagerImpl extends BaseManager<String, Role> implements RoleManager {
    @Resource
    RoleDao roleDao;

    @Override
    public Role getByAlias(String alias) {
        return roleDao.getByAlias(alias);
    }

    @Override
    public List<Role> getByUserId(String userId) {
        if (StringUtil.isEmpty(userId)) {
            return Collections.emptyList();
        }
        return roleDao.getByUserId(userId);
    }

    @Override
    public boolean isRoleExist(Role role) {
        return roleDao.isRoleExist(role) != 0;
    }

    /**
     * 设置子系统状态
     *
     * @param id
     * @param status
     * @author 谢石
     * @date 2020-7-29
     */
    @Override
    public void setStatus(String id, Integer status) {
        final String currentUserId = ContextUtil.getCurrentUserId();
        Role role = new Role();
        role.setId(id);
        role.setEnabled(status);
        role.setUpdateBy(currentUserId);
        roleDao.updateByPrimaryKeySelective(role);
    }
}
