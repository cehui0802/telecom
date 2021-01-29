package com.telecom.ecloudframework.sys.platform;

import com.telecom.ecloudframework.base.api.query.QueryFilter;
import com.telecom.ecloudframework.base.api.query.QueryOP;
import com.telecom.ecloudframework.base.core.util.StringUtil;
import com.telecom.ecloudframework.base.db.model.query.DefaultQueryFilter;
import com.telecom.ecloudframework.sys.api.model.ISysDataSource;
import com.telecom.ecloudframework.sys.api.platform.ISysDataSourcePlatFormService;
import com.telecom.ecloudframework.sys.core.manager.SysDataSourceManager;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
@Service
public class SysDataSourcePlatFormServiceImpl implements ISysDataSourcePlatFormService {
    @Resource
    private SysDataSourceManager sysDataSourceManager;
    @Override
    public List<? extends ISysDataSource> dataSourceList(String keys) {
        QueryFilter queryFilter = new DefaultQueryFilter(true);
        if (StringUtils.isNotEmpty(keys)){
            queryFilter.addFilter("key_",keys, QueryOP.IN);
        }
        return sysDataSourceManager.query(queryFilter);
    }
}
