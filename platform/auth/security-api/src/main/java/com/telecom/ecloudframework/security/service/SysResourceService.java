package com.telecom.ecloudframework.security.service;

import java.util.List;
import java.util.Set;

import com.telecom.ecloudframework.org.api.model.system.ISubsystem;
import com.telecom.ecloudframework.org.api.model.system.ISysResource;

public interface SysResourceService {

	List<ISubsystem> getCuurentUserSystem();

	ISubsystem getDefaultSystem(String currentUserId);

	List<ISysResource> getBySystemId(String systemId);

	List<ISysResource> getBySystemAndUser(String systemId, String userId);

	Set<String> getAccessRoleByUrl(String url);

}
