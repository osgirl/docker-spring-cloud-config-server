package com.kcom.config.server.config;

import com.amazonaws.ClientConfiguration;
import com.amazonaws.Protocol;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.internal.StaticCredentialsProvider;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.S3ClientOptions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AmazonConfig {

    @Bean
    @ConditionalOnProperty(value = "cloud.aws.endpoint")
    public AmazonS3Client amazonS3(@Value("${cloud.aws.endpoint}") String endpoint,
                                   @Value("${cloud.aws.credentials.accessKey}") String accessKey,
                                   @Value("${cloud.aws.credentials.secretKey}") String secretKey) {
        ClientConfiguration clientConfiguration = new ClientConfiguration();
        clientConfiguration.setProtocol(Protocol.HTTP);
        AmazonS3Client client = new AmazonS3Client(new StaticCredentialsProvider(new BasicAWSCredentials(accessKey, secretKey)), clientConfiguration);
        client.setS3ClientOptions(new S3ClientOptions().withPathStyleAccess(true));
        client.setEndpoint(endpoint);
        return client;
    }
}