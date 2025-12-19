package com.nurularifins.todolist.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

@Service
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;

    @Value("${app.base-url:http://localhost:8080}")
    private String baseUrl;

    @Value("${spring.mail.username:noreply@todolist.com}")
    private String fromEmail;

    public EmailServiceImpl(JavaMailSender mailSender, TemplateEngine templateEngine) {
        this.mailSender = mailSender;
        this.templateEngine = templateEngine;
    }

    @Override
    @Async
    public void sendVerificationEmail(String to, String token) {
        String subject = "Verify your email";
        Context context = new Context();
        context.setVariable("verificationUrl", baseUrl + "/auth/verify?token=" + token);

        sendHtmlEmail(to, subject, "email/verification", context);
    }

    @Override
    @Async
    public void sendPasswordResetEmail(String to, String token) {
        String subject = "Reset your password";
        Context context = new Context();
        context.setVariable("resetUrl", baseUrl + "/auth/reset-password?token=" + token);

        sendHtmlEmail(to, subject, "email/password-reset", context);
    }

    private void sendHtmlEmail(String to, String subject, String templateName, Context context) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(fromEmail);
            helper.setTo(to);
            helper.setSubject(subject);

            String htmlContent = templateEngine.process(templateName, context);
            helper.setText(htmlContent, true);

            mailSender.send(message);
        } catch (MessagingException e) {
            // Log error, don't throw to avoid breaking the user flow (since it's async
            // usually)
            // For now, we print stack trace or use a logger
            e.printStackTrace();
            throw new RuntimeException("Failed to send email", e);
        }
    }
}
