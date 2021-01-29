package com.telecom.ecloudframework.sys.core.manager;

import com.telecom.ecloudframework.base.manager.Manager;
import com.telecom.ecloudframework.sys.core.model.SysConnectRecord;

import java.util.List;

/**
 * 公共业务关联记录 Manager处理接口
 * @author Jeff
 * @email for_office@qq.com
 * @time 2019-08-10 21:54:11
 */
public interface SysConnectRecordManager extends Manager<String, SysConnectRecord>{

	List<SysConnectRecord> getByTargetId(String id, String type);
	
	public void bulkCreate(List<SysConnectRecord> list);

	void removeBySourceId(String sourceId, String type);
	
}
