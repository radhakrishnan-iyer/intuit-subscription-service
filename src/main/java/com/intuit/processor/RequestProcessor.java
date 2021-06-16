package com.intuit.processor;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.intuit.common.constant.Constants;
import com.intuit.common.model.Request;
import com.intuit.common.model.mq.Event;
import com.intuit.dao.ISubscriptionDao;
import com.intuit.service.TopicPublishService;
import com.intuit.service.ValidationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

import static com.intuit.common.constant.Constants.PRODUCT_VALIDATION_FAILURE;
import static com.intuit.common.constant.Constants.PRODUCT_VALIDATION_SUCCESS;

public class RequestProcessor implements IRequestProcessor {
    private static Logger logger = LoggerFactory.getLogger(RequestProcessor.class);

    private final TopicPublishService topicPublishService;
    private final ISubscriptionDao subscriptionDao;
    private final ValidationService validationService;
    private final ExecutorService executorService;

    public RequestProcessor(TopicPublishService topicPublishService, ISubscriptionDao subscriptionDao, ValidationService validationService , ExecutorService executorService) {
        this.topicPublishService = topicPublishService;
        this.subscriptionDao = subscriptionDao;
        this.validationService = validationService;
        this.executorService =  executorService;
    }

    public RequestProcessor(TopicPublishService topicPublishService, ISubscriptionDao subscriptionDao, ValidationService validationService) {
        this(topicPublishService, subscriptionDao, validationService, 10);
    }

    public RequestProcessor(TopicPublishService topicPublishService, ISubscriptionDao subscriptionDao, ValidationService validationService, int threadCount) {
        this(topicPublishService,subscriptionDao,
                validationService,  Executors.newFixedThreadPool(threadCount , new ThreadFactoryBuilder().setNameFormat("Validation-Service-%d").build()));
    }

    @Override
    public boolean process(Event event) {
        MDC.clear();
        MDC.put(Constants.request_id , event.getCorrelationId());
        MDC.put(Constants.customer_id , event.getCustomerId());
        logger.info("Received Event with correlationId {}" ,event.getCorrelationId());

        final Request request = event.getRequest();

        List<String> subscription = subscriptionDao.getSubscriptions(event.getCustomerId());

        boolean result = false;
        StringBuilder sb = new StringBuilder();
        if(subscription.size()>0) {
            logger.info("Subscriptions received {}", subscription);
            result = validateRequestAgainstSubscribedProducts(event, subscription);
            sb.append(result ? PRODUCT_VALIDATION_SUCCESS : PRODUCT_VALIDATION_FAILURE);
        }
        else {
            sb.append(Constants.PRODUCT_VALIDATION_NO_SUBSCRIPTION);
        }

        return topicPublishService.publish(event.getRequest() , result , sb.toString());
    }

    private boolean validateRequestAgainstSubscribedProducts(Event event , List<String> subscription) {
        List<CompletableFuture<Boolean>> completableFutureList = subscription
                .stream()
                .map(element -> isValidRequest(element , event))
                .collect(Collectors.toList());

        CompletableFuture<Void> allFutures = CompletableFuture.allOf(completableFutureList.toArray(new CompletableFuture[completableFutureList.size()]));

        try {
            List<Boolean> resultList = completableFutureList.stream()
                            .map(future -> future.join())
                            .collect(Collectors.toList());

            boolean result = resultList.stream().allMatch(e -> e == true);
            if(result) {
                logger.info(PRODUCT_VALIDATION_SUCCESS);

            } else {
                logger.info(PRODUCT_VALIDATION_FAILURE);
            }
            return result;
        }
        catch (Exception ex) {
            logger.error("Exception while validating the request {}" , event.getRequest().getCorrelationId());
            return false;
        }
    }

    private CompletableFuture<Boolean> isValidRequest(String endpoint , Event event) {
        return CompletableFuture.supplyAsync( () -> validationService.process(event , endpoint) , executorService);
    }
}
