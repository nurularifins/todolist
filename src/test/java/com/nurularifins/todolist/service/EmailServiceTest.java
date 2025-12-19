package com.nurularifins.todolist.service;

import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.util.ReflectionTestUtils;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("EmailService")
class EmailServiceTest {

    @Mock
    private JavaMailSender mailSender;

    @Mock
    private TemplateEngine templateEngine;

    @Mock
    private MimeMessage mimeMessage;

    private EmailService emailService;

    @BeforeEach
    void setUp() {
        emailService = new EmailServiceImpl(mailSender, templateEngine);
        ReflectionTestUtils.setField(emailService, "baseUrl", "http://localhost:8080");
        ReflectionTestUtils.setField(emailService, "fromEmail", "test@example.com");

        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);
    }

    @Test
    @DisplayName("Should send verification email")
    void shouldSendVerificationEmail() throws Exception {
        // Given
        String to = "user@example.com";
        String token = "verification-token-123";
        String htmlContent = "<html><body>Link: http://localhost:8080/auth/verify?token=...</body></html>";

        when(templateEngine.process(eq("email/verification"), any(Context.class))).thenReturn(htmlContent);

        // When
        emailService.sendVerificationEmail(to, token);

        // Then
        verify(mailSender).send(mimeMessage);

        // Use ArgumentCaptor to verify Context logic if needed, or just verify template
        // name
        verify(templateEngine).process(eq("email/verification"), any(Context.class));
    }

    @Test
    @DisplayName("Should send password reset email")
    void shouldSendPasswordResetEmail() throws Exception {
        // Given
        String to = "user@example.com";
        String token = "reset-token-456";
        String htmlContent = "<html><body>Reset Link: ...</body></html>";

        when(templateEngine.process(eq("email/password-reset"), any(Context.class))).thenReturn(htmlContent);

        // When
        emailService.sendPasswordResetEmail(to, token);

        // Then
        verify(mailSender).send(mimeMessage);
        verify(templateEngine).process(eq("email/password-reset"), any(Context.class));
    }
}
