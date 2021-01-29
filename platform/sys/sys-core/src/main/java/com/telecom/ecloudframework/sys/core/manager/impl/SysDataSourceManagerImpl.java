package com.telecom.ecloudframework.sys.core.manager.impl;

import javax.sql.DataSource;

import com.telecom.ecloudframework.sys.core.dao.SysDataSourceDao;
import com.telecom.ecloudframework.sys.core.model.def.SysDataSourceDefAttribute;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import com.telecom.ecloudframework.base.api.constant.BaseStatusCode;
import com.telecom.ecloudframework.base.api.exception.BusinessException;
import com.telecom.ecloudframework.base.api.query.QueryFilter;
import com.telecom.ecloudframework.base.api.query.QueryOP;
import com.telecom.ecloudframework.base.core.util.AppUtil;
import com.telecom.ecloudframework.base.core.util.BeanUtils;
import com.telecom.ecloudframework.base.core.util.StringUtil;
import com.telecom.ecloudframework.base.db.datasource.DataSourceUtil;
import com.telecom.ecloudframework.base.db.datasource.DbContextHolder;
import com.telecom.ecloudframework.base.db.model.query.DefaultQueryFilter;
import com.telecom.ecloudframework.base.manager.impl.BaseManager;
import com.telecom.ecloudframework.sys.core.manager.SysDataSourceManager;
import com.telecom.ecloudframework.sys.core.model.SysDataSource;

import cn.hutool.core.util.ReflectUtil;

/**
 * <pre>
 * 描述：数据源 Manager处理实现类
 * 构建组：白日梦团体
 * 作者:aschs
 * 邮箱:aschs@qq.com
 * 日期:2018-01-08 21:14:05
 * 版权：summer
 * </pre>
 */
@Service
public class SysDataSourceManagerImpl extends BaseManager<String, SysDataSource> implements SysDataSourceManager {
	@Autowired
    SysDataSourceDao sysDataSourceDao;

	@Override
	public DataSource tranform2DataSource(SysDataSource sysDataSource) {
		try {
			Class<?> cls = Class.forName(sysDataSource.getClassPath());// 拿到类路径
			DataSource dataSource = (DataSource) cls.newInstance();// 初始化一个对象
			// 设置值
			for (SysDataSourceDefAttribute attribute : sysDataSource.getAttributes()) {
				if (StringUtil.isEmpty(attribute.getValue())) {
					continue;
				}
				Object value = BeanUtils.getValue(attribute.getType(), attribute.getValue());
				String setMethodName = "set" + StringUtil.toFirst(attribute.getName(), true);
				ReflectUtil.invoke(dataSource, setMethodName, value);
			}
			return dataSource;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public SysDataSource getByKey(String key) {
		QueryFilter filter = new DefaultQueryFilter();
		filter.addFilter("key_", key, QueryOP.EQUAL);
		return this.queryOne(filter);
	}

	@Override
	public DataSource getDataSourceByKey(String key, boolean add) {
		//如果是线程中的数据源 
    	if(DbContextHolder.getDataSource().equals(key)) {
    		return (DataSource) AppUtil.getBean("dataSource");
    	}
		
		// 1从spring配置中取
		DataSource dataSource = DataSourceUtil.getDataSourceByAlias(key);
		if (dataSource != null) {
			return dataSource;
		}
		// 2 从系统配置中取
		SysDataSource sysDataSource = getByKey(key);
		if (sysDataSource != null) {
			dataSource = tranform2DataSource(sysDataSource);
			if (add) {
				DataSourceUtil.addDataSource(key, dataSource, sysDataSource.getDbType(), true);
			}
		}
		throw new BusinessException("在系统中找不到key为[" + key + "]的数据源", BaseStatusCode.SYSTEM_ERROR);
	}

	@Override
	public DataSource getDataSourceByKey(String key) {
		return getDataSourceByKey(key, true);
	}

	@Override
	public JdbcTemplate getJdbcTemplateByKey(String key) {
		//如果是线程中的数据源 
    	if(DbContextHolder.getDataSource().equals(key)) {
    		return (JdbcTemplate) AppUtil.getBean("jdbcTemplate");
    	}
    	
		return new JdbcTemplate(getDataSourceByKey(key));
	}

}
