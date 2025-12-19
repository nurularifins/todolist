-- Add user_id column to tasks table
ALTER TABLE tasks
ADD COLUMN user_id BINARY(16) AFTER id;

-- Add foreign key constraint
ALTER TABLE tasks
ADD CONSTRAINT fk_tasks_user
    FOREIGN KEY (user_id) REFERENCES users(id)
    ON DELETE CASCADE;

-- Add index for performance
CREATE INDEX idx_tasks_user_id ON tasks(user_id);
