package com.telecom.ecloudframework.sys.simplemq.handle.msg;


import com.telecom.ecloudframework.sys.api.jms.JmsHandler;
import com.telecom.ecloudframework.sys.api.jms.model.JmsDTO;
import java.io.Serializable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbsNotifyMessageHandler<T extends Serializable> implements JmsHandler<T> {
    protected static final Logger LOGGER = LoggerFactory.getLogger(AbsNotifyMessageHandler.class);

    public AbsNotifyMessageHandler() {
    }

    public abstract String getTitle();

    public boolean getIsDefault() {
        return false;
    }

    public boolean getSupportHtml() {
        return true;
    }

    public boolean handlerMessage(JmsDTO<T> message, String messageId) {
        return this.sendMessage(message.getData(), messageId);
    }

    public abstract boolean sendMessage(T var1, String var2);
}