package com.telecom.ecloudframework.sys.core.dao;

import com.telecom.ecloudframework.sys.core.model.SysTree;
import org.mybatis.spring.annotation.MapperScan;

import com.telecom.ecloudframework.base.dao.BaseDao;

/**
 * 系统树 DAO接口
 *
 * @author aschs
 * @email aschs@qq.com
 * @time 2018-03-13 19:58:28
 */
@MapperScan
public interface SysTreeDao extends BaseDao<String, SysTree> {

}
