package com.kcom.config.server.environment;

import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.config.environment.Environment;
import org.springframework.cloud.config.environment.PropertySource;
import org.springframework.cloud.config.server.environment.NativeEnvironmentRepository;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.util.StringUtils;

import java.io.File;
import java.util.Properties;

@ConfigurationProperties("spring.cloud.config.server.s3")
public class S3EnvironmentRepository extends NativeEnvironmentRepository {

    @Autowired
    private ResourceLoader resourceLoader;

    private String configurationLocation;

    public S3EnvironmentRepository(ConfigurableEnvironment environment, String configurationLocation) {
        super(environment);
        this.configurationLocation = removeTrailingFileSeperator(configurationLocation);
        this.setSearchLocations(configurationLocation);
    }

    @Override
    public Environment findOne(String config, String profile, String label) {
        Environment environment = super.findOne(config, profile, label);
        environment.getPropertySources().add(createPropertySource("application"));
        environment.getPropertySources().add(createPropertySource(config));
        return environment;
    }

    private PropertySource createPropertySource(String config) {
        Resource resource = resourceLoader.getResource(String.format("%s/%s.properties", configurationLocation, config));
        Properties properties = loadProperties(resource);
        return new PropertySource(String.format("%s/%s", configurationLocation, resource.getFilename()), properties);
    }

    @SneakyThrows
    private Properties loadProperties(Resource resource) {
        Properties properties = new Properties();
        if (resource.exists()) {
            properties.load(resource.getInputStream());
        }
        return properties;
    }

    private String removeTrailingFileSeperator(String path) {
        if (StringUtils.hasLength(path) && path.endsWith(File.separator)) {
            path = path.substring(0, path.length() - 1);
        }
        return path;
    }
}
