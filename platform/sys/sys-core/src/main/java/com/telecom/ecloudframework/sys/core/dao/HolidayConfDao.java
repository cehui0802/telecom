package com.telecom.ecloudframework.sys.core.dao;
import java.util.Date;

import org.apache.ibatis.annotations.Param;

import com.telecom.ecloudframework.base.dao.BaseDao;
import com.telecom.ecloudframework.sys.core.model.HolidayConf;

public interface HolidayConfDao extends BaseDao<String, HolidayConf> {
	public HolidayConf queryOne(@Param("name")String name,@Param("startDay") Date startDay, @Param("endDay")Date endDay);
}
