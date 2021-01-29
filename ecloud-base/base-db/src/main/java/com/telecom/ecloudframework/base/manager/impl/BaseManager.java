package com.telecom.ecloudframework.base.manager.impl;

import java.io.Serializable;
import java.util.List;

import com.telecom.ecloudframework.base.dao.BaseDao;
import com.telecom.ecloudframework.base.manager.Manager;
import org.springframework.beans.factory.annotation.Autowired;

import com.telecom.ecloudframework.base.api.Page;
import com.telecom.ecloudframework.base.api.query.QueryFilter;

/**
 * <pre>
 * 描述：抽象实体业务管理类实现
 * </pre>
 */
public abstract class BaseManager<PK extends Serializable, T extends Serializable> implements Manager<PK, T> {
    //获取基础类

    @Autowired
    protected BaseDao<PK, T> dao;
    
    @Override
    public void create(T entity) {
        dao.create(entity);
    }

   
    @Override
    public void update(T entity) {
        dao.update(entity);
    }

    @Override
    public void remove(PK entityId) {
        dao.remove(entityId);
    }


    @Override
    public T get(PK entityId) {
        return dao.get(entityId);
    }

   
    @Override
    public void removeByIds(PK... ids) {
        if (ids != null) {
            for (PK pk : ids) {
                remove(pk);
            }
        }
    }

  
    @Override
    public List<T> query(QueryFilter queryFilter) {
        return dao.query(queryFilter);
    }

    /**
     * <pre>
     * 根据QueryFilter获取唯一值
     * </pre>
     * @deprecated 请尽量写SQL。列表查询不会包含大数据文本 比如 form.html
     * @param queryFilter
     * @return
     */
    public T queryOne(QueryFilter queryFilter) {
        List<T> list = query(queryFilter);
        if (list.isEmpty()) {
            return null;
        } else {
            return list.get(0);
        }
    }

  
    @Override
    public List<T> getAll() {
        return dao.query();
    }
    
	@Override
	public List<T> getAllByPage(Page page) {
		return null;
	}

}
