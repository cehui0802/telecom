package com.telecom.ecloudframework.sys.core.dao;

import com.telecom.ecloudframework.base.dao.BaseDao;
import com.telecom.ecloudframework.sys.core.model.SysConnectRecord;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 公共业务关联记录 DAO接口
 * @author Jeff
 * @email for_office@qq.com
 * @time 2019-08-10 21:54:11
 */
public interface SysConnectRecordDao extends BaseDao<String, SysConnectRecord> {

	void removeBySourceId(@Param("sourceId") String sourceId, @Param("type") String type);

	void bulkCreate(List<SysConnectRecord> list);

	List<SysConnectRecord> getByTargetId(@Param("targetId") String id, @Param("type") String type);
	
}
