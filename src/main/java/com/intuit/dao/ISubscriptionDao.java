package com.intuit.dao;

import java.util.List;

public interface ISubscriptionDao {
    List<String> getSubscriptions(String customerId);
}
