# docker-spring-cloud-config-server

## Overview

A docker image for the [Spring Cloud Config Server](http://cloud.spring.io/spring-cloud-static/spring-cloud.html#_spring_cloud_config_server). Spring Cloud Config provides server and client-side support for externalized configuration in a distributed system.

This docker image is based on the lightweight Alpine Linux openjdk image `openjdk:8-jre-alpine`.

## Usage

To run the docker container:
```sh
docker run -p 8888:8888 kcomlabs/spring-cloud-config-server
```

The easiest way to run is to pass the spring cloud configuration as environment variables e.g.
```sh
docker run \
-e SPRING_CLOUD_CONFIG_SERVER_GIT_URI=https://github.com/spring-cloud-samples/config-repo.git \
-e ENCRYPT_KEY=S3cR3t \
-e JAVA_OPTS=-Xmx256m \
-p 8888:8888 \
kcomlabs/spring-cloud-config-server
```

To run the docker container using docker-compose:
```sh
spring-cloud-config-server:
  image: kcomlabs/spring-cloud-config-server
  ports:
    - "8888:8888"
```

After running this command Spring Cloud Config Server should be listening on port 8888 on your docker host.

You can pass options using the JAVA_OPTS environment variable.

To use SSH GIT urls you will need to mount known_hosts config and keys into /root/.ssh/.