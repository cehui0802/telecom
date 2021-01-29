package com.telecom.ecloudframework.base.errorlog.dao;

import com.telecom.ecloudframework.base.dao.BaseDao;
import com.telecom.ecloudframework.base.errorlog.mode.LogErr;
import org.mybatis.spring.annotation.MapperScan;

/**
 * <pre>
 * 描述：错误日志 DAO接口
 * </pre>
 */
@MapperScan
public interface LogErrDao extends BaseDao<String, LogErr> {
}
