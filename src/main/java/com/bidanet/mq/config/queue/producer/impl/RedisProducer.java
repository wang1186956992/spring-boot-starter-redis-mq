package com.bidanet.mq.config.queue.producer.impl;

import com.bidanet.mq.config.queue.model.Message;
import com.bidanet.mq.config.queue.producer.Producer;
import com.bidanet.mq.config.queue.util.JedisUtil;
import redis.clients.jedis.JedisPool;

/**
 * @author ScienJus
 * @date 2015/12/8.
 */
public class RedisProducer implements Producer {

    private JedisPool jedisPool;

    public void setJedisPool(JedisPool jedisPool) {
        this.jedisPool = jedisPool;
    }

    public void sendMessage(String topic, Message message) {
        JedisUtil.lpush(jedisPool, topic, message);
    }

}
