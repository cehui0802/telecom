package com.telecom.ecloudframework.base.operateLog;

import com.telecom.ecloudframework.base.api.response.impl.ResultMsg;
import com.telecom.ecloudframework.base.operateLog.manager.LogOperateManager;
import com.telecom.ecloudframework.base.operateLog.model.LogOperate;
import com.telecom.ecloudframework.base.rest.BaseController;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * 系统操作日志控制层
 *
 * @author guolihao
 * @date 2020/9/21 16:25
 */
@RestController
@RequestMapping("/base/sysLogOperate/")
@Api(description = "系统操作日志服务")
public class SysLogOperateController extends BaseController<LogOperate> {

    private Logger LOGGER = LoggerFactory.getLogger(SysLogOperateController.class);

    @Resource
    LogOperateManager logOperateManager;

    @Override
    protected String getModelDesc() {
        return "系统操作日志";
    }

    /**
     * 根据id查询操作日志
     *
     * @param id id
     * @return ResultMsg
     * @author guolihao
     * @date 2020/9/24 20:52
     */
    @GetMapping("getLogOperateById")
    @ApiOperation(value = "根据id查询操作日志",notes = "根据id查询操作日志")
    public ResultMsg getLogOperateById(HttpServletRequest request,@RequestParam(value = "id")String id){
        if(StringUtils.isNotEmpty(id)){
            LogOperate logOperate = logOperateManager.getLogOperateById(id);
            return getSuccessResult(logOperate);
        }
        return getSuccessResult();
    }
}
