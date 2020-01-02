package com.bidanet.mq.config.queue.producer;

import com.bidanet.mq.config.queue.model.Message;

/**
 * @author ScienJus
 * @date 2015/12/8.
 */
public interface Producer {

    void sendMessage(String topic, Message message);
}
