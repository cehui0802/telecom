package com.telecom.ecloudframework.org.core.manager;

import com.telecom.ecloudframework.base.manager.Manager;
import com.telecom.ecloudframework.org.core.model.OrgRelation;

import java.util.List;

/**
 * 用户组织关系 Manager处理接口
 *
 * @author Jeff
 * @email for_office@qq.com
 * @time 2018-12-16 01:07:59
 */
public interface OrgRelationManager extends Manager<String, OrgRelation> {

    /**
     * 获取用户的岗位
     *
     * @param userId
     * @return
     */
    List<OrgRelation> getPostByUserId(String userId);

    /**
     * 获取所有用户的关系
     *
     * @param userId
     * @param relationType
     * @return
     */
    List<OrgRelation> getUserRelation(String userId, String relationType);

    /**
     * 通过用户删除关系
     *
     * @param id
     * @param relationTypes
     * @author 谢石
     * @date 2020-9-8
     */
    void removeByUserId(String id, List<String> relationTypes);

    /**
     * 获取组织上的岗位
     *
     * @param groupId
     * @return
     */
    List<OrgRelation> getGroupPost(String groupId);

    /**
     * 根据组id删除关系
     *
     * @param groupType
     * @param groupId
     */
    void removeRelationByGroupId(String groupType, String groupId);

    /**
     * 设置主组织
     *
     * @param groupId
     * @param userId
     * @param groupType
     */
    void updateUserGroupRelationIsMaster(String groupId, String userId, String groupType);

    /**
     * 修改状态
     *
     * @param id
     * @param status
     */
    void changeStatus(String id, int status);

    /**
     * 保存 用户与组织的关系
     *
     * @param groupId
     * @param roleIds
     * @param userIds
     */
    void saveUserGroupRelation(String groupId, String[] roleIds, String[] userIds);

    /**
     * 保存 用户与角色的关系
     *
     * @param roleId
     * @param userIds
     * @return
     */
    int saveRoleUsers(String roleId, String[] userIds);

    /**
     * 获取用户角色 含岗位角色
     *
     * @param userId
     * @return
     */
    List<OrgRelation> getUserRole(String userId);

    /**
     * 获取岗位，岗位没有code，只有ID
     *
     * @param groupId
     * @return
     */
    OrgRelation getPost(String groupId);

    /**
     * 删除组检验
     *
     * @param groupId
     */
    void removeCheck(String groupId);

    /**
     * 删除所有组
     *
     * @param relationType
     */
    void removeAllRelation(String relationType);

    /**
     * 批量修改用户和组的关系
     *
     * @param relations
     */
    void modifyUserOrg(List<OrgRelation> relations);

    /**
     * 组下所有用户转组
     *
     * @param path
     * @param oldGroupId
     * @param newGroupId
     * @param type
     */
    void modifyAllUserGroup(String path, String oldGroupId, String newGroupId, String type);

    /**
     * 转组
     *
     * @param relations
     * @param relationType
     */
    void updateGroupIdByUserId(List<OrgRelation> relations, String relationType);


    /**
     * 批量添加关系
     *
     * @param relations
     * @author 谢石
     * @date 2020-9-8
     */
    void batchAdd(List<OrgRelation> relations);


    /**
     * 批量删除关系
     *
     * @param relations
     * @author 谢石
     * @date 2020-9-19
     */
    void batchRemove(List<OrgRelation> relations);

}
