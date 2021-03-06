package com.telecom.ecloudframework.sys.service.impl;

import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;

import com.telecom.ecloudframework.sys.api.constant.RightsObjectConstants;
import com.telecom.ecloudframework.sys.api.service.SysAuthorizationService;
import org.springframework.stereotype.Service;

import com.telecom.ecloudframework.sys.core.manager.SysAuthorizationManager;
@Service
public class DefaultSysAuthorizationService implements SysAuthorizationService {
@Resource
SysAuthorizationManager sysAuthorizationManager;

	@Override
	public Set<String> getUserRights(String userId) {
		return sysAuthorizationManager.getUserRights(userId);
	}

	@Override
	public Map<String, Object> getUserRightsSql(RightsObjectConstants rightsObject, String userId, String targetKey) {
		return sysAuthorizationManager.getUserRightsSql(rightsObject, userId, targetKey);
	}

}
