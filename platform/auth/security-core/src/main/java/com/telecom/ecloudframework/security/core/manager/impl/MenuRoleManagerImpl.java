package com.telecom.ecloudframework.security.core.manager.impl;

import com.telecom.ecloudframework.base.core.cache.ICache;
import com.telecom.ecloudframework.base.core.id.IdUtil;
import com.telecom.ecloudframework.base.manager.impl.BaseManager;
import com.telecom.ecloudframework.security.core.dao.MenuRoleDao;
import com.telecom.ecloudframework.security.core.manager.MenuRoleManager;
import com.telecom.ecloudframework.security.core.model.MenuRole;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * <pre>
 * 描述：角色菜单关联 处理实现类
 * </pre>
 *
 * @author 谢石
 * @date 2020-7-15 15:52
 */
@Service("menuRoleManager")
public class MenuRoleManagerImpl extends BaseManager<String, MenuRole> implements MenuRoleManager {
    @Resource
    MenuRoleDao menuRoleDao;
    @Resource
    ICache iCache;

    /**
     * 创建
     *
     * @param entity
     * @author 谢石
     * @date 2020-7-15 16:32
     */
    @Override
    public void create(MenuRole entity) {
        entity.setId(IdUtil.getSuid());
        super.create(entity);
    }

    /**
     * 删除
     *
     * @param systemId
     * @param roleId
     * @param menuId
     * @author 谢石
     * @date 2020-7-15 16:32
     */
    @Override
    public void remove(String systemId, String roleId, String menuId) {
        menuRoleDao.removeByInfo(systemId, roleId, menuId);
    }
}
