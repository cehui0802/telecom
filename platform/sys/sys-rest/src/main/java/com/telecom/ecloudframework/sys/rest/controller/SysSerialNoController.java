package com.telecom.ecloudframework.sys.rest.controller;


import com.telecom.ecloudframework.base.api.aop.annotion.CatchErr;
import com.telecom.ecloudframework.base.api.aop.annotion.OperateLog;
import com.telecom.ecloudframework.base.api.exception.BusinessMessage;
import com.telecom.ecloudframework.base.api.query.QueryFilter;
import com.telecom.ecloudframework.base.api.response.impl.ResultMsg;
import com.telecom.ecloudframework.base.core.util.StringUtil;
import com.telecom.ecloudframework.base.db.model.page.PageResult;
import com.telecom.ecloudframework.base.rest.ControllerTools;
import com.telecom.ecloudframework.base.rest.util.RequestUtil;
import com.telecom.ecloudframework.sys.api.constant.SysStatusCode;
import com.telecom.ecloudframework.sys.api.service.SerialNoService;
import com.telecom.ecloudframework.sys.core.manager.SerialNoManager;
import com.telecom.ecloudframework.sys.core.model.SerialNo;
import com.github.pagehelper.Page;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * 流水号生成管理
 */
@RestController
@RequestMapping("/sys/serialNo/")
public class SysSerialNoController extends ControllerTools {
    @Resource
    SerialNoManager serialNoManager;
    @Resource
    SerialNoService serialNoService;


    /**
     * 流水号生成列表(分页条件查询)数据
     *
     * @param request
     * @param response
     * @return
     * @throws Exception PageJson
     */
    @RequestMapping("listJson")
    @OperateLog
    public 
    PageResult listJson(HttpServletRequest request, HttpServletResponse response) throws Exception {

        QueryFilter queryFilter = getQueryFilter(request);
        Page<SerialNo> serialNoList = (Page<SerialNo>) serialNoManager.query(queryFilter);
        return new PageResult(serialNoList);
    }

    public static void main(String[] args) {

    }

    /**
     * 根据ID获取内容
     *
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    @RequestMapping("getById")
    @CatchErr(write2response = true)
    public ResultMsg<SerialNo> getById(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String id = RequestUtil.getString(request, "id");
        SerialNo SerialNo = serialNoManager.get(id);
        return getSuccessResult(SerialNo);
    }

    /**
     * 保存流水号生成信息
     *
     * @param SerialNo
     * @throws Exception void
     */
    @RequestMapping("save")
    @CatchErr
    @OperateLog
    public ResultMsg<String> save(@RequestBody SerialNo SerialNo) throws Exception {
        String resultMsg = null;

        boolean rtn = serialNoManager.isAliasExisted(SerialNo.getId(), SerialNo.getAlias());
        if (rtn) {
            throw new BusinessMessage("别名已经存在!", SysStatusCode.SERIALNO_EXSIT);
        }

        if (StringUtil.isEmpty(SerialNo.getId())) {
            serialNoManager.create(SerialNo);
            resultMsg = "添加流水号生成成功";
        } else {
            serialNoManager.update(SerialNo);
            resultMsg = "更新流水号生成成功";
        }

        return getSuccessResult(resultMsg);
    }


    /**
     * 批量删除流水号生成记录
     *
     * @param request
     * @param response
     * @throws Exception void
     */
    @RequestMapping("remove")
    @CatchErr("删除流水号失败")
    @OperateLog
    public ResultMsg<String> remove(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String[] aryIds = RequestUtil.getStringAryByStr(request, "id");

        serialNoManager.removeByIds(aryIds);
        return  getSuccessResult( "删除流水号成功");
    }

    /**
     * 取得流水号生成分页列表
     *
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    @RequestMapping("showlist")
    public 
    PageResult showlist(HttpServletRequest request, HttpServletResponse response) throws Exception {
        QueryFilter queryFilter = getQueryFilter(request);
        Page<SerialNo> SerialNoList = (Page<SerialNo>) serialNoManager.query(queryFilter);
        return new PageResult(SerialNoList);
    }

    /**
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    @RequestMapping("preview")
    public List<SerialNo> preview(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String alias = RequestUtil.getString(request, "alias");
        List<SerialNo> identities = serialNoManager.getPreviewIden(alias);

        return identities;
    }

    /**
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    @RequestMapping("getNextIdByAlias")
    public ResultMsg<String> getNextIdByAlias(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String alias = RequestUtil.getString(request, "alias");
        if (serialNoManager.isAliasExisted(null, alias)) {
            String nextId = serialNoService.genNextNo(alias);
            return getSuccessResult(nextId,"请求成功");
        }
        return getSuccessResult("","请求成功");
    }
}
