package com.taskmanagement.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Entity representing a task
 * Demonstrates:
 * - Task lifecycle management
 * - Priority-based organization
 * - Deadline tracking
 * - Recurring task support
 *
 * @author Yanamala Sanjay
 */
@Entity
@Table(name = "tasks")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Task {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User assignedTo;

    @Column(nullable = false)
    private String title;

    @Column(length = 1000)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TaskStatus status = TaskStatus.TODO;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TaskPriority priority = TaskPriority.MEDIUM;

    @Column(name = "due_date")
    private LocalDate dueDate;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "completed_at")
    private LocalDateTime completedAt;

    @Column(name = "is_recurring")
    private Boolean isRecurring = false;

    @Enumerated(EnumType.STRING)
    @Column(name = "recurrence_type")
    private RecurrenceType recurrenceType = RecurrenceType.NONE;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "template_id")
    private TaskTemplate template;  // If generated from a template

    @Column(name = "reminder_sent")
    private Boolean reminderSent = false;

    private String category;  // e.g., "Material Data Update", "Daily Reporting", "Documentation"

    /**
     * Check if task is overdue
     */
    public boolean isOverdue() {
        if (dueDate == null || status == TaskStatus.COMPLETED) {
            return false;
        }
        return LocalDate.now().isAfter(dueDate);
    }

    /**
     * Get days until deadline
     */
    public long getDaysUntilDeadline() {
        if (dueDate == null) {
            return Long.MAX_VALUE;
        }
        return java.time.temporal.ChronoUnit.DAYS.between(LocalDate.now(), dueDate);
    }

    /**
     * Check if reminder should be sent
     * Send reminder 1 day before deadline for non-critical tasks
     * Send reminder 2 days before for critical tasks
     */
    public boolean shouldSendReminder() {
        if (reminderSent || status == TaskStatus.COMPLETED || dueDate == null) {
            return false;
        }

        long daysUntil = getDaysUntilDeadline();

        return (priority == TaskPriority.CRITICAL && daysUntil <= 2) ||
               (priority != TaskPriority.CRITICAL && daysUntil <= 1);
    }
}
