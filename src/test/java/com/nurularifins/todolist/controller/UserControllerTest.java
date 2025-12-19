package com.nurularifins.todolist.controller;

import com.nurularifins.todolist.entity.User;

import com.nurularifins.todolist.repository.UserRepository;
import com.nurularifins.todolist.service.TaskService;
import com.nurularifins.todolist.service.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TaskService taskService;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private UserService userService;

    @Test
    @DisplayName("Should display dashboard with user stats")
    @WithMockUser(username = "user@example.com")
    void shouldDisplayDashboard() throws Exception {
        User user = new User("user@example.com", "hash", "Test User");
        when(userRepository.findByEmail("user@example.com")).thenReturn(Optional.of(user));
        when(taskService.getAllTasks(any(User.class))).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/dashboard"))
                .andExpect(status().isOk())
                .andExpect(view().name("user/dashboard"))
                .andExpect(model().attributeExists("user"))
                .andExpect(model().attributeExists("totalTasks"))
                .andExpect(model().attributeExists("completedTasks"))
                .andExpect(model().attributeExists("pendingTasks"));
    }

    @Test
    @DisplayName("Should display user profile")
    @WithMockUser(username = "user@example.com")
    void shouldDisplayUserProfile() throws Exception {
        User user = new User("user@example.com", "hash", "Test User");
        when(userRepository.findByEmail("user@example.com")).thenReturn(Optional.of(user));

        mockMvc.perform(get("/profile"))
                .andExpect(status().isOk())
                .andExpect(view().name("user/profile"))
                .andExpect(model().attribute("user", user));
    }
}
