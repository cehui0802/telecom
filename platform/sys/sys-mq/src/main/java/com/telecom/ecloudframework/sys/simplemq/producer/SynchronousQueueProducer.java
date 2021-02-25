package com.telecom.ecloudframework.sys.simplemq.producer;


import com.telecom.ecloudframework.sys.api.jms.MessageQueueSendException;
import com.telecom.ecloudframework.sys.api.jms.model.JmsDTO;
import com.telecom.ecloudframework.sys.api.jms.producer.JmsProducer;
import com.telecom.ecloudframework.sys.simplemq.consumer.AbstractMessageQueue;
import java.util.Iterator;
import java.util.List;

public class SynchronousQueueProducer extends AbstractMessageQueue implements JmsProducer {
    public SynchronousQueueProducer() {
    }

    public void sendToQueue(JmsDTO message) throws MessageQueueSendException {
        this.handleMessage(message, (String)null);
    }

    public void sendToQueue(List<JmsDTO> messages) throws MessageQueueSendException {
        if (messages != null && !messages.isEmpty()) {
            Iterator var2 = messages.iterator();

            while(var2.hasNext()) {
                JmsDTO jmsDTO = (JmsDTO)var2.next();
                this.sendToQueue(jmsDTO);
            }
        }

    }
}