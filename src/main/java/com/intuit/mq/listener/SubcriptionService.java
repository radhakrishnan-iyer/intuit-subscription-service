package com.intuit.mq.listener;

import com.rabbitmq.client.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SubcriptionService {
    private static Logger logger = LoggerFactory.getLogger(SubcriptionService.class);

    private final Channel channel;
    private final String queueName;
    private final Consumer consumer;

    public SubcriptionService(Channel channel, String queueName , Consumer consumer) {
        this.channel = channel;
        this.queueName = queueName;
        this.consumer = consumer;
    }

    public void init() {
        try {
            channel.queueDeclare(queueName, false, false, false, null);
            channel.basicConsume(queueName, true, consumer);
        }
        catch (Exception ex) {
            logger.error("Exception while registering listener with the queue", ex);
        }
    }
}
