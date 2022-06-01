//package com.com.example.core.transaction.config;
//
//import com.alibaba.druid.pool.DruidDataSource;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.beans.factory.annotation.Qualifier;
//import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.context.annotation.Primary;
//
//import javax.annotation.Resource;
//import javax.sql.DataSource;
//
///**
// * 分布式事务数据源配置
// *
// * @author houzhi
// */
//@Configuration
//public class DataSourceConfiguration {
//
//    private final static Logger logger = LoggerFactory.getLogger(DataSourceConfiguration.class);
//
//    @Autowired(required = true)
//    private DataSourceProperties dataSourceProperties;
//
//    @Bean(name = "dataSource") // 声明其为Bean实例
//    @Primary // 在同样的DataSource中，首先使用被标注的DataSource
//    public DataSource druidDataSource() {
//        DruidDataSource druidDataSource = new DruidDataSource();
//        druidDataSource.setUrl(dataSourceProperties.getUrl());
//        druidDataSource.setUsername(dataSourceProperties.getUsername());
//        druidDataSource.setPassword(dataSourceProperties.getPassword());
//        druidDataSource.setDriverClassName(dataSourceProperties.getDriverClassName());
//        druidDataSource.setInitialSize(0);
//        druidDataSource.setMaxActive(180);
//        druidDataSource.setMaxWait(60000);
//        druidDataSource.setMinIdle(0);
//        druidDataSource.setValidationQuery("Select 1 from DUAL");
//        druidDataSource.setTestOnBorrow(false);
//        druidDataSource.setTestOnReturn(false);
//        druidDataSource.setTestWhileIdle(true);
//        druidDataSource.setTimeBetweenEvictionRunsMillis(60000);
//        druidDataSource.setMinEvictableIdleTimeMillis(25200000);
//        druidDataSource.setRemoveAbandoned(true);
//        druidDataSource.setRemoveAbandonedTimeout(1800);
//        druidDataSource.setLogAbandoned(true);
//        return new DataSourceProxy(druidDataSource);
//    }
//}
