package com.telecom.ecloudframework.sys.core.manager;

import com.telecom.ecloudframework.base.manager.Manager;
import com.telecom.ecloudframework.sys.core.model.SysProperties;

import java.util.List;
import java.util.Map;

/**
 * <pre>
 * 描述：SYS_PROPERTIES 处理接口
 * </pre>
 */
public interface SysPropertiesManager extends Manager<String, SysProperties> {


    /**
     * 分组列表。
     *
     * @return
     */
    List<String> getGroups();

    /**
     * 判断别名是否存在。
     *
     * @param sysProperties
     * @return
     */
    boolean isExist(SysProperties sysProperties);


    /**
     * 重新读取属性配置。
     *
     * @return
     */
    Map<String, Map<String, String>> reloadProperty();

    /**
     * 通过别名获取配置
     *
     * @param alias
     * @return
     * @author 谢石
     * @date 2020-9-23
     */
    String getPropertyByAlias(String alias);

    /**
     * 根据别名更新
     *
     * @param sysProperties
     * @author 谢石
     * @date 2020-9-23
     */
    void updateByAlias(SysProperties sysProperties);
}
