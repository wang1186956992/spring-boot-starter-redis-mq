package com.bidanet.mq.config.queue.producer.worker;

import com.bidanet.mq.config.queue.consumer.Consumer;
import com.bidanet.mq.config.queue.cost.QueueCost;
import com.bidanet.mq.config.queue.model.Message;
import com.bidanet.mq.config.queue.producer.Producer;
import com.bidanet.mq.config.queue.producer.annotation.ToQueue;
import com.bidanet.mq.config.queue.util.JedisUtil;
import lombok.extern.slf4j.Slf4j;
import redis.clients.jedis.JedisPool;

/**
 * @author wanglu
 * @date 2020/1/4.
 */

@Slf4j
public class RetryProducerWorker {
    private Consumer consumer;

    private Producer producer;

    private JedisPool jedisPool;

    public Consumer getConsumer() {
        return consumer;
    }

    public void setConsumer(Consumer consumer) {
        this.consumer = consumer;
    }

    public Producer getProducer() {
        return producer;
    }

    public void setProducer(Producer producer) {
        this.producer = producer;
    }

    public JedisPool getJedisPool() {
        return jedisPool;
    }

    public void setJedisPool(JedisPool jedisPool) {
        this.jedisPool = jedisPool;
    }

    public void worker() {
        //获取消息
        Message message;
        while ((message = consumer.getMessage(QueueCost.RETRY_TOPIC)) != null) {
            String uuid = message.getUuid();
            //如果存在
            if(JedisUtil.exists(jedisPool,uuid)){
                if (message.getExpireAt() != ToQueue.ExpireTime.NEVER_EXPIRES && message.getExpireAt() < System.currentTimeMillis()) {
                    //说明这是一个过期任务，记录日志后丢弃掉
                    continue;
                }
                Message retryMessage = (Message) JedisUtil.get(jedisPool,uuid);
                if(retryMessage.getRetryTime() < System.currentTimeMillis()){
                    log.info("正在重试了->" + Thread.currentThread().getName() + "->" + message.getContent().toString());
                    //发送重试消息
                    consumer.retry(retryMessage.getTopic(),retryMessage);
                }else {
                    //从新加入重试队列
                    producer.sendMessage(QueueCost.RETRY_TOPIC,message);
                }
            }
        }
    }
}
