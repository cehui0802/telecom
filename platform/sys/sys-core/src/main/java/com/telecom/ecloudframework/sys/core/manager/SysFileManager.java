package com.telecom.ecloudframework.sys.core.manager;

import com.telecom.ecloudframework.base.manager.Manager;
import com.telecom.ecloudframework.sys.core.model.SysFile;

import java.io.InputStream;
import java.util.List;
import java.util.Map;

/**
 * 系统附件 Manager处理接口
 * @author aschs
 * @email aschs@qq.com
 * @time 2018-06-07 23:54:49
 */
public interface SysFileManager extends Manager<String, SysFile>{

	/**
	 * <pre>
	 * 上传附件
	 * </pre>
	 * @param is
	 * @param fileName
	 * @return
	 */
	SysFile upload(InputStream is, String fileName);

	/**
	 * <pre>
	 * 上传附件
	 * </pre>
	 * @param is
	 * @param sysFile
	 * @return
	 */
	void upload(InputStream is, SysFile sysFile);

	/**
	 * 根据上传器类型获取上传链接
	 * @return
	 */
	Map uploadUrl(String fileName, String remark, String uploaderType);

	/**
	 * 获取下载链接
	 * @param fileId
	 * @return
	 */
	String downloadUrl(String fileId);


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
	void delete(String fileId);


	/**
	 * <pre>
	 * 更新流程实例ID
	 * </pre>
	 * @param instId
	 * @param fileId
	 */
	void updateInstid(String instId, String fileId);

	/**
	 * 根据流程实例id获取附件列表
	 * @param instId
	 * @return
	 */
	List<SysFile> getFileByInstId(String instId);

	 @Override
	 public SysFile get(String id);


	/**
	 * 更新文件：保存编辑后的文件
	 * @param inputStream
	 * @param sysFile
	 * @param fileId
	 */
	void modify(InputStream inputStream, SysFile sysFile, String fileId);
}
