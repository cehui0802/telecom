package com.telecom.ecloudframework.sys.core.model;

import com.telecom.ecloudframework.base.core.model.BaseModel;
import com.telecom.ecloudframework.sys.api.model.ISysFile;

/**
 * <pre>
 * 描述：系统附件信息
 * 作者:aschs
 * 邮箱:aschs@qq.com
 * 日期:2018年6月7日
 * 版权:summer
 * </pre>
 */
public class SysFile extends BaseModel implements ISysFile {
	/**
	 * 附件名
	 */
	private String name;
	/**
	 * <pre>
	 * 这附件用的是上传器
	 * 具体类型可以看 IUploader 的实现类
	 * </pre>
	 */
	private String uploader;
	/**
	 * <pre>
	 * 路径，这个路径能从上传器中获取到对应的附件内容
	 * 所以也不一定是路径，根据不同上传器会有不同值
	 * </pre>
	 */
	private String path;

	/**
	 * 备注，可以用于关联查询
	 */
	private String remark;

	/**
	 * 流程实例ID
	 */
	private String instId;
	/**
	 * 创建人名称
	 */
	private String creator;

	public String getCreator() {
		return creator;
	}

	public void setCreator(String creator) {
		this.creator = creator;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getUploader() {
		return uploader;
	}

	public void setUploader(String uploader) {
		this.uploader = uploader;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public String getInstId() {
		return instId;
	}

	public void setInstId(String instId) {
		this.instId = instId;
	}
}
