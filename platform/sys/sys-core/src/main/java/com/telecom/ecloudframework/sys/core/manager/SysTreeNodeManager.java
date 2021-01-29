package com.telecom.ecloudframework.sys.core.manager;

import java.util.List;

import com.telecom.ecloudframework.base.manager.Manager;
import com.telecom.ecloudframework.sys.api.model.SysNodeOrderParam;
import com.telecom.ecloudframework.sys.core.model.SysTreeNode;

/**
 * 系统树节点 Manager处理接口
 *
 * @author aschs
 * @email aschs@qq.com
 * @time 2018-03-13 20:02:33
 */
public interface SysTreeNodeManager extends Manager<String, SysTreeNode> {

    /**
     * <pre>
     * 根据树id获取节点
     * 根据sn字段升序
     * </pre>
     *
     * @param treeId
     * @return
     */
    List<SysTreeNode> getByTreeId(String treeId);

    /**
     * <pre>
     * 获取指定树下的指定节点
     * </pre>
     *
     * @param treeId
     * @param key
     * @return
     */
    SysTreeNode getByTreeIdAndKey(String treeId, String key);

    /**
     * <pre>
     * 根据父节点获取其子节点
     * 不会进行递归查询，只获取第一层
     * </pre>
     *
     * @param parentId
     * @return
     */
    List<SysTreeNode> getByParentId(String parentId);

    List<SysTreeNode> getByParentKey(String parentKey);
    /**
     * <pre>
     * 获取以path开始的路径
     * </pre>
     *
     * @param path
     * @return
     */
    List<SysTreeNode> getStartWithPath(String path);

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
     * 改变节点顺序
     * @param sysNodeOrderParam
     * @return
     */
    int chageOrder(SysNodeOrderParam sysNodeOrderParam);
}
