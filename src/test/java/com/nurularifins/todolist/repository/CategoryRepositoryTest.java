package com.nurularifins.todolist.repository;

import com.nurularifins.todolist.entity.Category;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
@DisplayName("CategoryRepository")
class CategoryRepositoryTest {

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private TestEntityManager entityManager;

    private Category sampleCategory;

    @BeforeEach
    void setUp() {
        sampleCategory = new Category();
        sampleCategory.setName("Work");
        sampleCategory.setColor("#3B82F6");
    }

    @Nested
    @DisplayName("Save operations")
    class SaveOperations {

        @Test
        @DisplayName("should save category with generated UUID")
        void shouldSaveCategory() {
            // When
            Category saved = categoryRepository.save(sampleCategory);

            // Then
            assertThat(saved.getId()).isNotNull();
            assertThat(saved.getName()).isEqualTo("Work");
            assertThat(saved.getColor()).isEqualTo("#3B82F6");
            assertThat(saved.getCreatedAt()).isNotNull();
        }

        @Test
        @DisplayName("should update existing category")
        void shouldUpdateCategory() {
            // Given
            Category saved = categoryRepository.save(sampleCategory);
            UUID id = saved.getId();
            entityManager.flush();
            entityManager.clear();

            // When
            Category toUpdate = categoryRepository.findById(id).orElseThrow();
            toUpdate.setName("Personal");
            toUpdate.setColor("#10B981");
            Category updated = categoryRepository.save(toUpdate);

            // Then
            assertThat(updated.getName()).isEqualTo("Personal");
            assertThat(updated.getColor()).isEqualTo("#10B981");
        }
    }

    @Nested
    @DisplayName("Find operations")
    class FindOperations {

        @Test
        @DisplayName("should find category by ID")
        void shouldFindCategoryById() {
            // Given
            Category saved = categoryRepository.save(sampleCategory);
            entityManager.flush();
            entityManager.clear();

            // When
            Optional<Category> found = categoryRepository.findById(saved.getId());

            // Then
            assertThat(found).isPresent();
            assertThat(found.get().getName()).isEqualTo("Work");
        }

        @Test
        @DisplayName("should return empty when category not found")
        void shouldReturnEmptyWhenNotFound() {
            // When
            Optional<Category> found = categoryRepository.findById(UUID.randomUUID());

            // Then
            assertThat(found).isEmpty();
        }

        @Test
        @DisplayName("should find category by name")
        void shouldFindByName() {
            // Given
            categoryRepository.save(sampleCategory);

            Category personal = new Category();
            personal.setName("Personal");
            personal.setColor("#10B981");
            categoryRepository.save(personal);

            entityManager.flush();
            entityManager.clear();

            // When
            Optional<Category> found = categoryRepository.findByName("Work");

            // Then
            assertThat(found).isPresent();
            assertThat(found.get().getColor()).isEqualTo("#3B82F6");
        }

        @Test
        @DisplayName("should find all categories ordered by name")
        void shouldFindAllOrderedByName() {
            // Given
            categoryRepository.save(sampleCategory); // Work

            Category personal = new Category();
            personal.setName("Personal");
            personal.setColor("#10B981");
            categoryRepository.save(personal);

            Category urgent = new Category();
            urgent.setName("Urgent");
            urgent.setColor("#EF4444");
            categoryRepository.save(urgent);

            entityManager.flush();
            entityManager.clear();

            // When
            List<Category> categories = categoryRepository.findAllByOrderByNameAsc();

            // Then
            assertThat(categories).hasSize(3);
            assertThat(categories.get(0).getName()).isEqualTo("Personal");
            assertThat(categories.get(1).getName()).isEqualTo("Urgent");
            assertThat(categories.get(2).getName()).isEqualTo("Work");
        }
    }

    @Nested
    @DisplayName("Delete operations")
    class DeleteOperations {

        @Test
        @DisplayName("should delete category")
        void shouldDeleteCategory() {
            // Given
            Category saved = categoryRepository.save(sampleCategory);
            UUID id = saved.getId();
            entityManager.flush();
            entityManager.clear();

            // When
            categoryRepository.deleteById(id);
            entityManager.flush();
            entityManager.clear();

            // Then
            assertThat(categoryRepository.findById(id)).isEmpty();
        }
    }
}
