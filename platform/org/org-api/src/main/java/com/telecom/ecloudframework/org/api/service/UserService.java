package com.telecom.ecloudframework.org.api.service;

import com.telecom.ecloudframework.base.api.response.impl.PageResultDto;
import com.telecom.ecloudframework.org.api.model.IUser;
import com.telecom.ecloudframework.org.api.model.IUserRole;
import com.telecom.ecloudframework.org.api.model.dto.UserDTO;
import com.telecom.ecloudframework.org.api.model.dto.UserQueryDTO;

import java.util.List;
import java.util.Map;

/**
 * <pre>
 * 描述：用户服务接口类
 * </pre>
 *
 * @author
 */
public interface UserService {

    /**
     * 根据用户ID获取用户的对象。
     *
     * @param userId 用户ID
     * @return
     */
    IUser getUserById(String userId);

    /**
     * 根据用户帐号获取用户对象。
     *
     * @param account 账户
     * @return 接口处理结果
     * @author 谢石
     * @date 2020-8-18
     */
    IUser getUserByAccount(String account);

    /**
     * 根据组织id和组织类型获取用户列表。
     *
     * @param groupId   组织列表
     * @param groupType 组织类型
     * @return
     */
    List<? extends IUser> getUserListByGroup(String groupType, String groupId);

    /**
     * 获取用户的角色关系
     *
     * @param userId
     * @return
     */
    List<? extends IUserRole> getUserRole(String userId);

    /**
     * 获取有效用户列表
     *
     * @return
     * @author 谢石
     * @date 2020-8-18
     */
    List<? extends IUser> getAllUser();

    /**
     * 根据用户名称获取用户列表
     *
     * @param username
     * @return
     * @author 谢石
     * @date 2020-8-27
     */
    List<? extends IUser> getUsersByUsername(String username);


    /**
     * <pre>
     * 根据指定组织路径下的所有用户
     * </pre>
     *
     * @param path
     * @return
     */
    List<? extends IUser> getUserListByGroupPath(String path);

    /**
     * 通过openId 获取关联的用户
     *
     * @param openid
     * @return
     */
    IUser getUserByOpenid(String openid);

    /**
     * 设置用户 openID
     *
     * @param account
     * @param openid
     */
    void updateUserOpenId(String account, String openid);

    /**
     * 批量获取用户
     *
     * @param userIds
     * @return
     * @author 谢石
     * @date 2020-8-12
     */
    Map<String, ? extends IUser> getUserByIds(String userIds);

    /**
     * 根据部门查询用户
     *
     * @param groupIds
     * @return
     * @author 谢石
     * @date 2020-8-12
     */
    List<? extends IUser> getUsersByOrgIds(String groupIds);

    /**
     * 根据角色查询用户
     *
     * @param groupIds
     * @return
     * @author 谢石
     * @date 2020-8-12
     */
    List<? extends IUser> getUsersByRoleIds(String groupIds);

    /**
     * 根据岗位查询用户
     *
     * @param groupIds
     * @return
     * @author 谢石
     * @date 2020-8-12
     */
    List<? extends IUser> getUsersByPostIds(String groupIds);

    /**
     * 获取可用用户总数
     *
     * @return
     * @author 谢石
     * @date 2020-8-17
     */
    Integer countEnabledUser();

    /**
     * 根据用户名和岗位分页查询
     *
     * @param userName
     * @param postId
     * @param offset
     * @param limit
     * @return
     * @author 谢石
     * @date 2020-8-17
     */
    List<? extends IUser> getUsersByUserName(String userName, String postId, Integer offset, Integer limit);

    /**
     * 通过组织路径获取用户
     *
     * @param orgPath
     * @return
     * @author 谢石
     * @date 2020-8-17
     */
    List<? extends IUser> getUsersByOrgPath(String orgPath);

    /**
     * 根据用户帐号获取用户对象。
     *
     * @param accounts 账户
     * @return 接口处理结果
     * @author 谢石
     * @date 2020-8-18
     */
    List<? extends IUser> getUserByAccounts(String accounts);

    /**
     * 根据机构ID或者岗位ID查询所属人员
     *
     * @param orgIds
     * @param postIds
     * @return
     * @author 谢石
     * @date 2020-8-18
     */
    List<? extends IUser> getUsersByGroup(String orgIds, String postIds);

    /**
     * 根据手机批量获取用户
     *
     * @param mobiles
     * @return
     * @author 谢石
     * @date 2020-8-12
     */
    List<? extends IUser> getUsersByMobiles(String mobiles);

    /**
     * 查询用户汇总接口
     *
     * @param userQuery
     * @return
     * @author 谢石
     * @date 2020-9-9
     */
    PageResultDto getUsersByUserQuery(UserQueryDTO userQuery);

    /**
     * 添加用户
     *
     * @param userDTO
     * @return
     * @author 谢石
     * @date 2020-9-9
     */
    String addUser(UserDTO userDTO);

    /**
     * 修改用户
     *
     * @param userDTO
     * @return
     * @author 谢石
     * @date 2020-9-9
     */
    Integer editUser(UserDTO userDTO);
}
