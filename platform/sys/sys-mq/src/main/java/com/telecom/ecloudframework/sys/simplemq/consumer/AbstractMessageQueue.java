package com.telecom.ecloudframework.sys.simplemq.consumer;


import com.telecom.ecloudframework.sys.api.jms.JmsHandler;
import com.telecom.ecloudframework.sys.api.jms.model.JmsDTO;
import cn.hutool.core.map.MapUtil;
import com.alibaba.fastjson.JSON;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;

public abstract class AbstractMessageQueue implements ApplicationListener<ContextRefreshedEvent> {
    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractMessageQueue.class);
    private ApplicationContext applicationContext;
    private Map<String, JmsHandler> registerJmsHandler = Collections.emptyMap();

    public AbstractMessageQueue() {
    }

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        if (event.getApplicationContext().getParent() == null || "bootstrap".equals(event.getApplicationContext().getParent().getId())) {
            LOGGER.debug("准备加载JmsHandler实现集");
            this.applicationContext = event.getApplicationContext();
            Map<String, JmsHandler> jmsHandlerMap = this.applicationContext.getBeansOfType(JmsHandler.class);
            LOGGER.debug("加载JmsHandler实现共{}条", jmsHandlerMap.size());
            if (MapUtil.isNotEmpty(jmsHandlerMap)) {
                Collection<JmsHandler> jmsHandlers = jmsHandlerMap.values();
                this.registerJmsHandler = new HashMap(jmsHandlers.size(), 1.0F);
                Iterator var4 = jmsHandlers.iterator();

                while(var4.hasNext()) {
                    JmsHandler jmsHandler = (JmsHandler)var4.next();
                    this.registerJmsHandler.put(jmsHandler.getType(), jmsHandler);
                }
            }

            this.containerInitialCompleteAfter();
        }

    }

    protected void containerInitialCompleteAfter() {
    }

    protected ApplicationContext getApplicationContext() {
        return this.applicationContext;
    }

    public void handleMessage(JmsDTO<?> jmsDTO, String messageId) {
        JmsHandler jmsHandler = this.getJmsHandler(jmsDTO.getType());
        if (jmsHandler == null) {
            LOGGER.warn("未找到({})消息处理器", jmsDTO.getType());
        } else {
            try {
                jmsHandler.handlerMessage(jmsDTO, messageId);
            } catch (Exception var5) {
                LOGGER.error("处理消息({})出错, 参数入参：{}", new Object[]{jmsDTO.getType(), JSON.toJSONString(jmsDTO), var5});
            }

        }
    }

    protected JmsHandler getJmsHandler(String type) {
        return (JmsHandler)this.registerJmsHandler.get(type);
    }
}
