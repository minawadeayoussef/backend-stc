package com.backend.stc.service;

import com.backend.stc.dto.EmailRequest;

public interface EmailNotificationService
{
    void sendEmail(EmailRequest emailRequest);

}
