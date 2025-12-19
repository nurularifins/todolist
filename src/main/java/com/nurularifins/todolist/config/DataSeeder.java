package com.nurularifins.todolist.config;

import com.nurularifins.todolist.entity.Task;
import com.nurularifins.todolist.entity.User;
import com.nurularifins.todolist.enums.TaskPriority;
import com.nurularifins.todolist.enums.TaskStatus;
import com.nurularifins.todolist.repository.TaskRepository;
import com.nurularifins.todolist.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@Profile("dev")
public class DataSeeder implements CommandLineRunner {

    private final UserRepository userRepository;
    private final TaskRepository taskRepository;
    private final PasswordEncoder passwordEncoder;

    public DataSeeder(UserRepository userRepository, TaskRepository taskRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.taskRepository = taskRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        if (userRepository.findByEmail("user@example.com").isEmpty()) {
            createUserAndTasks();
            System.out.println("----------------------------------------------------------");
            System.out.println("DATA SEEDER: User created for development");
            System.out.println("Username: user@example.com");
            System.out.println("Password: password");
            System.out.println("----------------------------------------------------------");
        }
    }

    private void createUserAndTasks() {
        // Create User
        User user = new User();
        user.setEmail("user@example.com");
        user.setFullName("Demo User");
        user.setPasswordHash(passwordEncoder.encode("password"));
        user.setEmailVerified(true); // Auto-verify

        User savedUser = userRepository.save(user);

        // Create Sample Tasks
        createTask(savedUser, "Complete Project Implementation", "Finish the remaining features for the todo list app",
                TaskStatus.IN_PROGRESS, TaskPriority.HIGH, LocalDateTime.now().plusDays(2));
        createTask(savedUser, "Review Pull Requests", "Check code quality and run tests", TaskStatus.TODO,
                TaskPriority.MEDIUM, LocalDateTime.now().plusDays(1));
        createTask(savedUser, "Buy Groceries", "Milk, Eggs, Bread", TaskStatus.DONE, TaskPriority.LOW,
                LocalDateTime.now().minusDays(1));
        createTask(savedUser, "Schedule Dentist Appointment", "Call Dr. Smith", TaskStatus.TODO, TaskPriority.LOW,
                null);
    }

    private void createTask(User user, String title, String description, TaskStatus status, TaskPriority priority,
            LocalDateTime dueDate) {
        Task task = new Task();
        task.setUser(user);
        task.setTitle(title);
        task.setDescription(description);
        task.setStatus(status);
        task.setPriority(priority);
        task.setDueDate(dueDate);
        if (status == TaskStatus.DONE) {
            task.setCompletedAt(LocalDateTime.now());
        }

        taskRepository.save(task);
    }
}
