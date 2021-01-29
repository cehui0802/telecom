package com.telecom.ecloudframework.sys.service.impl;

import com.telecom.ecloudframework.sys.api.model.mq.RocketTransactionMessageDto;
import com.telecom.ecloudframework.sys.api.service.ITransactionSendBusService;
import org.apache.rocketmq.client.producer.LocalTransactionState;
import org.apache.rocketmq.client.producer.TransactionListener;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.remoting.common.RemotingHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author lxy
 */
@Service
public class TransactionListenerImp implements TransactionListener {
    Logger log = LoggerFactory.getLogger(TransactionListenerImp.class);
    @Resource
    ITransactionSendBusService transactionSendBusService;

    @Resource
    private Environment environment;
    private ConcurrentHashMap<String,Integer> locaTrans=new ConcurrentHashMap<>(0);
    @Override
    public LocalTransactionState executeLocalTransaction(Message message, Object o) {
        //本地事务
        String transactionId=message.getTransactionId();
        locaTrans.put(transactionId,0);
        //开始处理本地事务
        //0:状态未知 1：本地事务执行成功  2：本地事务执行失败
        log.info("开始执行本地事务..........");
        try {
            RocketTransactionMessageDto rocketTransactionMessageDto=new RocketTransactionMessageDto();
            rocketTransactionMessageDto.setKey(message.getKeys());
            rocketTransactionMessageDto.setTopic(message.getTopic());
            rocketTransactionMessageDto.setTag(message.getTags());
            rocketTransactionMessageDto.setMsg(new String(message.getBody(), RemotingHelper.DEFAULT_CHARSET));
            transactionSendBusService.handleBus(rocketTransactionMessageDto);
            locaTrans.put(transactionId,1);
            log.info("本地事务执行成功...........");
        } catch (Exception e) {
            e.printStackTrace();
            log.error("本地事务执行失败-----------");
            locaTrans.put(transactionId,2);
            return LocalTransactionState.ROLLBACK_MESSAGE;
        }

        return LocalTransactionState.COMMIT_MESSAGE;
    }

    @Override
    public LocalTransactionState checkLocalTransaction(MessageExt messageExt) {
        //获取对应事务的状态
        String transactionId=messageExt.getTransactionId();
        Integer status=locaTrans.get(transactionId);
        log.info("消息回查------------status:{},----------transactionId:{}",status,transactionId);
        switch (status){
            case 0:
                return LocalTransactionState.UNKNOW;
            case 1:
                return LocalTransactionState.COMMIT_MESSAGE;
            case 2:
                return LocalTransactionState.ROLLBACK_MESSAGE;
        }

        return LocalTransactionState.UNKNOW;
    }
}
