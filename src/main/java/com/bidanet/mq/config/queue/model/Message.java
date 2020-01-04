package com.bidanet.mq.config.queue.model;

import com.bidanet.mq.config.queue.producer.annotation.ToQueue;

import java.io.Serializable;

/**
 * @author wanglu
 * @date 2020/1/4.
 */
public class Message implements Serializable {

    private String uuid;

    /**
     * 当前消息主题
     */
    private String topic;

    private Object content;

    /**
     * 消息重试次数
     */
    private long retryNum;

    /**
     * 重试时间段
     */
    private long retryTime;


    private long expireAt;

    public Message(String uuid,String topic,Object content, long expireAt) {
        this.uuid = uuid;
        this.topic = topic;
        this.content = content;
        this.expireAt = expireAt;
    }

    public Message(String uuid,String topic,Object content) {
        this(uuid,topic,content, ToQueue.ExpireTime.NEVER_EXPIRES);
    }

    public Object getContent() {
        return content;
    }

    public void setContent(Object content) {
        this.content = content;
    }

    public long getExpireAt() {
        return expireAt;
    }

    public void setExpireAt(long expireAt) {
        this.expireAt = expireAt;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public long getRetryNum() {
        return retryNum;
    }

    public void setRetryNum(long retryNum) {
        this.retryNum = retryNum;
    }

    public long getRetryTime() {
        return retryTime;
    }

    public void setRetryTime(long retryTime) {
        this.retryTime = retryTime;
    }

    @Override
    public String toString() {
        return "Message{" +
                "uuid='" + uuid + '\'' +
                ", topic='" + topic + '\'' +
                ", content=" + content +
                ", retryNum=" + retryNum +
                ", retryTime=" + retryTime +
                ", expireAt=" + expireAt +
                '}';
    }
}
