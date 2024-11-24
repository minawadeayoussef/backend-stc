package com.backend.stc.service;

import com.backend.stc.dto.EmailRequest;
import com.backend.stc.exception.EmployeeNotFoundException;
import com.backend.stc.exception.InvalidInputException;
import com.backend.stc.model.Employee;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import jakarta.mail.internet.MimeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;

@Service
public class EmailNotificationServiceImpl implements EmailNotificationService
{

    private final JavaMailSender emailSender;
    private static final Logger logger = LoggerFactory.getLogger(EmailNotificationServiceImpl.class);

    public EmailNotificationServiceImpl(JavaMailSender emailSender) {
        this.emailSender = emailSender;
    }

    @Override
    @CircuitBreaker(name = "failBack", fallbackMethod = "sendEmailFallback")
    @Retry(name = "default", fallbackMethod = "retryFallback")
    @Async("virtualThreadExecutor")  // Ensure asynchronous execution
    public CompletableFuture<Void> sendEmail(EmailRequest emailRequest) {
        // Validate emailRequest
        validateEmailRequest(emailRequest);

        return CompletableFuture.runAsync(() -> {
            try {
                // Create MimeMessage and helper
                MimeMessage message = emailSender.createMimeMessage();
                MimeMessageHelper helper = new MimeMessageHelper(message, true);

                helper.setTo(emailRequest.getToEmail());
                helper.setSubject(emailRequest.getSubject());
                helper.setText(emailRequest.getBody(), true);

                // Log the sending of email
                logger.info("Sending email to: {} with subject: {}", emailRequest.getToEmail(), emailRequest.getSubject());

                // Send the email
                emailSender.send(message);

                // Log success
                logger.info("Email sent successfully to: {}", emailRequest.getToEmail());
            } catch (Exception e) {
                logger.error("Error sending email to: {}. Error: {}", emailRequest.getToEmail(), e.getMessage());
                throw new RuntimeException("Failed to send email", e);
            }
        });
    }


    private void validateEmailRequest(EmailRequest emailRequest) {
        if (emailRequest == null || emailRequest.getToEmail() == null || emailRequest.getSubject() == null || emailRequest.getBody() == null) {
            throw new InvalidInputException("email, subject, and body are required.");
        }
    }

    // Fallback method for retry in case of failure
    public void sendEmailFallback(EmailRequest emailRequest, Exception e) {
        logger.error("Fallback triggered for email to: {}. Error: {}", emailRequest.getToEmail(), e.getMessage());
    }
}
