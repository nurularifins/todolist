package com.nurularifins.todolist.repository;

import com.nurularifins.todolist.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository for Category entity operations.
 */
@Repository
public interface CategoryRepository extends JpaRepository<Category, UUID> {

    /**
     * Find category by name.
     */
    Optional<Category> findByName(String name);

    /**
     * Find all categories ordered by name ascending.
     */
    List<Category> findAllByOrderByNameAsc();

    /**
     * Check if category with given name exists.
     */
    boolean existsByName(String name);
}
