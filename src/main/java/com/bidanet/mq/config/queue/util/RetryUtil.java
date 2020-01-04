package com.bidanet.mq.config.queue.util;

/**
 * @author wanglu
 * @date 2020/1/4.
 */

public class RetryUtil {
//
//    one,two,three,four,five;

    /**
     * 重试机制 5秒  30秒   1分钟    30分钟   1 小时 ，
     * 超过五次  2小时一次
     * @param retryNum
     * @return
     */

    public static long retryTime(long retryNum){
        long currentTimeMillis = System.currentTimeMillis();

        switch ((int) retryNum){
            case 1:
                return currentTimeMillis + 5000;
            case 2:
                return currentTimeMillis + 10000;
            case 3:
                return currentTimeMillis + 30000;
            case 4:
                return currentTimeMillis + 30000;
            case 5:
                return currentTimeMillis + 30000;
            default:
                return currentTimeMillis + 30000;
        }
    }

}
