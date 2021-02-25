package com.telecom.ecloudframework.sys.simplemq.consumer;


import com.telecom.ecloudframework.sys.api.jms.model.JmsDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CommonMessageQueueConsumer extends AbstractMessageQueue {
    private static final Logger LOGGER = LoggerFactory.getLogger(CommonMessageQueueConsumer.class);
    private volatile boolean loadComplete;

    public CommonMessageQueueConsumer() {
    }

    @Override
    protected void containerInitialCompleteAfter() {
        this.loadComplete = true;
    }

    @Override
    public void handleMessage(JmsDTO<?> jmsDTO, String messageId) {
        while(!this.loadComplete) {
            try {
                Thread.sleep(500L);
            } catch (InterruptedException var4) {
                LOGGER.error("线程中断", var4.getMessage());
            }
        }

        super.handleMessage(jmsDTO, messageId);
    }
}

