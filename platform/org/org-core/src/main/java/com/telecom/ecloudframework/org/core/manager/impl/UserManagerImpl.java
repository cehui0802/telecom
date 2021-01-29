package com.telecom.ecloudframework.org.core.manager.impl;

import com.telecom.ecloudframework.base.api.exception.BusinessMessage;
import com.telecom.ecloudframework.base.core.cache.ICache;
import com.telecom.ecloudframework.base.core.encrypt.EncryptUtil;
import com.telecom.ecloudframework.base.core.util.StringUtil;
import com.telecom.ecloudframework.base.manager.impl.BaseManager;
import com.telecom.ecloudframework.org.api.constant.RelationTypeConstant;
import com.telecom.ecloudframework.org.api.constant.UserTypeConstant;
import com.telecom.ecloudframework.org.api.context.ICurrentContext;
import com.telecom.ecloudframework.org.core.dao.UserDao;
import com.telecom.ecloudframework.org.core.manager.GroupManager;
import com.telecom.ecloudframework.org.core.manager.OrgRelationManager;
import com.telecom.ecloudframework.org.core.manager.UserManager;
import com.telecom.ecloudframework.org.core.model.Group;
import com.telecom.ecloudframework.org.core.model.OrgRelation;
import com.telecom.ecloudframework.org.core.model.User;
import cn.hutool.core.collection.CollectionUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <pre>
 * 描述：用户表 处理实现类
 * </pre>
 *
 * @author
 */
@Service
public class UserManagerImpl extends BaseManager<String, User> implements UserManager {
    public static final String LOGIN_USER_CACHE_KEY = "ecloudframework:loginUser:";
    @Resource
    UserDao userDao;
    @Resource
    OrgRelationManager orgRelationMananger;
    @Resource
    GroupManager groupManager;
    @Autowired
    ICache icache;
    @Autowired
    ICurrentContext currentContext;

    @Override
    public User getByAccount(String account) {
        User user = this.userDao.getByAccount(account);
        if (user != null) {
            user.setOrgRelationList(orgRelationMananger.getUserRelation(user.getUserId(), null));
            user.setManagerGroupIdList(groupManager.getCurrentManagerOrgIds(user.getUserId()).stream().map(Group::getId).collect(Collectors.toList()));
        }
        return user;
    }

    @Override
    public boolean isUserExist(User user) {
        return userDao.isUserExist(user) > 0;
    }

    @Override
    public List<User> getUserListByRelation(String relId, String type) {
        return userDao.getUserListByRelation(relId, type);
    }

    /**
     * 通过ID 获取会带上用户关系信息
     */
    @Override
    public User get(String entityId) {
        User user = super.get(entityId);
        if (user != null) {
            user.setOrgRelationList(orgRelationMananger.getUserRelation(entityId, null));
            user.setManagerGroupIdList(groupManager.getCurrentManagerOrgIds(entityId).stream().map(Group::getId).collect(Collectors.toList()));
        }
        return user;
    }

    @Override
    public void remove(String entityId) {
        orgRelationMananger.removeByUserId(entityId, null);
        super.remove(entityId);
    }

    @Override
    public void saveUserInfo(User user) {
        List<OrgRelation> orgRelationList = user.getOrgRelationList();
        if (StringUtil.isEmpty(user.getId())) {
            String account = user.getAccount();
            String email = user.getEmail();
            String phone = user.getMobile();
            if (StringUtils.isNotEmpty(account) && null != getByAccount(account)) {
                throw new BusinessMessage(String.format("账号:%s已存在", account));
            }

            if (StringUtil.isEmpty(user.getPassword())) {
                user.setPassword("111111");
            }
            user.setPassword(EncryptUtil.encryptSha256(user.getPassword()));
            user.setStatus(0);
            user.setActiveStatus(0);
            //默认普通用户
            if (StringUtils.isEmpty(user.getType())) {
                user.setType(UserTypeConstant.NORMAL.key());
            }
            if (UserTypeConstant.MANAGER.key().equals(user.getType())) {
                user.setStatus(1);
                user.setActiveStatus(1);
            }
            //默认创建排序为0
            if (null == user.getSn()) {
                user.setSn(0);
            }
            this.create(user);
        } else {
            //不允许修改账号
            user.setAccount(null);
            if (StringUtil.isNotEmpty(user.getPassword())) {
                user.setPassword(EncryptUtil.encryptSha256(user.getPassword()));
            }
            user.setUpdateBy(currentContext.getCurrentUserId());
            user.setUpdateTime(new Date());
            this.updateByPrimaryKeySelective(user);
            if (!CollectionUtil.isEmpty(orgRelationList)) {
                //目前支持添加机构 添加前删除组织关系
                List<String> relationTypes = new ArrayList<>();
                relationTypes.add(RelationTypeConstant.GROUP_USER.getKey());
                orgRelationMananger.removeByUserId(user.getId(), relationTypes);
            }
        }
        //如果调用接口不当会造成没有主机构的bug
        if (!CollectionUtil.isEmpty(orgRelationList)) {
            orgRelationList.forEach(rel -> {
                //目前支持添加机构
                if (RelationTypeConstant.GROUP_USER.getKey().equals(rel.getType())) {
                    rel.setUserId(user.getId());
                    orgRelationMananger.create(rel);
                }
            });
        }
    }

    @Override
    public List<User> getUserListByGroupPath(String path) {
        return userDao.getUserListByGroupPath(path + "%");
    }

    @Override
    public void removeOutSystemUser() {
        userDao.removeOutSystemUser();

    }

    @Override
    public User getByOpneid(String openid) {
        return userDao.getByOpenid(openid);
    }

    @Override
    public int updateByPrimaryKeySelective(User record) {
        User entity = get(record.getId());
        clearUserCache(entity);
        return userDao.updateByPrimaryKeySelective(record);
    }

    @Override
    public String getIdByAccount(String account) {
        return userDao.getIdByAccount(account);
    }


    @Override
    public void update(User entity) {
        super.update(entity);
        clearUserCache(entity);
    }

    /**
     * 获取启用用户数量
     *
     * @return
     * @author 谢石
     * @date 2020-7-9 16:03
     */
    @Override
    public Integer getAllEnableUserNum() {
        return userDao.getAllEnableUserNum();
    }

    /**
     * 通过组织路径获取用户
     *
     * @param orgPath
     * @return
     * @author 谢石
     * @date 2020-8-17
     */
    @Override
    public List<User> getUsersByOrgPath(String orgPath) {
        if (StringUtils.isNotEmpty(orgPath)) {
            orgPath = orgPath.concat("%");
        }
        return userDao.getUsersByOrgPath(orgPath);
    }

    /**
     * 清理用户缓存
     */
    public void clearUserCache(User entity) {
        icache.delByKey(LOGIN_USER_CACHE_KEY.concat(entity.getAccount()));
        icache.delByKey(ICurrentContext.CURRENT_ORG.concat(entity.getId()));
    }
}
