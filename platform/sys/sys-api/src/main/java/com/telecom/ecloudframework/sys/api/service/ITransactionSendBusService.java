package com.telecom.ecloudframework.sys.api.service;

import com.telecom.ecloudframework.sys.api.model.mq.RocketTransactionMessageDto;

/**
 * 发送事务消息的 本地事务处理层
 * @author lxy
 */
public interface ITransactionSendBusService {

    /**
     * 事务业务处理
     * @throws Exception
     */
     void  handleBus(RocketTransactionMessageDto rocketTransactionMessageDto) throws Exception;
}
