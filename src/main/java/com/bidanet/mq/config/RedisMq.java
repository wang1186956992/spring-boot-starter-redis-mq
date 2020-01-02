package com.bidanet.mq.config;



import com.bidanet.mq.config.queue.consumer.Consumer;
import com.bidanet.mq.config.queue.consumer.impl.RedisConsumer;
import com.bidanet.mq.config.queue.producer.Producer;
import com.bidanet.mq.config.queue.producer.impl.RedisProducer;
import com.bidanet.mq.config.queue.producer.worker.ProducerWorker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import redis.clients.jedis.JedisPool;

@Configuration
public class RedisMq {

    @Autowired
    JedisPool jedisPool;

    @Bean
    public Consumer consumer() {
        RedisConsumer consumer = new RedisConsumer();
        consumer.setJedisPool(jedisPool);
        return consumer;
    }


    @Bean
    public Producer producer() {
        RedisProducer producer = new RedisProducer();
        producer.setJedisPool(jedisPool);
        return producer;
    }


    @Bean
    public ProducerWorker producerWorker() {
        ProducerWorker producerWorker = new ProducerWorker();
        producerWorker.setProducer(producer());
        return producerWorker;
    }


}
