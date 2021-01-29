package com.telecom.ecloudframework.security.core.dao;

import com.telecom.ecloudframework.base.dao.BaseDao;
import com.telecom.ecloudframework.security.core.model.Subsystem;
import org.mybatis.spring.annotation.MapperScan;

import java.util.List;

/**
 * <pre>
 * 描述：子系统定义 DAO接口
 * </pre>
 */
@MapperScan
public interface SubsystemDao extends BaseDao<String, Subsystem> {

    /**
     * 判断别名是否存在
     *
     * @param subsystem
     * @return
     */
    Integer isExist(Subsystem subsystem);

    /**
     * 获取子系统列表。
     *
     * @return
     */
    List<Subsystem> getList();

    /**
     * 更新为默认。
     */
    void updNoDefault();

    /**
     * 根据用户获取子系统列表。
     *
     * @param userId
     * @return
     */
    List<Subsystem> getSystemByUser(String userId);

    /**
     * 根据别名获取子系统
     *
     * @param systemAlias
     * @return
     * @author 谢石
     * @date 2020-7-30
     */
    Subsystem getByAlias(String systemAlias);

    /**
     * 部分更新
     *
     * @param subsystem
     * @return
     * @author 谢石
     * @date 2020-7-14 17:05
     */
    void updateByPrimaryKeySelective(Subsystem subsystem);
}
