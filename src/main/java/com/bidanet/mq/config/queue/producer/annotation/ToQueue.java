package com.bidanet.mq.config.queue.producer.annotation;

import java.lang.annotation.*;

/**
 * @author wanglu
 * @date 2020/1/4.
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ToQueue {

    String topic();

    int expire() default ExpireTime.NEVER_EXPIRES;

    class ExpireTime {
        public static final int NEVER_EXPIRES = -1;
    }
}
