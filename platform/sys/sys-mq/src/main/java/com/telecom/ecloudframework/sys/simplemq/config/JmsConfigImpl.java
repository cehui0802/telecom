package com.telecom.ecloudframework.sys.simplemq.config;


import com.telecom.ecloudframework.sys.api.jms.JmsConfig;
import org.springframework.stereotype.Component;

@Component
public class JmsConfigImpl implements JmsConfig {
    String name;

    public JmsConfigImpl() {
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }
}
