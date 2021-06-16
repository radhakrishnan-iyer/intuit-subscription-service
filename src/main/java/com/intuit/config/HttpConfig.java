package com.intuit.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

@Configuration
public class HttpConfig {

    @Value("${product.service.readTimeOut:5000}")
    private int productServiceReadTimeOut;

    @Value("${product.service.connectionTimeOut:1000}")
    private int productServiceConnectionTimeOut;

    @Bean
    public RestTemplate restTemplate() {
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.setRequestFactory(clientHttpRequestFactory());
        return restTemplate;
    }

    @Bean
    public ClientHttpRequestFactory clientHttpRequestFactory() {
        HttpComponentsClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory();
        factory.setReadTimeout(productServiceReadTimeOut); // setting timeout as read timeout
        factory.setConnectTimeout(productServiceConnectionTimeOut); // setting timeout as connect timeout
        return factory;
    }
}
