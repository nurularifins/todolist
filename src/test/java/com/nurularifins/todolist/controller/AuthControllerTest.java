package com.nurularifins.todolist.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import com.nurularifins.todolist.dto.UserDto;
import com.nurularifins.todolist.entity.User;
import com.nurularifins.todolist.service.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(AuthController.class)
@Import(com.nurularifins.todolist.config.SecurityConfig.class)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @Test
    @DisplayName("Should display registration form")
    void shouldShowRegistrationForm() throws Exception {
        mockMvc.perform(get("/register"))
                .andExpect(status().isOk())
                .andExpect(view().name("auth/register"))
                .andExpect(model().attributeExists("user"));
    }

    @Test
    @DisplayName("Should register user successfully")
    void shouldRegisterUser() throws Exception {
        UserDto dto = new UserDto("new@example.com", "Password123!", "New User");

        mockMvc.perform(post("/register")
                .with(csrf())
                .param("email", dto.getEmail())
                .param("password", dto.getPassword())
                .param("fullName", dto.getFullName()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login?registered"));

        verify(userService).registerUser(any(UserDto.class));
    }

    @Test
    @DisplayName("Should return validation error when input invalid")
    void shouldReturnValidationError() throws Exception {
        mockMvc.perform(post("/register")
                .with(csrf())
                .param("email", "invalid-email")
                .param("password", "short")
                .param("fullName", ""))
                .andExpect(status().isOk())
                .andExpect(view().name("auth/register"))
                .andExpect(model().attributeHasErrors("user"));
    }

    @Test
    @DisplayName("Should verify email successfully")
    void shouldVerifyEmail() throws Exception {
        mockMvc.perform(get("/verify-email")
                .param("token", "valid-token"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login?verified"));

        verify(userService).verifyEmail("valid-token");
    }
}
