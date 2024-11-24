package com.backend.stc;

import com.backend.stc.dto.EmailRequest;
import com.backend.stc.exception.InvalidInputException;
import com.backend.stc.service.EmailNotificationServiceImpl;
import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.mail.javamail.JavaMailSender;

import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class EmailNotificationServiceImplTest {

    @Mock
    private JavaMailSender emailSender;

    @InjectMocks
    private EmailNotificationServiceImpl emailNotificationService;

    private EmailRequest validEmailRequest;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        validEmailRequest = new EmailRequest("to@example.com", "Subject", "Body");
    }

    @Test
    void sendEmail_validRequest_shouldSendEmail() throws Exception {
        // Arrange
        MimeMessage message = mock(MimeMessage.class);  // Mock MimeMessage
        when(emailSender.createMimeMessage()).thenReturn(message);  // Mock the creation of MimeMessage

        // Act
        CompletableFuture<Void> result = emailNotificationService.sendEmail(validEmailRequest);

        result.get();

        // Assert
        verify(emailSender, times(1)).send(message);
    }

    @Test
    void sendEmail_invalidRequest_shouldThrowException() {
        // Arrange
        EmailRequest invalidRequest = new EmailRequest(null, null, null);

        // Act & Assert
        InvalidInputException exception = assertThrows(InvalidInputException.class, () -> {
            emailNotificationService.sendEmail(invalidRequest);
        });
        assertEquals("email, subject, and body are required.", exception.getMessage());
    }


}