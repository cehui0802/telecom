package com.telecom.ecloudframework.security.rest.controller;


import com.telecom.ecloudframework.base.api.aop.annotion.CatchErr;
import com.telecom.ecloudframework.base.api.aop.annotion.OperateLog;
import com.telecom.ecloudframework.base.api.exception.BusinessMessage;
import com.telecom.ecloudframework.base.api.query.QueryFilter;
import com.telecom.ecloudframework.base.api.query.QueryOP;
import com.telecom.ecloudframework.base.api.response.impl.ResultMsg;
import com.telecom.ecloudframework.base.core.id.IdUtil;
import com.telecom.ecloudframework.base.core.util.StringUtil;
import com.telecom.ecloudframework.base.db.model.page.PageResult;
import com.telecom.ecloudframework.base.rest.BaseController;
import com.telecom.ecloudframework.base.rest.util.RequestUtil;
import com.telecom.ecloudframework.org.api.model.IUser;
import com.telecom.ecloudframework.security.core.manager.SubsystemManager;
import com.telecom.ecloudframework.security.core.model.Subsystem;
import com.telecom.ecloudframework.sys.util.ContextUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;


/**
 * 描述：子系统定义 控制器类
 *
 * @author 谢石
 * @date 2020-7-30
 */
@RestController
@RequestMapping("/org/subsystem")
public class SubsystemController extends BaseController<Subsystem> {
    @Resource
    SubsystemManager subsystemManager;

    @RequestMapping("getUserSystem")
    @CatchErr(write2response = true)
    public @ResponseBody
    ResultMsg<List> getUserSystem(HttpServletRequest request, HttpServletResponse response) {
        List subsystem = subsystemManager.getAll();
        return getSuccessResult(subsystem);
    }

    /**
     * 子系统定义明细页面
     *
     * @param request
     * @param response
     * @return
     */
    @RequestMapping("getJson")
    public @ResponseBody
    Subsystem getJson(HttpServletRequest request, HttpServletResponse response) {
        String id = RequestUtil.getString(request, "id");
        if (StringUtil.isEmpty(id)) {
            return null;
        }
        Subsystem subsystem = subsystemManager.get(id);
        return subsystem;
    }

    /**
     * @param subsystem
     * @return 保存子系统定义信息
     * @throws
     */
    @RequestMapping("save")
    @CatchErr
    @OperateLog
    @Override
    public ResultMsg<String> save(@RequestBody Subsystem subsystem) {
        String resultMsg;

        boolean isExist = subsystemManager.isExist(subsystem);
        if (isExist) {
            throw new BusinessMessage("别名子系统中已存在!");
        }

        String id = subsystem.getId();
        if (StringUtil.isEmpty(id)) {
            subsystem.setId(IdUtil.getSuid());
            IUser user = ContextUtil.getCurrentUser();
            subsystemManager.create(subsystem);
            resultMsg = "添加子系统定义成功";
        } else {
            subsystemManager.update(subsystem);
            resultMsg = "更新子系统定义成功";
        }
        return getSuccessResult(resultMsg);
    }

    @Override
    protected String getModelDesc() {
        return "子系统定义";
    }

    /**
     * 查询 子系统
     *
     * @param request
     * @param response
     * @return
     */
    @Override
    @OperateLog
    @RequestMapping("listJson")
    public PageResult listJson(HttpServletRequest request, HttpServletResponse response) {
        String name = request.getParameter("name");
        QueryFilter filter = getQueryFilter(request);
        if (StringUtils.isNotEmpty(name)) {
            filter.addFilter("tsubsystem.name_", name, QueryOP.LIKE);
        }
        List<Subsystem> pageList = subsystemManager.query(filter);
        return new PageResult(pageList);
    }

    /**
     * 设置子系统状态
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
        subsystemManager.setStatus(id, status);
        return getSuccessResult();
    }

    /**
     * 批量删除
     */
    @RequestMapping("remove")
    @CatchErr
    @OperateLog
    public ResultMsg<String> remove(@RequestParam String id) throws Exception {
        String[] aryIds = StringUtil.getStringAryByStr(id);
        subsystemManager.removeByIds(aryIds);
        return getSuccessResult(String.format("删除%s成功", getModelDesc()));
    }

}
