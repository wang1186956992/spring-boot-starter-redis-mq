package com.bidanet.mq.config;



import com.bidanet.mq.config.queue.consumer.Consumer;
import com.bidanet.mq.config.queue.consumer.impl.RedisConsumer;
import com.bidanet.mq.config.queue.producer.Producer;
import com.bidanet.mq.config.queue.producer.impl.RedisProducer;
import com.bidanet.mq.config.queue.producer.worker.ProducerWorker;
import com.bidanet.mq.config.queue.producer.worker.RetryProducerWorker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import redis.clients.jedis.JedisPool;

import javax.annotation.PostConstruct;

@Configuration
@ConditionalOnBean({JedisPool.class,ApplicationContext.class})          //条件1
@ConditionalOnProperty(                                                 //条件2
        name = {"spring.redis.mq"},
        havingValue = "true",
        matchIfMissing = true
)
public class RedisMq {

    @Autowired(required = false)
    private ApplicationContext applicationContext;

    private static JedisPool jedisPool;

    @PostConstruct
    public void init() {
        jedisPool = applicationContext.getBean(JedisPool.class);
    }



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

    @Bean
    public RetryProducerWorker retryProducerWorker(){
        RetryProducerWorker retryProducerWorker = new RetryProducerWorker();
        retryProducerWorker.setConsumer(consumer());
        retryProducerWorker.setProducer(producer());
        retryProducerWorker.setJedisPool(jedisPool);
        return retryProducerWorker;
    }


}
