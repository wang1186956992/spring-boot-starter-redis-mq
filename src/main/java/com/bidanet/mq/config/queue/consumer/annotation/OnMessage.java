package com.bidanet.mq.config.queue.consumer.annotation;

import java.lang.annotation.*;

/**
 * @author wanglu
 * @date 2020/1/4.
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface OnMessage {

    String topic();
}
