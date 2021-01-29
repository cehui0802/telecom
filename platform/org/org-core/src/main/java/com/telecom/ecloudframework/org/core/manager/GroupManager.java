package com.telecom.ecloudframework.org.core.manager;

import com.telecom.ecloudframework.base.manager.Manager;
import com.telecom.ecloudframework.org.core.model.Group;

import java.util.List;
import java.util.Map;

/**
 * <pre>
 * 描述：组织架构 处理接口
 * </pre>
 *
 * @author
 */
public interface GroupManager extends Manager<String, Group> {
    /**
     * 根据Code取定义对象。
     *
     * @param code
     * @return
     */
    Group getByCode(String code);

    /**
     * 根据用户ID获取组织列表
     *
     * @param userId
     * @return
     */
    List<Group> getByUserId(String userId);

    /**
     * 获取主组织。
     *
     * @param userId
     * @return
     */
    Group getMainGroup(String userId);

    /**
     * 删除所有
     */
    void removeAll();

    /**
     * 排序
     *
     * @param groups
     */
    void chageOrder(List<Group> groups);

    /**
     * 选人组件：查询当前用户公司下所有机构
     *
     * @param
     * @return List<Group>
     * @author guolihao
     * @date 2020/9/7 20:27
     */
    List<Group> queryAllGroup();

    /**
     * 当前管理员管理的机构ids
     *
     * @param userId
     * @return
     * @author 谢石
     * @date 2020-10-19
     */
    List<Group> getCurrentManagerOrgIds(String userId);

    /**
     * 查找机构是否存在
     *
     * @param mapOrg
     * @param mainOrgPath
     * @return
     * @author 谢石
     * @date 2020-11-3
     */
    String findOrgId(Map<String, String> mapOrg, String mainOrgPath);

    /**
     * 局部更新机构信息
     * @param record
     * @return
     */
    int updateByPrimaryKeySelective(Group record);
}
