package com.kcom.config.server.environment;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.config.environment.Environment;
import org.springframework.cloud.config.environment.PropertySource;
import org.springframework.cloud.config.server.environment.EnvironmentRepository;
import org.springframework.cloud.config.server.environment.SearchPathLocator;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Properties;

import static java.lang.String.format;
import static java.util.Arrays.stream;
import static java.util.Collections.emptyMap;
import static java.util.stream.Collectors.toList;

@Profile("resourceBased")
@Component
@Slf4j
public class ResourceBasedEnvironmentRepository implements EnvironmentRepository, SearchPathLocator {

    private final ResourceLoader resourceLoader;
    private final List<String> configurationLocations;

    @Autowired
    public ResourceBasedEnvironmentRepository(ResourceLoader resourceLoader,
                                              @Value("${spring.config.location}") String configurationLocation) {
        this.resourceLoader = resourceLoader;
        this.configurationLocations = stream(configurationLocation.split(","))
                .map(String::trim)
                .collect(toList());
        log.info("\n\n\n\n LOOKING IN THESE LOCATIONS " + configurationLocations);
    }

    @Override
    public Environment findOne(String application, String profile, String label) {
        Environment environment = new Environment(application, profile);
        environment.setLabel(label);
        environment.addFirst(createPropertySource("application"));
        environment.add(createPropertySource(application));
        log.info("\n\n\n\n ENV " + environment);
        return environment;
    }

    private PropertySource createPropertySource(String application) {

        String propertiesFilename = format("%s.properties", application);

        for(String location : configurationLocations) {
            Resource resource = resourceLoader.getResource(format("%s/%s", location, propertiesFilename));
            log.info("\n\n\n\n resource " + resource);
            log.info("\n\n\n\n resource exists " + resource.exists());
            if (resource.exists()) {
                return new PropertySource(format("%s/%s", location, resource.getFilename()), loadProperties(resource));
            }
        }

        return new PropertySource(propertiesFilename, emptyMap());
    }

    @SneakyThrows
    private Properties loadProperties(Resource resource) {
        Properties properties = new Properties();
        properties.load(resource.getInputStream());
        return properties;
    }

    @Override
    public Locations getLocations(String application, String profile, String label) {
        return new Locations(application, profile, label, null, configurationLocations.toArray(new String[0]));
    }

}
