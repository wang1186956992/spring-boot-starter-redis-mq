package com.bidanet.mq.config.queue.consumer.impl;

import com.bidanet.mq.config.queue.consumer.Consumer;
import com.bidanet.mq.config.queue.model.Message;
import com.bidanet.mq.config.queue.util.JedisUtil;
import redis.clients.jedis.JedisPool;

/**
 * @author wanglu
 * @date 2020/1/4.
 */
public class RedisConsumer implements Consumer {

    private JedisPool jedisPool;

    public void setJedisPool(JedisPool jedisPool) {
        this.jedisPool = jedisPool;
    }

    @Override
    public Message getMessage(String topic) {
        return (Message) JedisUtil.rpop(jedisPool, topic);
    }

    @Override
    public void retry(String topic, Message message) {
        JedisUtil.lpush(jedisPool, topic, message);
    }
}
