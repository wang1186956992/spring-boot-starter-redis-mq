package com.bidanet.mq.config.queue.consumer.config;

import com.bidanet.mq.config.queue.consumer.Consumer;
import com.bidanet.mq.config.queue.consumer.annotation.OnMessage;
import com.bidanet.mq.config.queue.consumer.model.ConsumeHandlerMethod;
import com.bidanet.mq.config.queue.consumer.model.Task;
import com.bidanet.mq.config.queue.consumer.worker.ConsumeWorker;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.Trigger;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;
import org.springframework.scheduling.support.CronTrigger;
import redis.clients.jedis.JedisPool;

import javax.annotation.PreDestroy;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Configuration
@Slf4j
public class DynamicSchedule implements SchedulingConfigurer {

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    private Consumer consumer;


    /**
     * 测试数据，实际可从数据库获取
     */
    private List<Task> tasks = new ArrayList<>();

    private void init() {
        //获得所有消费者Bean
        Map<String, Object> beans = applicationContext.getBeansWithAnnotation(com.bidanet.mq.config.queue.consumer.annotation.Consumer.class);
        //获得Bean Factory
        ConfigurableApplicationContext configurableApplicationContext = (ConfigurableApplicationContext) applicationContext;
        DefaultListableBeanFactory beanFactory = (DefaultListableBeanFactory) configurableApplicationContext.getBeanFactory();
        for (Map.Entry<String, Object> entry : beans.entrySet()) {
            String name = entry.getKey();
            Object bean = entry.getValue();
            Class clazz = applicationContext.getType(name);
            for (Method method : clazz.getMethods()) {
                if (method.isAnnotationPresent(OnMessage.class)) {
                    OnMessage onMessage = method.getAnnotation(OnMessage.class);
                    ConsumeHandlerMethod consumeHandlerMethod = new ConsumeHandlerMethod(onMessage.topic(), method, bean);
                    ConsumeWorker worker = new ConsumeWorker(consumeHandlerMethod, consumer);
                    //注册JobDetail
                    String jobDetailBeanName = buildJobDetailBeanName(consumeHandlerMethod);
                    tasks.add(new Task(UUID.randomUUID().toString(), jobDetailBeanName, "*/30 * * * * *", worker));
                }
            }
        }
    }


    @PreDestroy
    public void destroy() {
        System.out.println("destroy.........");

    }

    private String buildJobDetailBeanName(ConsumeHandlerMethod method) {
        return method.getTopic().concat("JobDetail");
    }


    @Override
    public void configureTasks(ScheduledTaskRegistrar scheduledTaskRegistrar) {
        init();
        tasks.forEach(task -> {
            //任务执行线程
            Runnable runnable = () -> {
                log.info("execute task {}", task.getId());
                task.getWorker().invoke();
            };

            //任务触发器
            Trigger trigger = triggerContext -> {
                //获取定时触发器，这里可以每次从数据库获取最新记录，更新触发器，实现定时间隔的动态调整
                CronTrigger cronTrigger = new CronTrigger(task.getCron());
                return cronTrigger.nextExecutionTime(triggerContext);
            };

            //注册任务
            scheduledTaskRegistrar.addTriggerTask(runnable, trigger);
        });

    }
}
