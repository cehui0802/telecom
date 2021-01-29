package com.telecom.ecloudframework.org.core.dao;

import com.telecom.ecloudframework.base.dao.BaseDao;
import com.telecom.ecloudframework.org.core.model.User;
import org.apache.ibatis.annotations.Param;
import org.mybatis.spring.annotation.MapperScan;

import java.util.List;

/**
 * <pre>
 * 描述：用户表 DAO接口
 * </pre>
 *
 * @author
 */
@MapperScan
public interface UserDao extends BaseDao<String, User> {
    /**
     * 根据Account取定义对象。
     *
     * @param account
     * @return
     */
    User getByAccount(String account);

    /**
     * 判断用户是否存在。
     */
    Integer isUserExist(User user);


    List<User> getUserListByRelation(@Param("relationId") String relId, @Param("relationType") String type);

    /**
     * <pre>
     * 根据指定组织路径下的所有用户
     * </pre>
     *
     * @param path
     * @return
     */
    List<User> getUserListByGroupPath(@Param("path") String path);

    void removeOutSystemUser();

    /**
     * 通过openID 获取用户
     *
     * @param openId
     * @return
     */
    User getByOpenid(String openId);

    /**
     * 根据主键选择性更新记录
     *
     * @param record 更新记录
     * @return 影响行数
     */
    int updateByPrimaryKeySelective(User record);

    /**
     * 根据账户获取主键编号
     *
     * @param account 账户
     * @return 主键编号
     */
    String getIdByAccount(String account);

    /**
     * 获取启用用户数量
     *
     * @return
     * @author 谢石
     * @date 2020-7-9 16:03
     */
    Integer getAllEnableUserNum();

    /**
     * 通过组织路径获取用户
     *
     * @param orgPath
     * @return
     * @author 谢石
     * @date 2020-8-17
     */
    List<User> getUsersByOrgPath(@Param("orgPath") String orgPath);
}
