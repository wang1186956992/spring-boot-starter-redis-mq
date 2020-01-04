package com.bidanet.mq.config.queue.consumer;

import com.bidanet.mq.config.queue.model.Message;

/**
 * @author wanglu
 * @date 2020/1/4.
 */
public interface Consumer {

    Message getMessage(String topic);

    void retry(String topic, Message message);

}
