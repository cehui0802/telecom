package com.telecom.ecloudframework.sys.rest.controller;


import com.telecom.ecloudframework.base.api.aop.annotion.CatchErr;
import com.telecom.ecloudframework.base.api.aop.annotion.OperateLog;
import com.telecom.ecloudframework.base.api.exception.BusinessMessage;
import com.telecom.ecloudframework.base.api.query.QueryFilter;
import com.telecom.ecloudframework.base.api.response.impl.ResultMsg;
import com.telecom.ecloudframework.base.core.id.IdUtil;
import com.telecom.ecloudframework.base.core.util.StringUtil;
import com.telecom.ecloudframework.base.db.model.page.PageResult;
import com.telecom.ecloudframework.base.rest.ControllerTools;
import com.telecom.ecloudframework.base.rest.util.RequestUtil;
import com.telecom.ecloudframework.sys.core.manager.SysPropertiesManager;
import com.telecom.ecloudframework.sys.core.model.SysProperties;
import com.github.pagehelper.Page;
import io.swagger.annotations.*;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;
import java.util.List;


/**
 * 系统属性
 *
 * @author
 */
@RestController
@RequestMapping("/sys/sysProperties")
@Api(description = "系统属性接口")
public class SysPropertiesController extends ControllerTools {
    @Resource
    SysPropertiesManager sysPropertiesManager;

    /**
     * 系统属性列表(分页条件查询)数据
     */
    @RequestMapping("listJson")
    @OperateLog
    public @ResponseBody
    PageResult listJson(HttpServletRequest request, HttpServletResponse response) {
        QueryFilter queryFilter = getQueryFilter(request);
        Page<SysProperties> sysPropertiesList = (Page<SysProperties>) sysPropertiesManager.query(queryFilter);
        return new PageResult(sysPropertiesList);
    }


    /**
     * 系统属性明细页面
     */
    @RequestMapping("getJson")
    @CatchErr(write2response = true)
    public ResultMsg<SysProperties> getJson(HttpServletRequest request, HttpServletResponse response) {
        String id = RequestUtil.getString(request, "id");
        SysProperties sysProperties = new SysProperties();

        List<String> groups = sysPropertiesManager.getGroups();
        if (StringUtil.isEmpty(id)) {
            sysProperties.setCategorys(groups);
        } else {
            sysProperties = sysPropertiesManager.get(id);
            sysProperties.setCategorys(groups);
        }

        return getSuccessResult(sysProperties);
    }

    /**
     * 保存系统属性信息
     *
     * @param request
     * @param response
     * @param sysProperties
     * @throws
     * @void
     */
    @RequestMapping("save")
    @CatchErr("对系统属性操作失败")
    @OperateLog
    public ResultMsg<String> save(HttpServletRequest request, HttpServletResponse response, @RequestBody SysProperties sysProperties) throws Exception {
        String resultMsg;

        boolean isExist = sysPropertiesManager.isExist(sysProperties);
        if (isExist) {
            throw new BusinessMessage("别名系统中已存在!");
        }

        String id = sysProperties.getId();
        sysProperties.setValByEncrypt();

        if (StringUtil.isEmpty(id)) {
            sysProperties.setId(IdUtil.getSuid());
            sysProperties.setCreateTime(new Date());
            sysPropertiesManager.create(sysProperties);
            resultMsg = "添加系统属性成功";
        } else {
            sysPropertiesManager.update(sysProperties);
            resultMsg = "更新系统属性成功";
        }

        sysPropertiesManager.reloadProperty();

        return getSuccessResult(resultMsg);
    }

    /**
     * 批量删除系统属性记录
     */
    @RequestMapping("remove")
    @CatchErr("删除系统属性失败")
    @OperateLog
    public ResultMsg<String> remove(HttpServletRequest request, HttpServletResponse response) {
        String[] aryIds = RequestUtil.getStringAryByStr(request, "id");

        sysPropertiesManager.removeByIds(aryIds);
        return getSuccessResult("删除系统属性成功");
    }

    /**
     * 根据别名获取系统属性
     *
     * @param request
     * @param response
     * @return
     * @author 谢石
     * @date 2020-9-23
     */
    @RequestMapping("getPropertyByAlias")
    @ApiOperation(value = "获取系统属性")
    @CatchErr("获取系统属性失败")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "form", dataType = "String", name = "alias", value = "别名")})
    public ResultMsg<String> getPropertyByAlias(HttpServletRequest request, HttpServletResponse response) {
        String alias = RequestUtil.getString(request, "alias");
        return getSuccessResult(sysPropertiesManager.getPropertyByAlias(alias));
    }

    @RequestMapping("getLoginTypePropertyByAlias")
    @ApiOperation(value = "获取登录方式系统属性")
    @CatchErr("获取登录方式系统属性失败")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "form", dataType = "String", name = "alias", value = "别名")})
    public ResultMsg<String> getLoginTypePropertyByAlias(HttpServletRequest request, HttpServletResponse response) {
        String alias = RequestUtil.getString(request, "alias");
        return getSuccessResult(sysPropertiesManager.getPropertyByAlias(alias));
    }

    /**
     * 根据别名更新系统属性
     *
     * @param request
     * @param response
     * @return
     * @author 谢石
     * @date 2020-9-23
     */
    @RequestMapping("updateByAlias")
    @ApiOperation(value = "更新系统属性")
    @CatchErr("更新系统属性失败")
    public ResultMsg<String> updateByAlias(HttpServletRequest request, HttpServletResponse response,
                                           @RequestBody @ApiParam(value = "系统属性实体", required = true) SysProperties sysProperties) throws Exception {
        sysProperties.setValByEncrypt();
        sysPropertiesManager.updateByAlias(sysProperties);
        sysPropertiesManager.reloadProperty();
        return getSuccessResult("更新系统属性成功");
    }
}
