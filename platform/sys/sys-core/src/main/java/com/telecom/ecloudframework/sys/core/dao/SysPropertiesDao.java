package com.telecom.ecloudframework.sys.core.dao;

import com.telecom.ecloudframework.base.dao.BaseDao;
import com.telecom.ecloudframework.sys.core.model.SysProperties;

import java.util.List;

import org.mybatis.spring.annotation.MapperScan;

/**
 * <pre>
 * 描述：SYS_PROPERTIES DAO接口
 * </pre>
 */
@MapperScan
public interface SysPropertiesDao extends BaseDao<String, SysProperties> {

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
    Integer isExist(SysProperties sysProperties);

    /**
     * 根据别名更新内容
     *
     * @param sysProperties
     */
    void updateByAlias(SysProperties sysProperties);
}
