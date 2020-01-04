package com.bidanet.mq.config.queue.producer.model;

import com.bidanet.mq.config.queue.producer.worker.RetryProducerWorker;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @author wanglu
 * @date 2020/1/4.
 */
@Data
@AllArgsConstructor
public class RetryTask {
    private String id;
    private String jobDetailBeanName;
    private String cron;
    private RetryProducerWorker retryProducerWorker;
}
