package com.prabhath.AIO_Backend.Config;

import com.prabhath.AIO_Backend.Config.Source.MultitenantDataSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

import javax.sql.DataSource;
import java.io.File;
import java.io.FileInputStream;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

@Configuration
public class MultitenantConfiguration {
    @Value("${defaultTenant}")
    private String defaultTenant;
    @Value("${tenantsPropertiesPath}")
    private String TenantsPropertiesPath;


    @Bean
    @ConfigurationProperties(prefix = "tenants")
    public DataSource dataSource() {
        File[] files = Paths.get(TenantsPropertiesPath).toFile().listFiles();
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

        return dataSource;

    }
}
