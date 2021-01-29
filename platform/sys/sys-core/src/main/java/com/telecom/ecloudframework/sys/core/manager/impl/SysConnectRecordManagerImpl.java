package com.telecom.ecloudframework.sys.core.manager.impl;

import com.telecom.ecloudframework.base.manager.impl.BaseManager;
import com.telecom.ecloudframework.sys.core.dao.SysConnectRecordDao;
import com.telecom.ecloudframework.sys.core.manager.SysConnectRecordManager;
import com.telecom.ecloudframework.sys.core.model.SysConnectRecord;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * 公共业务关联记录 Manager处理实现类
 * @author Jeff
 * @email for_office@qq.com
 * @time 2019-08-10 21:54:11
 */
@Service("sysConnectRecordManager")
public class SysConnectRecordManagerImpl extends BaseManager<String, SysConnectRecord> implements SysConnectRecordManager{
	@Resource
    SysConnectRecordDao sysConnectRecordDao;

	@Override
	public List<SysConnectRecord> getByTargetId(String id, String type) {
		return sysConnectRecordDao.getByTargetId(id,type);
	}

	@Override
	public void bulkCreate(List<SysConnectRecord> list) {
		sysConnectRecordDao.bulkCreate(list);
	}

	@Override
	public void removeBySourceId(String sourceId, String type) {
		sysConnectRecordDao.removeBySourceId(sourceId,type);
	}

}
