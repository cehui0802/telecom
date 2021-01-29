package com.telecom.ecloudframework.sys.core.dao;
import java.util.Date;
import java.util.List;

import com.telecom.ecloudframework.sys.api.model.calendar.WorkCalenDar;
import org.apache.ibatis.annotations.Param;

import com.telecom.ecloudframework.base.dao.BaseDao;

/**
 *  jeff 
 */
public interface WorkCalenDarDao extends BaseDao<String, WorkCalenDar> {

	List<WorkCalenDar> getByDay(@Param("day")Date day);
	
	List<WorkCalenDar> getByPeriod(@Param("startDay")Date startDay, @Param("endDay")Date endDay);

	List<WorkCalenDar> getByPeriodWork(@Param("startDay")Date startDay, @Param("endDay")Date endDay);
	
	List<WorkCalenDar> getWorkDayByDays(@Param("startDay")Date startDay);
	
	List<WorkCalenDar> getWorkDayByDaysAndSystem(@Param("day")Date day, @Param("system")String system);
	
	public void updateWorkType(@Param("startDay")Date startDay, @Param("endDay")Date endDay, String isWorkDay, String type);

	List<WorkCalenDar> getByPeriodAndSystem(@Param("startDay")Date startDay, @Param("endDay")Date endDay, @Param("system")String system);

	List<WorkCalenDar> getWorkDayByDays(@Param("startDay")Date startDay,String system);

	List<WorkCalenDar> getByTimeContainPublic(@Param("startDay")Date startDay, @Param("endDay")Date endDay, @Param("system")String system);

	List<WorkCalenDar> getByDayAndSystem(Date day, String system);
}
