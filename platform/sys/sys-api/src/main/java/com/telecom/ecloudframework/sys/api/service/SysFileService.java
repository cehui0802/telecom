package com.telecom.ecloudframework.sys.api.service;

import com.telecom.ecloudframework.sys.api.model.dto.SysFileDTO;

import java.io.InputStream;
import java.util.List;


public interface SysFileService {

	/**
	 * <pre>
	 * 根据上传器类型上传附件
	 * </pre>
	 * @param is
	 * @param fileName
	 * @return
	 */
	SysFileDTO upload(InputStream is, String fileName);

	/**
	 * <pre>
	 * 根据上传器类型上传附件
	 * </pre>
	 * @param is
	 * @param sysFileDTO
	 * @return
	 */
	SysFileDTO upload(InputStream is, SysFileDTO sysFileDTO);

	/**
	 * <pre>
	 * 下载附件
	 * 返回流
	 * </pre>	
	 * @param fileId
	 * @return
	 */
	InputStream download(String fileId);
	
	/**
	 * <pre>
	 * 删除附件
	 * 包括流信息
	 * </pre>	
	 * @param fileId
	 */
	void delete(String... fileId);


	/**
	 * <pre>
	 *     更新流程实例ID
	 * </pre>
	 * @param fileId
	 */
	void updateInstid(String instId,String fileId);

	/**
	 * 根据流程实例id获取附件列表
	 * @param instanceId
	 * @return
	 */
	List<SysFileDTO> getFileByInstId(String instanceId);


	public SysFileDTO get(String id);
}
