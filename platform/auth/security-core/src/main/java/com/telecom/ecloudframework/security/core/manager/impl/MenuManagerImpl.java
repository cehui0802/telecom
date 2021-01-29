package com.telecom.ecloudframework.security.core.manager.impl;

import com.telecom.ecloudframework.base.api.exception.BusinessMessage;
import com.telecom.ecloudframework.base.api.query.QueryFilter;
import com.telecom.ecloudframework.base.api.query.QueryOP;
import com.telecom.ecloudframework.base.core.cache.ICache;
import com.telecom.ecloudframework.base.core.id.IdUtil;
import com.telecom.ecloudframework.base.db.model.query.DefaultQueryFilter;
import com.telecom.ecloudframework.base.manager.impl.BaseManager;
import com.telecom.ecloudframework.security.core.dao.MenuDao;
import com.telecom.ecloudframework.security.core.manager.MenuManager;
import com.telecom.ecloudframework.security.core.model.Menu;
import com.telecom.ecloudframework.sys.util.ContextUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * <pre>
 * 描述：菜单 处理实现类
 * </pre>
 *
 * @author 谢石
 * @date 2020-7-13 13:54
 */
@Service("menuManager")
public class MenuManagerImpl extends BaseManager<String, Menu> implements MenuManager {
    @Resource
    MenuDao menuDao;
    @Resource
    ICache iCache;

    /**
     * 创建
     *
     * @param entity
     * @author 谢石
     * @date 2020-7-14 16:08
     */
    @Override
    public void create(Menu entity) {
        if (menuDao.getByCode(entity.getCode(), entity.getSystemId(), null) != null) {
            throw new BusinessMessage("菜单编码“" + entity.getCode() + "”已存在，请修改！");
        }
        entity.setId(IdUtil.getSuid());
        super.create(entity);
    }

    /**
     * 修改
     *
     * @param entity
     * @author 谢石
     * @date 2020-7-14 16:08
     */
    @Override
    public void update(Menu entity) {
        if (menuDao.getByCode(entity.getCode(), entity.getSystemId(), entity.getId()) != null) {
            throw new BusinessMessage("菜单编码“" + entity.getCode() + "”已存在，请修改！");
        }
        super.update(entity);
    }

    /**
     * 删除
     *
     * @param id
     * @author 谢石
     * @date 2020-7-14 16:08
     */
    @Override
    public void remove(String id) {
        Menu temp = get(id);
        if (null != temp) {
            super.remove(id);
            QueryFilter queryFilter = new DefaultQueryFilter(true);
            queryFilter.addFilter("parent_id_", id, QueryOP.EQUAL);
            List<Menu> lstMenu = menuDao.query(queryFilter);
            lstMenu.forEach(menu -> remove(menu.getId()));
        }
    }

    /**
     * 保存菜单排序
     *
     * @param menu
     * @author 谢石
     * @date 2020-7-14 17:02
     */
    @Override
    public void saveOrder(Menu menu) {
        if (null != menu) {
            Menu temp = new Menu();
            temp.setId(menu.getId());
            temp.setSn(menu.getSn());
            temp.setParentId(menu.getParentId());
            temp.setUpdateBy(ContextUtil.getCurrentUserId());
            if (StringUtils.isNotEmpty(menu.getId()) && null != menu.getSn() && menu.getSn().compareTo(0) != 0) {
                menuDao.updateByPrimaryKeySelective(temp);
            }
        }
    }

    /**
     * 获取角色菜单列表
     *
     * @param roleId
     * @param systemId
     * @return
     * @author 谢石
     * @date 2020-7-15 15:07
     */
    @Override
    public List<String> getRoleMenuList(String roleId, String systemId) {
        return menuDao.getRoleMenuList(roleId, systemId);
    }
}
