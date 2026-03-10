package com.ayd2.congress.services;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import com.ayd2.congress.services.mail.MailServiceImpl;

@ExtendWith(MockitoExtension.class)
public class MailServiceImplTest {

    private static final String TO = "juan@mail.com";
    private static final String SUBJECT = "Asunto de prueba";
    private static final String TEXT = "Texto de prueba";

    private static final String EMAIL = "user@mail.com";
    private static final String CODE = "123456";

    @Mock
    private JavaMailSender mailSender;

    @InjectMocks
    private MailServiceImpl mailService;

    @Test
    void testSendText() {
        // Arrange
        ArgumentCaptor<SimpleMailMessage> messageCaptor = ArgumentCaptor.forClass(SimpleMailMessage.class);

        // Act
        mailService.sendText(TO, SUBJECT, TEXT);

        // Assert
        assertAll(
                () -> verify(mailSender).send(messageCaptor.capture()),
                () -> assertEquals(TO, messageCaptor.getValue().getTo()[0]),
                () -> assertEquals(SUBJECT, messageCaptor.getValue().getSubject()),
                () -> assertEquals(TEXT, messageCaptor.getValue().getText())
        );
    }

    @Test
    void testSendCode() {
        // Arrange
        MailServiceImpl spy = spy(mailService);

        String expectedBody = """
                Hola,

                Este es tu código de recuperacion de contrase;a:

                %s
                """.formatted(CODE);

        // Act
        spy.sendCode(SUBJECT, EMAIL, CODE);

        // Assert
        verify(spy).sendText(
                EMAIL,
                SUBJECT,
                expectedBody
        );
    }

    @Test
    void testSendCodeAlsoSendsMail() {
        // Arrange
        ArgumentCaptor<SimpleMailMessage> messageCaptor = ArgumentCaptor.forClass(SimpleMailMessage.class);

        String expectedBody = """
                Hola,

                Este es tu código de recuperacion de contrase;a:

                %s
                """.formatted(CODE);

        // Act
        mailService.sendCode(SUBJECT, EMAIL, CODE);

        // Assert
        assertAll(
                () -> verify(mailSender).send(messageCaptor.capture()),
                () -> assertEquals(EMAIL, messageCaptor.getValue().getTo()[0]),
                () -> assertEquals(SUBJECT, messageCaptor.getValue().getSubject()),
                () -> assertEquals(expectedBody, messageCaptor.getValue().getText())
        );
    }
}