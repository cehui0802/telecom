package com.telecom.ecloudframework.sys.service.impl;

import com.telecom.ecloudframework.sys.api.model.mq.RocketTransactionMessageDto;
import com.telecom.ecloudframework.sys.api.service.ITransactionSendBusService;
import com.alibaba.fastjson.JSON;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * @author lxy
 */
@Service
public class TransactionSendBusServiceImpl implements ITransactionSendBusService {

    Logger log = LoggerFactory.getLogger(TransactionSendBusServiceImpl.class);


    @Override
    public void handleBus(RocketTransactionMessageDto rocketTransactionMessageDto) throws Exception {
         log.info("事务消息默认业务处理........，消息message:{}", JSON.toJSONString(rocketTransactionMessageDto));
    }
}
