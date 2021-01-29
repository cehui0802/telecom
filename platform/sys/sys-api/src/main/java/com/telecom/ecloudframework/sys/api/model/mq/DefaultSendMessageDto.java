package com.telecom.ecloudframework.sys.api.model.mq;

/**
 * @author lxy
 */
public class DefaultSendMessageDto {
    /**
     * 生产者组名称
     */
    private String productGroupName;
    /**
     * 主题
     */
    private String topic ;

    /**
     * key  唯一标识
     */
    private String key;

    /**
     * tag  标记
     */
    private String tag;

    /**
     * 消息主题
     */
    private String message;
    public DefaultSendMessageDto(){
            super();
    }

    public DefaultSendMessageDto(String topic, String key, String tag, String message, String productGroupName){
        this.key=key;
        this.tag=tag;
        this.message=message;
        this.topic=topic;
        this.productGroupName=productGroupName;
    }

    public DefaultSendMessageDto(String topic, String tag, String message, String productGroupName){
        this.key="";
        this.tag=tag;
        this.message=message;
        this.topic=topic;
        this.productGroupName=productGroupName;
    }

    public DefaultSendMessageDto(String topic, String tag, String message){
        this.key="";
        this.tag=tag;
        this.message=message;
        this.topic=topic;
        this.productGroupName="defaultProductGroupName";
    }


    public DefaultSendMessageDto(String topic, String message){
        this.message=message;
        this.topic=topic;
        this.productGroupName="defaultProductGroupName";
    }

    public String getProductGroupName() {
        return productGroupName;
    }

    public void setProductGroupName(String productGroupName) {
        this.productGroupName = productGroupName;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
