package com.telecom.ecloudframework.org.core.dao;

import com.telecom.ecloudframework.base.api.query.QueryFilter;
import com.telecom.ecloudframework.base.dao.BaseDao;

import com.telecom.ecloudframework.org.core.model.OrgRelation;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 用户组织关系 DAO接口
 *
 * @author Jeff
 * @email for_office@qq.com
 * @time 2018-12-16 01:07:59
 */
public interface OrgRelationDao extends BaseDao<String, OrgRelation> {

    /**
     * 获取用户的 关系
     *
     * @param userId       必须
     * @param relationType 非必须
     * @return
     */
    List<OrgRelation> getUserRelation(@Param("userId") String userId, @Param("relationType") String relationType);

    /**
     * 通过 userId 刪除所有關係
     *
     * @param userId
     */
    void removeByUserId(@Param("userId") String userId, @Param("relationTypes") List<String> relationTypes);

    /**
     * 获取组织岗位
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
    void removeRelationByGroupId(@Param("groupType")String groupType, @Param("groupId")String groupId);

    /**
     * 通过 参数查询关系列表
     *
     * @param relationTypes 关系类型
     * @param userId        用户ID
     * @param groupId       组织ID
     * @param roleId        角色ID
     * @return
     */
    List<OrgRelation> getRelationsByParam(@Param("relationTypes") List<String> relationTypes, @Param("userId") String userId, @Param("groupId") String groupId, @Param("roleId") String roleId);

    /**
     * @param relation userId,roleId,groupId,relation 存在则相等判断
     * @return
     */
    int getCountByRelation(OrgRelation relation);

    /**
     * 获取用户角色，含岗位
     *
     * @param userId
     * @return
     */
    List<OrgRelation> getUserRole(String userId);

    /**
     * 获取岗位
     *
     * @param id
     * @return
     */
    OrgRelation getPost(String id);

    /**
     * 通过关系类型删除
     *
     * @param relationType
     */
    void removeAllRelation(@Param("relationType") String relationType);

    /**
     * 删除用户和组的关系
     *
     * @param userId
     * @param type
     * @param oldGroupId
     */
    void deleteRelationByUserIdAndType(@Param("userId") String userId, @Param("type") String type, @Param("oldGroupId") String oldGroupId);

    /**
     * 更新组id
     *
     * @param orgRelation
     */
    void updateGroupId(OrgRelation orgRelation);

    /**
     * 转组
     *
     * @param relation
     */
    void updateGroupIdByUserId(OrgRelation relation);


    /**
     * 根据主键选择性更新记录
     *
     * @param record 更新记录
     * @return 影响行数
     */
    int updateByPrimaryKeySelective(OrgRelation record);
}
