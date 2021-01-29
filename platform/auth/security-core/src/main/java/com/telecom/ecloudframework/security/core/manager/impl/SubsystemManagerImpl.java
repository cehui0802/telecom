package com.telecom.ecloudframework.security.core.manager.impl;

import com.telecom.ecloudframework.base.manager.impl.BaseManager;
import com.telecom.ecloudframework.org.api.model.IUser;
import com.telecom.ecloudframework.security.core.dao.SubsystemDao;
import com.telecom.ecloudframework.security.core.manager.SubsystemManager;
import com.telecom.ecloudframework.security.core.model.Subsystem;
import com.telecom.ecloudframework.sys.util.ContextUtil;
import cn.hutool.core.collection.CollectionUtil;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * <pre>
 * 描述：子系统定义 处理实现类
 * </pre>
 *
 * @author 谢石
 * @date 2020-7-29
 */
@Service("subsystemManager")
public class SubsystemManagerImpl extends BaseManager<String, Subsystem> implements SubsystemManager {
    @Resource
    SubsystemDao subsystemDao;

    @Override
    public boolean isExist(Subsystem subsystem) {
        return subsystemDao.isExist(subsystem) > 0;
    }

    @Override
    public List<Subsystem> getList() {
        return subsystemDao.getList();
    }

    @Override
    public Subsystem getDefaultSystem(String userId) {
        List<Subsystem> list = subsystemDao.getSystemByUser(userId);
        if (CollectionUtil.isEmpty(list)) {
            return null;
        }

        for (Subsystem system : list) {
            if (1 == system.getIsDefault()) {
                return system;
            }
        }
        return list.get(0);
    }

    @Override
    public void setDefaultSystem(String systemId) {
        Subsystem subSystem = subsystemDao.get(systemId);
        if (subSystem.getIsDefault() == 1) {
            subSystem.setIsDefault(0);
        } else {
            subsystemDao.updNoDefault();
            subSystem.setIsDefault(1);
        }
        subsystemDao.update(subSystem);
    }


    @Override
    public List<Subsystem> getCuurentUserSystem() {
        IUser user = ContextUtil.getCurrentUser();
        if (ContextUtil.isAdmin(user)) {
            return subsystemDao.getList();
        }

        return subsystemDao.getSystemByUser(user.getUserId());
    }

    @Override
    public Subsystem getByAlias(String systemAlias) {
        return subsystemDao.getByAlias(systemAlias);
    }

    /**
     * 设置子系统状态
     *
     * @param id
     * @param status
     * @author 谢石
     * @date 2020-7-29
     */
    @Override
    public void setStatus(String id, Integer status) {
        final String currentUserId = ContextUtil.getCurrentUserId();
        Subsystem subsystem = new Subsystem();
        subsystem.setId(id);
        subsystem.setEnabled(status);
        subsystem.setUpdateBy(currentUserId);
        subsystemDao.updateByPrimaryKeySelective(subsystem);
    }
}
