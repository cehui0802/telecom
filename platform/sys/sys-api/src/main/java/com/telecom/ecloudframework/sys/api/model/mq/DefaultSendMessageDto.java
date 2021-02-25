package com.telecom.ecloudframework.sys.api.model.mq;

public class DefaultSendMessageDto {
    private String productGroupName;
    private String topic;
    private String key;
    private String tag;
    private String message;

    public DefaultSendMessageDto() {
    }

    public DefaultSendMessageDto(String topic, String key, String tag, String message, String productGroupName) {
        this.key = key;
        this.tag = tag;
        this.message = message;
        this.topic = topic;
        this.productGroupName = productGroupName;
    }

    public DefaultSendMessageDto(String topic, String tag, String message, String productGroupName) {
        this.key = "";
        this.tag = tag;
        this.message = message;
        this.topic = topic;
        this.productGroupName = productGroupName;
    }

    public DefaultSendMessageDto(String topic, String tag, String message) {
        this.key = "";
        this.tag = tag;
        this.message = message;
        this.topic = topic;
        this.productGroupName = "defaultProductGroupName";
    }

    public DefaultSendMessageDto(String topic, String message) {
        this.message = message;
        this.topic = topic;
        this.productGroupName = "defaultProductGroupName";
    }

    public String getProductGroupName() {
        return this.productGroupName;
    }

    public void setProductGroupName(String productGroupName) {
        this.productGroupName = productGroupName;
    }

    public String getTopic() {
        return this.topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public String getKey() {
        return this.key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getTag() {
        return this.tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getMessage() {
        return this.message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
