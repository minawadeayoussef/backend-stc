package com.backend.stc.service;

import com.backend.stc.dto.EmailRequest;

import java.util.concurrent.CompletableFuture;

public interface EmailNotificationService
{
    CompletableFuture<Void> sendEmail(EmailRequest emailRequest);

}
