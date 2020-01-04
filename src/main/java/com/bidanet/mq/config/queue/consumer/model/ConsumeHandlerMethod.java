package com.bidanet.mq.config.queue.consumer.model;

import java.lang.reflect.Method;

/**
 * @author wanglu
 * @date 2020/1/4.
 */
public class ConsumeHandlerMethod {

    private String topic;

    private Method method;

    private Object bean;

    public ConsumeHandlerMethod(String topic, Method method, Object bean) {
        this.topic = topic;
        this.method = method;
        this.bean = bean;
    }

    public String getTopic() {
        return topic;
    }

    public Method getMethod() {
        return method;
    }

    public Object getBean() {
        return bean;
    }
}
