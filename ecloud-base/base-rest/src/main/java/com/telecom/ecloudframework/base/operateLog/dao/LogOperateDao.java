package com.telecom.ecloudframework.base.operateLog.dao;

import com.telecom.ecloudframework.base.dao.BaseDao;
import com.telecom.ecloudframework.base.operateLog.model.LogOperate;
import org.apache.ibatis.annotations.Param;
import org.mybatis.spring.annotation.MapperScan;

/**
 * 日志记录处理dao层
 *
 * @author guolihao
 * @date 2020/9/19 11:38
 */
@MapperScan
public interface LogOperateDao extends BaseDao<String, LogOperate> {

    /**
     * 根据id查询操作日志
     *
     * @param id id
     * @return ResultMsg
     * @author guolihao
     * @date 2020/9/24 20:52
     */
    LogOperate getLogOperateById(@Param("id") String id);
}
