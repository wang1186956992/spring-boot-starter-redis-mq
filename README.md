# spring-boot-starter-redis-mq
spring-boot-starter-redis-mq




使用前保证需要注入

    @Autowired
    JedisPool jedisPool;


```
package com.bidanet.supermarket.config.redis;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

@Configuration
public class RedisCacheConfiguration {

    @Autowired
    RedisProperties redisProperties;

    @Bean
    public JedisPool redisPoolFactory() {
        JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
        jedisPoolConfig.setMaxTotal(redisProperties.getPool().getMaxActive());
        jedisPoolConfig.setMaxIdle(redisProperties.getPool().getMaxIdle());
        jedisPoolConfig.setMinIdle(redisProperties.getPool().getMinIdle());
        JedisPool jedisPool = new JedisPool(jedisPoolConfig,
                redisProperties.getHost(),
                redisProperties.getPort(),
                redisProperties.getTimeout(),
                redisProperties.getPassword(),
                redisProperties.getDatabase());
        return jedisPool;
    }
    
    
    spring:
      redis:
        database: 20
        host: xxxxx
        port: 6379
        password: xxx
        pool:
          max-active: 600
          max-idle: 10
          min-idle: 5
        timeout: 3000

}



```

**创建生产者实例**

方法1：注入`producer`，调用`sendMessage`方法：

```
@Component
public class SayHelloProducer {

    @Autowired
    private Producer producer;

    public void sayHello(String name) {
        producer.sendMessage("say_hello", new Message(name));
    }
}
```

方法2：使用`@ToQueue`注解，`retrun`需要发送的对象（需要配置`producerWorker`）：

```
@Producer
public class UserServiceImpl implements UserService {

    @Autowired
    private UserDao userDao;

    @ToQueue(topic = "new_user")  //添加新的用户后，将其发送到消息队列
    public User insert(User user) {
        this.userDao.insert(user);
        return user;
    }
}
```

**创建消费者实例**

方法1：注入`consumer`，调用`getMessage`方法（需要自己开线程循环获取）：

```
@Component
public class SayHelloConsumer {

    @Autowired
    private Consumer consumer;

    public void sayHello() {
        Message message;
        while ((message = consumer.getMessage("say_hello")) != null) {
            System.out.println("Hello ! " + message.getContent() + " !");
        }
    }
}
```

方法2：为类添加`@Consumer`注解，为对应的方法添加`@OnMessage`注解（需要配置`schedulerBeanFactory`）：

```
@Consumer
public class SayHelloConsumer {

    @OnMessage(topic = "say_hello")
    public void onSayHello(String name) {
        System.out.println("Hello ! " + name + " !");
    }
}
```

**消费者的重试机制**

当`@OnMessage`方法的返回值类型为`boolean`类型，并且执行的结果为`false`时，系统认定此消息执行失败。

消息执行失败后，系统会将这个消息重新插入到消息队列中（顺序排在最后）。

通过`@ToQueue`的`expire`属性可以设置消息的生存时间（单位为秒），默认为永不过期。

当消息的生存时间超过后还没有消费成功，系统将会丢掉这个消息。

一个简单的例子：

```
//生产者

@Producer
public class UserServiceImpl implements UserService {

    @Autowired
    private UserDao userDao;

    @ToQueue(topic = "new_user", expire = 24 * 3600)  //添加新的用户后，将其发送到消息队列，消息的生存时间是24小时
    public User insert(User user) {
        this.userDao.insert(user);
        return user;
    }
}

@Consumer
public class NewUserConsumer {

    @OnMessage(value = "new_user")  //如果邮件发送失败，需要尝试重新发送。
    public boolean onNewUser(User user) {
        try {
            //发送邮件
            MailSender.sendWelcomeMail(user.getEmail(), user.getNickname());
            //发送成功，任务完成，返回true
            retrun true;
        } catch (Exception e) {
            //发送失败，尝试重试，返回false
            retrun false;
        }
    }
}

目前代码里写死了重试时间
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




```

当然，如果一个消费方法永远不会失败（或是失败后不需要重试），可以直接设置为`void`方法。

PS：之前版本使用重试次数控制失败处理。但是系统修复需要的是时间，而重试次数很有可能会被浪费掉，因此改为了生存时间。



### 帮助

参考
https://github.com/ScienJus/spring-redis-mq


由于 Redis 本身的限制，这个项目并不适合使用在生产环境中，在此推荐 Redis 作者开发的消息队列 Disque。一些介绍：

 - 该项目的地址：[Disque, an in-memory, distributed job queue][1]
 - 该项目的中文介绍：[Disque 使用教程][2]
 - Java 的客户端实现（Jedis 的作者开发）：[Jedisque][3]
 - 与这个项目用法相同的Disque实现：[Spring Dsique][4]

[1]: https://github.com/antirez/disque
[2]: http://disquebook.com/
[3]: https://github.com/xetorthio/jedisque
[4]: https://github.com/scienjus/spring-disque
