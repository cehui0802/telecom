package com.telecom.ecloudframework.sys.simplemq.handle.biz;


import com.telecom.ecloudframework.sys.api.jms.JmsHandler;
import java.io.Serializable;

public abstract class AbsBizMessageHandler<T extends Serializable> implements JmsHandler<T> {
    public AbsBizMessageHandler() {
    }
}
