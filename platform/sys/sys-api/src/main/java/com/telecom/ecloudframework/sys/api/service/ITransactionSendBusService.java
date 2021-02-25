package com.telecom.ecloudframework.sys.api.service;

import com.telecom.ecloudframework.sys.api.model.mq.RocketTransactionMessageDto;

public interface ITransactionSendBusService {
    void handleBus(RocketTransactionMessageDto var1) throws Exception;
}
