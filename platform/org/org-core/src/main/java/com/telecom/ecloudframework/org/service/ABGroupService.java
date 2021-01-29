package com.telecom.ecloudframework.org.service;

import com.telecom.ecloudframework.base.api.query.QueryFilter;
import com.telecom.ecloudframework.base.api.query.QueryOP;
import com.telecom.ecloudframework.base.api.response.impl.PageResultDto;
import com.telecom.ecloudframework.base.core.util.BeanCopierUtils;
import com.telecom.ecloudframework.base.db.model.page.PageResult;
import com.telecom.ecloudframework.base.db.model.query.DefaultPage;
import com.telecom.ecloudframework.base.db.model.query.DefaultQueryFilter;
import com.telecom.ecloudframework.org.api.constant.GroupTypeConstant;
import com.telecom.ecloudframework.org.api.context.ICurrentContext;
import com.telecom.ecloudframework.org.api.model.IGroup;
import com.telecom.ecloudframework.org.api.model.IUser;
import com.telecom.ecloudframework.org.api.model.dto.GroupDTO;
import com.telecom.ecloudframework.org.api.model.dto.GroupQueryDTO;
import com.telecom.ecloudframework.org.api.service.GroupService;
import com.telecom.ecloudframework.org.core.manager.*;
import com.telecom.ecloudframework.org.core.model.OrgRelation;
import com.telecom.ecloudframework.org.core.model.Role;
import com.telecom.ecloudframework.org.core.model.User;
import cn.hutool.core.collection.CollectionUtil;
import com.google.common.collect.Lists;
import com.telecom.ecloudframework.org.core.manager.*;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.session.RowBounds;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;

/**
 * 用户与组关系的实现类：通过用户找组，通过组找人等
 *
 * @author
 */
@Service("defaultGroupService")
public class ABGroupService implements GroupService {
    private Logger log = LoggerFactory.getLogger(getClass());
    @Resource
    UserManager userManager;
    @Resource
    GroupManager groupManager;
    @Autowired
    RoleManager roleManager;
    @Resource
    OrgRelationManager orgRelationManager;
    @Resource
    PostManager postManager;
    @Resource
    ICurrentContext iCurrentContext;

    @Override
    public List<? extends IGroup> getGroupsByGroupTypeUserId(String groupType, String userId) {
        List listGroup = null;

        if (groupType.equals(GroupTypeConstant.ORG.key())) {
            listGroup = groupManager.getByUserId(userId);
        } else if (groupType.equals(GroupTypeConstant.ROLE.key())) {
            listGroup = roleManager.getByUserId(userId);
        } else if (groupType.equals(GroupTypeConstant.POST.key())) {
            listGroup = postManager.getByUserId(userId);
        }

        if (listGroup != null) {
            return BeanCopierUtils.transformList(listGroup, GroupDTO.class);
        }

        return null;
    }

    @Override
    public Map<String, List<? extends IGroup>> getAllGroupByAccount(String account) {
        User user = userManager.getByAccount(account);
        if (user == null) {
            return Collections.emptyMap();
        }

        return getAllGroupByUserId(user.getId());
    }

    @Override
    public Map<String, List<? extends IGroup>> getAllGroupByUserId(String userId) {
        Map<String, List<? extends IGroup>> listMap = new HashMap<>();

        List<? extends IGroup> listOrg = groupManager.getByUserId(userId);
        if (CollectionUtil.isNotEmpty(listOrg)) {
            List<? extends IGroup> groupList = BeanCopierUtils.transformList(listOrg, GroupDTO.class);
            listMap.put(GroupTypeConstant.ORG.key(), groupList);
        }
        List<? extends IGroup> listRole = roleManager.getByUserId(userId);
        if (CollectionUtil.isNotEmpty(listRole)) {
            List<? extends IGroup> groupList = BeanCopierUtils.transformList(listRole, GroupDTO.class);
            listMap.put(GroupTypeConstant.ROLE.key(), groupList);
        }

        /*
         *  岗位对外post code postID 均为 【组织ID-组织ID】
         *  岗位选择框 postCODE 为 关系的ID
         *  对外提供岗位查询 CODE查询的时候为 关系ID 返回POST 对象ID已经被转换成 【组织ID-组织ID】
         *  岗位不支持ID 的查询
         *  当前用户的岗位ID 也为【组织ID-组织ID】
         */
        List<OrgRelation> listOrgRel = orgRelationManager.getPostByUserId(userId);
        if (CollectionUtil.isNotEmpty(listOrgRel)) {
            List<IGroup> userGroups = new ArrayList<>();
            listOrgRel.forEach(post -> userGroups.add(new GroupDTO(post.getPostId(), post.getPostName(), GroupTypeConstant.POST.key())));
            listMap.put(GroupTypeConstant.POST.key(), userGroups);
        }
        return listMap;
    }

    @Override
    public List<? extends IGroup> getGroupsByUserId(String userId) {
        List<IGroup> userGroups = new ArrayList<>();
        List<? extends IGroup> listOrg = groupManager.getByUserId(userId);
        if (CollectionUtil.isNotEmpty(listOrg)) {
            userGroups.addAll(listOrg);
        }
        List<? extends IGroup> listRole = roleManager.getByUserId(userId);
        if (CollectionUtil.isNotEmpty(listRole)) {
            userGroups.addAll(listRole);
        }
        List<? extends IGroup> listPost = postManager.getByUserId(userId);
        if (CollectionUtil.isNotEmpty(listPost)) {
            userGroups.addAll(listPost);
        }
        //转换成GROUP DTO 13556806587
        List<? extends IGroup> groupList = BeanCopierUtils.transformList(userGroups, GroupDTO.class);
        return groupList;
    }

    @Override
    public IGroup getById(String groupType, String groupId) {
        IGroup group = null;
        if (groupType.equals(GroupTypeConstant.ORG.key())) {
            group = groupManager.get(groupId);
        } else if (groupType.equals(GroupTypeConstant.ROLE.key())) {
            group = roleManager.get(groupId);
        } else if (groupType.equals(GroupTypeConstant.POST.key())) {
            group = postManager.get(groupId);
        }

        if (group == null) {
            return null;
        }

        return new GroupDTO(group);
    }

    @Override
    public IGroup getByCode(String groupType, String code) {
        IGroup group = null;

        if (groupType.equals(GroupTypeConstant.ORG.key())) {
            group = groupManager.getByCode(code);
        } else if (groupType.equals(GroupTypeConstant.ROLE.key())) {
            group = roleManager.getByAlias(code);
        } else if (groupType.equals(GroupTypeConstant.POST.key())) {
            group = postManager.getByAlias(code);
        }

        if (group == null) {
            return null;
        }
        return new GroupDTO(group);
    }


    @Override
    public IGroup getMainGroup(String userId) {
        return groupManager.getMainGroup(userId);
    }


    @Override
    public List<? extends IGroup> getSiblingsGroups(String code) {
        List<IGroup> lstGroup = null;
        IGroup group;
        QueryFilter filter = new DefaultQueryFilter(true);
        group = groupManager.getByCode(code);
        if (null != group && null != group.getParentId()) {
            filter.addFilter("torg.parent_id_", group.getParentId(), QueryOP.EQUAL);
            filter.addFilter("torg.code_", code, QueryOP.NOT_EQUAL);
            return BeanCopierUtils.transformList(groupManager.query(filter), GroupDTO.class);
        }
        return null;
    }

    @Override
    public List<? extends IGroup> getRoleList(QueryFilter queryFilter) {
        List<Role> pageList = roleManager.query(queryFilter);
        return pageList;
    }

    @Override
    public List<? extends IGroup> getSameLevel(String groupType, String groupId) {
        List<IGroup> lstGroup = null;
        IGroup group;
        QueryFilter filter = new DefaultQueryFilter(true);
        if (groupType.equals(GroupTypeConstant.ORG.key())) {
            group = groupManager.get(groupId);
            if (null != group && null != group.getParentId()) {
                filter.addFilter("torg.parent_id_", group.getParentId(), QueryOP.EQUAL);
                return BeanCopierUtils.transformList(groupManager.query(filter), GroupDTO.class);
            }
        }
        return null;
    }

    @Override
    public List<? extends IGroup> getGroupListByType(String groupType) {
        List<? extends IGroup> groups = null;
        //组织，角色，岗位
        if (groupType.equals(GroupTypeConstant.ORG.key())) {
            groups = groupManager.getAll();
        } else if (groupType.equals(GroupTypeConstant.ROLE.key())) {
            groups = roleManager.getAll();
        } else if (groupType.equals(GroupTypeConstant.POST.key())) {
            groups = postManager.getAll();
        }
        return groups;
    }

    @Override
    public List<? extends IGroup> getGroupsById(String groupType, String groupIds) {
        QueryFilter filter = new DefaultQueryFilter(true);
        List<? extends IGroup> lstIGroup = null;
        if (groupType.equals(GroupTypeConstant.ORG.key())) {
            filter.addFilter("torg.id_", groupIds, QueryOP.IN);
            lstIGroup = groupManager.query(filter);
        } else if (groupType.equals(GroupTypeConstant.ROLE.key())) {
            filter.addFilter("trole.id_", groupIds, QueryOP.IN);
            lstIGroup = roleManager.query(filter);
        } else if (groupType.equals(GroupTypeConstant.POST.key())) {
            filter.addFilter("tpost.id_", groupIds, QueryOP.IN);
            lstIGroup = postManager.query(filter);
        }
        return lstIGroup;
    }

    @Override
    public List<? extends IGroup> getChildrenGroupsById(String groupType, String groupId) {
        QueryFilter filter = new DefaultQueryFilter(true);
        List<? extends IGroup> lstIGroup = null;
        if (groupType.equals(GroupTypeConstant.ORG.key())) {
            filter.addFilter("torg.parent_id_", groupId, QueryOP.EQUAL);
            lstIGroup = groupManager.query(filter);
        }
        return lstIGroup;
    }

    @Override
    public IGroup getMasterGroupByUserId(String groupType, String userId) {
        IGroup group = null;

        if (groupType.equals(GroupTypeConstant.ORG.key())) {
            group = groupManager.getMainGroup(userId);
        } else if (groupType.equals(GroupTypeConstant.POST.key())) {
            group = postManager.getMasterByUserId(userId);
        }

        if (group != null) {
            group = new GroupDTO(group);
        }

        return group;
    }

    @Override
    public PageResultDto<? extends IGroup> getGroupsByGroupQuery(GroupQueryDTO groupQuery) {
        QueryFilter filter = new DefaultQueryFilter(true);
        if (!groupQuery.getNoPage()) {
            filter.setPage(new DefaultPage(new RowBounds(groupQuery.getOffset(), groupQuery.getLimit())));
        }
        String tableName = "";
        String type = groupQuery.getGroupType();
        if (type.equals(GroupTypeConstant.ORG.key())) {
            tableName = "torg";
        } else if (type.equals(GroupTypeConstant.POST.key())) {
            tableName = "tpost";
        } else if (type.equals(GroupTypeConstant.ROLE.key())) {
            tableName = "trole";
        }
        if (null != groupQuery.getLstQueryConf()) {
            String finalTableName = tableName;
            groupQuery.getLstQueryConf().forEach(queryConf -> filter.addFilter(finalTableName + "." + queryConf.getName(), queryConf.getValue(), QueryOP.getByVal(queryConf.getType())));
        }
        if (StringUtils.isNotEmpty(groupQuery.getUserId())) {
            String userId = groupQuery.getUserId();
            filter.getParams().put("userId", userId);
        }

        if (CollectionUtil.isNotEmpty(groupQuery.getOrgIds())) {
            List<String> orgIds = groupQuery.getOrgIds();
            filter.getParams().put("orgIds", orgIds);
        }

        if (CollectionUtil.isNotEmpty(groupQuery.getNoHasChildOrgIds())) {
            List<String> noHasChildOrgIds = groupQuery.getNoHasChildOrgIds();
            filter.getParams().put("noHasChildOrgIds", noHasChildOrgIds);
        }

        if (null != groupQuery.getOrgHasChild()) {
            boolean orgHasChild = groupQuery.getOrgHasChild();
            filter.getParams().put("orgHasChild", orgHasChild);
        }

        if (StringUtils.isNotEmpty(groupQuery.getResultType())) {
            filter.getParams().put("resultType", groupQuery.getResultType());
        }
        if (StringUtils.isNotEmpty(groupQuery.getSort()) && StringUtils.isNotEmpty(groupQuery.getOrder())) {
            filter.addFieldSort(groupQuery.getSort(), groupQuery.getOrder());
        }
        List<? extends IGroup> list = Lists.newArrayList();
        if (StringUtils.isNotEmpty(type)) {
            if (type.equals(GroupTypeConstant.ORG.key())) {
                if (StringUtils.isNotEmpty(groupQuery.getGroupPath())) {
                    filter.addFilter("torg.path_", groupQuery.getGroupPath(), QueryOP.RIGHT_LIKE);
                }
                list = groupManager.query(filter);
            } else if (type.equals(GroupTypeConstant.POST.key())) {
                list = postManager.query(filter);
            } else if (type.equals(GroupTypeConstant.ROLE.key())) {
                list = roleManager.query(filter);
            }
        }
        PageResult result = new PageResult(list);
        result.setRows(BeanCopierUtils.transformList(result.getRows(), GroupDTO.class));
        return BeanCopierUtils.transformBean(result, PageResultDto.class);
    }

    @Override
    public String getCurrentManagerOrgIds() {
        IUser user = iCurrentContext.getCurrentUser();
        List<String> lstOrgId = new ArrayList<>();
        if (null != user) {
            lstOrgId = user.getManagerGroupIdList();
        }
        return String.join(",", lstOrgId);
    }
}
