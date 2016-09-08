package com.kcom;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.boot.test.WebIntegrationTest;
import org.springframework.cloud.config.environment.Environment;
import org.springframework.cloud.config.server.ConfigServerApplication;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.web.client.RestTemplate;

import static org.junit.Assert.assertEquals;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(ConfigServerApplication.class)
@WebIntegrationTest(randomPort = true)
public class SpringCloudConfigServerApplicationTests {

    @Value("${local.server.port}")
    private int localServerPort;

    private RestTemplate restTemplate = new RestTemplate();

    @Test
    public void loadAProperty() {
        Environment environment = restTemplate.getForObject(String.format("http://localhost:%d/foo/default", localServerPort), Environment.class);

        assertEquals("bar-value", environment.getPropertySources().iterator().next().getSource().get("bar"));
    }
}
