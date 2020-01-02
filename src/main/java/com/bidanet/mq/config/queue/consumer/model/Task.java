package com.bidanet.mq.config.queue.consumer.model;

import com.bidanet.mq.config.queue.consumer.worker.ConsumeWorker;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Task {
    private String id;
    private String jobDetailBeanName;

    private String cron;

    private ConsumeWorker worker;


}
