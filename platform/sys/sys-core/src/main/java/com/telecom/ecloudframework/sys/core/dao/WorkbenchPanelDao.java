package com.telecom.ecloudframework.sys.core.dao;

import com.telecom.ecloudframework.base.api.query.QueryFilter;
import com.telecom.ecloudframework.base.dao.BaseDao;
import com.telecom.ecloudframework.sys.core.model.WorkbenchPanel;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;
import org.mybatis.spring.annotation.MapperScan;

@MapperScan
public interface WorkbenchPanelDao extends BaseDao<String, WorkbenchPanel> {
    /**
     * 获取用户可用的
     *
     * @param query
     * @return
     */
    List<WorkbenchPanel> getUsablePanelsByUserRight(QueryFilter query);

    /**
     * 通过权限过滤获取用户的panel
     *
     * @param userPermission
     * @return
     */
    List<WorkbenchPanel> getByUser(Map<String, Object> userPermission);


    List<WorkbenchPanel> getBylayoutKey(@Param("layoutKey")String layoutKey, @Param("dbType")String dbType);

}
