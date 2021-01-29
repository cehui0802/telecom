package com.telecom.ecloudframework.sys.service.impl;

import com.telecom.ecloudframework.base.api.aop.annotion.ParamValidate;
import com.telecom.ecloudframework.base.api.exception.BusinessMessage;
import com.telecom.ecloudframework.base.core.util.BeanCopierUtils;
import com.telecom.ecloudframework.sys.api.model.SysConnectRecordDTO;
import com.telecom.ecloudframework.sys.api.service.SysConnectRecordService;
import com.telecom.ecloudframework.sys.core.manager.SysConnectRecordManager;
import com.telecom.ecloudframework.sys.core.model.SysConnectRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class DefaultSysConnectRecordService implements SysConnectRecordService {
	
	@Autowired
	SysConnectRecordManager connectRecordMananger;
	
	@Override
	public List<SysConnectRecordDTO> getByTargetId(String id, String type) {
		List<SysConnectRecord> list = connectRecordMananger.getByTargetId(id,type);
		return BeanCopierUtils.transformList(list, SysConnectRecordDTO.class);
	}

	@Override @ParamValidate
	public void save(List<SysConnectRecordDTO> records) {
		List<SysConnectRecord> recordsList = BeanCopierUtils.transformList(records, SysConnectRecord.class);
		connectRecordMananger.bulkCreate(recordsList);
	}

	@Override
	@ParamValidate
	public void save(SysConnectRecordDTO records) {
		connectRecordMananger.create(BeanCopierUtils.transformBean(records, SysConnectRecord.class));
	}


	@Override
	public void removeBySourceId(String sourceId,String type) {
		connectRecordMananger.removeBySourceId(sourceId,type);
	}

	@Override
	public void checkIsRelatedWithBusinessMessage(String targetId,String type) {
		List<SysConnectRecord> list = connectRecordMananger.getByTargetId(targetId,type);
		if(list.isEmpty())return;
		
		Set<String> notices = new HashSet<>();
		
		StringBuilder sb = new StringBuilder();
		list.forEach(record ->{
			// 排下重
			if(!notices.contains(record.getNotice())) {
				if(sb.length()>0) {
					sb.append("<br/>");
				}
				sb.append(record.getNotice());
				notices.add(record.getNotice());
			}
			
		});
		
		throw new BusinessMessage(sb.toString());
	}

}
