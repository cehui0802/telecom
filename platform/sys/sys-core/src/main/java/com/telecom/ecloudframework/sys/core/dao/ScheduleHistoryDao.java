package com.telecom.ecloudframework.sys.core.dao;

import com.telecom.ecloudframework.base.dao.BaseDao;
import com.telecom.ecloudframework.sys.api.model.calendar.ScheduleHistory;
import org.apache.ibatis.annotations.Param;
import org.mybatis.spring.annotation.MapperScan;

import java.util.List;


@MapperScan
public interface ScheduleHistoryDao extends BaseDao<String,ScheduleHistory> {
    public List<ScheduleHistory> queryHistory(@Param("scheduleId") String scheduleId);
}
