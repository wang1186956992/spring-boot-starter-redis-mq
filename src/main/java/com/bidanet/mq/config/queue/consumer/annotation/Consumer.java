package com.bidanet.mq.config.queue.consumer.annotation;

import org.springframework.stereotype.Component;

import java.lang.annotation.*;

/**
 * @author wanglu
 * @date 2020/1/4.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Component
public @interface Consumer {
}
