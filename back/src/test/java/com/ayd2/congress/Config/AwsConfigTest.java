package com.ayd2.congress.Config;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import com.ayd2.congress.config.AwsConfig;

import software.amazon.awssdk.services.s3.S3Client;

public class AwsConfigTest {

    @Test
    public void testS3ClientBeanCreation() {
        AwsConfig config = new AwsConfig();

        ReflectionTestUtils.setField(config, "accessKey", "test-access-key");
        ReflectionTestUtils.setField(config, "secretKey", "test-secret-key");
        ReflectionTestUtils.setField(config, "region", "us-east-1");

        S3Client client = config.s3Client();

        assertNotNull(client);
        client.close();
    }
}