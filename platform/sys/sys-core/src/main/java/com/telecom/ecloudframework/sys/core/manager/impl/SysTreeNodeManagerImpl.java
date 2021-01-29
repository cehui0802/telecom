package com.telecom.ecloudframework.sys.core.manager.impl;

import java.util.List;

import javax.annotation.Resource;

import com.telecom.ecloudframework.sys.api.model.SysNodeOrderParam;
import com.telecom.ecloudframework.sys.util.CustomDefaultQueryFilterUtil;
import org.springframework.stereotype.Service;

import com.telecom.ecloudframework.base.api.constant.Direction;
import com.telecom.ecloudframework.base.api.query.QueryFilter;
import com.telecom.ecloudframework.base.api.query.QueryOP;
import com.telecom.ecloudframework.base.manager.impl.BaseManager;
import com.telecom.ecloudframework.sys.core.dao.SysTreeNodeDao;
import com.telecom.ecloudframework.sys.core.manager.SysTreeNodeManager;
import com.telecom.ecloudframework.sys.core.model.SysTreeNode;

/**
 * 系统树节点 Manager处理实现类
 *
 * @author aschs
 * @email aschs@qq.com
 * @time 2018-03-13 20:02:33
 */
@Service("sysTreeNodeManager")
public class SysTreeNodeManagerImpl extends BaseManager<String, SysTreeNode> implements SysTreeNodeManager {
    @Resource
    SysTreeNodeDao sysTreeNodeDao;

    @Override
    public List<SysTreeNode> getByTreeId(String treeId) {
        QueryFilter filter = CustomDefaultQueryFilterUtil.setDefaultQueryFilter();
        filter.addFilter("tree_id_", treeId, QueryOP.EQUAL);
        filter.addFieldSort("sn_", Direction.ASC.getKey());
        return this.query(filter);
    }

    @Override
    public SysTreeNode getByTreeIdAndKey(String treeId, String key) {
        QueryFilter filter = CustomDefaultQueryFilterUtil.setDefaultQueryFilter();
        filter.addFilter("tree_id_", treeId, QueryOP.EQUAL);
        filter.addFilter("key_", key, QueryOP.EQUAL);
        return this.queryOne(filter);
    }

    @Override
    public List<SysTreeNode> getByParentId(String parentId) {
        QueryFilter filter = CustomDefaultQueryFilterUtil.setDefaultQueryFilter();
        filter.addFilter("parent_id_", parentId, QueryOP.EQUAL);
        filter.addFieldSort("sn_", Direction.ASC.getKey());
        return this.query(filter);
    }

    public List<SysTreeNode> getByParentKey(String parentKey) {
        QueryFilter filter = CustomDefaultQueryFilterUtil.setDefaultQueryFilter();
        filter.addParamsFilter("parentkey", parentKey);
        filter.addFieldSort("sn_", Direction.ASC.getKey());
        return this.query(filter);
    }

    @Override
    public List<SysTreeNode> getStartWithPath(String path) {
        QueryFilter filter = CustomDefaultQueryFilterUtil.setDefaultQueryFilter();
        filter.addFilter("path_", path, QueryOP.RIGHT_LIKE);
        return this.query(filter);
    }

    @Override
    public void removeByTreeId(String treeId) {
        sysTreeNodeDao.removeByTreeId(treeId);
    }

    @Override
    public void removeByPath(String path) {
        sysTreeNodeDao.removeByPath(path);
    }

    @Override
    public int chageOrder(SysNodeOrderParam sysNodeOrderParam) {
        return sysTreeNodeDao.chageOrder(sysNodeOrderParam);
    }
}
