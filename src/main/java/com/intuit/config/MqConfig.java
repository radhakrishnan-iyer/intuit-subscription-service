package com.intuit.config;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MqConfig {

    @Value("${rabbit.mq.host:localhost}")
    private String rabbitMqHost;

    @Value("${rabbit.mq.port:5672}")
    private int rabbitMqPort;

    @Value("${rabbit.mq.user:guess}")
    private String rabbitMqUser;

    @Value("${rabbit.mq.password:guess}")
    private String rabbitMqPassword;

    @Value("${rabbit.mq.request.queue:new_request_queue}")
    private String queueName;

    @Value("${rabbit.mq.reply.topic:reply_topic}")
    private String replyTopic;

    @Value("${routing.key.prefix:reply.}")
    private String routingKeyPrefix;

    public String getQueueName() {
        return queueName;
    }

    public String getReplyTopic() {
        return replyTopic;
    }

    public String getRoutingKeyPrefix() {
        return routingKeyPrefix;
    }

    @Bean
    public ConnectionFactory connectionFactory() {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost(rabbitMqHost);
        factory.setUsername(rabbitMqUser);
        factory.setPassword(rabbitMqPassword);
        factory.setPort(rabbitMqPort);
        return factory;
    }

    @Bean
    public Connection mqConnection() throws Exception {
        return connectionFactory().newConnection();
    }

    @Bean
    public Channel channel() throws Exception {
        return mqConnection().createChannel();
    }

    @Bean
    public Channel topicChannel() throws Exception {
        Channel topicChannel = mqConnection().createChannel();
        topicChannel.exchangeDeclare(replyTopic, "topic");
        return topicChannel;
    }
}
