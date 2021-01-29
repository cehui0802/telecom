package com.telecom.ecloudframework.sys.core.manager;

import java.util.List;
import java.util.Map;
import java.util.Set;

import com.telecom.ecloudframework.base.manager.Manager;
import com.telecom.ecloudframework.sys.api.constant.RightsObjectConstants;
import com.telecom.ecloudframework.sys.core.model.SysAuthorization;


public interface SysAuthorizationManager extends Manager<String, SysAuthorization> {

    Set<String> getUserRights(String userId);

    /**
     * 获取授权sql
     *
     * @param userId    用户id
     * @param targetKey 默认为Id_ 关联authorization的 targetId在数据库中的字段名字，
     * @return
     */
    Map<String, Object> getUserRightsSql(RightsObjectConstants rightsObject, String userId, String targetKey);

    List<SysAuthorization> getByTarget(RightsObjectConstants rightsObject, String rightsTarget);


    void createAll(List<SysAuthorization> sysAuthorizationList, String targetId, String targetObject);

}
