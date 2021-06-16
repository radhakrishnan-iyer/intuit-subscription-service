package com.intuit.config;

import com.intuit.common.config.WebConfig;
import com.intuit.dao.ISubscriptionDao;
import com.intuit.mq.listener.MessageListener;
import com.intuit.mq.listener.SubcriptionService;
import com.intuit.service.TopicPublishService;
import com.intuit.processor.IRequestProcessor;
import com.intuit.processor.RequestProcessor;
import com.intuit.service.ValidationService;
import com.rabbitmq.client.Consumer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.web.client.RestTemplate;

@Configuration
@Import({WebConfig.class,MqConfig.class,DBConfig.class,HttpConfig.class})
public class AppConfig {

    @Value("${product.service.url}")
    private String productServiceUrl;

    @Value("${validation.service.thread.pool.size:10}")
    private int validationServiceThreadPoolSize;

    @Value("${request.processor.thread.pool.size:20}")
    private int requestProcessThreadPoolSize;

    @Autowired
    private MqConfig mqConfig;

    @Autowired
    private ISubscriptionDao subscriptionDao;

    @Autowired
    private RestTemplate restTemplate;

    @Bean
    public ValidationService validationService() throws Exception {
        ValidationService validationService = new ValidationService(restTemplate , productServiceUrl);
        return validationService;
    }

    @Bean
    public IRequestProcessor requestProcessor() throws Exception {
        RequestProcessor requestProcessor = new RequestProcessor(topicPublishService(),subscriptionDao , validationService() , validationServiceThreadPoolSize);
        return requestProcessor;
    }

    @Bean
    public Consumer messageListener() throws Exception {
        MessageListener messageListener = new MessageListener(mqConfig.channel(), requestProcessor() , requestProcessThreadPoolSize);
        return messageListener;
    }

    @Bean(initMethod = "init")
    public SubcriptionService subcriptionService() throws Exception {
        SubcriptionService subcriptionService = new SubcriptionService(mqConfig.channel(),mqConfig.getQueueName() , messageListener());
        return subcriptionService;
    }

    @Bean
    public TopicPublishService topicPublishService() throws Exception {
        TopicPublishService topicPublishService = new TopicPublishService(mqConfig.topicChannel() ,mqConfig.getReplyTopic() ,mqConfig.getRoutingKeyPrefix());
        return topicPublishService;
    }
}