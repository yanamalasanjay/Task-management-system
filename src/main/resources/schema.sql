-- Task Management System Database Schema
-- Author: Yanamala Sanjay

-- Create database
CREATE DATABASE IF NOT EXISTS task_management_db;
USE task_management_db;

-- Users table
CREATE TABLE IF NOT EXISTS users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    department VARCHAR(100),
    designation VARCHAR(100),
    employee_id VARCHAR(50),
    email_digest_enabled BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Tasks table
CREATE TABLE IF NOT EXISTS tasks (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    title VARCHAR(500) NOT NULL,
    description TEXT,
    status VARCHAR(50) NOT NULL,
    priority VARCHAR(50) NOT NULL,
    due_date DATE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    completed_at TIMESTAMP,
    is_recurring BOOLEAN DEFAULT FALSE,
    recurrence_type VARCHAR(50),
    template_id BIGINT,
    reminder_sent BOOLEAN DEFAULT FALSE,
    category VARCHAR(100),
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- Task templates table (for recurring tasks)
CREATE TABLE IF NOT EXISTS task_templates (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    title VARCHAR(500) NOT NULL,
    description TEXT,
    priority VARCHAR(50) NOT NULL,
    recurrence_type VARCHAR(50) NOT NULL,
    cron_expression VARCHAR(100),
    schedule_time TIME,
    day_of_week INT,  -- 1=Monday, 7=Sunday
    day_of_month INT,  -- 1-31
    days_to_complete INT DEFAULT 1,
    category VARCHAR(100),
    is_active BOOLEAN DEFAULT TRUE,
    last_generated TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- Add foreign key for template_id in tasks table
ALTER TABLE tasks
ADD CONSTRAINT fk_template
FOREIGN KEY (template_id) REFERENCES task_templates(id) ON DELETE SET NULL;

-- Indexes for better performance
CREATE INDEX idx_tasks_user ON tasks(user_id);
CREATE INDEX idx_tasks_status ON tasks(status);
CREATE INDEX idx_tasks_due_date ON tasks(due_date);
CREATE INDEX idx_tasks_priority ON tasks(priority);
CREATE INDEX idx_tasks_category ON tasks(category);
CREATE INDEX idx_templates_user ON task_templates(user_id);
CREATE INDEX idx_templates_recurrence ON task_templates(recurrence_type);
CREATE INDEX idx_templates_active ON task_templates(is_active);
