package com.telecom.ecloudframework.sys.service.impl;

import com.telecom.ecloudframework.sys.api.model.ISysTreeNode;
import com.telecom.ecloudframework.sys.api.service.ISysTreeNodeService;
import com.telecom.ecloudframework.sys.core.manager.SysTreeManager;
import com.telecom.ecloudframework.sys.core.manager.SysTreeNodeManager;
import com.telecom.ecloudframework.sys.core.model.SysTree;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

/**
 * <pre>
 * 描述：SysTreeNodeService接口
 * 作者:aschs
 * 邮箱:aschs@qq.com
 * 日期:2018年3月28日 下午3:31:25
 * 版权:summer
 * </pre>
 */
@Service
public class SysTreeNodeService implements ISysTreeNodeService {
    @Autowired
    SysTreeNodeManager sysTreeNodeManager;
    @Autowired
    SysTreeManager sysTreeManager;
    @Override
    public ISysTreeNode getById(String id) {
        return sysTreeNodeManager.get(id);
    }

    @Override
    public List<? extends ISysTreeNode> getTreeNodesByType(String treeKey){
        SysTree tree = sysTreeManager.getByKey(treeKey);
        if(tree == null) return Collections.emptyList();

        return sysTreeNodeManager.getByTreeId(tree.getId());
    }
}
