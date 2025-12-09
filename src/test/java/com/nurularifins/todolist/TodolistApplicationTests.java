package com.nurularifins.todolist;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Basic application tests.
 * Note: Full context loading tests require database connection.
 * Use IntegrationTestBase for tests that need database.
 */
@DisplayName("TodoList Application")
class TodolistApplicationTests {

    @Test
    @DisplayName("Application main class exists")
    void applicationClassExists() {
        // Verify main application class is available
        assertThat(TodolistApplication.class).isNotNull();
    }

}
