package com.intuit.processor;

import com.intuit.common.model.Request;
import com.intuit.common.model.mq.Event;
import com.intuit.dao.ISubscriptionDao;
import com.intuit.service.TopicPublishService;
import com.intuit.service.ValidationService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.*;

import static com.intuit.common.constant.Constants.*;

public class RequestProcessorTest {
    private RequestProcessor requestProcessor;
    private TopicPublishService topicPublishService;
    private ISubscriptionDao subscriptionDao;
    private ValidationService validationService;
    private ExecutorService executorService;

    @BeforeEach
    public void setUp() {
        topicPublishService = Mockito.mock(TopicPublishService.class);
        subscriptionDao = Mockito.mock(ISubscriptionDao.class);
        validationService = Mockito.mock(ValidationService.class);
        executorService = new CurrentThreadExecutor();
        requestProcessor = new RequestProcessor(topicPublishService, subscriptionDao, validationService, executorService);
    }

    @Test
    public void testProcessSuccess() {
        Event event = new Event();
        event.setCorrelationId("corrId1");
        event.setCustomerId("customerId");
        Request request  = new Request();

        List<String> subscriptions = new ArrayList<>();
        subscriptions.add("product1");
        subscriptions.add("product2");
        Mockito.when(subscriptionDao.getSubscriptions(Mockito.any())).thenReturn(subscriptions);
        Mockito.when(validationService.process(Mockito.any(), Mockito.any())).thenReturn(true);
        Mockito.when(topicPublishService.publish(Mockito.any(),Mockito.eq(true), Mockito.eq(PRODUCT_VALIDATION_SUCCESS))).thenReturn(true);

        boolean result = requestProcessor.process(event);
        Assertions.assertTrue(result);
    }

    @Test
    public void testProcessFailure() {
        Event event = new Event();
        event.setCorrelationId("corrId1");
        event.setCustomerId("customerId");
        Request request  = new Request();

        List<String> subscriptions = new ArrayList<>();
        subscriptions.add("product1");
        subscriptions.add("product2");
        Mockito.when(subscriptionDao.getSubscriptions(Mockito.any())).thenReturn(subscriptions);
        Mockito.when(validationService.process(Mockito.any(), Mockito.any())).thenReturn(false);
        Mockito.when(topicPublishService.publish(Mockito.any(),Mockito.eq(false), Mockito.eq(PRODUCT_VALIDATION_FAILURE))).thenReturn(true);

        boolean result = requestProcessor.process(event);
        Assertions.assertTrue(result);
    }

    @Test
    public void testProcessNoSubscription() {
        Event event = new Event();
        event.setCorrelationId("corrId1");
        event.setCustomerId("customerId");

        List<String> subscriptions = new ArrayList<>();
        Mockito.when(subscriptionDao.getSubscriptions(Mockito.any())).thenReturn(subscriptions);
        Mockito.when(validationService.process(Mockito.any(), Mockito.any())).thenReturn(false);
        Mockito.when(topicPublishService.publish(Mockito.any(),Mockito.eq(false), Mockito.eq(PRODUCT_VALIDATION_NO_SUBSCRIPTION))).thenReturn(true);

        boolean result = requestProcessor.process(event);
        Assertions.assertTrue(result);
    }

    class CurrentThreadExecutor implements ExecutorService {
        public void execute(Runnable r) {
            r.run();
        }

        @Override
        public void shutdown() {

        }

        @Override
        public List<Runnable> shutdownNow() {
            return null;
        }

        @Override
        public boolean isShutdown() {
            return false;
        }

        @Override
        public boolean isTerminated() {
            return false;
        }

        @Override
        public boolean awaitTermination(long timeout, TimeUnit unit) throws InterruptedException {
            return false;
        }

        @Override
        public <T> Future<T> submit(Callable<T> task) {
            return null;
        }

        @Override
        public <T> Future<T> submit(Runnable task, T result) {
            return null;
        }

        @Override
        public Future<?> submit(Runnable task) {
            return null;
        }

        @Override
        public <T> List<Future<T>> invokeAll(Collection<? extends Callable<T>> tasks) throws InterruptedException {
            return null;
        }

        @Override
        public <T> List<Future<T>> invokeAll(Collection<? extends Callable<T>> tasks, long timeout, TimeUnit unit) throws InterruptedException {
            return null;
        }

        @Override
        public <T> T invokeAny(Collection<? extends Callable<T>> tasks) throws InterruptedException, ExecutionException {
            return null;
        }

        @Override
        public <T> T invokeAny(Collection<? extends Callable<T>> tasks, long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
            return null;
        }
    }
}
