# docker-spring-cloud-config-server

## Overview

A docker image for the [Spring Cloud Config Server](http://cloud.spring.io/spring-cloud-static/spring-cloud.html#_spring_cloud_config_server). Spring Cloud Config provides server and client-side support for externalized configuration in a distributed system.

This docker image adds support for the org.springframework.core.io.ResourceLoader interface, allowing properties to be loaded from Amazon S3, the file system, servlet context and the classpath.

To use the resource profile just launch the Config Server with `spring.profiles.active=resourceBased`.

This docker image is based on the lightweight Alpine Linux openjdk image `openjdk:8-jre-alpine`.

### Amazon S3 Backend

Properties can be loaded from Amazon S3 by using the s3 protocol to reference Amazon S3 buckets and objects inside their bucket. The typical pattern is `s3://<bucket>/<object>` where bucket is the global and unique bucket name and object is a valid object name inside the bucket. The object name can be a directory in the root folder of a bucket or a nested directory inside a bucket.  You would specify your search location using `spring.config.location=s3://test-bucket`

You can also provide multiple search locations to search across different directories or buckets.  Locations take precedence based on the order they were specified i.e `spring.config.location=s3://test-bucket1,s3://test-bucket2,s3://test-bucket3`.  In this example `test-bucket1` would be searched first, followed by `test-bucket2` and then `test-bucket3`.

## Usage

To run the docker container:
```sh
docker run -p 8888:8888 kcomlabs/spring-cloud-config-server
```

The easiest way to run is to pass the spring cloud configuration as environment variables e.g.
```sh
docker run \
-e spring.profiles.active=resourceBased \
-e spring.config.location=s3://test-bucket \
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