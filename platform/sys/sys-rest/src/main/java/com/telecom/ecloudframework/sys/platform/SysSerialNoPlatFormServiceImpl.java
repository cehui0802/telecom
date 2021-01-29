package com.telecom.ecloudframework.sys.platform;

import com.telecom.ecloudframework.sys.api.platform.ISysSerialNoPlatFormService;
import com.telecom.ecloudframework.sys.api.service.SerialNoService;
import com.telecom.ecloudframework.sys.core.manager.SerialNoManager;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class SysSerialNoPlatFormServiceImpl implements ISysSerialNoPlatFormService {
    @Resource
    SerialNoManager serialNoManager;
    @Resource
    SerialNoService serialNoService;
    @Override
    public String getNextIdByAlias(String alias) {
        if (serialNoManager.isAliasExisted(null, alias)) {
            String nextId = serialNoService.genNextNo(alias);
            return nextId;
        }
        return null;
    }
}
