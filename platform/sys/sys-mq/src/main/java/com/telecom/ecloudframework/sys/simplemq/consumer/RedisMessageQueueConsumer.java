package com.telecom.ecloudframework.sys.simplemq.consumer;


import com.telecom.ecloudframework.sys.api.jms.JmsConfig;
import com.telecom.ecloudframework.sys.api.jms.JmsHandler;
import com.telecom.ecloudframework.sys.api.jms.model.JmsDTO;
import java.util.concurrent.TimeUnit;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundListOperations;
import org.springframework.data.redis.core.RedisTemplate;

public class RedisMessageQueueConsumer extends AbstractMessageQueue implements DisposableBean {
    private static final Logger LOGGER = LoggerFactory.getLogger(RedisMessageQueueConsumer.class);
    @Autowired
    private JmsConfig jmsConfig;
    private String redisTemplateBeanName;
    private int listenThreadSize = 1;
    private long listenInterval = 1000L;
    private BoundListOperations<String, Object> messageQueue;
    private ThreadGroup threadGroup;

    public RedisMessageQueueConsumer() {
    }

    public void setRedisTemplateBeanName(String redisTemplateBeanName) {
        this.redisTemplateBeanName = redisTemplateBeanName;
    }

    public void setListenInterval(long listenInterval) {
        this.listenInterval = listenInterval;
    }

    public void setListenThreadSize(int listenThreadSize) {
        this.listenThreadSize = listenThreadSize;
    }

    public void destroy() throws Exception {
        LOGGER.debug("准备关闭Redis消息队列消息消费端");
        this.threadGroup.interrupt();
        LOGGER.debug("关闭Redis消息队列消息消费完毕");
    }

    @Override
    protected void containerInitialCompleteAfter() {
        LOGGER.debug("初始化Redis消息队列处理");
        if (StringUtils.isEmpty(this.redisTemplateBeanName)) {
            this.messageQueue = ((RedisTemplate)this.getApplicationContext().getBean(RedisTemplate.class)).boundListOps(this.jmsConfig.getName());
        } else {
            this.messageQueue = ((RedisTemplate)this.getApplicationContext().getBean(this.redisTemplateBeanName, RedisTemplate.class)).boundListOps(this.jmsConfig.getName());
        }

        RedisMessageQueueConsumer.RedisQueueListener redisQueueListener = new RedisMessageQueueConsumer.RedisQueueListener();
        ThreadGroup threadGroup = new ThreadGroup(String.format("redis-queue-%s-listen", this.jmsConfig.getName()));
        threadGroup.setDaemon(true);

        for(int i = 0; i < this.listenThreadSize; ++i) {
            (new Thread(threadGroup, redisQueueListener)).start();
        }

        this.threadGroup = threadGroup;
    }

    private class RedisQueueListener implements Runnable {
        private RedisQueueListener() {
        }

        public void run() {
            while(!Thread.interrupted()) {
                RedisMessageQueueConsumer.LOGGER.trace("监听Redis消息队列({})返回结果......", RedisMessageQueueConsumer.this.jmsConfig.getName());

                Object jmsDTO;
                try {
                    jmsDTO = RedisMessageQueueConsumer.this.messageQueue.leftPop(RedisMessageQueueConsumer.this.listenInterval, TimeUnit.MILLISECONDS);
                } catch (Exception var4) {
                    RedisMessageQueueConsumer.LOGGER.warn("监听Redis消息队列({})返回结果出错", RedisMessageQueueConsumer.this.jmsConfig.getName(), var4);
                    continue;
                }

                if (jmsDTO != null) {
                    if (!(jmsDTO instanceof JmsDTO)) {
                        RedisMessageQueueConsumer.LOGGER.warn("Redis消息队列({})返回数据类型({})非[com.dstz.sys.api.jms.model.JmsDTO]", RedisMessageQueueConsumer.this.jmsConfig.getName(), jmsDTO.getClass());
                    } else {
                        JmsDTO data = (JmsDTO)jmsDTO;
                        JmsHandler jmsHandler = RedisMessageQueueConsumer.this.getJmsHandler(data.getType());
                        jmsHandler.handlerMessage(data, (String)null);
                    }
                }
            }

            RedisMessageQueueConsumer.LOGGER.info("退出Redis消息队列({})监听", RedisMessageQueueConsumer.this.jmsConfig.getName());
        }
    }
}
