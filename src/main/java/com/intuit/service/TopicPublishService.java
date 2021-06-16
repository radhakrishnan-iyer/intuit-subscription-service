package com.intuit.service;

import com.intuit.common.constant.Constants;
import com.intuit.common.model.Request;
import com.intuit.common.model.mq.EventReply;
import com.rabbitmq.client.Channel;
import org.apache.commons.lang.SerializationUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

public class TopicPublishService {

    private static Logger logger = LoggerFactory.getLogger(TopicPublishService.class);

    private final Channel topicChannel;
    private final String topicName;
    private final String routingKeyPrefix;

    public TopicPublishService(Channel topicChannel, String topicName, String routingKeyPrefix) {
        this.topicChannel = topicChannel;
        this.topicName = topicName;
        this.routingKeyPrefix = routingKeyPrefix;
    }

    public boolean publish(Request request , boolean isValid, String message) {
        EventReply event = new EventReply();
        event.setCorrelationId(MDC.get(Constants.request_id));
        event.setCustomerId(MDC.get(Constants.customer_id));
        event.setIsValid(isValid);
        event.setMessage(message);
        event.setRequest(request);
        try {
            String routingKey = routingKeyPrefix+MDC.get(Constants.request_id);
            byte[] data = SerializationUtils.serialize(event);
            topicChannel.basicPublish(topicName, routingKey, null, data);
            logger.info("Reply for request id {} published on the {} : Message published : {}" , event.getCorrelationId() , topicName , event);
            return true;
        }
        catch (Exception ex) {
            logger.error("Exception while pushing the message on " + topicName , ex);
            return false;
        }
    }
}
