package com.intuit.service;

import com.intuit.common.model.Request;
import com.rabbitmq.client.Channel;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.io.IOException;

import static com.intuit.common.constant.Constants.PRODUCT_VALIDATION_SUCCESS;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doThrow;

public class TopicPublishServiceTest {

    private TopicPublishService topicPublishService;
    private Channel topicChannel;
    private String topicName;
    private String routingKeyPrefix;

    @BeforeEach
    public void setUp() {
        topicChannel = Mockito.mock(Channel.class);
        topicName = "topicChannel";
        routingKeyPrefix = "request*";

        topicPublishService = new TopicPublishService(topicChannel , topicName, routingKeyPrefix);
    }

    @Test
    public void testPublish() {
        try {
            doAnswer(invocation -> {
                Object arg0 = invocation.getArgument(0);
                Object arg1 = invocation.getArgument(1);
                Object arg3 = invocation.getArgument(1);
                Object arg4 = invocation.getArgument(1);

                Assertions.assertEquals(topicName , arg0);
                return null;
            }).when(topicChannel).basicPublish(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any());

            Request request  = new Request();
            boolean result = topicPublishService.publish( request, true , PRODUCT_VALIDATION_SUCCESS);
            Assertions.assertTrue(result);
        }
        catch (Exception ex) {
            Assertions.fail("Code should not reach here");
        }
    }

    @Test
    public void testPublishFailure() {
        try {
            Request request  = new Request();
            doThrow(IOException.class)
                    .when(topicChannel)
                    .basicPublish(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any());
            boolean result = topicPublishService.publish( request, true , PRODUCT_VALIDATION_SUCCESS);
            Assertions.assertFalse(result);
        }
        catch (Exception ex) {
            Assertions.fail("Code should not reach here");
        }
    }
}
