package com.kcom.config.server

import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.test.SpringApplicationConfiguration
import org.springframework.boot.test.TestRestTemplate
import org.springframework.boot.test.WebIntegrationTest
import org.springframework.cloud.config.environment.Environment
import spock.lang.Specification

@SpringApplicationConfiguration(SpringCloudConfigServerApplication.class)
@WebIntegrationTest(randomPort=true)
class SpringCloudConfigServerApplicationIT extends Specification {

    @Value('${local.server.port}')
    int port

    def 'Ensure a context initialised successfully'() {

        when:
        def result = new TestRestTemplate().getForObject(String.format("http://localhost:%d/foo/default", port), Environment.class);

        then:
        result.getName() == "foo"
        result.getProfiles() == ["default"]
        !result.getPropertySources().isEmpty()
        result.getPropertySources().get(0).getName() == "classpath:/config/foo.properties"
        result.getPropertySources().get(0).getSource().get("foo") == "foo-value"
    }

    def 'Ensure an invalid context is not initialised'() {

        when:
        def result = new TestRestTemplate().getForObject(String.format("http://localhost:%d/bad/default", port), Environment.class);

        then:
        result.getName() == "bad"
        result.getProfiles() == ["default"]
        result.getPropertySources().isEmpty()
    }
}
