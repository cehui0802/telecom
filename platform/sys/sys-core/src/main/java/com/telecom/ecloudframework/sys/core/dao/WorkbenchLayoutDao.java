package com.telecom.ecloudframework.sys.core.dao;

import com.telecom.ecloudframework.base.dao.BaseDao;
import com.telecom.ecloudframework.sys.core.model.WorkbenchLayout;

import java.util.List;

import org.mybatis.spring.annotation.MapperScan;

@MapperScan
public interface WorkbenchLayoutDao extends BaseDao<String, WorkbenchLayout> {

    void removeByUserId(String currentUserId);

    List<WorkbenchLayout> getByUserId(String userId);
}
