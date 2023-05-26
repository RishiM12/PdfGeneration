package com.demo.pdfGen.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CouchbaseProperties {
    private final String bootstrapHosts;
    private final String bucketName;
    private final String bucketPassword;
    private final String username;
    private final String port;

    //    private final String scope;
    public String getBootstrapHosts() {
        return bootstrapHosts;
    }

    public String getPort() {
        return port;
    }

    public String getBucketName() {
        return bucketName;
    }

    public String getBucketPassword() {
        return bucketPassword;
    }


    public String getUsername() {
        return username;
    }

//    public String getScope(){return scope;}

    public CouchbaseProperties(
            @Value("${spring.couchbase.bootstrap-hosts}") String bootstrapHosts,
            @Value("${spring.couchbase.bucket.name}") String bucketName,
            @Value("${spring.couchbase.bucket.password}") String bucketPassword,
            @Value("${spring.couchbase.username}") String username,
            @Value("${spring.couchbase.port}") String port
//            @Value("${spring.couchbase.scope}")String scope
    ) {
        this.bootstrapHosts = bootstrapHosts;
        this.bucketName = bucketName;
        this.bucketPassword = bucketPassword;
        this.username = username;
        this.port = port;
//        this.scope=scope;
    }
}
