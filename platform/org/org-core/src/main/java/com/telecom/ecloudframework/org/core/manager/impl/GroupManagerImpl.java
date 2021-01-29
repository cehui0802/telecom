package com.telecom.ecloudframework.org.core.manager.impl;

import com.telecom.ecloudframework.base.api.exception.BusinessMessage;
import com.telecom.ecloudframework.base.api.query.QueryFilter;
import com.telecom.ecloudframework.base.api.query.QueryOP;
import com.telecom.ecloudframework.base.core.id.IdUtil;
import com.telecom.ecloudframework.base.core.util.StringUtil;
import com.telecom.ecloudframework.base.db.model.query.DefaultQueryFilter;
import com.telecom.ecloudframework.base.manager.impl.BaseManager;
import com.telecom.ecloudframework.org.api.context.ICurrentContext;
import com.telecom.ecloudframework.org.core.dao.GroupDao;
import com.telecom.ecloudframework.org.core.dao.UserDao;
import com.telecom.ecloudframework.org.core.manager.GroupManager;
import com.telecom.ecloudframework.org.core.manager.OrgRelationManager;
import com.telecom.ecloudframework.org.core.model.Group;
import com.telecom.ecloudframework.org.core.model.OrgRelation;
import com.telecom.ecloudframework.org.core.model.User;
import com.telecom.ecloudframework.sys.util.ContextUtil;
import cn.hutool.core.collection.CollectionUtil;
import com.google.common.collect.Lists;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <pre>
 * 描述：组织架构 处理实现类
 * </pre>
 *
 * @author
 */
@Service
public class GroupManagerImpl extends BaseManager<String, Group> implements GroupManager {
    protected final Logger logger = LoggerFactory.getLogger(getClass());
    @Resource
    GroupDao groupDao;
    @Resource
    UserDao userDao;
    @Autowired
    OrgRelationManager orgRelationMananger;
    @Resource
    ICurrentContext iCurrentContext;


    @Override
    public Group getByCode(String code) {
        return groupDao.getByCode(code, null);
    }

    @Override
    public void remove(String id) {
        orgRelationMananger.removeCheck(id);
        Group group = groupDao.get(id);
        List<Group> childList = groupDao.getChildByPath(group.getPath() + "%");

        // 级联删除子组织
        childList.forEach(g -> {
            orgRelationMananger.removeCheck(g.getId());
            super.remove(g.getId());
        });

        super.remove(id);
    }

    @Override
    public Group get(String entityId) {
        Group group = super.get(entityId);
        if (group != null) {
            List<OrgRelation> orgRelationList = orgRelationMananger.getGroupPost(group.getId());
            group.setOrgRelationList(orgRelationList);
        }
        return group;
    }

    @Override
    public void create(Group entity) {
        if (groupDao.getByCode(entity.getCode(), null) != null) {
            throw new BusinessMessage("组织编码“" + entity.getCode() + "”已存在，请修改！");
        }
        //默认创建排序为0
        if (null == entity.getSn()) {
            entity.setSn(0);
        }
        entity.setId(IdUtil.getSuid());
        setPath(entity);

        // 创建组织岗位
        List<OrgRelation> list = entity.getOrgRelationList();
        if (CollectionUtil.isNotEmpty(list)) {
            list.forEach(orgRelation -> {
                orgRelation.setGroupId(entity.getId());
                orgRelationMananger.create(orgRelation);
            });
        }

        super.create(entity);
    }

    @Override
    public void update(Group entity) {
        if (groupDao.getByCode(entity.getCode(), entity.getId()) != null) {
            throw new BusinessMessage("组织编码“" + entity.getCode() + "”已存在，请修改！");
        }
        // 创建组织岗位
        List<OrgRelation> list = entity.getOrgRelationList();
        if (CollectionUtil.isNotEmpty(list)) {
            list.forEach(orgRelation -> {
                orgRelation.setGroupId(entity.getId());
                orgRelationMananger.create(orgRelation);
            });
        }
        setPath(entity);
        super.update(entity);
    }

    /**
     * 更新路径
     *
     * @param entity
     */
    private void setPath(Group entity) {
        entity.setPath(entity.getId());
        if (StringUtil.isNotZeroEmpty(entity.getParentId())) {
            Group parent = groupDao.get(entity.getParentId());
            if (parent != null && parent.getPath() != null) {
                entity.setPath(parent.getPath().concat(".").concat(entity.getId()));
            }
        }
    }

    @Override
    public List<Group> getByUserId(String userId) {
        return groupDao.getByUserId(userId);
    }

    public List<Group> getByUserAccount(String account) {
        User user = userDao.getByAccount(account);
        return groupDao.getByUserId(user.getId());
    }

    @Override
    public Group getMainGroup(String userId) {
        //第一个一定是master
        List<Group> groups = groupDao.getByUserId(userId);
        if (CollectionUtil.isEmpty(groups)) {
            return null;
        }

        return groups.get(0);
    }

    @Override
    public void removeAll() {
        groupDao.removeAll();
    }

    /**
     * 排序
     *
     * @param groups
     */
    @Override
    public void chageOrder(List<Group> groups) {
        groups.forEach(group -> {
            if (StringUtils.isEmpty(group.getId())) {
                throw new BusinessMessage("ID不能为空");
            } else {
                groupDao.chageOrder(group);
            }
        });

    }

    /**
     * 选人组件：查询当前用户公司下所有机构
     *
     * @param
     * @return List<Group>
     * @author guolihao
     * @date 2020/9/7 20:27
     */
    @Override
    public List<Group> queryAllGroup() {
        String groupId = ContextUtil.getCurrentGroupId();
        Group group = getParentGroup(groupId);
        //机构，子机构，
        List<Group> groupList = Lists.newArrayList();
        List<Group> groupChilds = Lists.newArrayList();
        groupChilds.add(group);
        return queryChildGroup(groupList, groupChilds);
    }

    /**
     * 嵌套查询公司所有机构
     *
     * @param groupList   机构
     * @param childGroups 子机构
     * @return List<Group>
     * @author guolihao
     * @date 2020/9/8 9:28
     */
    private List<Group> queryChildGroup(List<Group> groupList, List<Group> childGroups) {
        for (Group group : childGroups) {
            groupList.add(group);
            QueryFilter queryFilter = new DefaultQueryFilter();
            queryFilter.addFilter("torg.parent_id_", group.getId(), QueryOP.EQUAL);
            List<Group> groups = groupDao.query(queryFilter);
            if (!groups.isEmpty()) {
                queryChildGroup(groupList, groups);
            }
        }
        return groupList;
    }

    /**
     * 嵌套查询出公司信息
     *
     * @param groupId 机构id
     * @return Group
     * @author guolihao
     * @date 2020/9/7 20:40
     */
    private Group getParentGroup(String groupId) {
        Group group = groupDao.get(groupId);
        //parentId为0，返回公司
        if ("0".equals(group.getParentId())) {
            return group;
        }
        return getParentGroup(group.getParentId());
    }

    /**
     * 获取所有机构
     *
     * @param userId
     * @return
     * @author 谢石
     * @date 2020-10-15
     */
    @Override
    public List<Group> getCurrentManagerOrgIds(String userId) {
        List<Group> groupList = groupDao.query();
        return groupList;
    }

    /**
     * 查找机构是否存在
     *
     * @param mapOrg
     * @param mainOrgPath
     * @return
     * @author 谢石
     * @date 2020-10-23
     */
    @Override
    public String findOrgId(Map<String, String> mapOrg, String mainOrgPath) {
        String orgId = mapOrg.get(mainOrgPath);
        if (StringUtils.isEmpty(orgId)) {
            String[] arrOrgName = mainOrgPath.split(">");
            //过程计算数据
            List<Map<String, String>> lstData = new ArrayList<>();


            for (String orgName : arrOrgName) {
                QueryFilter filter = new DefaultQueryFilter();
                filter.addFilter("name_", orgName, QueryOP.EQUAL);
                List<Group> lstGroup = query(filter);
                if (lstGroup.size() > 0) {
                    Map<String, String> temp = new HashMap<>();
                    for (Group group : lstGroup) {
                        temp.put(group.getId(), group.getParentId());
                    }
                    lstData.add(temp);
                } else {
                    throw new BusinessMessage("机构：" + orgName + "不存在");
                }
            }
            A:
            for (Map.Entry<String, String> entry : lstData.get(lstData.size() - 1).entrySet()) {
                String key = entry.getKey();
                String parentId = entry.getValue();
                if (lstData.size() == 1 && "0".equals(parentId)) {
                    orgId = key;
                    break;
                }
                for (int i = lstData.size() - 2; i >= 0; i--) {
                    Map<String, String> temp = lstData.get(i);
                    String parentIdTemp = temp.get(parentId);
                    if (i == 0 && "0".equals(parentIdTemp)) {
                        orgId = key;
                        break A;
                    } else {
                        if (StringUtils.isNotEmpty(parentIdTemp)) {
                            parentId = parentIdTemp;
                        }
                    }
                }
            }
            if (StringUtils.isEmpty(orgId)) {
                throw new BusinessMessage("机构：" + mainOrgPath + "不存在");
            }
            mapOrg.put(mainOrgPath, orgId);
        }
        return orgId;
    }

    @Override
    public int updateByPrimaryKeySelective(Group record) {
        return groupDao.updateByPrimaryKeySelective(record);
    }
}
