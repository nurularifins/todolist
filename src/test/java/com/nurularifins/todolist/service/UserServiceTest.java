package com.nurularifins.todolist.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.nurularifins.todolist.dto.UserDto;
import com.nurularifins.todolist.entity.User;
import com.nurularifins.todolist.repository.UserRepository;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private EmailService emailService;

    @InjectMocks
    private UserService userService;

    @Test
    @DisplayName("Should initiate password reset")
    void shouldInitiatePasswordReset() {
        // Given
        String email = "user@example.com";
        User user = new User(email, "hash", "User");
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenAnswer(i -> i.getArgument(0));

        // When
        userService.initiatePasswordReset(email);

        // Then
        assertThat(user.getResetPasswordToken()).isNotNull();
        assertThat(user.getResetPasswordExpiry()).isNotNull();
        verify(emailService).sendPasswordResetEmail(eq(email), any(String.class));
        verify(userRepository).save(user);
    }

    @Test
    @DisplayName("Should throw exception when email not found for reset")
    void shouldThrowExceptionWhenEmailNotFoundForReset() {
        // Given
        String email = "unknown@example.com";
        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

        // When/Then
        assertThatThrownBy(() -> userService.initiatePasswordReset(email))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("User not found");

        verify(emailService, org.mockito.Mockito.never()).sendPasswordResetEmail(any(), any());
    }

    @Test
    @DisplayName("Should complete password reset")
    void shouldCompletePasswordReset() {
        // Given
        String token = "valid-reset-token";
        String newPassword = "NewPassword123!";
        User user = new User("user@example.com", "oldHash", "User");
        user.setResetPasswordToken(token);
        user.setResetPasswordExpiry(java.time.LocalDateTime.now().plusHours(1));

        when(userRepository.findByResetPasswordToken(token)).thenReturn(Optional.of(user));
        when(passwordEncoder.encode(newPassword)).thenReturn("newHash");
        when(userRepository.save(any(User.class))).thenReturn(user);

        // When
        userService.completePasswordReset(token, newPassword);

        // Then
        assertThat(user.getPasswordHash()).isEqualTo("newHash");
        assertThat(user.getResetPasswordToken()).isNull();
        assertThat(user.getResetPasswordExpiry()).isNull();
        verify(userRepository).save(user);
    }

    @Test
    @DisplayName("Should throw exception when reset token invalid")
    void shouldThrowExceptionWhenResetTokenInvalid() {
        // Given
        when(userRepository.findByResetPasswordToken("invalid")).thenReturn(Optional.empty());

        // When/Then
        assertThatThrownBy(() -> userService.completePasswordReset("invalid", "pass"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Invalid reset token");
    }

    @Test
    @DisplayName("Should throw exception when reset token expired")
    void shouldThrowExceptionWhenResetTokenExpired() {
        // Given
        String token = "expired-token";
        User user = new User();
        user.setResetPasswordToken(token);
        user.setResetPasswordExpiry(java.time.LocalDateTime.now().minusMinutes(1));

        when(userRepository.findByResetPasswordToken(token)).thenReturn(Optional.of(user));

        // When/Then
        assertThatThrownBy(() -> userService.completePasswordReset(token, "pass"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Reset token expired");
    }

    @Test
    @DisplayName("Should register user successfully")
    void shouldRegisterUser() {
        // Given
        UserDto dto = new UserDto("new@example.com", "Password123!", "New User");
        when(userRepository.existsByEmail(dto.getEmail())).thenReturn(false);
        when(passwordEncoder.encode(dto.getPassword())).thenReturn("encodedHash");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
            User user = invocation.getArgument(0);
            user.setId(java.util.UUID.randomUUID());
            return user;
        });

        // When
        User result = userService.registerUser(dto);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getEmail()).isEqualTo("new@example.com");
        assertThat(result.getFullName()).isEqualTo("New User");
        assertThat(result.getPasswordHash()).isEqualTo("encodedHash");

        verify(userRepository).save(any(User.class));
    }

    @Test
    @DisplayName("Should throw exception when email already exists")
    void shouldThrowExceptionWhenEmailExists() {
        // Given
        UserDto dto = new UserDto("existing@example.com", "Pass123", "Existing User");
        when(userRepository.existsByEmail(dto.getEmail())).thenReturn(true);

        // When/Then
        assertThatThrownBy(() -> userService.registerUser(dto))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Email already registered");
    }

    @Test
    @DisplayName("Should verify email with valid token")
    void shouldVerifyEmail() {
        // Given
        String token = "valid-token";
        User user = new User("user@example.com", "hash", "User");
        user.setVerificationToken(token);

        when(userRepository.findByVerificationToken(token)).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenReturn(user);

        // When
        userService.verifyEmail(token);

        // Then
        assertThat(user.isEmailVerified()).isTrue();
        assertThat(user.getVerificationToken()).isNull();
        verify(userRepository).save(user);
    }

    @Test
    @DisplayName("Should throw exception when verification token invalid")
    void shouldThrowExceptionWhenTokenInvalid() {
        // Given
        when(userRepository.findByVerificationToken("invalid")).thenReturn(Optional.empty());

        // When/Then
        assertThatThrownBy(() -> userService.verifyEmail("invalid"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Invalid verification token");
    }
}
