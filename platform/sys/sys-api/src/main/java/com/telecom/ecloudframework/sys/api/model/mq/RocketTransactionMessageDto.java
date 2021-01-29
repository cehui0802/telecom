package com.telecom.ecloudframework.sys.api.model.mq;

/**
 * 事务消息dto
 * @author lxy
 */

public class RocketTransactionMessageDto {
    /**
     * 主题
     */
    private String topic;
    /**
     * 标记
     */
    private String tag;
    /**
     * key
     */
    private String key;
    /**
     * 消息体
     */
    private String msg;

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
