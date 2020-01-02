package com.bidanet.mq.config.queue.consumer;

import com.bidanet.mq.config.queue.model.Message;

/**
 * @author ScienJus
 * @date 2015/12/8.
 */
public interface Consumer {

    Message getMessage(String topic);

    void retry(String topic, Message message);

}
