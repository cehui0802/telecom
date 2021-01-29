package com.telecom.ecloudframework.org.sync.orm;

import com.alibaba.fastjson.annotation.JSONField;
import com.telecom.ecloudframework.base.core.util.StringUtil;

/**
 * User 映射类
 * 
 * @author jeff
 *
 */
public class UserJsonOrm {
	
	protected String id;

	protected String account;

	protected String fullname;

	protected String password = "n4bQgYhMfWWaL+qgxVrQFaO/TxsrC4Is0V1sFbDwCgg=";

	protected String email;

	protected String mobile;

	protected Integer status = 1;

	protected String from = "external";

	public String getId() {
		return id;
	}

	@JSONField(name = "userId")
	public void setId(String id) {
		this.id = id;
	}

	public String getAccount() {
		return account;
	}

	@JSONField(name = "username")
	public void setAccount(String account) {
		this.account = account;
	}

	public String getFullname() {
		if(StringUtil.isEmpty(fullname)) {
			return this.account;
		}
		return fullname;
	}

	public void setFullname(String fullname) {
		this.fullname = fullname;
	}

	@JSONField(name = "password")
	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getEmail() {
		return email;
	}
	@JSONField(name = "email")
	public void setEmail(String email) {
		this.email = email;
	}

	public String getMobile() {
		return mobile;
	}
	
	@JSONField(name = "userPhone")
	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	public Integer getStatus() {
		return status;
	}
	
	public void setStatus(Integer status) {
		this.status = status;
	}
	
	public String getFrom() {
		return from;
	}
	
	public void setFrom(String from) {
		this.from = from;
	}

}
