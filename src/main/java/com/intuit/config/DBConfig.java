package com.intuit.config;

import com.intuit.dao.ISubscriptionDao;
import com.intuit.dao.SubscriptionDao;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import javax.sql.DataSource;

@Configuration
public class DBConfig {

    @Value("${datasource.driver:com.mysql.jdbc.Driver}")
    private String dataSourceDriver;

    @Value("${datasource.url:jdbc:mysql://localhost:3306/intuit}")
    private String datasourceUrl;

    @Value("${datasource.db.user}")
    private String datasourceUser;

    @Value("${datasource.db.password}")
    private String datasourcePass;

    @Value("${query.subscription.by.customer.id}")
    private String getSubscriptionByCustomerId;

    @Bean
    public DataSource mysqlDataSource() {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName(dataSourceDriver);
        dataSource.setUrl(datasourceUrl);
        dataSource.setUsername(datasourceUser);
        dataSource.setPassword(datasourcePass);
        return dataSource;
    }

    @Bean
    public JdbcTemplate mysqlJdbcTemplate() {
        JdbcTemplate mysqlJdbcTemplate = new JdbcTemplate(mysqlDataSource());
        return mysqlJdbcTemplate;
    }

    @Bean
    public ISubscriptionDao subscriptionDao() {
        return new SubscriptionDao(mysqlJdbcTemplate(), getSubscriptionByCustomerId);
    }
}
