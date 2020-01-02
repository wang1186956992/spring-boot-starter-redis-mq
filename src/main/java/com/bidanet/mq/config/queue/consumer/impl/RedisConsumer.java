package com.bidanet.mq.config.queue.consumer.impl;

import com.bidanet.mq.config.queue.consumer.Consumer;
import com.bidanet.mq.config.queue.model.Message;
import com.bidanet.mq.config.queue.util.JedisUtil;
import redis.clients.jedis.JedisPool;

/**
 * @author ScienJus
 * @date 2015/12/8.
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
