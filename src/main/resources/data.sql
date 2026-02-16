-- Sample Data for Task Management System
-- Author: Yanamala Sanjay

-- Insert sample users
INSERT INTO users (name, email, password, department, designation, employee_id, email_digest_enabled) VALUES
('Sanjay Yanamala', 'sanjayyanamala07@gmail.com', '$2a$10$abcdefghijklmnopqrstuv', 'Engineering', 'Graduate Engineer Trainee', 'EMP001', TRUE),
('Rahul Kumar', 'rahul@example.com', '$2a$10$abcdefghijklmnopqrstuv', 'Operations', 'Engineer', 'EMP002', TRUE),
('Priya Sharma', 'priya@example.com', '$2a$10$abcdefghijklmnopqrstuv', 'Engineering', 'Senior Engineer', 'EMP003', FALSE);

-- Insert sample tasks
INSERT INTO tasks (user_id, title, description, status, priority, due_date, category, is_recurring) VALUES
(1, 'Daily Material Data Update', 'Update material inventory data in SAP', 'TODO', 'HIGH', CURDATE(), 'Material Data Update', FALSE),
(1, 'Weekly Progress Report', 'Submit weekly progress report to manager', 'TODO', 'MEDIUM', DATE_ADD(CURDATE(), INTERVAL 2 DAY), 'Daily Reporting', FALSE),
(1, 'Review CAM Equipment Documentation', 'Review vendor documentation for CAM project', 'IN_PROGRESS', 'HIGH', DATE_ADD(CURDATE(), INTERVAL 3 DAY), 'Documentation', FALSE),
(2, 'Morning Equipment Inspection', 'Inspect battery cell equipment', 'TODO', 'HIGH', CURDATE(), 'Daily Reporting', FALSE),
(3, 'Monthly Safety Audit', 'Conduct monthly safety audit', 'TODO', 'CRITICAL', DATE_ADD(CURDATE(), INTERVAL 7 DAY), 'Documentation', FALSE);

-- Insert sample recurring task templates
INSERT INTO task_templates (user_id, title, description, priority, recurrence_type, day_of_week, days_to_complete, category, is_active) VALUES
(1, 'Daily Material Data Update', 'Update material inventory data in SAP system', 'HIGH', 'DAILY', NULL, 1, 'Material Data Update', TRUE),
(1, 'Weekly Team Meeting', 'Attend weekly team sync meeting', 'MEDIUM', 'WEEKLY', 1, 0, 'Daily Reporting', TRUE),
(2, 'Monthly Equipment Maintenance Log', 'Submit monthly equipment maintenance report', 'HIGH', 'MONTHLY', NULL, 2, 'Documentation', TRUE);

-- Update last_generated for templates (set to yesterday so they generate today)
UPDATE task_templates SET last_generated = DATE_SUB(NOW(), INTERVAL 2 DAY);
