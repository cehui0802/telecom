package com.telecom.ecloudframework.security.core.manager.impl;

import com.telecom.ecloudframework.base.core.id.IdUtil;
import com.telecom.ecloudframework.base.manager.impl.BaseManager;
import com.telecom.ecloudframework.security.core.dao.ClientDao;
import com.telecom.ecloudframework.security.core.manager.ClientManager;
import com.telecom.ecloudframework.security.core.model.Client;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import javax.annotation.Resource;

/**
 * <pre>
 * 描述：客户端 处理实现类
 * </pre>
 *
 * @author 谢石
 * @date 2020-7-13 13:54
 */
@Service("clientManager")
public class ClientManagerImpl extends BaseManager<String, Client> implements ClientManager {
    @Resource
    ClientDao clientDao;

    @Override
    public void create(Client entity) {
        entity.setId(IdUtil.getSuid());
        entity.setSecretKey(encry(entity.getId()));
        clientDao.create(entity);
    }

    /**
     * 部分更新
     *
     * @param entity
     * @author 谢石
     * @date 2020-8-20
     */
    @Override
    public void update(Client entity) {
        clientDao.updateByPrimaryKeySelective(entity);
    }

    /**
     * 加密
     *
     * @param clientId
     * @return
     * @author 谢石
     * @date 2020-8-7
     */
    private String encry(String clientId) {
        String encryString = String.format("%s-%s-ecloud", clientId, System.currentTimeMillis());
        encryString = DigestUtils.md5DigestAsHex(encryString.getBytes());
        return encryString;
    }

    /**
     * 重新生成秘钥
     *
     * @param id
     * @author 谢石
     * @date 2020-8-7
     */
    @Override
    public void updateSecretKey(String id) {
        Client entity = get(id);
        if (null != entity) {
            entity.setSecretKey(encry(entity.getId()));
            clientDao.updateByPrimaryKeySelective(entity);
        }
    }
}
