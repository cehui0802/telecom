package com.telecom.ecloudframework.org.rest.controller;

import com.telecom.ecloudframework.base.api.aop.annotion.CatchErr;
import com.telecom.ecloudframework.base.api.aop.annotion.OperateLog;
import com.telecom.ecloudframework.base.api.exception.BusinessMessage;
import com.telecom.ecloudframework.base.api.query.QueryFilter;
import com.telecom.ecloudframework.base.api.query.QueryOP;
import com.telecom.ecloudframework.base.api.response.impl.ResultMsg;
import com.telecom.ecloudframework.base.db.model.page.PageResult;
import com.telecom.ecloudframework.base.rest.BaseController;
import com.telecom.ecloudframework.org.api.context.ICurrentContext;
import com.telecom.ecloudframework.org.core.manager.GroupManager;
import com.telecom.ecloudframework.org.core.manager.OrgRelationManager;
import com.telecom.ecloudframework.org.core.manager.RoleManager;
import com.telecom.ecloudframework.org.core.model.Role;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * <pre>
 * 描述：角色管理 控制器类
 * </pre>
 *
 * @author 谢石
 * @date 2020-7-2 11:16
 */
@RestController
@RequestMapping("/org/role/default")
public class RoleController extends BaseController<Role> {
    @Resource
    RoleManager roleManager;
    @Resource
    OrgRelationManager orgRelationMananger;
    @Resource
    GroupManager groupManager;
    @Autowired
    ICurrentContext currentContext;

    @Override
    protected String getModelDesc() {
        return "角色";
    }

    /**
     * 编辑角色
     *
     * @param role
     * @return
     * @throws Exception
     */
    @Override
    @CatchErr
    @OperateLog
    @RequestMapping("save")
    public ResultMsg<String> save(@RequestBody Role role) throws Exception {
        boolean isExist = roleManager.isRoleExist(role);
        if (isExist) {
            throw new BusinessMessage("角色在系统中已存在!");
        }
        return super.save(role);
    }

    /**
     * 移除 角色 权限
     *
     * @param id
     * @return
     * @throws Exception
     */
    @Override
    @CatchErr
    @OperateLog
    @RequestMapping("remove")
    public ResultMsg<String> remove(String id) throws Exception {
        orgRelationMananger.removeCheck(id);
        return super.remove(id);
    }

    /**
     * 查询 角色
     *
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    @Override
    @OperateLog
    @RequestMapping("listJson")
    public PageResult listJson(HttpServletRequest request, HttpServletResponse response) {
        String name = request.getParameter("name");
        String userId = request.getParameter("userId");
        String excludeUserId = request.getParameter("excludeUserId");
        QueryFilter filter = getQueryFilter(request);
        if (StringUtils.isNotEmpty(name)) {
            filter.addFilter("trole.name_", name, QueryOP.LIKE);
        }
        if (StringUtils.isNotEmpty(userId)) {
            filter.getParams().put("userId", userId);
        }
        if (StringUtils.isNotEmpty(excludeUserId)) {
            filter.getParams().put("excludeUserId", excludeUserId);
        }
        List<Role> pageList = roleManager.query(filter);
        return new PageResult(pageList);
    }

    /**
     * 设置角色状态
     *
     * @param id
     * @param status
     * @return
     * @author 谢石
     * @date 2020-7-29
     */
    @RequestMapping("setStatus")
    @CatchErr
    public ResultMsg<String> setStatus(@RequestParam(name = "id") String id, @RequestParam(name = "status") Integer status) {
        roleManager.setStatus(id, status);
        return getSuccessResult();
    }
}
