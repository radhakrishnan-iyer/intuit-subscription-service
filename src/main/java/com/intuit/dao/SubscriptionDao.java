package com.intuit.dao;

import org.springframework.jdbc.core.JdbcTemplate;
import java.util.List;

public class SubscriptionDao implements ISubscriptionDao {

    private final JdbcTemplate mysqlJdbcTemplate;
    private final String getSubscriptionByCustomerId;

    public SubscriptionDao(JdbcTemplate mysqlJdbcTemplate, String getSubscriptionByCustomerId) {
        this.mysqlJdbcTemplate = mysqlJdbcTemplate;
        this.getSubscriptionByCustomerId = getSubscriptionByCustomerId;
    }

    @Override
    public List<String> getSubscriptions(String customerId) {
        List<String> subscriptions =  mysqlJdbcTemplate.query(String.format(getSubscriptionByCustomerId , customerId), new SubscriptionRowMapper());
        return subscriptions;
    }
}
