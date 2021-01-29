package com.telecom.ecloudframework.sys.core.manager;

import java.util.Date;

import com.telecom.ecloudframework.base.manager.Manager;
import com.telecom.ecloudframework.sys.core.model.HolidayConf;

/**
 * 
 * <pre> 
 * 描述：节假日配置 处理接口
 * @author jeff
 * </pre>
 */
public interface HolidayConfManager extends Manager<String, HolidayConf>{
	public HolidayConf queryOne(String name, Date startDay, Date endDay);
}
