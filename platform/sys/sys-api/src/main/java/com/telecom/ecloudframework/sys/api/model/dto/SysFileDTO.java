package com.telecom.ecloudframework.sys.api.model.dto;

/**
 * <pre>
 * 描述：系统附件信息
 * 作者:aschs
 * 邮箱:aschs@qq.com
 * 日期:2018年6月7日
 * 版权:summer
 * </pre>
 */
public class SysFileDTO  {
	
	protected String id;
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
	 * 流程实例ID
	 */
	private String instId;

	/**
	 * 备注
	 */
	private String remark;


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

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public String getInstId() {
		return instId;
	}

	public void setInstId(String instId) {
		this.instId = instId;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}
}
