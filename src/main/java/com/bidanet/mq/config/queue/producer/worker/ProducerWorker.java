package com.bidanet.mq.config.queue.producer.worker;

import com.bidanet.mq.config.queue.cost.QueueCost;
import com.bidanet.mq.config.queue.model.Message;
import com.bidanet.mq.config.queue.producer.Producer;
import com.bidanet.mq.config.queue.producer.annotation.ToQueue;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;

import java.util.UUID;

/**
 * @author wanglu
 * @date 2020/1/4.
 */

@Aspect
public class ProducerWorker {

    private Producer producer;

    public void setProducer(Producer producer) {
        this.producer = producer;
    }

    @Around("@annotation(toQueue)")
    public Object around(ProceedingJoinPoint point, ToQueue toQueue) {
        Object content = null;
        try {
            content = point.proceed();
            String topic = toQueue.topic();
            long expireAt =
                    toQueue.expire() == ToQueue.ExpireTime.NEVER_EXPIRES ?
                            ToQueue.ExpireTime.NEVER_EXPIRES :
                            System.currentTimeMillis() + toQueue.expire() * 1000;
            String uuid = QueueCost.UUID_PREFIX + UUID.randomUUID().toString();
            producer.sendMessage(topic, new Message(uuid,topic,content, expireAt));
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
        return content;
    }

}
