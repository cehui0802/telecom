package com.telecom.ecloudframework.org.service;


import com.telecom.ecloudframework.base.api.exception.BusinessException;
import com.telecom.ecloudframework.base.api.exception.BusinessMessage;
import com.telecom.ecloudframework.base.api.query.QueryFilter;
import com.telecom.ecloudframework.base.api.query.QueryOP;
import com.telecom.ecloudframework.base.api.response.impl.PageResultDto;
import com.telecom.ecloudframework.base.core.util.BeanCopierUtils;
import com.telecom.ecloudframework.base.db.model.page.PageResult;
import com.telecom.ecloudframework.base.db.model.query.DefaultPage;
import com.telecom.ecloudframework.base.db.model.query.DefaultQueryFilter;
import com.telecom.ecloudframework.org.api.constant.RelationTypeConstant;
import com.telecom.ecloudframework.org.api.context.ICurrentContext;
import com.telecom.ecloudframework.org.api.model.IUser;
import com.telecom.ecloudframework.org.api.model.IUserRole;
import com.telecom.ecloudframework.org.api.model.dto.UserDTO;
import com.telecom.ecloudframework.org.api.model.dto.UserQueryDTO;
import com.telecom.ecloudframework.org.api.model.dto.UserRoleDTO;
import com.telecom.ecloudframework.org.api.service.UserService;
import com.telecom.ecloudframework.org.core.manager.GroupManager;
import com.telecom.ecloudframework.org.core.manager.OrgRelationManager;
import com.telecom.ecloudframework.org.core.manager.PostManager;
import com.telecom.ecloudframework.org.core.manager.UserManager;
import com.telecom.ecloudframework.org.core.model.Group;
import com.telecom.ecloudframework.org.core.model.OrgRelation;
import com.telecom.ecloudframework.org.core.model.Post;
import com.telecom.ecloudframework.org.core.model.User;
import cn.hutool.core.collection.CollectionUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.session.RowBounds;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;


/**
 * @author
 */
@Service(value = "defaultUserService")
public class ABUserService implements UserService {
    private Logger logger = LoggerFactory.getLogger(this.getClass());
    @Resource
    UserManager userManager;
    @Resource
    GroupManager groupManager;
    @Resource
    PostManager postManager;
    @Resource
    OrgRelationManager orgRelationMananger;
    @Resource
    ICurrentContext iCurrentContext;
    @Resource
    OrgRelationManager orgRelationManager;


    @Override
    public IUser getUserById(String userId) {
        User user = userManager.get(userId);
        return getUserDetailInfo(user);
    }

    @Override
    public IUser getUserByAccount(String account) {
        User user = userManager.getByAccount(account);
        return getUserDetailInfo(user);
    }

    /**
     * 用户附加信息 主岗位 角色信息列表
     *
     * @param user
     * @return
     * @author 谢石
     * @date 2020-10-27
     */
    public UserDTO getUserDetailInfo(User user) {
        UserDTO userDTO = BeanCopierUtils.transformBean(user, UserDTO.class);
        if (null != user) {
            user.getOrgRelationList().stream().filter(orgRelation -> RelationTypeConstant.POST_USER.getKey().equals(orgRelation.getType()) && null != orgRelation.getIsMaster() && 1 == orgRelation.getIsMaster()).findFirst().ifPresent(postRelation -> {
                userDTO.setPostId(postRelation.getPostId());
                userDTO.setPostName(postRelation.getPostName());
                Post post = postManager.get(postRelation.getGroupId());
                if (null != post) {
                    userDTO.setPostCode(post.getCode());
                }
            });
            user.getOrgRelationList().stream().filter(orgRelation -> RelationTypeConstant.GROUP_USER.getKey().equals(orgRelation.getType()) && null != orgRelation.getIsMaster() && 1 == orgRelation.getIsMaster()).findFirst().ifPresent(orgRelation -> {
                userDTO.setOrgId(orgRelation.getGroupId());
                userDTO.setOrgName(orgRelation.getGroupName());
                Group group = groupManager.get(orgRelation.getGroupId());
                if (null != group) {
                    userDTO.setOrgCode(group.getCode());
                }
            });
            List<OrgRelation> lstOrgRelation = orgRelationMananger.getUserRole(user.getUserId());
            List<IUserRole> lstUserRoleDTO = new ArrayList<>();
            lstOrgRelation.forEach(temp -> lstUserRoleDTO.add(new UserRoleDTO(temp.getRoleId(), temp.getRoleName(), temp.getRoleAlias())));
            userDTO.setRoles(lstUserRoleDTO);
        }
        return userDTO;
    }

    @Override
    public List<? extends IUser> getUserListByGroup(String groupType, String groupId) {
        //此处可以根据不同的groupType去调用真实的实现：如角色下的人，组织下的人
        RelationTypeConstant relationType = RelationTypeConstant.getUserRelationTypeByGroupType(groupType);
        if (relationType == null) {
            throw new BusinessException(groupType + "查找不到对应用户的类型！");
        }

        List<User> userList = userManager.getUserListByRelation(groupId, relationType.getKey());

        if (CollectionUtil.isNotEmpty(userList)) {
            return BeanCopierUtils.transformList(userList, UserDTO.class);
        }

        return Collections.emptyList();
    }

    @Override
    public List<? extends IUserRole> getUserRole(String userId) {
        List<OrgRelation> orgRelationList = orgRelationMananger.getUserRole(userId);
        List<UserRoleDTO> userRoleList = new ArrayList<>();

        for (OrgRelation orgRelation : orgRelationList) {
            UserRoleDTO userRole = new UserRoleDTO(orgRelation.getRoleId(), orgRelation.getUserId(), orgRelation.getUserName(), orgRelation.getRoleName());
            userRole.setAlias(orgRelation.getRoleAlias());
            userRoleList.add(userRole);
        }

        return userRoleList;
    }

    /**
     * 获取有效用户列表
     *
     * @return
     * @author 谢石
     * @date 2020-8-18
     */
    @Override
    public List<? extends IUser> getAllUser() {
        QueryFilter query = new DefaultQueryFilter(true);
        query.addFilter("tuser.status_", 1, QueryOP.EQUAL);

        List<User> userList = userManager.query(query);

        return BeanCopierUtils.transformList(userList, UserDTO.class);
    }

    /**
     * 根据用户名称获取用户列表
     *
     * @param username
     * @return
     * @author 谢石
     * @date 2020-8-27
     */
    @Override
    public List<? extends IUser> getUsersByUsername(String username) {
        QueryFilter query = new DefaultQueryFilter(true);
        query.addFilter("tuser.status_", 1, QueryOP.EQUAL);
        if (StringUtils.isNotEmpty(username)) {
            query.addFilter("tuser.fullname_", username, QueryOP.LIKE);
        }

        List<User> userList = userManager.query(query);

        return BeanCopierUtils.transformList(userList, UserDTO.class);
    }


    @Override
    public List<? extends IUser> getUserListByGroupPath(String path) {
        List<User> userList = userManager.getUserListByGroupPath(path);
        return BeanCopierUtils.transformList(userList, UserDTO.class);
    }

    @Override
    public IUser getUserByOpenid(String openid) {
        IUser user = userManager.getByOpneid(openid);
        return BeanCopierUtils.transformBean(user, UserDTO.class);
    }

    @Override
    public void updateUserOpenId(String account, String openid) {
        User user = userManager.getByAccount(account);
        if (user == null) {
            throw new BusinessMessage("账户不存在:" + account);
        }
        user.setOpenid(openid);
        userManager.update(user);
    }

    /**
     * 批量获取用户
     *
     * @param userIds
     * @return
     * @author 谢石
     * @date 2020-8-12
     */
    @Override
    public Map<String, ? extends IUser> getUserByIds(String userIds) {
        QueryFilter filter = new DefaultQueryFilter(true);
        filter.addFilter("tuser.id_", userIds, QueryOP.IN);
        List<User> lstUser = userManager.query(filter);
        Map<String, IUser> mapUser = new HashMap<>();
        lstUser.forEach(user -> mapUser.put(user.getId(), BeanCopierUtils.transformBean(user, UserDTO.class)));
        return mapUser;
    }

    /**
     * 根据部门查询用户
     *
     * @param groupIds
     * @return
     * @author 谢石
     * @date 2020-8-12
     */
    @Override
    public List<? extends IUser> getUsersByOrgIds(String groupIds) {
        QueryFilter filter = new DefaultQueryFilter(true);
        Map<String, Object> params = new HashMap<>();
        params.put("orgIds", groupIds.split(","));
        filter.addParams(params);
        List<User> lstUser = userManager.query(filter);
        return BeanCopierUtils.transformList(lstUser, UserDTO.class);
    }

    /**
     * 根据角色查询用户
     *
     * @param groupIds
     * @return
     * @author 谢石
     * @date 2020-8-12
     */
    @Override
    public List<? extends IUser> getUsersByRoleIds(String groupIds) {
        QueryFilter filter = new DefaultQueryFilter(true);
        Map<String, Object> params = new HashMap<>();
        params.put("roleIds", groupIds.split(","));
        filter.addParams(params);
        List<User> lstUser = userManager.query(filter);
        return BeanCopierUtils.transformList(lstUser, UserDTO.class);
    }

    /**
     * 根据岗位查询用户
     *
     * @param groupIds
     * @return
     * @author 谢石
     * @date 2020-8-12
     */
    @Override
    public List<? extends IUser> getUsersByPostIds(String groupIds) {
        QueryFilter filter = new DefaultQueryFilter(true);
        Map<String, Object> params = new HashMap<>();
        params.put("postIds", groupIds.split(","));
        filter.addParams(params);
        List<User> lstUser = userManager.query(filter);
        return BeanCopierUtils.transformList(lstUser, UserDTO.class);
    }

    /**
     * 获取可用用户总数
     *
     * @return
     * @author 谢石
     * @date 2020-8-17
     */
    @Override
    public Integer countEnabledUser() {
        return userManager.getAllEnableUserNum();
    }

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
    @Override
    public List<? extends IUser> getUsersByUserName(String userName, String postId, Integer offset, Integer limit) {
        QueryFilter filter = new DefaultQueryFilter();
        Map<String, Object> params = new HashMap<>();
        params.put("postId", postId);
        filter.addParams(params);
        if (StringUtils.isNotEmpty(userName)) {
            filter.addFilter("tuser.fullname_", userName, QueryOP.LEFT_LIKE);
        }
        RowBounds rowBounds = new RowBounds(offset, limit);
        DefaultPage page = new DefaultPage(rowBounds);
        filter.setPage(page);
        List<User> lstUser = userManager.query(filter);
        return BeanCopierUtils.transformList(lstUser, UserDTO.class);
    }

    /**
     * 通过组织路径
     *
     * @param orgPath
     * @return
     * @author 谢石
     * @date 2020-8-17
     */
    @Override
    public List<? extends IUser> getUsersByOrgPath(String orgPath) {
        List<User> lstUser = userManager.getUsersByOrgPath(orgPath);
        return BeanCopierUtils.transformList(lstUser, UserDTO.class);
    }

    @Override
    public List<? extends IUser> getUserByAccounts(String accounts) {
        QueryFilter filter = new DefaultQueryFilter(true);
        filter.addFilter("tuser.account_", accounts, QueryOP.IN);
        List<User> lstUser = userManager.query(filter);
        return BeanCopierUtils.transformList(lstUser, UserDTO.class);
    }

    /**
     * 根据机构ID或者岗位ID查询所属人员
     *
     * @param orgIds
     * @param postIds
     * @return
     * @author 谢石
     * @date 2020-8-18
     */
    @Override
    public List<? extends IUser> getUsersByGroup(String orgIds, String postIds) {
        QueryFilter filter = new DefaultQueryFilter(true);
        Map<String, Object> params = new HashMap<>();
        params.put("orgIds", orgIds.split(","));
        if (StringUtils.isNotEmpty(postIds)) {
            params.put("postIds", postIds.split(","));
        }
        filter.addParams(params);
        List<User> lstUser = userManager.query(filter);
        return BeanCopierUtils.transformList(lstUser, UserDTO.class);
    }

    @Override
    public List<? extends IUser> getUsersByMobiles(String mobiles) {
        QueryFilter filter = new DefaultQueryFilter(true);
        filter.addFilter("tuser.mobile_", mobiles, QueryOP.IN);
        List<User> lstUser = userManager.query(filter);
        return BeanCopierUtils.transformList(lstUser, UserDTO.class);
    }

    @Override
    public PageResultDto<? extends IUser> getUsersByUserQuery(UserQueryDTO userQuery) {
        QueryFilter filter = new DefaultQueryFilter(userQuery.getNoPage());
        if (!userQuery.getNoPage()) {
            filter.setPage(new DefaultPage(new RowBounds(userQuery.getOffset(), userQuery.getLimit())));
        }
        if (null != userQuery.getLstQueryConf()) {
            userQuery.getLstQueryConf().forEach(queryConf -> filter.addFilter(queryConf.getName(), queryConf.getValue(), QueryOP.getByVal(queryConf.getType())));
        }
        if (StringUtils.isNotEmpty(userQuery.getOrgIds())) {
            String orgIds = userQuery.getOrgIds();
            filter.getParams().put("orgIds", orgIds.split(","));
            Boolean orgHasChild = userQuery.getOrgHasChild();
            if (null == orgHasChild) {
                orgHasChild = false;
            }
            filter.getParams().put("orgHasChild", orgHasChild);
        }
        if (StringUtils.isNotEmpty(userQuery.getOrgPath())) {
            filter.getParams().put("orgPath", userQuery.getOrgPath().concat("%"));
        }
        if (StringUtils.isNotEmpty(userQuery.getRoleIds())) {
            String roleIds = userQuery.getRoleIds();
            filter.getParams().put("roleIds", roleIds.split(","));
        }
        if (StringUtils.isNotEmpty(userQuery.getPostIds())) {
            String postIds = userQuery.getPostIds();
            filter.getParams().put("postIds", postIds.split(","));
        }
        if (StringUtils.isNotEmpty(userQuery.getResultType())) {
            filter.getParams().put("resultType", userQuery.getResultType());
        }
        if (StringUtils.isNotEmpty(userQuery.getTeamIds())) {
            String teamIds = userQuery.getTeamIds();
            filter.getParams().put("teamIds", teamIds.split(","));
        }
        if (StringUtils.isNotEmpty(userQuery.getTeamCustomIds())) {
            String teamCustomIds = userQuery.getTeamCustomIds();
            filter.getParams().put("teamCustomIds", teamCustomIds.split(","));
        }
        if (null != userQuery.getUserSelectHistory() && userQuery.getUserSelectHistory()) {
            filter.getParams().put("teamHistory", true);
            filter.getParams().put("currentUserId", iCurrentContext.getCurrentUserId());
        }

        List<User> lstUser = userManager.query(filter);
        PageResult result = new PageResult(lstUser);
        result.setRows(BeanCopierUtils.transformList(result.getRows(), UserDTO.class));
        return BeanCopierUtils.transformBean(result, PageResultDto.class);
    }

    @Override
    public String addUser(UserDTO userDTO) {
        if (null != iCurrentContext.getCurrentUserId()) {
            User user = BeanCopierUtils.transformBean(userDTO, User.class);
            List<OrgRelation> lstOrgRelation = BeanCopierUtils.transformList(userDTO.getOrgRelationList(), OrgRelation.class);
            user.setOrgRelationList(lstOrgRelation);
            user.setId(null);
            userManager.saveUserInfo(user);
            return user.getId();
        }
        return null;
    }

    @Override
    public Integer editUser(UserDTO userDTO) {
        try {
            if (null == iCurrentContext.getCurrentUserId()) {
                IUser user = new User();
                user.setUserId("0");
                user.setFullname("系统");
                iCurrentContext.setCurrentUser(user);
            }
            User user = BeanCopierUtils.transformBean(userDTO, User.class);
            if (null != userDTO.getOrgRelationList()) {
                List<OrgRelation> lstOrgRelation = BeanCopierUtils.transformList(userDTO.getOrgRelationList(), OrgRelation.class);
                user.setOrgRelationList(lstOrgRelation);
            }
            if (StringUtils.isNotEmpty(user.getId())) {
                userManager.saveUserInfo(user);
            } else {
                throw new BusinessMessage("ID必填");
            }
            return 1;
        } catch (Exception e) {
            logger.error("修改用户信息出错", e);
            return 0;
        }
    }
}
