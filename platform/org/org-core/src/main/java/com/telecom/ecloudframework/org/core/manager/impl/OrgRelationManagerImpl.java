package com.telecom.ecloudframework.org.core.manager.impl;

import com.telecom.ecloudframework.base.api.exception.BusinessMessage;
import com.telecom.ecloudframework.base.api.query.QueryFilter;
import com.telecom.ecloudframework.base.api.query.QueryOP;
import com.telecom.ecloudframework.base.core.cache.ICache;
import com.telecom.ecloudframework.base.core.util.AppUtil;
import com.telecom.ecloudframework.base.core.util.StringUtil;
import com.telecom.ecloudframework.base.db.model.query.DefaultQueryFilter;
import com.telecom.ecloudframework.base.manager.impl.BaseManager;
import com.telecom.ecloudframework.org.api.constant.RelationTypeConstant;
import com.telecom.ecloudframework.org.api.context.ICurrentContext;
import com.telecom.ecloudframework.org.api.model.IGroup;
import com.telecom.ecloudframework.org.core.dao.GroupDao;
import com.telecom.ecloudframework.org.core.dao.OrgRelationDao;
import com.telecom.ecloudframework.org.core.manager.OrgRelationManager;
import com.telecom.ecloudframework.org.core.model.OrgRelation;
import com.telecom.ecloudframework.sys.util.ContextUtil;
import cn.hutool.core.util.ArrayUtil;
import com.alibaba.fastjson.JSON;
import com.github.pagehelper.Page;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;

/**
 * 用户组织关系 Manager处理实现类
 *
 * @author Jeff
 * @email for_office@qq.com
 * @time 2018-12-16 01:07:59
 */
@Service
public class OrgRelationManagerImpl extends BaseManager<String, OrgRelation> implements OrgRelationManager {
    protected final Logger log = LoggerFactory.getLogger(this.getClass());
    @Resource
    OrgRelationDao orgRelationDao;
    @Resource
    GroupDao groupDao;

    @Override
    public List<OrgRelation> getPostByUserId(String userId) {
        return orgRelationDao.getUserRelation(userId, RelationTypeConstant.POST_USER.getKey());
    }

    @Override
    public List<OrgRelation> getUserRelation(String userId, String relationType) {
        return orgRelationDao.getUserRelation(userId, relationType);
    }

    @Override
    public void removeByUserId(String userId, List<String> relationTypes) {
        ContextUtil.clearCurrentUser();
        orgRelationDao.removeByUserId(userId, relationTypes);
    }

    @Override
    public List<OrgRelation> getGroupPost(String groupId) {
        return orgRelationDao.getGroupPost(groupId);
    }

    @Override
    public void removeRelationByGroupId(String groupType, String groupId) {
        orgRelationDao.removeRelationByGroupId(groupType, groupId);
    }

    @Override
    public void updateUserGroupRelationIsMaster(String groupId, String userId, String groupType) {
        RelationTypeConstant relationType = RelationTypeConstant.getUserRelationTypeByGroupType(groupType);
        if (StringUtils.isEmpty(groupId)) {
            throw new BusinessMessage("组织id不能为空");
        }
        if (StringUtils.isEmpty(userId)) {
            throw new BusinessMessage("用户id不能为空");
        }
        if (null == relationType) {
            throw new BusinessMessage("组织类型不正确");
        }
        QueryFilter filter = new DefaultQueryFilter(true);
        filter.addFilter("relation.type_", relationType.getKey(), QueryOP.EQUAL);
        filter.addFilter("relation.user_id_", userId, QueryOP.EQUAL);
        filter.addFilter("relation.group_id_", groupId, QueryOP.EQUAL);
        List<OrgRelation> lstOrgRelation = query(filter);
        if (lstOrgRelation.size() > 0) {
            OrgRelation orgRelation = lstOrgRelation.get(0);
            orgRelation.setIsMaster(1);
            orgRelationDao.update(orgRelation);
            for (int i = 1; i < lstOrgRelation.size(); i++) {
                orgRelationDao.remove(lstOrgRelation.get(i).getId());
            }
        } else {
            OrgRelation orgRelation = new OrgRelation();
            orgRelation.setIsMaster(1);
            orgRelation.setType(relationType.getKey());
            orgRelation.setUserId(userId);
            orgRelation.setGroupId(groupId);
            orgRelationDao.create(orgRelation);
        }
        filter = new DefaultQueryFilter(true);
        filter.addFilter("relation.type_", relationType.getKey(), QueryOP.EQUAL);
        filter.addFilter("relation.user_id_", userId, QueryOP.EQUAL);
        filter.addFilter("relation.is_master_", "1", QueryOP.EQUAL);
        filter.addFilter("relation.group_id_", groupId, QueryOP.NOT_EQUAL);
        lstOrgRelation = query(filter);
        if (lstOrgRelation.size() > 0) {
            for (OrgRelation orgRelation : lstOrgRelation) {
                orgRelationDao.remove(orgRelation.getId());
            }
        }
    }

    @Override
    public void changeStatus(String id, int status) {
        OrgRelation relation = orgRelationDao.get(id);
        if (relation == null) {
            return;
        }

        relation.setStatus(status);
        orgRelationDao.update(relation);

        String userId = relation.getUserId();
        if (StringUtil.isEmpty(userId)) {
            return;
        }

        //清楚缓存
        ICache<IGroup> iCache = AppUtil.getBean(ICache.class);
        String userKey = ICurrentContext.CURRENT_ORG + relation.getUserId();
        if (iCache != null) {
            iCache.delByKey(userKey);
        }
    }

    @Override
    public void saveUserGroupRelation(String groupId, String[] roleIds, String[] userIds) {
        for (String userId : userIds) {
            if (StringUtil.isEmpty(userId)) {
                continue;
            }
            OrgRelation orgRelation = new OrgRelation(groupId, userId, RelationTypeConstant.GROUP_USER.getKey());
            if (ArrayUtil.isNotEmpty(roleIds)) {
                for (String roleId : roleIds) {
                    orgRelation.setRoleId(roleId);
                    orgRelation.setId(null);
                    orgRelation.setType(RelationTypeConstant.POST_USER.getKey());
                    // 不存在则创建
                    if (!checkRelationIsExist(orgRelation)) {
                        this.create(orgRelation);
                    } else {
                        log.warn("关系重复添加，已跳过  {}", JSON.toJSONString(orgRelation));
                    }
                }
                continue;
            }
            if (!checkRelationIsExist(orgRelation)) {
                this.create(orgRelation);
            } else {
                log.warn("关系重复添加，已跳过  {}", JSON.toJSONString(orgRelation));
            }
        }
    }


    private boolean checkRelationIsExist(OrgRelation orgRelation) {
        int count = orgRelationDao.getCountByRelation(orgRelation);
        return count > 0;
    }

    @Override
    public int saveRoleUsers(String roleId, String[] userIds) {
        List<String> relationTypes = new ArrayList<>();
        List<OrgRelation> lstOrgRelation = orgRelationDao.getRelationsByParam(relationTypes, "", "", roleId);
        Map<String, OrgRelation> mapOrgRelation = new HashMap<>();
        lstOrgRelation.forEach(orgRelation -> mapOrgRelation.put(orgRelation.getUserId() + "-" + orgRelation.getRoleId(), orgRelation));
        int i = 0;
        if (null != userIds) {
            for (String userId : userIds) {
                OrgRelation orgRelation = new OrgRelation(roleId, userId, RelationTypeConstant.USER_ROLE.getKey());
                if (checkRelationIsExist(orgRelation)) {
                    mapOrgRelation.remove(orgRelation.getUserId() + "-" + orgRelation.getRoleId());
                    continue;
                }
                i++;
                this.create(orgRelation);
            }
        }
        mapOrgRelation.values().forEach(orgRelation -> orgRelationDao.remove(orgRelation.getId()));

        return i;
    }

    @Override
    public List<OrgRelation> getUserRole(String userId) {
        return orgRelationDao.getUserRole(userId);
    }

    @Override
    public OrgRelation getPost(String id) {
        return orgRelationDao.getPost(id);
    }

    /**
     * 删除组检验
     */
    @Override
    public void removeCheck(String groupId) {
        // 通过  关系查询 用户
        QueryFilter filter = new DefaultQueryFilter();
        // 岗位检查 岗位下人员是否存在
        if (StringUtil.isNotEmpty(groupId)) {
            filter.addFilter("relation.group_id_", groupId, QueryOP.EQUAL);
        }

        Page<OrgRelation> relationList = (Page<OrgRelation>) this.query(filter);
        if (relationList.isEmpty()) {
            return;
        }


        StringBuilder sb = new StringBuilder("请先移除以下关系：<br>");
        for (OrgRelation relation : relationList) {
            getRelationNotes(relation, sb);
        }
        if (relationList.getTotal() > 10) {
            sb.append("......<br>");
        }

        sb.append(" 共[").append(relationList.getTotal()).append("]条，待移除关系");

        throw new BusinessMessage(sb.toString());
    }

    private void getRelationNotes(OrgRelation relation, StringBuilder sb) {
        // 岗位
        if (relation.getType().equals(RelationTypeConstant.POST_USER.getKey())) {
            sb.append("岗位 [").append(relation.getPostName()).append("] 下用户：").append(relation.getUserName());
        } else if (relation.getType().equals(RelationTypeConstant.GROUP_USER.getKey())) {
            sb.append("组织 [").append(relation.getGroupName()).append("] 下用户：").append(relation.getUserName());
        } else if (relation.getType().equals(RelationTypeConstant.USER_ROLE.getKey())) {
            sb.append("角色 [").append(relation.getRoleName()).append("] 下用户：").append(relation.getUserName());
        }
        sb.append("<br>");
    }

    /**
     * 删除所有组
     *
     * @param relationType
     */
    @Override
    public void removeAllRelation(String relationType) {
        orgRelationDao.removeAllRelation(relationType);
    }

    /**
     * 批量修改用户和组的关系
     *
     * @param relations
     */
    @Override
    public void modifyUserOrg(List<OrgRelation> relations) {
        //删除原用户与机构关系
        for (OrgRelation relation : relations) {
            List<String> relationTypes = new ArrayList<>();
            relationTypes.add(relation.getType());
            List<OrgRelation> lstOrgRelation = orgRelationDao.getRelationsByParam(relationTypes, relation.getUserId(), relation.getOldGroupId(), relation.getRoleId());
            OrgRelation relationTemp = lstOrgRelation.stream().filter(orgRelation -> 1 == orgRelation.getIsMaster()).findFirst().orElse(null);
            //如果原来老的关系为主关系 创建新的关系也为主关系
            if (null != relationTemp) {
                relation.setIsMaster(1);
            }
            orgRelationDao.deleteRelationByUserIdAndType(relation.getUserId(), relation.getType(), relation.getOldGroupId());
            lstOrgRelation = orgRelationDao.getRelationsByParam(relationTypes, relation.getUserId(), relation.getGroupId(), relation.getRoleId());
            //新增用户与新机构关系
            if (lstOrgRelation.isEmpty()) {
                this.create(relation);
            } else {
                if (null != relationTemp) {
                    //如果是主机构 需要更新关系
                    relationTemp = lstOrgRelation.get(0);
                    relationTemp.setIsMaster(1);
                    orgRelationDao.updateByPrimaryKeySelective(relationTemp);
                }
            }
        }
    }

    /**
     * 组下所有用户转组
     *
     * @param path
     * @param oldGroupId
     * @param newGroupId
     * @param type
     */
    @Override
    public void modifyAllUserGroup(String path, String oldGroupId, String newGroupId, String type) {
        //1.根据path获取所有用户
        QueryFilter filter = new DefaultQueryFilter();
        if (StringUtils.isNotEmpty(path)) {
            filter.addFilter("tgroup.path_", path, QueryOP.RIGHT_LIKE);
        } else {
            filter.addFilter("relation.group_id_", oldGroupId, QueryOP.EQUAL);
        }
        filter.addFilter("relation.type_", type, QueryOP.IN);
        List<OrgRelation> lstOrgRelation = orgRelationDao.query(filter);
        for (OrgRelation orgRelation : lstOrgRelation) {
            //2.在关系表中更新所有用户
            orgRelation.setGroupId(newGroupId);
            List<String> relationTypes = new ArrayList<>();
            relationTypes.add(orgRelation.getType());
            List<OrgRelation> temp = orgRelationDao.getRelationsByParam(relationTypes, orgRelation.getUserId(), orgRelation.getGroupId(), orgRelation.getRoleId());
            if (temp == null || temp.isEmpty()) {
                orgRelationDao.updateGroupId(orgRelation);
            } else {
                orgRelationDao.remove(orgRelation.getId());
                if (1 == orgRelation.getIsMaster()) {
                    OrgRelation orgRelationTemp = temp.get(0);
                    orgRelationTemp.setIsMaster(1);
                    orgRelationDao.updateByPrimaryKeySelective(orgRelationTemp);
                }
            }
        }
    }

    /**
     * 转组
     *
     * @param relations
     * @param relationType
     */
    @Override
    public void updateGroupIdByUserId(List<OrgRelation> relations, String relationType) {
        relations.forEach(relation -> {
            relation.setType(relationType);
            orgRelationDao.updateGroupIdByUserId(relation);
        });
    }

    /**
     * 批量添加关系
     *
     * @param relations
     * @author 谢石
     * @date 2020-9-8
     */
    @Override
    public void batchAdd(List<OrgRelation> relations) {
        relations.forEach(orgRelation -> {
            if (!checkRelationIsExist(orgRelation)) {
                this.create(orgRelation);
            }
        });
    }

    /**
     * 更新采用局部更新（目前只支持排序修改）
     *
     * @param entity
     * @author 谢石
     * @date 2020-9-8
     */
    @Override
    public void update(OrgRelation entity) {
        orgRelationDao.updateByPrimaryKeySelective(entity);
    }

    /**
     * 批量删除关系
     *
     * @param relations
     * @author 谢石
     * @date 2020-9-19
     */
    @Override
    public void batchRemove(List<OrgRelation> relations) {
        relations.forEach(orgRelation -> {
            if (StringUtils.isNotEmpty(orgRelation.getId())) {
                orgRelationDao.remove(orgRelation.getId());
            } else {
                orgRelationDao.deleteRelationByUserIdAndType(orgRelation.getUserId(), orgRelation.getType(), orgRelation.getGroupId());
            }
        });
    }

    /**
     * 创建关系
     *
     * @param entity
     */
    @Override
    public void create(OrgRelation entity) {
        if (StringUtils.isEmpty(entity.getGroupId())) {
            throw new BusinessMessage("添加组关系 组id不能为空");
        }
        if (StringUtils.isEmpty(entity.getUserId())) {
            throw new BusinessMessage("添加组关系 用户id不能为空");
        }
        orgRelationDao.create(entity);
    }

}
