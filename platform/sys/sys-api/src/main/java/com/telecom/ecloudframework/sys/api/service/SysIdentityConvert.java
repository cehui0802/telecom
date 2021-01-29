package com.telecom.ecloudframework.sys.api.service;

import java.util.List;

import com.telecom.ecloudframework.org.api.model.IUser;
import com.telecom.ecloudframework.sys.api.model.SysIdentity;

public interface SysIdentityConvert {
	
	/**
	 *  identity type 必须为 user
	 * @param identity
	 * @return
	 */
	public IUser convert2User(SysIdentity identity);
	
	public List<? extends IUser> convert2Users(SysIdentity identity);
	
	public List<? extends IUser> convert2Users(List<SysIdentity> identity);
	
}
