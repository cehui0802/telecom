package com.telecom.ecloudframework.security.service;

import java.util.List;
import java.util.Set;

import javax.annotation.Resource;

import com.telecom.ecloudframework.security.core.manager.ResRoleManager;
import com.telecom.ecloudframework.security.core.manager.SubsystemManager;
import com.telecom.ecloudframework.security.core.manager.SysResourceManager;
import org.springframework.stereotype.Service;

import com.telecom.ecloudframework.org.api.model.system.ISubsystem;
import com.telecom.ecloudframework.org.api.model.system.ISysResource;

/**
 * 用户系统资源服务接口
 * @author jeff
 */
@Service
public class SysResourceServiceImpl implements SysResourceService{
	@Resource
    SysResourceManager sysResourceManager;
	@Resource
    SubsystemManager sybSystemManager;
	@Resource
    ResRoleManager resRoleManager;
	
	
	@Override
	public List<ISubsystem> getCuurentUserSystem() {
		return (List)sybSystemManager.getCuurentUserSystem();
	}

	@Override
	public ISubsystem getDefaultSystem(String currentUserId) {
		return sybSystemManager.getDefaultSystem(currentUserId);
	}

	@Override
	public List<ISysResource> getBySystemId(String systemId) {
		return (List)sysResourceManager.getBySystemId(systemId);
	}

	@Override
	public List<ISysResource> getBySystemAndUser(String systemId, String userId) {
		return (List)sysResourceManager.getBySystemAndUser(systemId, userId);
	}


	@Override
	public Set<String> getAccessRoleByUrl(String url) {
		return resRoleManager.getAccessRoleByUrl(url);
	}

}
