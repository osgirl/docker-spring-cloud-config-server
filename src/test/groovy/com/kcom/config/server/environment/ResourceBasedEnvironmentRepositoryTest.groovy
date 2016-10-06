package com.kcom.config.server.environment

import org.springframework.core.io.DefaultResourceLoader
import org.springframework.core.io.Resource
import spock.lang.Specification
import spock.lang.Subject

class ResourceBasedEnvironmentRepositoryTest extends Specification {

    def resourceLoader = new DefaultResourceLoader()
    def configurationLocation = "classpath:config"

    @Subject
    def subject = new ResourceBasedEnvironmentRepository(resourceLoader, configurationLocation)

    def "Default application.properties are loaded correctly"() {

        given: "A valid application.properties file"
        def applicationProperties = resourceLoader.getResource("$configurationLocation/application.properties")

        and: "A profile and environment name"
        def profile = "profile"
        def label = "label"

        when: "I call findOne using an application that does not exist"
        def result = subject.findOne("fooXXX", profile, label)

        then: "The result inherits the profile, label and application name"
        result.profiles.size() == 1
        result.profiles[0] == profile
        result.name == "fooXXX"
        result.label == label

        and: "A properties source matching the default application.properties is returned"
        def propertySource = result.propertySources.find { it.name == "$configurationLocation/application.properties" }
        propertySource.source == loadProperties(applicationProperties)

        and: "Properties for the invalid application are blank"
        def propertySourceFooXXX = result.propertySources.find { it.name == "fooXXX.properties" }
        propertySourceFooXXX.source.isEmpty()

    }

    def "Properties for an additional application are loaded"() {

        given: "A valid application.properties file"
        def applicationProperties = resourceLoader.getResource("$configurationLocation/application.properties")

        and: "A valid configuration file for application 'foo'"
        def fooProperties = resourceLoader.getResource("$configurationLocation/foo.properties")

        when: "I call findOne using foo as the application name"
        def result = subject.findOne("foo", null, null)

        then: "A properties source matching the default application.properties is returned"
        def propertySource = result.propertySources.find { it.name == "$configurationLocation/application.properties" }
        propertySource.source == loadProperties(applicationProperties)

        and: "A properties source matching the foo.properties is returned"
        def propertySourceFoo = result.propertySources.find { it.name == "$configurationLocation/foo.properties" }
        propertySourceFoo.source == loadProperties(fooProperties)

    }

    def "findOne can handle multiple configuration locations"() {

        given: "Multiple configuration locations"
        subject = new ResourceBasedEnvironmentRepository(resourceLoader, "classpath:config2, classpath:config")

        when: "I call find one for a valid application"
        def result = subject.findOne("foo", null, null)

        then: "Property sources take precedence based on the order they were passed in the constructor"
        def propertySource = result.propertySources.find { it.name == "classpath:config/application.properties" }
        propertySource.source == loadProperties(resourceLoader.getResource("classpath:config/application.properties"))

        def propertySourceFoo = result.propertySources.find { it.name == "classpath:config2/foo.properties" }
        propertySourceFoo.source == loadProperties(resourceLoader.getResource("classpath:config2/foo.properties"))

    }

    private Properties loadProperties(Resource propertiesResource) {
        new Properties() {
            {
                load(propertiesResource.inputStream)
            }
        }
    }

}
