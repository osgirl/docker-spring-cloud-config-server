FROM openjdk:8-jre-alpine
MAINTAINER KCOM Labs
EXPOSE 8888

ADD target/spring-cloud-config-server-*.jar /spring-cloud-config-server.jar

ENV JAVA_OPTS=""
CMD exec java ${JAVA_OPTS} -jar /spring-cloud-config-server.jar