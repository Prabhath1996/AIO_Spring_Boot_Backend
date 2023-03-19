package com.prabhath.websecurity.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

import javax.sql.DataSource;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

@Configuration
public class MultitenantConfiguration {
    @Value("${defaultTenant}")
    private String defaultTenant;


    @Bean
    @ConfigurationProperties(prefix = "tenants")
    public DataSource dataSource() {
        File[] files = Paths.get("src/main/java/com/prabhath/websecurity/allTenants").toFile().listFiles();
        Map<Object, Object> resolvedDataSources = new HashMap<>();

        try {
            for (File propertyFile : files) {
                Properties tenantProperties = new Properties();
                DataSourceBuilder dataSourceBuilder = DataSourceBuilder.create();
                tenantProperties.load(new FileInputStream(propertyFile));
                String tenantId = tenantProperties.getProperty("name");
                System.out.println("loop tenantId  : "+ tenantId);
                dataSourceBuilder.username(tenantProperties.getProperty("datasource.username"));
                dataSourceBuilder.password(tenantProperties.getProperty("datasource.password"));
                dataSourceBuilder.url(tenantProperties.getProperty("datasource.url"));
                resolvedDataSources.put(tenantId, dataSourceBuilder.build());
            }
        } catch (Exception e) {
                      System.out.println("error  : "+e);
        }
        AbstractRoutingDataSource dataSource = new MultitenantDataSource();
        System.out.println("defaultTenant  : "+defaultTenant);
        dataSource.setDefaultTargetDataSource(resolvedDataSources.get(defaultTenant));
        dataSource.setTargetDataSources(resolvedDataSources);

        dataSource.afterPropertiesSet();
        System.out.println("dataSource  : "+resolvedDataSources);
        return dataSource;

//        DriverManagerDataSource dataSource = new DriverManagerDataSource();
//        dataSource.setDriverClassName("com.mysql.cj.jdbc.Driver");
//        dataSource.setUrl("jdbc:mysql://localhost:3307/erp");
//        dataSource.setUsername("root");
//        dataSource.setPassword("root");
//        return dataSource;
    }
}
