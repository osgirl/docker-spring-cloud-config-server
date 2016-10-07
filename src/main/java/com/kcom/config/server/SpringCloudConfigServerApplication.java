package com.kcom.config.server;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.config.server.EnableConfigServer;

@SpringBootApplication
@EnableConfigServer
@Slf4j
public class SpringCloudConfigServerApplication {

    public static void main(String[] args) {
        log.info("\n\n\n\n I AM RUNNING");
        SpringApplication.run(SpringCloudConfigServerApplication.class, args);
    }

}
