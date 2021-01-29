package com.telecom.ecloudframework.org.core.manager;

import com.telecom.ecloudframework.base.manager.Manager;
import com.telecom.ecloudframework.org.core.model.Post;

import java.util.List;

/**
 * <pre>
 * 描述：岗位Manager处理接口
 * </pre>
 *
 * @author 谢石
 * @date 2020-7-1 11:36
 */
public interface PostManager extends Manager<String, Post> {
    /**
     * 通过别名获取岗位
     *
     * @param alias
     * @return
     */
    Post getByAlias(String alias);

    /**
     * 根据用户ID获取岗位列表
     *
     * @param userId
     * @return
     */
    List<Post> getByUserId(String userId);

    /**
     * 获取主岗位
     *
     * @param userId
     * @return
     * @author 谢石
     * @date 2020-8-27
     */
    Post getMasterByUserId(String userId);
}
