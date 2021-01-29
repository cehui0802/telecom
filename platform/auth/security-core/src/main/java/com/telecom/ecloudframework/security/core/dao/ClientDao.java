package com.telecom.ecloudframework.security.core.dao;

import com.telecom.ecloudframework.base.dao.BaseDao;
import com.telecom.ecloudframework.security.core.model.Client;
import org.mybatis.spring.annotation.MapperScan;

@MapperScan
public interface ClientDao extends BaseDao<String, Client> {

    /**
     * 部分更新
     *
     * @param client
     * @return
     * @author 谢石
     * @date 2020-8-3
     */
    int updateByPrimaryKeySelective(Client client);
}
