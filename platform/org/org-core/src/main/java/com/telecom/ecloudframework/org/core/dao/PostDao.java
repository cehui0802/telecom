package com.telecom.ecloudframework.org.core.dao;

import com.telecom.ecloudframework.base.dao.BaseDao;
import com.telecom.ecloudframework.org.core.model.Post;
import org.apache.ibatis.annotations.Param;
import org.mybatis.spring.annotation.MapperScan;

/**
 * <pre>
 * 描述：岗位DAO接口
 * </pre>
 *
 * @author 谢石
 * @date 2020-7-1 11:39
 */
@MapperScan
public interface PostDao extends BaseDao<String, Post> {

    /**
     * 根据Code取定义对象。
     *
     * @param code
     * @param excludeId
     * @return
     */
    Post getByCode(@Param("code") String code, @Param("excludeId") String excludeId);

    /**
     * 根据主键选择性更新记录
     *
     * @param record 更新记录
     * @return 影响行数
     */
    int updateByPrimaryKeySelective(Post record);
}
