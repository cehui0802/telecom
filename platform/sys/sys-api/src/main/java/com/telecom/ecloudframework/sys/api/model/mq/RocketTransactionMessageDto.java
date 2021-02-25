package com.telecom.ecloudframework.sys.api.model.mq;


public class RocketTransactionMessageDto {
    private String topic;
    private String tag;
    private String key;
    private String msg;

    public RocketTransactionMessageDto() {
    }

    public String getTopic() {
        return this.topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public String getTag() {
        return this.tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getKey() {
        return this.key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getMsg() {
        return this.msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
