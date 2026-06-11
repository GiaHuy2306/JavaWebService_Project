package com.project.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {
    private final ObjectProvider<JavaMailSender> mailSenderProvider;

    @Value("${app.mail.enabled:false}")
    private boolean mailEnabled;

    @Value("${spring.mail.username:no-reply@example.com}")
    private String fromEmail;

    public void sendTemporaryPassword(String toEmail, String temporaryPassword) {
        if (!mailEnabled) {
            log.info("Email sending is disabled. Temporary password for {} was generated but not sent.", toEmail);
            return;
        }

        JavaMailSender mailSender = mailSenderProvider.getIfAvailable();
        if (mailSender == null) {
            log.warn("JavaMailSender is not configured. Cannot send temporary password to {}", toEmail);
            return;
        }

        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromEmail);
        message.setTo(toEmail);
        message.setSubject("Dat lai mat khau");
        message.setText("""
                Mat khau tam thoi cua ban la: %s

                Vui long dang nhap va doi lai mat khau sau khi truy cap tai khoan.
                """.formatted(temporaryPassword));

        try {
            mailSender.send(message);
        } catch (MailException ex) {
            log.error("Cannot send temporary password email to {}", toEmail, ex);
            throw ex;
        }
    }
}
