package com.telecom.ecloudframework.sys.rest.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.telecom.ecloudframework.base.api.aop.annotion.CatchErr;
import com.telecom.ecloudframework.base.api.aop.annotion.OperateLog;
import com.telecom.ecloudframework.base.api.query.QueryFilter;
import com.telecom.ecloudframework.base.api.response.impl.ResultMsg;
import com.telecom.ecloudframework.base.core.util.StringUtil;
import com.telecom.ecloudframework.base.db.model.page.PageResult;
import com.telecom.ecloudframework.sys.api.groovy.IGroovyScriptEngine;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSONObject;
import com.telecom.ecloudframework.base.core.id.IdGenerator;
import com.telecom.ecloudframework.base.rest.BaseController;
import com.telecom.ecloudframework.base.rest.util.RequestUtil;
import com.telecom.ecloudframework.sys.core.manager.ScriptManager;
import com.telecom.ecloudframework.sys.core.model.Script;

@RestController
@RequestMapping("/sys/script/")
public class SysScriptController extends BaseController<Script> {

    @Resource
    private ScriptManager scriptManager;
    @Resource
    private IdGenerator idGenerator;
    @Resource
    IGroovyScriptEngine groovyScriptEngine;



    /**
     * 编辑@名称：系统脚本信息页面
     *
     * @param request
     * @param response
     * @return
     * @throws Exception ModelAndView
     */
    @RequestMapping("getCategoryList")
    public List<String> getCategoryList(HttpServletRequest request, HttpServletResponse response) throws Exception {
        List<String> categoryList = scriptManager.getDistinctCategory();
        return categoryList;
    }


    /**
     * 系统属性明细页面
     */
    @RequestMapping("getJson")
    @CatchErr(write2response = true)
    public ResultMsg<Script> getJson(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String id = RequestUtil.getString(request, "id");
        Script sysProperties = new Script();

        List<String> groups = scriptManager.getDistinctCategory();
        if (!StringUtil.isEmpty(id)) {
            sysProperties = scriptManager.get(id);
        }
        sysProperties.setCategorys(groups);

        return getSuccessResult(sysProperties);
    }


    @RequestMapping("executeScript")
    public Object executeScript(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String script = RequestUtil.getString(request, "script");
        String key = RequestUtil.getString(request, "key");
        Map<String, Object> map = new HashMap<String, Object>();
        JSONObject jsonObject = new JSONObject();
        try {
            Object obj = groovyScriptEngine.executeObject(script, map);
            jsonObject.put("val", obj);
            jsonObject.put("success", true);
        } catch (Exception e) {
            e.printStackTrace();
            jsonObject.put("val", "出错了" + e.getMessage());
        }
        jsonObject.put("key", key);
        jsonObject.put("script", script);
        return jsonObject;
    }

    @Override
    protected String getModelDesc() {
        return "常用脚本";
    }

    /**
     * 分页列表
     */
    @RequestMapping("listJson")
    @OperateLog
    public PageResult listJson(HttpServletRequest request, HttpServletResponse response) throws Exception {
        QueryFilter queryFilter = getQueryFilter(request);
        List<Script> pageList = scriptManager.query(queryFilter);
        return new PageResult(pageList);
    }

    /**
     * 保存
     */
    @RequestMapping("save")
    @CatchErr
    @OperateLog
    public ResultMsg<String> save(@RequestBody Script t) throws Exception {
        String desc;
        if (StringUtil.isEmpty(t.getId())) {
            desc = "添加%s成功";
            scriptManager.create(t);
        } else {
            scriptManager.update(t);
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
        scriptManager.removeByIds(aryIds);
        return getSuccessResult(String.format("删除%s成功", getModelDesc()));
    }
}
