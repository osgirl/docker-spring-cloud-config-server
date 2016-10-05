package com.kcom.config.server.environment;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.config.server.config.EnvironmentRepositoryConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.env.ConfigurableEnvironment;

public class S3EnvironmentRepositoryConfiguration extends EnvironmentRepositoryConfiguration {

    @Configuration
    @Profile("s3")
    protected static class S3RepositoryConfiguration {

        @Bean
        public S3EnvironmentRepository environmentRepository(ConfigurableEnvironment environment,
                                                             @Value("${spring.config.location}") String configurationLocation) {
            return new S3EnvironmentRepository(environment, configurationLocation);
        }
    }
}
