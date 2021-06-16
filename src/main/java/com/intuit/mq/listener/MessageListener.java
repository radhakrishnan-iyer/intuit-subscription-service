package com.intuit.mq.listener;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.intuit.common.model.mq.Event;
import com.intuit.processor.IRequestProcessor;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;
import org.apache.commons.lang.SerializationUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MessageListener extends DefaultConsumer {
    private static Logger logger = LoggerFactory.getLogger(MessageListener.class);

    private final IRequestProcessor requestProcessor;
    private final ExecutorService executorService;

    public MessageListener(Channel channel,IRequestProcessor requestProcessor) {
        this(channel , requestProcessor , 20);
    }

    public MessageListener(Channel channel,IRequestProcessor requestProcessor, int threadCount) {
        super(channel);
        this.requestProcessor = requestProcessor;
        executorService = Executors.newFixedThreadPool(threadCount , new ThreadFactoryBuilder().setNameFormat("New-Request-Processor-%d").build());
    }

    @Override
    public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body)
            throws IOException {
        Object object = SerializationUtils.deserialize(body);
        if(object instanceof Event) {
            Event event = (Event) object;
            executorService.execute(() -> requestProcessor.process(event));
        } else {
            logger.error("Unable to parse the message.... Ignoring the message {}" , object);
        }
    }

}
