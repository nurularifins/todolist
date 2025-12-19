package com.nurularifins.todolist.controller;

import com.nurularifins.todolist.dto.TaskDto;
import com.nurularifins.todolist.entity.User;
import com.nurularifins.todolist.enums.TaskStatus;
import com.nurularifins.todolist.repository.UserRepository;
import com.nurularifins.todolist.service.TaskService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.nurularifins.todolist.dto.UserProfileDto;
import com.nurularifins.todolist.dto.PasswordChangeDto;
import com.nurularifins.todolist.service.UserService;
import jakarta.validation.Valid;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.util.List;

@Controller
public class UserController {

    private final UserRepository userRepository;
    private final TaskService taskService;
    private final UserService userService;

    public UserController(UserRepository userRepository, TaskService taskService, UserService userService) {
        this.userRepository = userRepository;
        this.taskService = taskService;
        this.userService = userService;
    }

    private User getAuthenticatedUser(Principal principal) {
        return userRepository.findByEmail(principal.getName())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model, Principal principal) {
        User user = getAuthenticatedUser(principal);
        List<TaskDto> allTasks = taskService.getAllTasks(user);

        long totalTasks = allTasks.size();
        long completedTasks = allTasks.stream().filter(t -> t.getStatus() == TaskStatus.DONE).count();
        long pendingTasks = totalTasks - completedTasks;

        model.addAttribute("user", user);
        model.addAttribute("totalTasks", totalTasks);
        model.addAttribute("completedTasks", completedTasks);
        model.addAttribute("pendingTasks", pendingTasks);
        // We could also add "recentTasks" here (e.g., top 5 sorted by due date or
        // created date)

        return "user/dashboard";
    }

    @GetMapping("/profile")
    public String profile(Model model, Principal principal) {
        User user = getAuthenticatedUser(principal);
        model.addAttribute("user", user);
        return "user/profile";
    }

    @GetMapping("/profile/edit")
    public String editProfile(Model model, Principal principal) {
        User user = getAuthenticatedUser(principal);
        model.addAttribute("userProfileDto", new UserProfileDto(user.getFullName()));
        return "user/profile-edit";
    }

    @PostMapping("/profile/edit")
    public String updateProfile(@Valid @ModelAttribute("userProfileDto") UserProfileDto dto,
            BindingResult result,
            Principal principal,
            RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            return "user/profile-edit";
        }

        try {
            User user = getAuthenticatedUser(principal);
            userService.updateProfile(user, dto);
            redirectAttributes.addFlashAttribute("success", "Profile updated successfully");
            return "redirect:/profile";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Failed to update profile: " + e.getMessage());
            return "redirect:/profile/edit";
        }
    }

    @GetMapping("/profile/change-password")
    public String changePassword(Model model) {
        model.addAttribute("passwordChangeDto", new PasswordChangeDto());
        return "user/password-change";
    }

    @PostMapping("/profile/change-password")
    public String updatePassword(@Valid @ModelAttribute("passwordChangeDto") PasswordChangeDto dto,
            BindingResult result,
            Principal principal,
            RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            return "user/password-change";
        }

        try {
            User user = getAuthenticatedUser(principal);
            userService.changePassword(user, dto);
            redirectAttributes.addFlashAttribute("success", "Password changed successfully");
            return "redirect:/profile";
        } catch (IllegalArgumentException e) {
            result.rejectValue("oldPassword", "error.passwordChangeDto", e.getMessage());
            return "user/password-change";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Failed to change password: " + e.getMessage());
            return "redirect:/profile/change-password";
        }
    }
}
