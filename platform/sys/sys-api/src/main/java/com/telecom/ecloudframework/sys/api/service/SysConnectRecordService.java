package com.telecom.ecloudframework.sys.api.service;

import com.telecom.ecloudframework.sys.api.model.SysConnectRecordDTO;

import java.util.List;

public interface SysConnectRecordService {
	/**
	 * 通过 targetId 获取关联源
	 * @param type
	 * @return
	 */
	public List<SysConnectRecordDTO> getByTargetId(String targetId, String type);

	/**
	 * 批量保存
	 * @param records
	 */
	void save(List<SysConnectRecordDTO> records);

	/**
	 * 批量保存
	 * @param records
	 */
	void save(SysConnectRecordDTO records);

	/**
	 * 通过sourceId 删除
	 * @param type
	 * @param id
	 */
	void removeBySourceId(String id, String type);

	/**
	 * 检查是否被关联，并
	 * @param targetId
	 * @param type
	 */
	void checkIsRelatedWithBusinessMessage(String targetId, String type);
	
}
