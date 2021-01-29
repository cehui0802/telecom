package com.telecom.ecloudframework.org.core.manager.impl;

import com.telecom.ecloudframework.base.api.exception.BusinessMessage;
import com.telecom.ecloudframework.base.api.query.QueryFilter;
import com.telecom.ecloudframework.base.api.query.QueryOP;
import com.telecom.ecloudframework.base.core.id.IdUtil;
import com.telecom.ecloudframework.base.core.util.StringUtil;
import com.telecom.ecloudframework.base.db.model.query.DefaultQueryFilter;
import com.telecom.ecloudframework.base.manager.impl.BaseManager;
import com.telecom.ecloudframework.org.core.dao.PostDao;
import com.telecom.ecloudframework.org.core.manager.OrgRelationManager;
import com.telecom.ecloudframework.org.core.manager.PostManager;
import com.telecom.ecloudframework.org.core.model.Post;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <pre>
 * 描述：岗位Manager处理接口实现
 * </pre>
 *
 * @author 谢石
 * @date 2020-7-1 11:37
 */
@Service
public class PostManagerImpl extends BaseManager<String, Post> implements PostManager {
    @Resource
    PostDao postDao;
    @Autowired
    OrgRelationManager orgRelationMananger;

    /**
     * 创建
     *
     * @param entity
     */
    @Override
    public void create(Post entity) {
        if (postDao.getByCode(entity.getCode(), null) != null) {
            throw new BusinessMessage("岗位编码“" + entity.getCode() + "”已存在，请修改！");
        }
        entity.setId(IdUtil.getSuid());
        super.create(entity);
    }

    /**
     * 更新
     *
     * @param entity
     */
    @Override
    public void update(Post entity) {
        if (postDao.getByCode(entity.getCode(), entity.getId()) != null) {
            throw new BusinessMessage("岗位编码“" + entity.getCode() + "”已存在，请修改！");
        }
        super.update(entity);
    }

    /**
     * 通过别名获取岗位
     *
     * @param alias
     * @return
     * @author 谢石
     * @date 2020-8-18
     */
    @Override
    public Post getByAlias(String alias) {
        return postDao.getByCode(alias, null);
    }

    /**
     * 删除岗位
     *
     * @param id
     * @author 谢石
     * @date 2020-9-14
     */
    @Override
    public void remove(String id) {
        orgRelationMananger.removeCheck(id);
        super.remove(id);
    }

    /**
     * 通过用户id获取岗位列表
     *
     * @param userId
     * @return
     * @author 谢石
     * @date 2020-8-18
     */
    @Override
    public List<Post> getByUserId(String userId) {
        if (StringUtil.isEmpty(userId)) {
            return Collections.emptyList();
        }
        QueryFilter filter = new DefaultQueryFilter(true);
        Map<String, Object> params = new HashMap<>();
        params.put("userId", userId);
        filter.addParams(params);
        return postDao.query(filter);
    }

    /**
     * 获取主岗位
     *
     * @param userId
     * @return
     * @author 谢石
     * @date 2020-8-27
     */
    @Override
    public Post getMasterByUserId(String userId) {
        if (StringUtil.isEmpty(userId)) {
            return null;
        }
        QueryFilter filter = new DefaultQueryFilter(true);
        Map<String, Object> params = new HashMap<>();
        params.put("userId", userId);
        params.put("isMaster", "1");
        filter.addFilter("trelation.is_master_", 1, QueryOP.EQUAL);
        filter.addParams(params);
        List<Post> lstPost = postDao.query(filter);
        if (null != lstPost && lstPost.size() > 0) {
            return lstPost.get(0);
        }
        return null;
    }
}
