package com.intuit.service;

import com.intuit.common.constant.Constants;
import com.intuit.common.model.Request;
import com.intuit.common.model.Response;
import com.intuit.common.model.mq.Event;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

public class ValidationService {

    private static Logger logger = LoggerFactory.getLogger(ValidationService.class);

    private final RestTemplate restTemplate;
    private final String serviceBaseUrl;

    public ValidationService(RestTemplate restTemplate , String serviceBaseUrl) {
        this.restTemplate = restTemplate;
        this.serviceBaseUrl = serviceBaseUrl;
    }

    public boolean process(Event event , String endpoint) {
        try {
            // Since this processing happens in different thread hence need to set it again
            MDC.clear();
            MDC.put(Constants.request_id , event.getCorrelationId());
            MDC.put(Constants.customer_id , event.getCustomerId());

            String url = String.format(serviceBaseUrl , endpoint);
            Request request = event.getRequest();
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.add(Constants.request_id, MDC.get(Constants.request_id));
            headers.add(Constants.customer_id, MDC.get(Constants.customer_id));

            HttpEntity<Request> entity = new HttpEntity<>(request, headers);

            ResponseEntity<Response> response = restTemplate
                                    .exchange(url, HttpMethod.POST, entity, Response.class);

            if(response.getBody()!=null && response.getBody().getMessage().equalsIgnoreCase("Valid")) {
                logger.info("Request is valid as per product {} " , endpoint);
                return true;
            }
            else {
                logger.info("Request is invalid as per product {} " , endpoint);
                return false;
            }
        }
        catch (Exception ex) {
            logger.error("Exception while invoking the endpoint for {} product validation service" , endpoint, ex);
            return false;
        }
    }
}
