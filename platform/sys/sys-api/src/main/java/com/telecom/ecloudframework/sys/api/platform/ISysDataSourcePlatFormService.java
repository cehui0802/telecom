package com.telecom.ecloudframework.sys.api.platform;

import com.telecom.ecloudframework.sys.api.model.ISysDataSource;

import java.util.List;

public interface ISysDataSourcePlatFormService {
    /**
     * 查询数据源
     * @param key 为空时查所有
     * @return
     */
    List<? extends ISysDataSource> dataSourceList(String key);
}
