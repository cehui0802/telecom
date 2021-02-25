package com.telecom.ecloudframework.sys.simplemq.producer;


import com.telecom.ecloudframework.sys.api.jms.JmsConfig;
import com.telecom.ecloudframework.sys.api.jms.MessageQueueSendException;
import com.telecom.ecloudframework.sys.api.jms.model.JmsDTO;
import com.telecom.ecloudframework.sys.api.jms.producer.JmsProducer;
import cn.hutool.core.collection.CollectionUtil;
import com.alibaba.fastjson.JSON;
import java.util.Iterator;
import java.util.List;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.ObjectMessage;
import javax.jms.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;

public class CommonMessageQueueProducer implements JmsProducer {
    private static final Logger LOGGER = LoggerFactory.getLogger(CommonMessageQueueProducer.class);
    @Autowired
    private JmsConfig jmsConfig;
    @Autowired
    private JmsTemplate jmsTemplate;

    public CommonMessageQueueProducer() {
    }

    public void sendToQueue(final JmsDTO message) {
        if (message == null) {
            LOGGER.info("传入参数为空, 跳过执行");
        } else {
            try {
                this.jmsTemplate.send(this.jmsConfig.getName(), new MessageCreator() {
                    public Message createMessage(Session session) throws JMSException {
                        ObjectMessage objMessage = session.createObjectMessage(message);
                        objMessage.setStringProperty("type", message.getType());
                        return objMessage;
                    }
                });
            } catch (Exception var3) {
                LOGGER.warn("JMS发送失败, 发送参数：{}", JSON.toJSONString(message));
                throw new MessageQueueSendException("发送队列消息失败, " + var3.getMessage());
            }
        }
    }

    public void sendToQueue(List<JmsDTO> messages) {
        if (CollectionUtil.isEmpty(messages)) {
            LOGGER.info("传入参数为空, 跳过执行");
        } else {
            Iterator var2 = messages.iterator();

            while(var2.hasNext()) {
                JmsDTO jmsDTO = (JmsDTO)var2.next();
                this.sendToQueue(jmsDTO);
            }

        }
    }
}
