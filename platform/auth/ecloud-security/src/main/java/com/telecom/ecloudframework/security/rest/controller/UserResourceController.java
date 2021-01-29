package com.telecom.ecloudframework.security.rest.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.telecom.ecloudframework.security.service.SysResourceService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.telecom.ecloudframework.base.api.aop.annotion.CatchErr;
import com.telecom.ecloudframework.base.api.exception.BusinessMessage;
import com.telecom.ecloudframework.base.api.response.impl.ResultMsg;
import com.telecom.ecloudframework.base.core.util.AppUtil;
import com.telecom.ecloudframework.base.core.util.BeanUtils;
import com.telecom.ecloudframework.base.core.util.CookieUitl;
import com.telecom.ecloudframework.base.core.util.StringUtil;
import com.telecom.ecloudframework.base.rest.ControllerTools;
import com.telecom.ecloudframework.base.rest.util.RequestUtil;
import com.telecom.ecloudframework.org.api.constant.GroupTypeConstant;
import com.telecom.ecloudframework.org.api.model.IGroup;
import com.telecom.ecloudframework.org.api.model.IUser;
import com.telecom.ecloudframework.org.api.model.system.ISubsystem;
import com.telecom.ecloudframework.org.api.model.system.ISysResource;
import com.telecom.ecloudframework.org.api.service.GroupService;
import com.telecom.ecloudframework.security.util.SubSystemUtil;
import com.telecom.ecloudframework.sys.api.constant.ResouceTypeConstant;
import com.telecom.ecloudframework.sys.util.ContextUtil;

import cn.hutool.core.collection.CollectionUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

/**
 * 用户资源
 *
 * @author Jeff
 */
@RestController
@Api(description = "登陆用户信息服务接口")
public class UserResourceController extends ControllerTools {
    @Resource
    GroupService orgService;
    @Resource
    SysResourceService sysResourceService;


    @RequestMapping(value = "/org/userResource/userMsg", method = {RequestMethod.POST, RequestMethod.GET})
    @CatchErr
    @ApiOperation(value = "用户信息", notes = "获取用户信息，当前组织，可切换的组织岗位，当前系统，拥有的系统列表等信息")
    public ResultMsg userMsg(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String systemAlias = RequestUtil.getString(request, "system");
        if (StringUtil.isEmpty(systemAlias)) {
            systemAlias = SubSystemUtil.getSystemId(request);
        }
        List<? extends ISubsystem> subsystemList = sysResourceService.getCuurentUserSystem();
        if (CollectionUtil.isEmpty(subsystemList)) {
            throw new BusinessMessage("当前用户尚未分配任何资源权限！");
        }

        ISubsystem currentSystem = null;
        //获取当前系统
        if (StringUtil.isNotEmpty(systemAlias)) {
            for (ISubsystem system : subsystemList) {
                if (system.getAlias().equals(systemAlias)) {
                    currentSystem = system;
                    break;
                }
            }
        } else {
            //获取默认系统
            currentSystem = sysResourceService.getDefaultSystem(ContextUtil.getCurrentUserId());
        }

        //没有之前使用的系统
        if (currentSystem == null) {
            currentSystem = subsystemList.get(0);
        }

        IGroup group = ContextUtil.getCurrentGroup();
        IGroup groupParent = null;
        if (null != group) {
            groupParent = orgService.getById(GroupTypeConstant.ORG.key(), group.getParentId());
        }
        List<? extends IGroup> orgList = orgService.getGroupsByGroupTypeUserId(GroupTypeConstant.ORG.key(), ContextUtil.getCurrentUserId());

        ResultMsg result = getSuccessResult()
                .addMapParam("currentEnviroment", AppUtil.getCtxEnvironment())
                .addMapParam("subsystemList", subsystemList)
                .addMapParam("currentSystem", currentSystem)
                .addMapParam("currentOrg", group)
                .addMapParam("currentOrgParentOrg", groupParent)
                .addMapParam("orgList", orgList)
                .addMapParam("user", ContextUtil.getCurrentUser());

        getSysResource(result, currentSystem.getId());

        return result;
    }

    // 切换系统
    @RequestMapping("userResource/changeSystem")
    public ResultMsg changeSystem(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String systemAlias = RequestUtil.getString(request, "systemAlias");
        SubSystemUtil.setSystemId(request, response, systemAlias);

        return getSuccessResult("切换成功");
    }

    // 切换组织
    @RequestMapping("userResource/changeOrg")
    public ResultMsg changeOrg(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String id = RequestUtil.getString(request, "orgId");
        IGroup org = orgService.getById(GroupTypeConstant.ORG.key(), id);
        ContextUtil.setCurrentOrg(org);
        CookieUitl.addCookie("currentOrg", id, CookieUitl.cookie_no_expire);

        return getSuccessResult("切换成功");
    }

    private void getSysResource(ResultMsg result, String systemId) {
        IUser user = ContextUtil.getCurrentUser();
        boolean isAdmin = ContextUtil.isAdmin(user);
        List<? extends ISysResource> list = null;
        if (isAdmin) {
            list = sysResourceService.getBySystemId(systemId);
        } else {
            list = sysResourceService.getBySystemAndUser(systemId, user.getUserId());
        }

        // 菜单和按钮分离
        List<ISysResource> menuList = new ArrayList<>();
        Map<String, Boolean> buttonPermision = new HashMap<>();

        list.forEach(resouces -> {
            System.out.println(resouces.getName() + " ---- " + resouces.getType());
            if (ResouceTypeConstant.MENU.getKey().equals(resouces.getType())) {
                menuList.add(resouces);
            } else {
                buttonPermision.put(resouces.getAlias(), resouces.getEnable() == 1);
            }
        });


        result.addMapParam("userMenuList", BeanUtils.listToTree(menuList));
        result.addMapParam("buttonPermision", buttonPermision);
    }
}
