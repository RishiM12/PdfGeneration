package com.demo.pdfGen.config;

import com.couchbase.client.java.Bucket;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.couchbase.CouchbaseClientFactory;
import org.springframework.data.couchbase.config.AbstractCouchbaseConfiguration;
import org.springframework.data.couchbase.repository.config.EnableReactiveCouchbaseRepositories;

@Configuration
@EnableReactiveCouchbaseRepositories
public class CouchbaseConfig extends AbstractCouchbaseConfiguration {
    @Autowired
    private final CouchbaseProperties couchbaseProperties;

    public CouchbaseConfig(CouchbaseProperties couchbaseProperties) {
        this.couchbaseProperties = couchbaseProperties;
    }

    @Bean
    public Bucket bucket(CouchbaseClientFactory couchbaseClientFactory) {
        return couchbaseClientFactory.getBucket();
    }

    @Override
    public String getConnectionString() {
        return couchbaseProperties.getBootstrapHosts();
    }

    @Override
    public String getUserName() {
        return couchbaseProperties.getUsername();
    }

    @Override
    public String getPassword() {
        return couchbaseProperties.getBucketPassword();
    }

    @Override
    public String getBucketName() {
        return couchbaseProperties.getBucketName();
    }
}
