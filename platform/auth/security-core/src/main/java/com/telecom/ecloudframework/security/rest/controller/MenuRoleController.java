package com.telecom.ecloudframework.security.rest.controller;


import com.telecom.ecloudframework.base.api.aop.annotion.CatchErr;
import com.telecom.ecloudframework.base.api.query.QueryFilter;
import com.telecom.ecloudframework.base.api.query.QueryOP;
import com.telecom.ecloudframework.base.api.response.impl.ResultMsg;
import com.telecom.ecloudframework.base.db.model.query.DefaultQueryFilter;
import com.telecom.ecloudframework.base.rest.BaseController;
import com.telecom.ecloudframework.security.core.manager.MenuManager;
import com.telecom.ecloudframework.security.core.manager.MenuRoleManager;
import com.telecom.ecloudframework.security.core.model.MenuRole;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.*;


/**
 * <pre>
 * 描述：角色菜单关联 控制器类
 * </pre>
 *
 * @author 谢石
 * @date 2020-7-15 15:58
 */
@RestController
@RequestMapping("/org/menuRole")
public class MenuRoleController extends BaseController<MenuRole> {
    @Resource
    MenuRoleManager menuRoleManager;
    @Resource
    MenuManager menuManager;

    @Override
    protected String getModelDesc() {
        return "角色菜单关联";
    }

    /**
     * 保存角色菜单关联信息
     *
     * @param param
     * @return
     * @author 谢石
     * @date 2020-7-14 16:57
     */
    @PostMapping("saveMenuRole")
    @CatchErr(write2response = true, value = "保存角色菜单关联信息失败")
    public ResultMsg saveMenuRole(@RequestBody List<MenuRole> param) {
        if (param.size() > 0) {
            MenuRole menuRole = param.get(0);
            String roleId = menuRole.getRoleId();
            String systemId = menuRole.getSystemId();
            QueryFilter queryFilter = new DefaultQueryFilter(true);
            queryFilter.addFilter("system_id_", systemId, QueryOP.EQUAL);
            queryFilter.addFilter("role_id_", roleId, QueryOP.EQUAL);

            List<MenuRole> lstMenuId = menuRoleManager.query(queryFilter);
            Set<String> mapRoleMenu = new HashSet<>();
            lstMenuId.forEach(temp -> mapRoleMenu.add(temp.getMenuId()));
            param.forEach(temp -> {
                if (!mapRoleMenu.contains(temp.getMenuId())) {
                    menuRoleManager.create(temp);
                } else {
                    mapRoleMenu.remove(temp.getMenuId());
                }
            });
            mapRoleMenu.forEach(temp -> {
                menuRoleManager.remove(systemId, roleId, temp);
            });
        }
        return getSuccessResult();
    }

}
