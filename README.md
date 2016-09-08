# docker-spring-cloud-config-server

## Overview

A docker image for the [Spring Cloud Config Server](http://cloud.spring.io/spring-cloud-static/spring-cloud.html#_spring_cloud_config_server). Spring Cloud Config provides server and client-side support for externalized configuration in a distributed system.

This docker image is based on the lightweight Alpine Linux openjdk image `openjdk:8-jre-alpine`.

## Usage

To run the docker container:
```sh
docker run -p 8888:8080 kcomlabs/docker-spring-cloud-config-server
```

To run the docker container using docker-compose:
```sh
spring-cloud-config-server:
  image: kcomlabs/docker-spring-cloud-config-server
  ports:
    - "8888:8080"
```

After running this command Spring Cloud Config Server should be listening on port 8888 on your docker host.

You can pass options using the JAVA_OPTS environment variable.