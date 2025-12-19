package com.nurularifins.todolist.controller;

import com.nurularifins.todolist.service.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PasswordResetController.class)
@AutoConfigureMockMvc(addFilters = false) // Bypass security filters for logic testing, or configure explicitly
class PasswordResetControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @Test
    @DisplayName("Should display forgot password request form")
    void shouldDisplayForgotPasswordForm() throws Exception {
        mockMvc.perform(get("/auth/forgot-password"))
                .andExpect(status().isOk())
                .andExpect(view().name("auth/forgot-password"));
    }

    @Test
    @DisplayName("Should process forgot password request")
    void shouldProcessForgotPasswordRequest() throws Exception {
        mockMvc.perform(post("/auth/forgot-password")
                .param("email", "user@example.com")
                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/auth/login?resetSent"));

        verify(userService).initiatePasswordReset("user@example.com");
    }

    @Test
    @DisplayName("Should handle error when processing forgot password request")
    void shouldHandleErrorInForgotPasswordRequest() throws Exception {
        doThrow(new IllegalArgumentException("User not found"))
                .when(userService).initiatePasswordReset("unknown@example.com");

        mockMvc.perform(post("/auth/forgot-password")
                .param("email", "unknown@example.com")
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name("auth/forgot-password"))
                .andExpect(model().attributeExists("error"));
    }

    @Test
    @DisplayName("Should display reset password form")
    void shouldDisplayResetPasswordForm() throws Exception {
        mockMvc.perform(get("/auth/reset-password")
                .param("token", "valid-token"))
                .andExpect(status().isOk())
                .andExpect(view().name("auth/reset-password"))
                .andExpect(model().attribute("token", "valid-token"));
    }

    @Test
    @DisplayName("Should process reset password")
    void shouldProcessResetPassword() throws Exception {
        mockMvc.perform(post("/auth/reset-password")
                .param("token", "valid-token")
                .param("password", "NewPass123")
                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/auth/login?resetSuccess"));

        verify(userService).completePasswordReset("valid-token", "NewPass123");
    }

    @Test
    @DisplayName("Should handle error when resetting password")
    void shouldHandleErrorInResetPassword() throws Exception {
        doThrow(new IllegalArgumentException("Invalid token"))
                .when(userService).completePasswordReset(eq("invalid"), any());

        mockMvc.perform(post("/auth/reset-password")
                .param("token", "invalid")
                .param("password", "Pass123")
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name("auth/reset-password"))
                .andExpect(model().attributeExists("error"));
    }
}
