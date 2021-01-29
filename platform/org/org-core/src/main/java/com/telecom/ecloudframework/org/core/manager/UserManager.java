package com.telecom.ecloudframework.org.core.manager;

import com.telecom.ecloudframework.base.manager.Manager;
import com.telecom.ecloudframework.org.core.model.User;

import java.util.List;

/**
 * <pre>
 * 描述：用户表 处理接口
 * </pre>
 *
 * @author
 */
public interface UserManager extends Manager<String, User> {
    /**
     * 根据Account取定义对象。
     *
     * @param account
     * @return
     */
    User getByAccount(String account);

    /**
     * 根据角色ID获取用户列表
     *
     * @param relId 关系ID
     * @param type  关系类型  post , org , role
     * @return
     */
    List<User> getUserListByRelation(String relId, String type);

    /**
     * 判断系统中用户是否存在。
     *
     * @param user
     * @return
     */
    boolean isUserExist(User user);

    /**
     * 保存用户信息
     *
     * @param user
     */
    void saveUserInfo(User user);

    /**
     * <pre>
     * 根据指定组织路径下的所有用户
     * </pre>
     *
     * @param path
     * @return
     */
    List<User> getUserListByGroupPath(String path);

    /**
     * 删除系统外的用户
     */
    void removeOutSystemUser();

    /**
     * 根据openid获取用户信息
     *
     * @param openid
     * @return
     */
    User getByOpneid(String openid);

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
    List<User> getUsersByOrgPath(String orgPath);
}
