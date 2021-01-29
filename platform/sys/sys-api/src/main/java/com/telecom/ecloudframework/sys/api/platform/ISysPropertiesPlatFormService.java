package com.telecom.ecloudframework.sys.api.platform;

import java.util.Map;

public interface ISysPropertiesPlatFormService {
    /**
     * 根据别名返回参数值。
     *
     * @param alias
     * @return
     */
    String getByAlias(String alias);

    /**
     * 重新读取属性配置。
     *
     * @return
     */
    Map<String, Map<String, String>> reloadProperty();
}
