package com.telecom.ecloudframework.sys.core.model;

import com.telecom.ecloudframework.base.core.model.BaseModel;


/**
 * 公共业务关联记录 实体对象
 * @author Jeff
 * @email for_office@qq.com
 * @time 2019-08-10 21:54:11
 */
public class SysConnectRecord extends BaseModel{
	/**
	* 类型
	*/
	protected  String type; 
	/**
	* 源ID
	*/
	protected  String sourceId; 
	/**
	* 关联ID
	*/
	protected  String targetId; 
	/**
	* 提示信息
	*/
	protected  String notice; 
	
	
	
	
	
	public void setType( String type) {
		this.type = type;
	}
	
	/**
	 * 返回 类型
	 * @return
	 */
	public  String getType() {
		return this.type;
	}
	
	
	
	
	public void setSourceId( String sourceId) {
		this.sourceId = sourceId;
	}
	
	/**
	 * 返回 源ID
	 * @return
	 */
	public  String getSourceId() {
		return this.sourceId;
	}
	
	
	
	
	public void setTargetId( String targetId) {
		this.targetId = targetId;
	}
	
	/**
	 * 返回 关联ID
	 * @return
	 */
	public  String getTargetId() {
		return this.targetId;
	}
	
	
	
	
	public void setNotice( String notice) {
		this.notice = notice;
	}
	
	/**
	 * 返回 提示信息
	 * @return
	 */
	public  String getNotice() {
		return this.notice;
	}
	
 
}