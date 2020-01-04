package com.bidanet.mq.config.queue.consumer.worker;


import com.bidanet.mq.config.queue.consumer.Consumer;
import com.bidanet.mq.config.queue.consumer.model.ConsumeHandlerMethod;
import com.bidanet.mq.config.queue.cost.QueueCost;
import com.bidanet.mq.config.queue.model.Message;
import com.bidanet.mq.config.queue.producer.Producer;
import com.bidanet.mq.config.queue.producer.annotation.ToQueue;
import com.bidanet.mq.config.queue.util.JedisUtil;
import com.bidanet.mq.config.queue.util.RetryUtil;
import lombok.extern.slf4j.Slf4j;
import redis.clients.jedis.JedisPool;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * @author wanglu
 * @date 2020/1/4.
 */
@Slf4j
public class ConsumeWorker {


    private JedisPool jedisPool;

    private ConsumeHandlerMethod consumeHandlerMethod;

    private Consumer consumer;

    private Producer producer;

    public ConsumeWorker(JedisPool jedisPool, ConsumeHandlerMethod consumeHandlerMethod, Consumer consumer, Producer producer) {
        this.jedisPool = jedisPool;
        this.consumeHandlerMethod = consumeHandlerMethod;
        this.consumer = consumer;
        this.producer = producer;
    }

    public void invoke() {
        Object bean = consumeHandlerMethod.getBean();
        Method method = consumeHandlerMethod.getMethod();
        String topic = consumeHandlerMethod.getTopic();
        //获取消息
        Message message;
        while ((message = consumer.getMessage(topic)) != null) {
            try {
                if (message.getExpireAt() != ToQueue.ExpireTime.NEVER_EXPIRES && message.getExpireAt() < System.currentTimeMillis()) {
                    //说明这是一个过期任务，记录日志后丢弃掉
                    log.info("过期任务=》消息数据=》：" + message.toString());
                    continue;
                }
                if (method.getReturnType().isAssignableFrom(Boolean.TYPE)) {
                    if (!((boolean) method.invoke(bean, message.getContent()))) {
                        retryRule(message);
                    }else {
                        this.finish(message);
                    }
                } else {
                    method.invoke(bean, message.getContent());
                    this.finish(message);
                }
            } catch (InvocationTargetException | IllegalAccessException e) {
                e.printStackTrace();
                retryRule(message);
            }
        }
    }

    /**
     * 如果消息执行失败，重试
     * @param message
     */
    private void retryRule(Message message){
        String uuid = message.getUuid();
        log.info("添加消息到重试队列=》消息数据=》：" + message.toString());
        long num = message.getRetryNum() + 1;
        long retryTime = RetryUtil.retryTime(num);
        message.setRetryNum(num);
        message.setRetryTime(retryTime);
        JedisUtil.set(jedisPool,uuid,message);
        producer.sendMessage(QueueCost.RETRY_TOPIC,message);
    }

    /**
     * 消息执行完成
     * @param message
     */
    private void finish(Message message){
        JedisUtil.del(jedisPool,message.getUuid());
    }
}
