package com.telecom.ecloudframework.sys.rest.controller;

import com.telecom.ecloudframework.base.api.aop.annotion.CatchErr;
import com.telecom.ecloudframework.base.api.aop.annotion.OperateLog;
import com.telecom.ecloudframework.base.api.query.QueryFilter;
import com.telecom.ecloudframework.base.api.response.impl.ResultMsg;
import com.telecom.ecloudframework.base.core.id.IdUtil;
import com.telecom.ecloudframework.base.core.util.StringUtil;
import com.telecom.ecloudframework.base.db.model.page.PageResult;
import com.telecom.ecloudframework.base.rest.ControllerTools;
import com.telecom.ecloudframework.base.rest.util.RequestUtil;
import com.telecom.ecloudframework.sys.core.manager.SysDataSourceManager;
import com.telecom.ecloudframework.sys.core.model.SysDataSource;
import com.github.pagehelper.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;
import java.sql.Connection;

/**
 * <pre>
 * 描述：sysDataSource层的controller
 * 作者:aschs
 * 邮箱:aschs@qq.com
 * 日期:下午5:11:06
 * 版权:summer
 * </pre>
 */
@RestController
@RequestMapping("/sys/sysDataSource/")
public class SysDataSourceController extends ControllerTools {
    @Autowired
    SysDataSourceManager sysDataSourceManager;

    /**
     * <pre>
     * 测试连接
     * </pre>
     *
     * @param request
     * @param response
     * @throws Exception
     */
    @RequestMapping("checkConnection")
    @CatchErr(write2response = true, value = "连接失败")
    public ResultMsg<Boolean> checkConnection(HttpServletRequest request, HttpServletResponse response) throws Exception {
    	String key = RequestUtil.getString(request, "key");
    	String msg = "连接成功" ;
    	boolean connectable = true;
        try {
        	DataSource dataSource = sysDataSourceManager.getDataSourceByKey(key);
        	Connection connection = dataSource.getConnection();
            connection.close();
        } catch (Exception e) {
        	msg ="连接失败："+ e.getMessage();
        	connectable = false;
        	e.printStackTrace();
        }

        return getSuccessResult(connectable,msg);
    }

    /**
     * <pre>
     * sysDataSourceEdit.html的save后端
     * </pre>
     *
     * @param sysDataSource
     * @throws Exception
     */
    @RequestMapping("save")
    @OperateLog
    @CatchErr(write2response = true, value = "保存数据源失败")
    public ResultMsg<SysDataSource> save(@RequestBody SysDataSource sysDataSource) throws Exception {
        if (StringUtil.isEmpty(sysDataSource.getId())) {
            sysDataSource.setId(IdUtil.getSuid());
            sysDataSourceManager.create(sysDataSource);
        } else {
            sysDataSourceManager.update(sysDataSource);
        }

        return getSuccessResult(sysDataSource, "保存数据源成功");
    }

    /**
     * <pre>
     * 获取sysDataSource的后端
     * 目前支持根据id 获取sysDataSource
     * </pre>
     *
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    @RequestMapping("getObject")
    @CatchErr(write2response = true, value = "获取sysDataSource异常")
    public ResultMsg<SysDataSource> getObject(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String id = RequestUtil.getString(request, "id");
        SysDataSource sysDataSource = null;
        if (StringUtil.isNotEmpty(id)) {
            sysDataSource = sysDataSourceManager.get(id);
        }
        return getSuccessResult(sysDataSource);
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
    @OperateLog
    @ResponseBody
    public PageResult listJson(HttpServletRequest request, HttpServletResponse response) throws Exception {

        QueryFilter queryFilter = getQueryFilter(request);
        Page<SysDataSource> sysDataSourceList = (Page<SysDataSource>) sysDataSourceManager.query(queryFilter);
        return new PageResult(sysDataSourceList);
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
    @OperateLog
    @CatchErr(write2response = true, value = "删除数据源失败")
    public ResultMsg<String> remove(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String[] aryIds = RequestUtil.getStringAryByStr(request, "id");
        sysDataSourceManager.removeByIds(aryIds);
        return getSuccessResult("删除数据源成功");
    }
}
