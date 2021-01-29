package com.telecom.ecloudframework.org.sync.orm;

import com.alibaba.fastjson.annotation.JSONField;

/*
 * 用户组 关系映射
 */
public class UserGroupRelJsonOrm {
	protected String id;
	protected  String groupId; 
	
	protected  String userId; 
	
	protected  String type = "groupUser";

	
	public String getGroupId() {
		return groupId;
	}
	
	@JSONField(name = "orgId")
	public void setGroupId(String groupId) {
		this.groupId = groupId;
	}

	public String getUserId() {
		return userId;
	}
	
	@JSONField(name = "userId")
	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getType() {
		return type;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public void setType(String type) {
		this.type = type;
	}
	
	
}
