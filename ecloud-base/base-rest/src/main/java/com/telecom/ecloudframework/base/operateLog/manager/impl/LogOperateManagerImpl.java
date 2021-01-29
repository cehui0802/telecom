package com.telecom.ecloudframework.base.operateLog.manager.impl;

import com.telecom.ecloudframework.base.manager.impl.BaseManager;
import com.telecom.ecloudframework.base.operateLog.dao.LogOperateDao;
import com.telecom.ecloudframework.base.operateLog.manager.LogOperateManager;
import com.telecom.ecloudframework.base.operateLog.model.LogOperate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * 处理操作日志业务处理层
 * 
 * @author guolihao
 * @date 2020/9/19 10:00
 */
@Service("sysLogOperateManager")
public class LogOperateManagerImpl extends BaseManager<String,LogOperate> implements LogOperateManager {

    @Resource
    LogOperateDao logOperateDao;

    /**
     * 根据id查询操作日志
     *
     * @param id id
     * @return ResultMsg
     * @author guolihao
     * @date 2020/9/24 20:52
     */
    @Override
    public LogOperate getLogOperateById(String id) {
        return logOperateDao.getLogOperateById(id);
    }
}
