-- Categories Table
-- Phase 1: Basic Category without user relation (will be added in Phase 2)

CREATE TABLE categories (
    id BINARY(16) PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    color VARCHAR(7) NOT NULL DEFAULT '#3B82F6',
    icon VARCHAR(50),
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,

    INDEX idx_categories_name (name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Add category_id foreign key to tasks table
ALTER TABLE tasks
ADD COLUMN category_id BINARY(16),
ADD CONSTRAINT fk_tasks_category
    FOREIGN KEY (category_id) REFERENCES categories(id)
    ON DELETE SET NULL;

CREATE INDEX idx_tasks_category ON tasks(category_id);
