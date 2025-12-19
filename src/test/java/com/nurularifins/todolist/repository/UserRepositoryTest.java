package com.nurularifins.todolist.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.nurularifins.todolist.entity.User;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.dao.DataIntegrityViolationException;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class UserRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private UserRepository userRepository;

    @Test
    @DisplayName("Should save user successfully")
    void shouldSaveUser() {
        // Given
        User user = new User("test@example.com", "hashedpassword", "Test User");

        // When
        User savedUser = userRepository.save(user);

        // Then
        assertThat(savedUser.getId()).isNotNull();
        assertThat(savedUser.getEmail()).isEqualTo("test@example.com");
        assertThat(savedUser.getCreatedAt()).isNotNull();
        assertThat(savedUser.getUpdatedAt()).isNotNull();
    }

    @Test
    @DisplayName("Should find user by email")
    void shouldFindByEmail() {
        // Given
        User user = new User("find@example.com", "pass123", "Find Me");
        entityManager.persist(user);

        // When
        Optional<User> found = userRepository.findByEmail("find@example.com");

        // Then
        assertThat(found).isPresent();
        assertThat(found.get().getFullName()).isEqualTo("Find Me");
    }

    @Test
    @DisplayName("Should enforce unique email")
    void shouldEnforceUniqueEmail() {
        // Given
        User user1 = new User("duplicate@example.com", "pass1", "User 1");
        entityManager.persist(user1);

        User user2 = new User("duplicate@example.com", "pass2", "User 2");

        // When/Then
        assertThatThrownBy(() -> userRepository.saveAndFlush(user2))
                .isInstanceOf(DataIntegrityViolationException.class);
    }

    @Test
    @DisplayName("Should check if email exists")
    void shouldCheckExistsByEmail() {
        // Given
        User user = new User("exists@example.com", "pass", "Exists");
        entityManager.persist(user);

        // When
        boolean exists = userRepository.existsByEmail("exists@example.com");
        boolean notExists = userRepository.existsByEmail("other@example.com");

        // Then
        assertThat(exists).isTrue();
        assertThat(notExists).isFalse();
    }

    @Test
    @DisplayName("Should find by verification token")
    void shouldFindByVerificationToken() {
        // Given
        User user = new User("token@example.com", "pass", "Token User");
        user.setVerificationToken("verify-123");
        entityManager.persist(user);

        // When
        Optional<User> found = userRepository.findByVerificationToken("verify-123");

        // Then
        assertThat(found).isPresent();
        assertThat(found.get().getEmail()).isEqualTo("token@example.com");
    }

    @Test
    @DisplayName("Should find by reset password token")
    void shouldFindByResetToken() {
        // Given
        User user = new User("reset@example.com", "pass", "Reset User");
        user.setResetPasswordToken("reset-123");
        entityManager.persist(user);

        // When
        Optional<User> found = userRepository.findByResetPasswordToken("reset-123");

        // Then
        assertThat(found).isPresent();
        assertThat(found.get().getEmail()).isEqualTo("reset@example.com");
    }
}
