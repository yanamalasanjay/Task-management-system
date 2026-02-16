package com.taskmanagement.repository;

import com.taskmanagement.model.Task;
import com.taskmanagement.model.TaskPriority;
import com.taskmanagement.model.TaskStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

/**
 * Repository interface for Task entity
 * Demonstrates abstraction with Spring Data JPA
 */
@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {

    // Find tasks by user
    List<Task> findByAssignedToId(Long userId);

    // Find tasks by status
    List<Task> findByStatus(TaskStatus status);

    // Find tasks by user and status
    List<Task> findByAssignedToIdAndStatus(Long userId, TaskStatus status);

    // Find tasks by priority
    List<Task> findByPriority(TaskPriority priority);

    // Find overdue tasks
    @Query("SELECT t FROM Task t WHERE t.dueDate < CURRENT_DATE AND t.status != 'COMPLETED'")
    List<Task> findOverdueTasks();

    // Find tasks due today
    @Query("SELECT t FROM Task t WHERE t.dueDate = CURRENT_DATE AND t.status != 'COMPLETED'")
    List<Task> findTasksDueToday();

    // Find tasks needing reminders
    @Query("SELECT t FROM Task t WHERE t.reminderSent = false AND t.status != 'COMPLETED' AND t.dueDate IS NOT NULL")
    List<Task> findTasksNeedingReminders();

    // Find user's tasks due between dates
    List<Task> findByAssignedToIdAndDueDateBetween(Long userId, LocalDate start, LocalDate end);

    // Find user's pending tasks ordered by priority and due date
    @Query("SELECT t FROM Task t WHERE t.assignedTo.id = :userId AND t.status != 'COMPLETED' ORDER BY t.priority DESC, t.dueDate ASC")
    List<Task> findUserPendingTasksOrdered(Long userId);

    // Count tasks by status for a user
    @Query("SELECT COUNT(t) FROM Task t WHERE t.assignedTo.id = :userId AND t.status = :status")
    Long countByUserAndStatus(Long userId, TaskStatus status);

    // Find recurring tasks
    List<Task> findByIsRecurringTrue();

    // Find tasks by category
    List<Task> findByCategory(String category);

    // Find tasks by user and category
    List<Task> findByAssignedToIdAndCategory(Long userId, String category);
}
