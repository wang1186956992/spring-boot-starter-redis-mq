package com.bidanet.mq.config.queue.util;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

/**
 * @author wanglu
 * @date 2020/1/4.
 */
public class JedisUtil {

    public static void lpush(JedisPool jedisPool, String key, Object value) {
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            jedis.lpush(key.getBytes(), SerializeUtil.serialize(value));
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
    }

    public static Object rpop(JedisPool jedisPool, String key) {
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            return SerializeUtil.unserialize(jedis.rpop(key.getBytes()));
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
    }


    public static boolean exists(JedisPool jedisPool, String key) {
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            return jedis.exists(key);
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
    }

    public static void set(JedisPool jedisPool, String key, Object value) {

        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            jedis = jedisPool.getResource();
            jedis.set(key.getBytes(), SerializeUtil.serialize(value));
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
    }

    public static Object get(JedisPool jedisPool, String key) {
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            return SerializeUtil.unserialize(jedis.get(key.getBytes()));
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
    }

    public static void del(JedisPool jedisPool, String key) {
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            jedis.del(key.getBytes());
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
    }
}
