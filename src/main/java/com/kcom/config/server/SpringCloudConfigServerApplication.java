package com.kcom.config.server;

import com.amazonaws.ClientConfiguration;
import com.amazonaws.Protocol;
import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.auth.DefaultAWSCredentialsProviderChain;
import com.amazonaws.internal.StaticCredentialsProvider;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.S3ClientOptions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cloud.config.server.EnableConfigServer;
import org.springframework.context.annotation.Bean;
import org.springframework.util.StringUtils;

@SpringBootApplication
@EnableConfigServer
public class SpringCloudConfigServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringCloudConfigServerApplication.class, args);
    }

    @Bean
    @ConditionalOnProperty(value="cloud.aws.credentials.endpoint")
    public AmazonS3Client amazonS3(@Value("${cloud.aws.credentials.endpoint}") String endpoint,
                                   @Value("${cloud.aws.credentials.accessKey}") String accessKey,
                                   @Value("${cloud.aws.credentials.secretKey}") String secretKey) {
        AmazonS3Client client = new AmazonS3Client(getCredentialsProvider(accessKey, secretKey), getClientConfiguration());
        client.setEndpoint(endpoint);
        client.setS3ClientOptions(new S3ClientOptions().withPathStyleAccess(true));
        return client;
    }

    private static AWSCredentialsProvider getCredentialsProvider(String accessKey, String secretKey) {
        AWSCredentialsProvider credentialsProvider;
        if (!StringUtils.hasLength(accessKey) && !StringUtils.hasLength(secretKey)) {
            credentialsProvider = new DefaultAWSCredentialsProviderChain();
        } else {
            credentialsProvider = new StaticCredentialsProvider(new BasicAWSCredentials(accessKey, secretKey));
        }
        return credentialsProvider;
    }

    private static ClientConfiguration getClientConfiguration() {
        ClientConfiguration clientConfiguration = new ClientConfiguration();
        clientConfiguration.setProtocol(Protocol.HTTP);
        return clientConfiguration;
    }
}
