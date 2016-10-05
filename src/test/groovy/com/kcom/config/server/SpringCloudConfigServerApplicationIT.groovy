package com.kcom.config.server

import com.amazonaws.ClientConfiguration
import com.amazonaws.Protocol
import com.amazonaws.auth.BasicAWSCredentials
import com.amazonaws.internal.StaticCredentialsProvider
import com.amazonaws.services.s3.AmazonS3
import com.amazonaws.services.s3.AmazonS3Client
import com.amazonaws.services.s3.S3ClientOptions
import groovy.io.FileType
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.test.SpringApplicationConfiguration
import org.springframework.boot.test.TestRestTemplate
import org.springframework.boot.test.WebIntegrationTest
import org.springframework.cloud.config.environment.Environment
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.io.ClassPathResource
import sirius.kernel.Setup
import sirius.kernel.Sirius
import spock.lang.Specification

@SpringApplicationConfiguration(SpringCloudConfigServerApplication.class)
@WebIntegrationTest(randomPort=true)
class SpringCloudConfigServerApplicationIT extends Specification {

    @Configuration
    public static class AmazonConfig {

        @Bean
        public AmazonS3Client amazonS3(@Value('${cloud.aws.endpoint}') String endpoint,
                                       @Value('${cloud.aws.credentials.accessKey}') String accessKey,
                                       @Value('${cloud.aws.credentials.secretKey}') String secretKey) {
            ClientConfiguration clientConfiguration = new ClientConfiguration();
            clientConfiguration.setProtocol(Protocol.HTTP);
            AmazonS3Client client = new AmazonS3Client(new StaticCredentialsProvider(new BasicAWSCredentials(accessKey, secretKey)), clientConfiguration);
            client.setS3ClientOptions(new S3ClientOptions().withPathStyleAccess(true));
            client.setEndpoint(endpoint);
            return client;
        }
    }

    @Autowired
    AmazonS3 amazonClient

    @Value('${local.server.port}')
    int port

    @Value('${cloud.aws.bucket}')
    String bucket

    def setupSpec() {
        Sirius.start(new Setup(Setup.Mode.TEST, ClassLoader.getSystemClassLoader()))
    }

    def setup() {
        amazonClient.createBucket(bucket)
        new ClassPathResource('config').file.eachFileRecurse (FileType.FILES) { file ->
            amazonClient.putObject(bucket, file.name, file)
        }
    }

    def cleanup() {
        amazonClient.deleteBucket(bucket)
    }

    def cleanupSpec() {
        Sirius.stop()
    }

    def "Ensure a context is initialised correctly"() {

        when:
        def result = new TestRestTemplate().getForObject(String.format("http://localhost:%d/${configName}/default", port), Environment.class);

        then:
        result.getName() == configName
        result.getProfiles() == ["default"]
        result.getPropertySources().size() == 2
        result.getPropertySources().find() { it.getName() == "s3://${bucket}/application.properties" } != null
        result.getPropertySources().find() { it.getName() == "s3://${bucket}/application.properties" }.source.get("application-key").equals("application-value")
        result.getPropertySources().find() { it.getName() == "s3://${bucket}/${configName}.properties" } != null
        result.getPropertySources().find() { it.getName() == "s3://${bucket}/${configName}.properties" }.source.get("${configName}-key".toString()).equals("${configName}-value".toString())

        where:
        configName << ["foo", "bar" ]
    }

    def "Ensure a context with a missing config file is initialised but with zero properties"() {

        when:
        def result = new TestRestTemplate().getForObject(String.format("http://localhost:%d/unknown/default", port), Environment.class);

        then:
        result.getName() == "unknown"
        result.getProfiles() == ["default"]
        result.getPropertySources().size() == 2
        result.getPropertySources().find() { it.getName() == "s3://${bucket}/application.properties" } != null
        result.getPropertySources().find() { it.getName() == "s3://${bucket}/application.properties" }.source.get("application-key").equals("application-value")
        result.getPropertySources().find() { it.getName() == "s3://${bucket}/unknown.properties" } != null
        result.getPropertySources().find() { it.getName() == "s3://${bucket}/unknown.properties" }.source.size() == 0
    }

    def "Ensure a context with an empty config file is initialised but with zero properties"() {

        when:
        def result = new TestRestTemplate().getForObject(String.format("http://localhost:%d/empty/default", port), Environment.class);

        then:
        result.getName() == "empty"
        result.getProfiles() == ["default"]
        result.getPropertySources().size() == 2
        result.getPropertySources().find() { it.getName() == "s3://${bucket}/application.properties" } != null
        result.getPropertySources().find() { it.getName() == "s3://${bucket}/application.properties" }.source.get("application-key").equals("application-value")
        result.getPropertySources().find() { it.getName() == "s3://${bucket}/empty.properties" } != null
        result.getPropertySources().find() { it.getName() == "s3://${bucket}/empty.properties" }.source.size() == 0
    }
}
