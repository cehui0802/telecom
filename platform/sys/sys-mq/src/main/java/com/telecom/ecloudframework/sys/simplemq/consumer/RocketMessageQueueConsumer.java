package com.telecom.ecloudframework.sys.simplemq.consumer;


import com.telecom.ecloudframework.base.core.util.SerializeUtil;
import com.telecom.ecloudframework.base.core.util.StringUtil;
import com.telecom.ecloudframework.sys.api.jms.JmsConfig;
import com.telecom.ecloudframework.sys.api.jms.JmsHandler;
import com.telecom.ecloudframework.sys.api.jms.model.JmsDTO;
import java.util.Iterator;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.common.consumer.ConsumeFromWhere;
import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.common.protocol.heartbeat.MessageModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.annotation.Autowired;
public class RocketMessageQueueConsumer extends AbstractMessageQueue implements DisposableBean {
    private static final Logger LOGGER = LoggerFactory.getLogger(RocketMessageQueueConsumer.class);
    @Autowired
    private JmsConfig jmsConfig;
    private String consumerGroup;
    private String nameServer;
    private String subExpression;

    public RocketMessageQueueConsumer() {
    }

    @Override
    public void destroy() throws Exception {
    }

    @Override
    protected void containerInitialCompleteAfter() {
        try {
            if (StringUtil.isEmpty(this.nameServer) || StringUtil.isEmpty(this.subExpression)) {
                LOGGER.info("消费者启动失败...");
                return;
            }

            DefaultMQPushConsumer consumer = new DefaultMQPushConsumer(this.consumerGroup);
            consumer.setNamesrvAddr(this.nameServer);
            consumer.setConsumeFromWhere(ConsumeFromWhere.CONSUME_FROM_LAST_OFFSET);
            consumer.subscribe(this.jmsConfig.getName(), this.subExpression);
            consumer.setMessageModel(MessageModel.CLUSTERING);
            consumer.registerMessageListener((MessageListenerConcurrently) (msgs, context) -> {
                Iterator var3 = msgs.iterator();

                while(var3.hasNext()) {
                    MessageExt msg = (MessageExt)var3.next();

                    try {
                        JmsDTO jmsDTO = (JmsDTO)SerializeUtil.unserialize(msg.getBody());
                        JmsHandler jmsHandler = this.getJmsHandler(jmsDTO.getType());
                        jmsHandler.handlerMessage(jmsDTO, msg.getMsgId());
                    } catch (Exception var7) {
                        var7.printStackTrace();
                    }
                }

                return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
            });
            consumer.start();
            LOGGER.info("消费者启动成功...,启动状态：{}", consumer.getDefaultMQPushConsumerImpl().getServiceState());
        } catch (MQClientException var2) {
            LOGGER.error("rocket消费者启动失败", var2);
        }

    }

    public void setConsumerGroup(String consumerGroup) {
        this.consumerGroup = consumerGroup;
    }

    public void setNameServer(String nameServer) {
        this.nameServer = nameServer;
    }

    public void setSubExpression(String subExpression) {
        this.subExpression = subExpression;
    }

    public void setJmsConfig(JmsConfig jmsConfig) {
        this.jmsConfig = jmsConfig;
    }
}

