package com.telecom.ecloudframework.security.core.manager.impl;

import com.telecom.ecloudframework.base.api.exception.BusinessMessage;
import com.telecom.ecloudframework.base.api.query.QueryFilter;
import com.telecom.ecloudframework.base.api.query.QueryOP;
import com.telecom.ecloudframework.base.core.id.IdUtil;
import com.telecom.ecloudframework.base.db.model.query.DefaultQueryFilter;
import com.telecom.ecloudframework.base.manager.impl.BaseManager;
import com.telecom.ecloudframework.security.core.dao.SysResourceDao;
import com.telecom.ecloudframework.security.core.manager.SysResourceManager;
import com.telecom.ecloudframework.security.core.model.SysResource;
import cn.hutool.core.collection.CollectionUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * <pre>
 * 描述：子系统资源 处理实现类
 * </pre>
 *
 * @author
 */
@Service("sysResourceManager")
public class SysResourceManagerImpl extends BaseManager<String, SysResource> implements SysResourceManager {
    @Resource
    SysResourceDao sysResourceDao;


    @Override
    public List<SysResource> getBySystemId(String id) {
        List<SysResource> list = sysResourceDao.getBySystemId(id);
        return list;
    }

    @Override
    public List<SysResource> getBySystemAndRole(String systemId, String roleId) {
        return sysResourceDao.getBySystemAndRole(systemId, roleId);
    }

    @Override
    public boolean isExist(SysResource resource) {
        boolean rtn = sysResourceDao.isExist(resource) > 0;
        return rtn;
    }

    @Override
    public void removeByResId(String resId) {
        SysResource resource = sysResourceDao.get(resId);
        if (resource == null) {
            return;
        }
        List<SysResource> relatedResouces = new ArrayList<>();

        getChildList(relatedResouces, resource.getId());
        for (SysResource r : relatedResouces) {
            this.remove(r.getId());
        }
        this.remove(resId);
    }


    private void getChildList(List<SysResource> relatedResouces, String id) {
        List<SysResource> children = sysResourceDao.getByParentId(id);
        if (CollectionUtil.isEmpty(children)) {
            return;
        }

        for (SysResource r : children) {
            getChildList(relatedResouces, r.getId());
        }
        relatedResouces.addAll(children);
    }

    @Override
    public List<SysResource> getBySystemAndUser(String systemId, String userId) {
        List<SysResource> list = sysResourceDao.getBySystemAndUser(systemId, userId);
        return list;
    }

    /**
     * 创建
     *
     * @param entity
     * @author 谢石
     * @date 2020-7-14 16:08
     */
    @Override
    public void create(SysResource entity) {
        if (sysResourceDao.getByCode(entity.getAlias(), null) != null) {
            throw new BusinessMessage("资源别名“" + entity.getAlias() + "”已存在，请修改！");
        }
        entity.setId(IdUtil.getSuid());
        super.create(entity);
    }

    /**
     * 修改
     *
     * @param entity
     * @author 谢石
     * @date 2020-7-14 16:08
     */
    @Override
    public void update(SysResource entity) {
        if (sysResourceDao.getByCode(entity.getAlias(), entity.getId()) != null) {
            throw new BusinessMessage("资源别名“" + entity.getAlias() + "”已存在，请修改！");
        }
        super.update(entity);
    }

    /**
     * 删除
     *
     * @param id
     * @author 谢石
     * @date 2020-7-14 16:08
     */
    @Override
    public void remove(String id) {
        SysResource temp = get(id);
        if (null != temp) {
            super.remove(id);
            QueryFilter queryFilter = new DefaultQueryFilter(true);
            queryFilter.addFilter("parent_id_", id, QueryOP.EQUAL);
            List<SysResource> lstSysResource = sysResourceDao.query(queryFilter);
            lstSysResource.forEach(resource -> remove(resource.getId()));
        }
    }

    /**
     * 保存资源排序
     *
     * @param resource
     * @author 谢石
     * @date 2020-7-14 17:02
     */
    @Override
    public void saveOrder(SysResource resource) {
        if (null != resource) {
            SysResource temp = new SysResource();
            temp.setId(resource.getId());
            temp.setSn(resource.getSn());
            temp.setParentId(resource.getParentId());
            if (StringUtils.isNotEmpty(resource.getId()) && null != resource.getSn() && resource.getSn().compareTo(0) != 0) {
                sysResourceDao.updateByPrimaryKeySelective(temp);
            }
        }
    }

    /**
     * 获取角色资源列表
     *
     * @param roleId
     * @param systemId
     * @return
     * @author 谢石
     * @date 2020-7-15 15:07
     */
    @Override
    public List<String> getResResList(String roleId,String systemId) {
        return sysResourceDao.getResResList(roleId,systemId);
    }

}
