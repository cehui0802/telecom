package com.telecom.ecloudframework.sys.simplemq.producer;


import com.telecom.ecloudframework.sys.api.jms.JmsConfig;
import com.telecom.ecloudframework.sys.api.jms.model.JmsDTO;
import com.telecom.ecloudframework.sys.api.jms.producer.JmsProducer;
import cn.hutool.core.collection.CollectionUtil;
import com.alibaba.fastjson.JSON;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;

public class RedisMessageQueueProducer implements JmsProducer {
    private static final Logger LOGGER = LoggerFactory.getLogger(RedisMessageQueueProducer.class);
    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private JmsConfig jmsConfig;

    public RedisMessageQueueProducer() {
    }

    public void sendToQueue(JmsDTO message) {
        if (message == null) {
            LOGGER.info("传入参数为空, 跳过执行");
        } else {
            LOGGER.debug(JSON.toJSONString(message));
            this.redisTemplate.boundListOps(this.jmsConfig.getName()).rightPush(message);
        }
    }

    public void sendToQueue(List<JmsDTO> messages) {
        if (CollectionUtil.isEmpty(messages)) {
            LOGGER.info("传入参数为空, 跳过执行");
        } else {
            LOGGER.debug(JSON.toJSONString(messages));
            this.redisTemplate.boundListOps(this.jmsConfig.getName()).rightPushAll(messages.toArray());
        }
    }
}
