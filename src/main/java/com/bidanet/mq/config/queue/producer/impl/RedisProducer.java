package com.bidanet.mq.config.queue.producer.impl;

import com.bidanet.mq.config.queue.model.Message;
import com.bidanet.mq.config.queue.producer.Producer;
import com.bidanet.mq.config.queue.util.JedisUtil;
import redis.clients.jedis.JedisPool;

/**
 * @author wanglu
 * @date 2020/1/4.
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
