package com.nurularifins.todolist.controller;

import com.nurularifins.todolist.dto.TaskDto;
import com.nurularifins.todolist.enums.TaskPriority;
import com.nurularifins.todolist.enums.TaskStatus;
import com.nurularifins.todolist.exception.TaskNotFoundException;
import com.nurularifins.todolist.service.TaskService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.UUID;

/**
 * Controller for task management operations.
 */
@Controller
@RequestMapping("/tasks")
public class TaskController {

    private final TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    /**
     * Display task list with optional filtering.
     */
    @GetMapping
    public String listTasks(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String priority,
            @RequestParam(required = false) String search,
            Model model) {

        List<TaskDto> tasks;

        if (search != null && !search.isBlank()) {
            tasks = taskService.searchTasks(search);
        } else if (status != null && !status.isBlank()) {
            tasks = taskService.getTasksByStatus(TaskStatus.valueOf(status));
        } else if (priority != null && !priority.isBlank()) {
            tasks = taskService.getTasksByPriority(TaskPriority.valueOf(priority));
        } else {
            tasks = taskService.getAllTasks();
        }

        model.addAttribute("tasks", tasks);
        model.addAttribute("statuses", TaskStatus.values());
        model.addAttribute("priorities", TaskPriority.values());
        model.addAttribute("currentStatus", status);
        model.addAttribute("currentPriority", priority);
        model.addAttribute("searchQuery", search);

        return "tasks/list";
    }

    /**
     * Display task detail page.
     */
    @GetMapping("/{id}")
    public String viewTask(@PathVariable UUID id, Model model) {
        TaskDto task = taskService.getTaskById(id);
        model.addAttribute("task", task);
        return "tasks/detail";
    }

    /**
     * Display create task form.
     */
    @GetMapping("/new")
    public String showCreateForm(Model model) {
        model.addAttribute("task", new TaskDto());
        model.addAttribute("statuses", TaskStatus.values());
        model.addAttribute("priorities", TaskPriority.values());
        model.addAttribute("isEdit", false);
        return "tasks/form";
    }

    /**
     * Create a new task.
     */
    @PostMapping
    public String createTask(
            @Valid @ModelAttribute("task") TaskDto taskDto,
            BindingResult result,
            Model model,
            RedirectAttributes redirectAttributes) {

        if (result.hasErrors()) {
            model.addAttribute("statuses", TaskStatus.values());
            model.addAttribute("priorities", TaskPriority.values());
            model.addAttribute("isEdit", false);
            return "tasks/form";
        }

        taskService.createTask(taskDto);
        redirectAttributes.addFlashAttribute("success", "Task created successfully!");
        return "redirect:/tasks";
    }

    /**
     * Display edit task form.
     */
    @GetMapping("/{id}/edit")
    public String showEditForm(@PathVariable UUID id, Model model) {
        TaskDto task = taskService.getTaskById(id);
        model.addAttribute("task", task);
        model.addAttribute("statuses", TaskStatus.values());
        model.addAttribute("priorities", TaskPriority.values());
        model.addAttribute("isEdit", true);
        return "tasks/form";
    }

    /**
     * Update an existing task.
     */
    @PostMapping("/{id}")
    public String updateTask(
            @PathVariable UUID id,
            @Valid @ModelAttribute("task") TaskDto taskDto,
            BindingResult result,
            Model model,
            RedirectAttributes redirectAttributes) {

        if (result.hasErrors()) {
            model.addAttribute("statuses", TaskStatus.values());
            model.addAttribute("priorities", TaskPriority.values());
            model.addAttribute("isEdit", true);
            return "tasks/form";
        }

        taskService.updateTask(id, taskDto);
        redirectAttributes.addFlashAttribute("success", "Task updated successfully!");
        return "redirect:/tasks/" + id;
    }

    /**
     * Delete (archive) a task.
     */
    @PostMapping("/{id}/delete")
    public String deleteTask(@PathVariable UUID id, RedirectAttributes redirectAttributes) {
        taskService.deleteTask(id);
        redirectAttributes.addFlashAttribute("success", "Task deleted successfully!");
        return "redirect:/tasks";
    }

    /**
     * Mark task as complete.
     */
    @PostMapping("/{id}/complete")
    public String markAsComplete(@PathVariable UUID id, RedirectAttributes redirectAttributes) {
        taskService.markAsComplete(id);
        redirectAttributes.addFlashAttribute("success", "Task marked as complete!");
        return "redirect:/tasks/" + id;
    }

    /**
     * Handle task not found exception.
     */
    @ExceptionHandler(TaskNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String handleTaskNotFound(TaskNotFoundException ex, Model model) {
        model.addAttribute("error", ex.getMessage());
        return "error/404";
    }
}
