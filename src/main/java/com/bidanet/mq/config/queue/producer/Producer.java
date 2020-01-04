package com.bidanet.mq.config.queue.producer;

import com.bidanet.mq.config.queue.model.Message;

/**
 * @author wanglu
 * @date 2020/1/4.
 */
public interface Producer {

    void sendMessage(String topic, Message message);
}
