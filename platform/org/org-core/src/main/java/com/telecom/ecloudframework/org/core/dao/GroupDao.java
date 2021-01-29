package com.telecom.ecloudframework.org.core.dao;

import com.telecom.ecloudframework.base.dao.BaseDao;
import com.telecom.ecloudframework.org.core.model.Group;
import org.apache.ibatis.annotations.Param;
import org.mybatis.spring.annotation.MapperScan;

import java.util.List;


/**
 * <pre>
 * 描述：组织架构 DAO接口
 * </pre>
 */
@MapperScan
public interface GroupDao extends BaseDao<String, Group> {
    /**
     * 根据Code取定义对象。
     *
     * @param code
     * @param excludeId
     * @return
     */
    Group getByCode(@Param("code") String code, @Param("excludeId") String excludeId);

    /**
     * 根据用户ID获取组织列表，含岗位，组织，第一个为主组织
     *
     * @param userId
     * @return
     */
    List<Group> getByUserId(String userId);

    /**
     * 获取子组织
     *
     * @param path
     * @return
     */
    List<Group> getChildByPath(String path);

    /**
     * 删除所有
     */
    void removeAll();

    /**
     * 排序
     *
     * @param group
     */
    void chageOrder(Group group);

    /**
     * 根据主键选择性更新记录
     *
     * @param record 更新记录
     * @return 影响行数
     */
    int updateByPrimaryKeySelective(Group record);
}
