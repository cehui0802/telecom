package com.telecom.ecloudframework.sys.core.manager.impl;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.telecom.ecloudframework.base.api.query.QueryFilter;
import com.telecom.ecloudframework.base.api.query.QueryOP;
import com.telecom.ecloudframework.base.db.model.query.DefaultQueryFilter;
import com.telecom.ecloudframework.base.manager.impl.BaseManager;
import com.telecom.ecloudframework.sys.core.dao.SysTreeDao;
import com.telecom.ecloudframework.sys.core.manager.SysTreeManager;
import com.telecom.ecloudframework.sys.core.manager.SysTreeNodeManager;
import com.telecom.ecloudframework.sys.core.model.SysTree;

/**
 * 系统树 Manager处理实现类
 *
 * @author aschs
 * @email aschs@qq.com
 * @time 2018-03-13 19:58:28
 */
@Service("sysTreeManager")
public class SysTreeManagerImpl extends BaseManager<String, SysTree> implements SysTreeManager {
    @Resource
    SysTreeDao sysTreeDao;
    @Autowired
    SysTreeNodeManager sysTreeNodeManager;

    @Override
    public SysTree getByKey(String key) {
        QueryFilter filter = new DefaultQueryFilter();
        filter.addFilter("key_", key, QueryOP.EQUAL);
        return this.queryOne(filter);
    }

    @Override
    public void removeContainNode(String id) {
        this.remove(id);
        sysTreeNodeManager.removeByTreeId(id);
    }
}
