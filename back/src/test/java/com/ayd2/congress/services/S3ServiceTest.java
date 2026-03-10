package com.ayd2.congress.services;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import com.ayd2.congress.services.aws.S3Service;

import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectResponse;

@ExtendWith(MockitoExtension.class)
public class S3ServiceTest {

    private static final String BUCKET_NAME = "my-test-bucket";
    private static final String REGION = "us-east-2";
    private static final String FILE_NAME = "profile.png";

    @Mock
    private S3Client s3Client;

    @InjectMocks
    private S3Service s3Service;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(s3Service, "bucketName", BUCKET_NAME);
        ReflectionTestUtils.setField(s3Service, "region", REGION);
    }

    @Test
    void testUploadBase64WithDataUrl() throws Exception {
        // Arrange
        byte[] fileBytes = "hello world".getBytes(StandardCharsets.UTF_8);
        String base64 = Base64.getEncoder().encodeToString(fileBytes);
        String input = "data:image/png;base64," + base64;

        ArgumentCaptor<PutObjectRequest> requestCaptor = ArgumentCaptor.forClass(PutObjectRequest.class);
        ArgumentCaptor<RequestBody> bodyCaptor = ArgumentCaptor.forClass(RequestBody.class);

        when(s3Client.putObject(any(PutObjectRequest.class), any(RequestBody.class)))
                .thenReturn(PutObjectResponse.builder().build());

        // Act
        String result = s3Service.uploadBase64(input, FILE_NAME);

        // Assert
        assertAll(
                () -> verify(s3Client).putObject(requestCaptor.capture(), bodyCaptor.capture()),
                () -> assertEquals(BUCKET_NAME, requestCaptor.getValue().bucket()),
                () -> assertEquals("Fotos/" + FILE_NAME, requestCaptor.getValue().key()),
                () -> assertEquals("image/png", requestCaptor.getValue().contentType()),
                () -> assertArrayEquals(fileBytes, readRequestBody(bodyCaptor.getValue())),
                () -> assertEquals(
                        "https://" + BUCKET_NAME + ".s3." + REGION + ".amazonaws.com/Fotos/" + FILE_NAME,
                        result)
        );
    }

    @Test
    void testUploadBase64WithRawBase64ShouldUseOctetStream() throws Exception {
        // Arrange
        byte[] fileBytes = "plain-content".getBytes(StandardCharsets.UTF_8);
        String input = Base64.getEncoder().encodeToString(fileBytes);
        String fileNameWithoutExtension = "document";

        ArgumentCaptor<PutObjectRequest> requestCaptor = ArgumentCaptor.forClass(PutObjectRequest.class);
        ArgumentCaptor<RequestBody> bodyCaptor = ArgumentCaptor.forClass(RequestBody.class);

        when(s3Client.putObject(any(PutObjectRequest.class), any(RequestBody.class)))
                .thenReturn(PutObjectResponse.builder().build());

        // Act
        String result = s3Service.uploadBase64(input, fileNameWithoutExtension);

        // Assert
        assertAll(
                () -> verify(s3Client).putObject(requestCaptor.capture(), bodyCaptor.capture()),
                () -> assertEquals("application/octet-stream", requestCaptor.getValue().contentType()),
                () -> assertEquals("Fotos/" + fileNameWithoutExtension, requestCaptor.getValue().key()),
                () -> assertArrayEquals(fileBytes, readRequestBody(bodyCaptor.getValue())),
                () -> assertEquals(
                        "https://" + BUCKET_NAME + ".s3." + REGION + ".amazonaws.com/Fotos/" + fileNameWithoutExtension,
                        result)
        );
    }

    @Test
    void testUploadBase64WithUrlSafeAndWhitespaceInput() throws Exception {
        // Arrange
        byte[] fileBytes = new byte[] { (byte) 251, (byte) 239, (byte) 255 };
        String input = Base64.getUrlEncoder().withoutPadding().encodeToString(fileBytes);

        ArgumentCaptor<RequestBody> bodyCaptor = ArgumentCaptor.forClass(RequestBody.class);

        when(s3Client.putObject(any(PutObjectRequest.class), any(RequestBody.class)))
                .thenReturn(PutObjectResponse.builder().build());

        // Act
        s3Service.uploadBase64("  \n" + input + "\r  ", "urlsafe-file");

        // Assert
        verify(s3Client).putObject(any(PutObjectRequest.class), bodyCaptor.capture());
        assertArrayEquals(fileBytes, readRequestBody(bodyCaptor.getValue()));
    }

    @Test
    void testUploadBase64WhenInputIsNull() {
        // Assert
        assertThrows(IllegalArgumentException.class,
                () -> s3Service.uploadBase64(null, FILE_NAME));

        verify(s3Client, never()).putObject(any(PutObjectRequest.class), any(RequestBody.class));
    }

    @Test
    void testUploadBase64WhenInputIsEmpty() {
        // Assert
        assertThrows(IllegalArgumentException.class,
                () -> s3Service.uploadBase64("   ", FILE_NAME));

        verify(s3Client, never()).putObject(any(PutObjectRequest.class), any(RequestBody.class));
    }

    @Test
    void testUploadBase64WhenDataUrlHasNoComma() {
        // Arrange
        String invalidInput = "data:image/png;base64ABCDEF";

        // Assert
        assertThrows(IllegalArgumentException.class,
                () -> s3Service.uploadBase64(invalidInput, FILE_NAME));

        verify(s3Client, never()).putObject(any(PutObjectRequest.class), any(RequestBody.class));
    }

    private byte[] readRequestBody(RequestBody body) throws IOException {
        try (InputStream is = body.contentStreamProvider().newStream()) {
            return is.readAllBytes();
        }
    }
}