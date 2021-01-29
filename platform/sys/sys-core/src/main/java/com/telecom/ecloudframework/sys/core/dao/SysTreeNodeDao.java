package com.telecom.ecloudframework.sys.core.dao;

import com.telecom.ecloudframework.sys.api.model.SysNodeOrderParam;
import com.telecom.ecloudframework.sys.core.model.SysTreeNode;
import org.mybatis.spring.annotation.MapperScan;

import com.telecom.ecloudframework.base.dao.BaseDao;

/**
 * 系统树节点 DAO接口
 *
 * @author aschs
 * @email aschs@qq.com
 * @time 2018-03-13 20:02:33
 */
@MapperScan
public interface SysTreeNodeDao extends BaseDao<String, SysTreeNode> {

    /**
     * <pre>
     * 根据树id删除节点
     * </pre>
     *
     * @param treeId
     */
    void removeByTreeId(String treeId);
    
    /**
     * <pre>
     * 删除path下的全部节点
     * </pre>
     *
     * @param path
     */
    void removeByPath(String path);

    /**
     * 改变分类树顺序
     * @param sysNodeOrderParam
     * @return
     */
    int chageOrder(SysNodeOrderParam sysNodeOrderParam);

}
