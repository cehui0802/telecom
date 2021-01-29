package com.telecom.ecloudframework.org.core.script;

import com.telecom.ecloudframework.base.api.exception.BusinessMessage;
import com.telecom.ecloudframework.base.core.util.StringUtil;
import com.telecom.ecloudframework.org.api.constant.GroupTypeConstant;
import com.telecom.ecloudframework.org.api.model.IGroup;
import com.telecom.ecloudframework.org.api.model.IUser;
import com.telecom.ecloudframework.org.api.service.GroupService;
import com.telecom.ecloudframework.org.api.service.UserService;
import com.telecom.ecloudframework.sys.api.groovy.IScript;
import com.telecom.ecloudframework.sys.api.model.DefaultIdentity;
import com.telecom.ecloudframework.sys.api.model.SysIdentity;
import com.telecom.ecloudframework.sys.util.ContextUtil;
import cn.hutool.core.collection.CollectionUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * <pre>
 * 描述：常用org的脚本
 * 作者:aschs
 * 邮箱:aschs@qq.com
 * 日期:2019年5月26日
 * 版权:summer
 * </pre>
 */
@Component
public class OrgScript implements IScript {
    @Autowired
    @Qualifier("defaultGroupService")
    GroupService groupService;
    @Autowired
    @Qualifier("defaultUserService")
    UserService userService;

    /**
     * <pre>
     * 获取某个组织下的某些角色的人员列表
     * </pre>
     *
     * @param groupIds  组织id，多个以“,”分隔；为空，则取当前用户所在主组织
     * @param roleCodes 角色编码，多个以“,”分隔
     * @return
     */
    public Set<SysIdentity> getSisByGroupAndRole(String groupIds, String roleCodes) {
        Set<SysIdentity> identities = new HashSet<>();
        if (StringUtil.isEmpty(groupIds)) {
            if (ContextUtil.getCurrentGroup() == null) {
                throw new BusinessMessage("请为当前人员分配组织");
            }
            groupIds = ContextUtil.getCurrentGroup().getGroupId();
        }
        for (String gi : groupIds.split(",")) {
            List<? extends IUser> users = userService.getUserListByGroup(GroupTypeConstant.ORG.key(), gi);
            users.forEach(user -> {
                List<? extends IGroup> roles = groupService.getGroupsByGroupTypeUserId(GroupTypeConstant.ROLE.key(), user.getUserId());
                roles.forEach(role -> {
                    if (roleCodes.contains(role.getGroupCode())) {
                        SysIdentity identity = new DefaultIdentity(user.getUserId(), user.getFullname(), SysIdentity.TYPE_USER);
                        identities.add(identity);
                    }
                });
            });
        }

        return identities;
    }

    /**
     * 获取所有兄弟组织机构
     *
     * @return
     */
    public Set<SysIdentity> getSiblingsGroups() {
        String code = ContextUtil.getCurrentGroup().getGroupCode();
        Set<SysIdentity> sysIdentities = new HashSet<>();
        List<IGroup> groups = (List<IGroup>) groupService.getSiblingsGroups(code);
        if (CollectionUtil.isNotEmpty(groups)) {
            groups.forEach(org -> {
                sysIdentities.add(new DefaultIdentity(org.getGroupId(), org.getGroupName(), org.getGroupType()));
            });
        }
        return sysIdentities;
    }

    public Set<SysIdentity> getChildrenOrgGroups(String key) {
        IGroup group = groupService.getByCode("org", key);
        if (group != null) {
			List<IGroup> groups = (List<IGroup>) groupService.getChildrenGroupsById("org", group.getGroupId());
           if (CollectionUtil.isNotEmpty(groups)){
			   Set<SysIdentity> sysIdentities = new HashSet<>();
			   groups.forEach(org -> {
				   sysIdentities.add(new DefaultIdentity(org.getGroupId(),org.getGroupName(),org.getGroupType(),org.getSn()));
			   });
			   return sysIdentities;
		   }
        }
		return Collections.emptySet();
    }
}
