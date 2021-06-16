package com.intuit.service;

import com.intuit.common.model.Request;
import com.intuit.common.model.Response;
import com.intuit.common.model.mq.Event;
import com.intuit.common.model.profile.Profile;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

public class ValidationServiceTest {
    private ValidationService validationService;
    private RestTemplate restTemplate;
    private String serviceBaseUrl;

    @BeforeEach
    public void setUp() {
        restTemplate = Mockito.mock(RestTemplate.class);
        serviceBaseUrl = "http://localhost:8080/";
        validationService = new ValidationService(restTemplate, serviceBaseUrl);
    }

    @Test
    public void testProcessValid() {
        String endpoint = "endpoint";
        Response response = new Response();
        response.setMessage("Valid");
        ResponseEntity<Response> responseEntity = ResponseEntity.accepted().body(response);
        Mockito.when(restTemplate.exchange(Mockito.eq(serviceBaseUrl), Mockito.eq(HttpMethod.POST), Mockito.any(HttpEntity.class), Mockito.eq(Response.class))).thenReturn(responseEntity);

        Event event = new Event();
        event.setCorrelationId("corrId1");
        event.setCustomerId("customerId");
        Request request  = new Request();
        Profile profile = new Profile();
        profile.setCompanyName("Intuit");
        request.setProfile(profile);

        event.setRequest(request);
        try {
            Assertions.assertTrue(validationService.process(event , endpoint));
        }
        catch (Exception ex) {
            Assertions.fail("Code should not reach here");
        }
    }

    @Test
    public void testProcessInvalid() {
        String endpoint = "endpoint";
        Response response = new Response();
        response.setMessage("Invalid");
        ResponseEntity<Response> responseEntity = ResponseEntity.accepted().body(response);
        Mockito.when(restTemplate.exchange(Mockito.eq(serviceBaseUrl), Mockito.eq(HttpMethod.POST), Mockito.any(HttpEntity.class), Mockito.eq(Response.class))).thenReturn(responseEntity);

        Event event = new Event();
        event.setCorrelationId("corrId1");
        event.setCustomerId("customerId");
        Request request  = new Request();
        Profile profile = new Profile();
        profile.setCompanyName("Intuit");
        request.setProfile(profile);

        event.setRequest(request);
        try {
            Assertions.assertFalse(validationService.process(event , endpoint));
        }
        catch (Exception ex) {
            Assertions.fail("Code should not reach here");
        }
    }

    @Test
    public void testProcessException() {
        String endpoint = "endpoint";
        Response response = new Response();
        response.setMessage("Invalid");
        ResponseEntity<Response> responseEntity = ResponseEntity.accepted().body(response);
        Mockito.when(restTemplate.exchange(Mockito.eq(serviceBaseUrl), Mockito.eq(HttpMethod.POST), Mockito.any(HttpEntity.class), Mockito.eq(Response.class)))
                .thenThrow(new RuntimeException());

        Event event = new Event();
        event.setCorrelationId("corrId1");
        event.setCustomerId("customerId");
        Request request  = new Request();
        Profile profile = new Profile();
        profile.setCompanyName("Intuit");
        request.setProfile(profile);

        event.setRequest(request);
        try {
            Assertions.assertFalse(validationService.process(event , endpoint));
        }
        catch (Exception ex) {
            Assertions.fail("Code should not reach here");
        }
    }
}
