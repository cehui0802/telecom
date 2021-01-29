package com.telecom.ecloudframework.base.errorlog.manager.impl;

import com.telecom.ecloudframework.base.core.util.StringUtil;
import com.telecom.ecloudframework.base.errorlog.dao.LogErrDao;
import com.telecom.ecloudframework.base.errorlog.manager.LogErrManager;
import com.telecom.ecloudframework.base.errorlog.mode.LogErr;
import com.telecom.ecloudframework.base.manager.impl.BaseManager;
import com.telecom.ecloudframework.base.rest.util.IPAddressUtil;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.UnsupportedEncodingException;

/**
 * <pre>
 * 描述：错误日志 处理实现类
 * </pre>
 */
@Service("sysLogErrManager")
public class LogErrManagerImpl extends BaseManager<String, LogErr> implements LogErrManager {
    @Resource
	LogErrDao sysLogErrDao;

    
    @Override
    public void create(LogErr entity) {
    	String ip = entity.getIp();
    	if(StringUtil.isNotEmpty(ip)) {
    		try {
    			entity.setIpAddress(IPAddressUtil.getAddresses(ip));
			} catch (UnsupportedEncodingException e) {
			}
    	}
    	
    	sysLogErrDao.create(entity);
    }
    
    
    
}
