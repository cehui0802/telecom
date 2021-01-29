package com.telecom.ecloudframework.org.sync.orm;

import com.alibaba.fastjson.annotation.JSONField;
import com.telecom.ecloudframework.base.core.util.StringUtil;

/**
 * 组映射
 * @author Administrator
 *
 */
public class GroupJsonOrm {
	
	protected  String id; 
	
	protected  String parentId; 
	
	protected  String name; 
	
	protected  String code; 
	
	protected  Integer type = 2;
	
	protected  Integer sn = 1;

	public String getId() {
		return id;
	}

	@JSONField(name = "orgId")
	public void setId(String id) {
		this.id = id;
	}

	public String getParentId() {
		return parentId;
	}

	@JSONField(name = "pid")
	public void setParentId(String parentId) {
		this.parentId = parentId;
	}

	public String getName() {
		return name;
	}
	
	@JSONField(name = "orgName")
	public void setName(String name) {
		this.name = name;
	}

	public String getCode() {
		if(StringUtil.isEmpty(code)) {
			return "code-".concat( this.id);
		}
		return code;
	}

	@JSONField(name = "orgCode")
	public void setCode(String code) {
		this.code = code;
	}

	public Integer getType() {
		return type;
	}

	@JSONField(name = "type")
	public void setType(Integer type) {
		this.type = type;
	}

	public Integer getSn() {
		return sn;
	}

	@JSONField(name = "sn")
	public void setSn(Integer sn) {
		this.sn = sn;
	} 
	
	
	
	
	
}
