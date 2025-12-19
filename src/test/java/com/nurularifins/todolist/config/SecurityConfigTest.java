package com.nurularifins.todolist.config;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.web.servlet.MockMvc;
import com.nurularifins.todolist.controller.AuthController;
import com.nurularifins.todolist.controller.PasswordResetController;
import com.nurularifins.todolist.service.UserService;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrlPattern;

@WebMvcTest(controllers = { AuthController.class, PasswordResetController.class })
@Import(SecurityConfig.class)
class SecurityConfigTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @MockBean
    private UserDetailsService userDetailsService;

    @Test
    @DisplayName("Should allow public access to login page")
    void shouldAllowPublicAccessToLoginPage() throws Exception {
        mockMvc.perform(get("/login"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Should allow public access to register page")
    void shouldAllowPublicAccessToRegisterPage() throws Exception {
        mockMvc.perform(get("/register"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Should allow public access to forgot password page")
    void shouldAllowPublicAccessToForgotPasswordPage() throws Exception {
        mockMvc.perform(get("/auth/forgot-password"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Should allow public access to reset password page")
    void shouldAllowPublicAccessToResetPasswordPage() throws Exception {
        // Token parameter is required for the controller logic, but security should
        // allow access first.
        // If token is missing, controller might throw exception or return 400, but NOT
        // 401/403.
        // Actually, without token, controller might return 400 Bad Request if
        // @RequestParam is required.
        // Let's provide a token to be safe and check for 200 OK.
        mockMvc.perform(get("/auth/reset-password").param("token", "dummy"))
                .andExpect(status().isOk());
    }
}
