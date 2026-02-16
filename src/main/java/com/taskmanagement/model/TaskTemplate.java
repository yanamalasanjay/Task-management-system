package com.taskmanagement.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalTime;

/**
 * Entity representing a recurring task template
 * Demonstrates recurring task engine design
 * Templates are used to automatically generate tasks based on schedule
 *
 * Example: Daily reporting duty at 9:00 AM every day
 *
 * @author Yanamala Sanjay
 */
@Entity
@Table(name = "task_templates")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TaskTemplate {

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
    private TaskPriority priority = TaskPriority.MEDIUM;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RecurrenceType recurrenceType;

    /**
     * Cron expression for scheduling
     * Examples:
     * - "0 0 9 * * ?" - Every day at 9:00 AM
     * - "0 0 9 * * MON" - Every Monday at 9:00 AM
     * - "0 0 9 1 * ?" - First day of every month at 9:00 AM
     */
    @Column(name = "cron_expression")
    private String cronExpression;

    /**
     * For simple schedules without cron
     */
    @Column(name = "schedule_time")
    private LocalTime scheduleTime;  // Time of day to create the task

    /**
     * For weekly recurrence: which day of week (1=Monday, 7=Sunday)
     */
    @Column(name = "day_of_week")
    private Integer dayOfWeek;

    /**
     * For monthly recurrence: which day of month (1-31)
     */
    @Column(name = "day_of_month")
    private Integer dayOfMonth;

    /**
     * Days before due date (if task needs to be completed within X days)
     */
    @Column(name = "days_to_complete")
    private Integer daysToComplete = 1;

    private String category;

    @Column(name = "is_active")
    private Boolean isActive = true;  // Can be disabled without deletion

    @Column(name = "last_generated")
    private java.time.LocalDateTime lastGenerated;  // Track when last task was created
}
