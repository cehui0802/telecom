package com.telecom.ecloudframework.security.rest.controller;


import com.telecom.ecloudframework.base.api.aop.annotion.CatchErr;
import com.telecom.ecloudframework.base.api.aop.annotion.OperateLog;
import com.telecom.ecloudframework.base.api.query.QueryFilter;
import com.telecom.ecloudframework.base.api.query.QueryOP;
import com.telecom.ecloudframework.base.api.response.impl.ResultMsg;
import com.telecom.ecloudframework.base.core.util.StringUtil;
import com.telecom.ecloudframework.base.db.model.page.PageResult;
import com.telecom.ecloudframework.base.rest.BaseController;
import com.telecom.ecloudframework.security.core.manager.ClientManager;
import com.telecom.ecloudframework.security.core.model.Client;
import org.apache.commons.lang3.StringUtils;
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
 * 描述：客户端 控制器类
 * </pre>
 *
 * @author 谢石
 * @date 2020-7-13 13:59
 */
@RestController
@RequestMapping("/org/client")
public class ClientController extends BaseController<Client> {
    @Resource
    ClientManager clientManager;

    @Override
    protected String getModelDesc() {
        return "客户端";
    }

    /**
     * 查询 子系统
     *
     * @param request
     * @param response
     * @return
     */
    @Override
    @RequestMapping("listJson")
    @OperateLog
    public PageResult listJson(HttpServletRequest request, HttpServletResponse response) {
        String name = request.getParameter("name");
        QueryFilter filter = getQueryFilter(request);
        if (StringUtils.isNotEmpty(name)) {
            filter.addFilter("tclient.name_", name, QueryOP.LIKE);
        }
        List<Client> pageList = clientManager.query(filter);
        return new PageResult(pageList);
    }

    /**
     * 重新生成秘钥
     *
     * @param id
     * @return
     * @author 谢石
     * @date 2020-8-7
     */
    @RequestMapping("updateSecretKey")
    @CatchErr
    public ResultMsg<String> updateSecretKey(@RequestParam String id) {
        clientManager.updateSecretKey(id);
        return getSuccessResult();
    }

    /**
     * 保存
     */
    @RequestMapping("save")
    @CatchErr
    @OperateLog
    public ResultMsg<String> save(@RequestBody Client t) throws Exception {
        String desc;
        if (StringUtil.isEmpty(t.getId())) {
            desc = "添加%s成功";
            clientManager.create(t);
        } else {
            clientManager.update(t);
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
        clientManager.removeByIds(aryIds);
        return getSuccessResult(String.format("删除%s成功", getModelDesc()));
    }
}
