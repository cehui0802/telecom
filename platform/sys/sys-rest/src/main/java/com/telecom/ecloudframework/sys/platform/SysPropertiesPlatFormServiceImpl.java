package com.telecom.ecloudframework.sys.platform;

import com.telecom.ecloudframework.sys.api.platform.ISysPropertiesPlatFormService;
import com.telecom.ecloudframework.sys.api.service.PropertyService;
import com.telecom.ecloudframework.sys.core.manager.SysPropertiesManager;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Map;

@Service
public class SysPropertiesPlatFormServiceImpl implements ISysPropertiesPlatFormService {
    @Resource
    private PropertyService propertyService;
    @Resource
    private SysPropertiesManager sysPropertiesManager;
    @Override
    public String getByAlias(String alias) {
        return propertyService.getByAlias(alias);
    }

    @Override
    public Map<String, Map<String, String>> reloadProperty() {
        return sysPropertiesManager.reloadProperty();
    }
}
