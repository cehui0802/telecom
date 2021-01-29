package com.telecom.ecloudframework.security.rest.controller;


import com.telecom.ecloudframework.base.api.aop.annotion.CatchErr;
import com.telecom.ecloudframework.base.api.aop.annotion.OperateLog;
import com.telecom.ecloudframework.base.api.exception.BusinessMessage;
import com.telecom.ecloudframework.base.api.query.QueryFilter;
import com.telecom.ecloudframework.base.api.query.QueryOP;
import com.telecom.ecloudframework.base.api.response.impl.ResultMsg;
import com.telecom.ecloudframework.base.core.util.StringUtil;
import com.telecom.ecloudframework.base.db.model.query.DefaultQueryFilter;
import com.telecom.ecloudframework.base.rest.BaseController;
import com.telecom.ecloudframework.base.rest.util.RequestUtil;
import com.telecom.ecloudframework.security.core.manager.SubsystemManager;
import com.telecom.ecloudframework.security.core.manager.SysResourceManager;
import com.telecom.ecloudframework.security.core.model.Subsystem;
import com.telecom.ecloudframework.security.core.model.SysResource;
import com.telecom.ecloudframework.sys.api.constant.ResouceTypeConstant;
import cn.hutool.core.collection.CollectionUtil;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;


/**
 * <pre>
 * 描述：系统资源 控制器类
 * </pre>
 *
 * @author 谢石
 * @date 2020-7-13 13:59
 */
@RestController
@RequestMapping("/org/sysResource")
public class SysResourceController extends BaseController<SysResource> {
    @Resource
    SysResourceManager sysResourceManager;
    @Resource
    SubsystemManager subsystemManager;

    @Override
    protected String getModelDesc() {
        return "资源";
    }

    /**
     * 子系统资源明细页面
     *
     * @param request
     * @param response
     * @return
     * @throws Exception ModelAndView
     */
    @RequestMapping("getJson")
    public ResultMsg<SysResource> getJson(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String id = RequestUtil.getString(request, "id");
        if (StringUtil.isEmpty(id)) {
            String parentId = RequestUtil.getString(request, "parentId");
            String sysytemId = RequestUtil.getString(request, "systemId");
            SysResource sysResource = new SysResource();
            sysResource.setSystemId(sysytemId);
            sysResource.setParentId(parentId);
            sysResource.setOpened(1);
            return getSuccessResult(sysResource);
        } else {
            return getSuccessResult(sysResourceManager.get(id));
        }
    }

    private void checkResouce(SysResource sysResource) {
        boolean isExist = sysResourceManager.isExist(sysResource);
        if (isExist) {
            throw new BusinessMessage("资源别名已存在，请修改！");
        }
        // 如果是菜单、那上级也必须是菜单、防止按钮下面配置菜单
        if (ResouceTypeConstant.MENU.getKey().equals(sysResource.getType())) {
            SysResource parent = sysResourceManager.get(sysResource.getParentId());
            if (parent == null) {
                return;
            }

            if (!ResouceTypeConstant.MENU.getKey().equals(parent.getType())) {
                throw new BusinessMessage("菜单类型的资源，上级资源[" + parent.getName() + "]也必须是菜单！");
            }
        }
        if (StringUtil.isNotEmpty(sysResource.getUrl())) {
            sysResource.setUrl(sysResource.getUrl().trim());
        }

    }

    /**
     * 获取资源树
     *
     * @param request
     * @param response
     * @param systemId
     * @param resourceType
     * @return
     * @throws Exception
     * @author 谢石
     * @date 2020-7-15 11:03
     */
    @RequestMapping("getTreeData")
    @CatchErr
    @OperateLog
    public List<SysResource> getTreeData(HttpServletRequest request, HttpServletResponse response, @RequestParam(name = "systemId") String systemId
            , @RequestParam(name = "resourceType") String resourceType) throws Exception {
        Subsystem subsystem = subsystemManager.get(systemId);
        QueryFilter queryFilter = new DefaultQueryFilter(true);
        if (!"1".equals(resourceType)) {
            queryFilter.addFilter("tresource.enable_", 1, QueryOP.EQUAL);
        }
        queryFilter.addFilter("tresource.system_id_", systemId, QueryOP.EQUAL);
        List<SysResource> lstSysResource = sysResourceManager.query(queryFilter);
        if (CollectionUtil.isEmpty(lstSysResource)) {
            lstSysResource = new ArrayList<>();
        }
        return lstSysResource;
    }

    private List<SysResource> getGroupTree(String systemId) {
        List<SysResource> groupList = sysResourceManager.getBySystemId(systemId);
        return groupList;
    }

    /**
     * 调整资源树节点顺序
     *
     * @param param
     * @return
     * @author 谢石
     * @date 2020-7-14 16:57
     */
    @PostMapping("saveOrder")
    @CatchErr(write2response = true, value = "保存资源树节点顺序失败")
    public ResultMsg saveOrder(@RequestBody List<SysResource> param) {

        param.forEach(temp -> sysResourceManager.saveOrder(temp));
        return getSuccessResult();
    }

    /**
     * 获取角色资源列表
     *
     * @param roleId
     * @param systemId
     * @return
     * @author 谢石
     * @date 2020-7-15 15:05
     */
    @GetMapping("getRoleResourceList")
    public ResultMsg<List<String>> getRoleMenuList(@RequestParam(name = "roleId") String roleId, @RequestParam(name = "systemId") String systemId) {
        List<String> lstResourceId = sysResourceManager.getResResList(roleId, systemId);
        return getSuccessResult(lstResourceId);
    }

    /**
     * 保存
     */
    @RequestMapping("save")
    @CatchErr
    @OperateLog
    public ResultMsg<String> save(@RequestBody SysResource t) throws Exception {
        String desc;
        if (StringUtil.isEmpty(t.getId())) {
            desc = "添加%s成功";
            sysResourceManager.create(t);
        } else {
            sysResourceManager.update(t);
            desc = "更新%s成功";
        }
        return getSuccessResult(t.getId(),String.format(desc, getModelDesc()));
    }

    /**
     * 批量删除
     */
    @RequestMapping("remove")
    @CatchErr
    @OperateLog
    public ResultMsg<String> remove(@RequestParam String id) throws Exception {
        String[] aryIds = StringUtil.getStringAryByStr(id);
        sysResourceManager.removeByIds(aryIds);
        return getSuccessResult(String.format("删除%s成功", getModelDesc()));
    }
}
