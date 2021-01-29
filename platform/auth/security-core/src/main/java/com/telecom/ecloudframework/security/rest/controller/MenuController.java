package com.telecom.ecloudframework.security.rest.controller;


import com.telecom.ecloudframework.base.api.aop.annotion.CatchErr;
import com.telecom.ecloudframework.base.api.aop.annotion.OperateLog;
import com.telecom.ecloudframework.base.api.query.QueryFilter;
import com.telecom.ecloudframework.base.api.query.QueryOP;
import com.telecom.ecloudframework.base.api.response.impl.ResultMsg;
import com.telecom.ecloudframework.base.core.util.StringUtil;
import com.telecom.ecloudframework.base.db.model.query.DefaultQueryFilter;
import com.telecom.ecloudframework.base.rest.BaseController;
import com.telecom.ecloudframework.security.core.manager.MenuManager;
import com.telecom.ecloudframework.security.core.manager.MenuRoleManager;
import com.telecom.ecloudframework.security.core.manager.SubsystemManager;
import com.telecom.ecloudframework.security.core.model.Menu;
import com.telecom.ecloudframework.security.core.model.MenuRole;
import com.telecom.ecloudframework.security.core.model.MenuTree;
import com.telecom.ecloudframework.security.core.model.Subsystem;
import com.telecom.ecloudframework.sys.util.ContextUtil;
import cn.hutool.core.collection.CollectionUtil;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;


/**
 * <pre>
 * 描述：菜单 控制器类
 * </pre>
 *
 * @author 谢石
 * @date 2020-7-13 13:59
 */
@RestController
@RequestMapping("/org/menu")
public class MenuController extends BaseController<Menu> {
    @Resource
    MenuManager menuManager;
    @Resource
    SubsystemManager subsystemManager;
    @Resource
    MenuRoleManager menuRoleManager;

    @Override
    protected String getModelDesc() {
        return "菜单";
    }

    /**
     * 获取菜单树
     *
     * @param request
     * @param response
     * @return
     * @throws Exception
     * @author 谢石
     * @date 2020-7-15 11:02
     */
    @RequestMapping("getTreeData")
    @OperateLog
    public List<MenuTree> getTreeData(HttpServletRequest request, HttpServletResponse response, @RequestParam(name = "systemId") String systemId
            , @RequestParam(name = "menuType") String menuType) throws Exception {
        Subsystem subsystem = subsystemManager.get(systemId);
        QueryFilter queryFilter = new DefaultQueryFilter(true);
        if ("1".equals(menuType) && !ContextUtil.isAdmin(ContextUtil.getCurrentUser())) {
            queryFilter.addFilter("tmenu.enable_", 1, QueryOP.EQUAL);
            Map<String, Object> mapParams = new HashMap<>();
            mapParams.put("userId", ContextUtil.getCurrentUserId());
            queryFilter.addParams(mapParams);
        }
        queryFilter.addFilter("tmenu.system_id_", systemId, QueryOP.EQUAL);
        List<Menu> lstMenu = menuManager.query(queryFilter);
        List<MenuTree> lstMenuTree = new ArrayList<>();
        if (!CollectionUtil.isEmpty(lstMenu)) {
            lstMenu.forEach(menu -> lstMenuTree.add(new MenuTree(menu)));
        }
        // 根节点
        Menu rootMenu = new Menu();
        rootMenu.setName(subsystem.getName());
        rootMenu.setId("0");
        rootMenu.setSystemId(systemId);
        lstMenu.add(rootMenu);
        return lstMenuTree;
    }

    /**
     * 调整菜单树节点顺序
     *
     * @param param
     * @return
     * @author 谢石
     * @date 2020-7-14 16:57
     */
    @PostMapping("saveOrder")
    @CatchErr(write2response = true, value = "保存菜单树节点顺序失败")
    public ResultMsg saveOrder(@RequestBody List<Menu> param) {

        param.forEach(temp -> menuManager.saveOrder(temp));
        return getSuccessResult();
    }

    /**
     * 获取角色菜单列表
     *
     * @param roleId
     * @param systemId
     * @return
     * @author 谢石
     * @date 2020-7-15 15:05
     */
    @GetMapping("getRoleMenuList")
    public ResultMsg<List<String>> getRoleMenuList(@RequestParam(name = "roleId") String roleId, @RequestParam(name = "systemId") String systemId) {
        List<String> lstMenuId = menuManager.getRoleMenuList(roleId, systemId);
        return getSuccessResult(lstMenuId);
    }

    /**
     * 保存
     */
    @Override
    @RequestMapping("save")
    @CatchErr
    @OperateLog
    public ResultMsg<String> save(@RequestBody Menu t) {
        String desc;
        if (StringUtil.isEmpty(t.getId())) {
            desc = "添加%s成功";
            menuManager.create(t);
        } else {
            menuManager.update(t);
            desc = "更新%s成功";
        }
        String menuId = t.getId();
        String systemId = t.getSystemId();
        QueryFilter queryFilter = new DefaultQueryFilter(true);
        queryFilter.addFilter("system_id_", systemId, QueryOP.EQUAL);
        queryFilter.addFilter("menu_id_", menuId, QueryOP.EQUAL);

        List<MenuRole> lstMenuId = menuRoleManager.query(queryFilter);
        Set<String> mapRoleMenu = new HashSet<>();
        lstMenuId.forEach(temp -> mapRoleMenu.add(temp.getRoleId()));
        if (null != t.getRoles() && t.getRoles().size() > 0) {
            t.getRoles().forEach(temp -> {
                if (!mapRoleMenu.contains(temp)) {
                    MenuRole menuRole = new MenuRole();
                    menuRole.setMenuId(menuId);
                    menuRole.setRoleId(temp);
                    menuRole.setSystemId(systemId);
                    menuRoleManager.create(menuRole);
                } else {
                    mapRoleMenu.remove(temp);
                }
            });
        }
        mapRoleMenu.forEach(temp -> {
            menuRoleManager.remove(systemId, temp, menuId);
        });
        return getSuccessResult(t.getId(), String.format(desc, getModelDesc()));
    }

    /**
     * 批量删除
     */
    @Override
    @RequestMapping("remove")
    @CatchErr
    @OperateLog
    public ResultMsg<String> remove(@RequestParam String id) {
        String[] aryIds = StringUtil.getStringAryByStr(id);
        menuManager.removeByIds(aryIds);
        return getSuccessResult(String.format("删除%s成功", getModelDesc()));
    }
}
