package com.telecom.ecloudframework.org.api.service;

import com.telecom.ecloudframework.base.api.query.QueryFilter;
import com.telecom.ecloudframework.base.api.response.impl.PageResultDto;
import com.telecom.ecloudframework.org.api.model.IGroup;
import com.telecom.ecloudframework.org.api.model.dto.GroupQueryDTO;

import java.util.List;
import java.util.Map;

/**
 * 描述：用户与组服务接口
 * <pre>
 * </pre>
 *
 * @author
 */
public interface GroupService {


    /**
     * 根据用户ID和组类别获取相关的组。
     *
     * @param groupType 用户组类型
     * @param userId    用户ID
     * @return
     */
    List<? extends IGroup> getGroupsByGroupTypeUserId(String groupType, String userId);

    /**
     * 根据用户账号获取用户当前所在的组。
     *
     * @param account 用户帐号
     * @return 返回一个Map，键为维度类型，值为组列表。
     */
    Map<String, List<? extends IGroup>> getAllGroupByAccount(String account);


    /**
     * 获取用户所在的所有组织。
     *
     * @param userId 用户ID
     * @return 返回一个Map，键为维度类型，值为组列表。
     */
    Map<String, List<? extends IGroup>> getAllGroupByUserId(String userId);


    /**
     * 根据用户获取用户所属的组。
     *
     * @param userId
     * @return
     */
    List<? extends IGroup> getGroupsByUserId(String userId);


    /**
     * 根据组织ID和类型获取组织对象。
     *
     * @param groupType
     * @param groupId
     * @return
     */
    IGroup getById(String groupType, String groupId);


    /**
     * 根据组织ID和类型获取组织对象。
     *
     * @param groupType
     * @param code
     * @return
     */
    IGroup getByCode(String groupType, String code);

    /**
     * 获取主机构
     *
     * @param userId
     * @return
     */
    IGroup getMainGroup(String userId);

    /**
     * 获取所有兄弟组织机构
     *
     * @param code
     * @return
     */
    List<? extends IGroup> getSiblingsGroups(String code);

    /**
     * 获取角色列表
     *
     * @param queryFilter
     * @return
     */
    List<? extends IGroup> getRoleList(QueryFilter queryFilter);

    /**
     * 根据组织类型和组id获取同组对象。
     *
     * @param groupType
     * @param groupId
     * @return
     * @author 谢石
     * @date 2020-8-18
     */
    List<? extends IGroup> getSameLevel(String groupType, String groupId);

    /**
     * 根据组织类型和组织id获取组列表
     *
     * @param groupType
     * @param groupIds
     * @return
     * @author 谢石
     * @date 2020-8-27
     */
    List<? extends IGroup> getGroupsById(String groupType, String groupIds);

    /**
     * 根据组织类型和组织id获取一级组织列表
     *
     * @param groupType
     * @param groupId
     * @return
     * @author 谢石
     * @date 2020-8-27
     */
    List<? extends IGroup> getChildrenGroupsById(String groupType, String groupId);

    /**
     * 根据用户id获取主组
     *
     * @param groupType
     * @param userId
     * @return
     * @author 谢石
     * @date 2020-8-27
     */
    IGroup getMasterGroupByUserId(String groupType, String userId);

    /**
     * 根据grouptype获取所有组信息（机构 角色 岗位）
     *
     * @param groupType 机构 角色 岗位
     * @return List
     * @author guolihao
     * @date 2020/8/31 10:51
     */
    List<? extends IGroup> getGroupListByType(String groupType);

    /**
     * 查询机构组汇总接口
     *
     * @param groupQuery 汇总参数实体类
     * @return List
     * @author guolihao
     * @date 2020/9/16 10:17
     */
    PageResultDto getGroupsByGroupQuery(GroupQueryDTO groupQuery);

    /**
     * 获取当前用户管理的机构关系列表
     *
     * @return
     * @author 谢石
     * @date 2020-10-15
     */
    String getCurrentManagerOrgIds();
}
