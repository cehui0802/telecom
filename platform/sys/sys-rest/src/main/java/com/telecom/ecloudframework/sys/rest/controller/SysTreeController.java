package com.telecom.ecloudframework.sys.rest.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.telecom.ecloudframework.base.api.aop.annotion.OperateLog;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.telecom.ecloudframework.base.api.aop.annotion.CatchErr;
import com.telecom.ecloudframework.base.api.query.QueryFilter;
import com.telecom.ecloudframework.base.api.response.impl.ResultMsg;
import com.telecom.ecloudframework.base.core.id.IdUtil;
import com.telecom.ecloudframework.base.core.util.StringUtil;
import com.telecom.ecloudframework.base.db.model.page.PageResult;
import com.telecom.ecloudframework.base.rest.ControllerTools;
import com.telecom.ecloudframework.base.rest.util.RequestUtil;
import com.telecom.ecloudframework.sys.core.manager.SysTreeManager;
import com.telecom.ecloudframework.sys.core.manager.SysTreeNodeManager;
import com.telecom.ecloudframework.sys.core.model.SysTree;

/**
 * <pre>
 * 描述：sysTree层的controller
 * 作者:aschs
 * 邮箱:aschs@qq.com
 * 日期:下午5:11:06
 * 版权:summer
 * </pre>
 */
@RestController
@RequestMapping("/sys/sysTree/")
public class SysTreeController extends ControllerTools {
    @Autowired
    SysTreeManager sysTreeManager;
    @Autowired
    SysTreeNodeManager sysTreeNodeManager;

    /**
     * <pre>
     * sysTreeEdit.html的save后端
     * </pre>
     *
     * @param request
     * @param response
     * @param sysTree
     * @throws Exception
     */
    @RequestMapping("save")
    @CatchErr(write2response = true, value = "保存系统树失败")
    public ResultMsg<SysTree> save(HttpServletRequest request, HttpServletResponse response, @RequestBody SysTree sysTree) throws Exception {
        if (StringUtil.isEmpty(sysTree.getId())) {
            sysTree.setId(IdUtil.getSuid());
            if(null==sysTree.getDesc()&&null!=sysTree.getDescription()){
                sysTree.setDesc(sysTree.getDescription());
            }
            sysTreeManager.create(sysTree);
        } else {
            if(null==sysTree.getDesc()&&null!=sysTree.getDescription()){
                sysTree.setDesc(sysTree.getDescription());
            }
            sysTreeManager.update(sysTree);
        }
        return getSuccessResult( sysTree, "保存系统树成功");
    }

    /**
     * <pre>
     * 获取sysTree的后端
     * </pre>
     *
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    @RequestMapping("getObject")
    @CatchErr(write2response = true, value = "获取sysTree异常")
    public ResultMsg<SysTree> getObject(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String id = RequestUtil.getString(request, "id");
        String key = RequestUtil.getString(request, "key");
        SysTree sysTree = null;
        if (StringUtil.isNotEmpty(id)) {
            sysTree = sysTreeManager.get(id);
        } else if (StringUtil.isNotEmpty(key)) {
            sysTree = sysTreeManager.getByKey(key);
        }
        if(null!=sysTree.getDesc()&&null==sysTree.getDescription()){
            sysTree.setDescription(sysTree.getDesc());
        }
        return getSuccessResult(sysTree);
    }

    /**
     * <pre>
     * list页的后端
     * </pre>
     *
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    @RequestMapping("listJson")
    @ResponseBody
    @OperateLog
    public PageResult listJson(HttpServletRequest request, HttpServletResponse response) throws Exception {
        QueryFilter queryFilter = getQueryFilter(request);
        List<SysTree> list = sysTreeManager.query(queryFilter);
        return new PageResult(list);
    }

    /**
     * <pre>
     * 批量删除
     * </pre>
     *
     * @param request
     * @param response
     * @throws Exception
     */
    @RequestMapping("remove")
    @CatchErr(write2response = true, value = "删除系统树失败")
    public ResultMsg<String> remove(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String[] aryIds = RequestUtil.getStringAryByStr(request, "id");
        for (String id : aryIds) {
            sysTreeManager.removeContainNode(id);
        }
        return  getSuccessResult( "删除系统树成功");
    }
}
