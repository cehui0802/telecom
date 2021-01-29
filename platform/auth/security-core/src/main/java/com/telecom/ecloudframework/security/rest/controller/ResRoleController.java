package com.telecom.ecloudframework.security.rest.controller;


import com.telecom.ecloudframework.base.api.aop.annotion.CatchErr;
import com.telecom.ecloudframework.base.api.exception.BusinessError;
import com.telecom.ecloudframework.base.api.query.QueryFilter;
import com.telecom.ecloudframework.base.api.response.impl.ResultMsg;
import com.telecom.ecloudframework.base.core.util.BeanUtils;
import com.telecom.ecloudframework.base.core.util.StringUtil;
import com.telecom.ecloudframework.base.db.model.page.PageResult;
import com.telecom.ecloudframework.base.rest.ControllerTools;
import com.telecom.ecloudframework.base.rest.util.RequestUtil;
import com.telecom.ecloudframework.security.core.manager.ResRoleManager;
import com.telecom.ecloudframework.security.core.manager.SubsystemManager;
import com.telecom.ecloudframework.security.core.manager.SysResourceManager;
import com.telecom.ecloudframework.security.core.model.ResRole;
import com.telecom.ecloudframework.security.core.model.Subsystem;
import com.telecom.ecloudframework.security.core.model.SysResource;
import cn.hutool.core.collection.CollectionUtil;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


/**
 * <pre>
 * 描述：角色资源分配 控制器类
 * </pre>
 *
 * @author
 */
@RestController
@RequestMapping("/org/resRole")
public class ResRoleController extends ControllerTools {
    @Resource
    ResRoleManager resRoleManager;

    @Resource
    SysResourceManager sysResourceManager;

    @Resource
    SubsystemManager subsystemManager;

    /**
     * 角色资源分配列表(分页条件查询)数据
     *
     * @param request
     * @param response
     * @return
     * @throws Exception PageJson
     * @throws
     */
    @RequestMapping("listJson")
    public @ResponseBody
    PageResult listJson(HttpServletRequest request, HttpServletResponse response) {
        QueryFilter queryFilter = getQueryFilter(request);
        List<ResRole> pageList = resRoleManager.query(queryFilter);
        return new PageResult(pageList);
    }


    /**
     * 角色资源分配明细页面
     *
     * @param request
     * @param response
     * @return
     * @throws Exception ModelAndView
     */
    @RequestMapping("getJson")
    public @ResponseBody
    ResRole getJson(HttpServletRequest request, HttpServletResponse response) {
        String id = RequestUtil.getString(request, "id");
        if (StringUtil.isEmpty(id)) {
            return null;
        }
        ResRole resRole = resRoleManager.get(id);
        return resRole;
    }

    /**
     * 保存角色资源分配信息
     *
     * @param request
     * @param response
     * @throws Exception void
     * @throws
     */
    @RequestMapping("save")
    @CatchErr("对角色资源分配操作失败")
    public ResultMsg<String> save(HttpServletRequest request, HttpServletResponse response) {
        String roleId = RequestUtil.getString(request, "roleId");
        String systemId = RequestUtil.getString(request, "systemId");
        String resIds = RequestUtil.getString(request, "resIds");

        resRoleManager.assignResByRoleSys(resIds, systemId, roleId);
        return getSuccessResult(" 添加角色资源分配成功");
    }

    /**
     * 批量删除角色资源分配记录
     *
     * @param request
     * @param response
     * @throws Exception void
     * @throws
     */
    @RequestMapping("remove")
    public ResultMsg remove(HttpServletRequest request, HttpServletResponse response) {
        ResultMsg message;
        try {
            String[] aryIds = RequestUtil.getStringAryByStr(request, "id");
            resRoleManager.removeByIds(aryIds);
            message = new ResultMsg(ResultMsg.SUCCESS, "删除角色资源分配成功");
        } catch (Exception e) {
            message = new ResultMsg(ResultMsg.FAIL, "删除角色资源分配失败");
        }
        return message;
    }


    @RequestMapping("getTreeData")
    @CatchErr
    public ResultMsg<List<SysResource>> getTreeData(HttpServletRequest request, HttpServletResponse response) {
        String roleId = RequestUtil.getRQString(request, "roleId");
        String systemId = RequestUtil.getRQString(request, "systemId");

        List<SysResource> roleResourceList = sysResourceManager.getBySystemAndRole(systemId, roleId);
        Set<String> userResourceId = new HashSet<>(roleResourceList.size(), 1);
        roleResourceList.forEach(resouces -> userResourceId.add(resouces.getId()));

        List<SysResource> resourceList = sysResourceManager.getBySystemId(systemId);
        for (SysResource sysResource : resourceList) {
            if (userResourceId.contains(sysResource.getId())) {
                sysResource.setChecked(true);
            }
        }

        if (CollectionUtil.isEmpty(resourceList)) {
            resourceList = new ArrayList<>();
        }
        // 根节点
        SysResource rootRes = new SysResource();
        String rootName = subsystemManager.get(systemId).getName();
        rootRes.setName(rootName);
        rootRes.setId("0");
        rootRes.setSystemId(systemId);
        resourceList.add(rootRes);
        return getSuccessResult(resourceList);
    }


    @RequestMapping("getRoleResTreeData")
    @CatchErr
    public ResultMsg<List<SysResource>> getRoleResTreeData(HttpServletRequest request, HttpServletResponse response) {
        String roleId = RequestUtil.getRQString(request, "roleId");
        String systemAlias = RequestUtil.getRQString(request, "systemAlias");

        Subsystem system = subsystemManager.getByAlias(systemAlias);
        if (system == null) {
            throw new BusinessError("不存在的系统 ,系统别名:" + systemAlias);
        }
        String systemId = system.getId();

        List<SysResource> roleResourceList = sysResourceManager.getBySystemAndRole(systemId, roleId);
        Set<String> userResourceId = new HashSet<>(roleResourceList.size(), 1);
        roleResourceList.forEach(resouces -> userResourceId.add(resouces.getId()));

        List<SysResource> resourceList = sysResourceManager.getBySystemId(system.getId());
        for (SysResource sysResource : resourceList) {
            if (userResourceId.contains(sysResource.getId())) {
                sysResource.setChecked(true);
            }
        }

        if (CollectionUtil.isEmpty(resourceList)) {
            resourceList = new ArrayList<>();
        }
        // 根节点
        SysResource rootRes = new SysResource();
        String rootName = subsystemManager.get(systemId).getName();
        rootRes.setName(rootName);
        rootRes.setId("0");
        rootRes.setAlias("root");
        rootRes.setSystemId(systemId);
        resourceList.add(rootRes);

        return getSuccessResult(BeanUtils.listToTree(resourceList));
    }

    /**
     * 保存角色资源关联信息
     *
     * @param param
     * @return
     * @author 谢石
     * @date 2020-7-14 16:57
     */
    @PostMapping("saveResRole")
    @CatchErr(write2response = true, value = "保存角色资源关联信息失败")
    public ResultMsg saveResRole(@RequestBody List<ResRole> param) {
        resRoleManager.assignResByRoleSys(param);
        return getSuccessResult(" 添加角色资源分配成功");
    }
}
