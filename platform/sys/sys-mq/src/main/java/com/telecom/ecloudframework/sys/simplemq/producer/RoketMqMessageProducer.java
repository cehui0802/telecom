package com.telecom.ecloudframework.sys.simplemq.producer;


import com.telecom.ecloudframework.base.core.util.SerializeUtil;
import com.telecom.ecloudframework.sys.api.jms.JmsConfig;
import com.telecom.ecloudframework.sys.api.jms.MessageQueueSendException;
import com.telecom.ecloudframework.sys.api.jms.model.JmsDTO;
import com.telecom.ecloudframework.sys.api.jms.producer.JmsProducer;
import cn.hutool.core.collection.CollectionUtil;
import com.alibaba.fastjson.JSON;
import java.util.List;
import javax.annotation.Resource;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.MQProducer;
import org.apache.rocketmq.common.message.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

public class RoketMqMessageProducer implements JmsProducer {
    private static final Logger LOGGER = LoggerFactory.getLogger(RoketMqMessageProducer.class);
    @Resource
    private MQProducer mqProducer;
    @Autowired
    private JmsConfig jmsConfig;

    public RoketMqMessageProducer() {
    }

    public void sendToQueue(JmsDTO jmsDTOMessage) throws MessageQueueSendException {
        if (jmsDTOMessage == null) {
            LOGGER.info("传入参数为空, 跳过执行");
        } else {
            Message message = null;

            try {
                message = new Message(this.jmsConfig.getName(), jmsDTOMessage.getType(), SerializeUtil.serialize(jmsDTOMessage));
                this.mqProducer.send(message);
            } catch (Exception var4) {
                LOGGER.warn("RoketMessage发送失败, 发送参数：{}", JSON.toJSONString(message));
                throw new MessageQueueSendException("发送队列消息失败, " + var4.getMessage());
            }
        }
    }

    public void sendToQueue(List<JmsDTO> messages) throws MessageQueueSendException {
        if (CollectionUtil.isEmpty(messages)) {
            LOGGER.info("传入参数为空, 跳过执行");
        } else {
            LOGGER.debug(JSON.toJSONString(messages));
            messages.forEach((message) -> {
                this.sendToQueue(message);
            });
        }
    }

    public void init() throws MQClientException {
        if (null != this.mqProducer) {
            this.mqProducer.start();
        }

    }

    public void destory() {
        if (null != this.mqProducer) {
            this.mqProducer.shutdown();
        }

    }

    public void setMqProducer(MQProducer mqProducer) {
        this.mqProducer = mqProducer;
    }

    public void setJmsConfig(JmsConfig jmsConfig) {
        this.jmsConfig = jmsConfig;
    }
}

