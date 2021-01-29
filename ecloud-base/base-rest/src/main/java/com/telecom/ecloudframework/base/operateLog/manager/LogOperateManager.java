package com.telecom.ecloudframework.base.operateLog.manager;

import com.telecom.ecloudframework.base.manager.Manager;
import com.telecom.ecloudframework.base.operateLog.model.LogOperate;

/**
 * 记录操作日志处理接口
 *
 * @author guolihao
 * @date 2020/9/19 10:01
 */
public interface LogOperateManager extends Manager<String, LogOperate> {

    /**
     * 根据id查询操作日志
     *
     * @param id id
     * @return ResultMsg
     * @author guolihao
     * @date 2020/9/24 20:52
     */
    LogOperate getLogOperateById(String id);
}
