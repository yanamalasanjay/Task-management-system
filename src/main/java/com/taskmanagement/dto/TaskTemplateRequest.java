package com.taskmanagement.dto;

import com.taskmanagement.model.RecurrenceType;
import com.taskmanagement.model.TaskPriority;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalTime;

/**
 * DTO for creating recurring task templates
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TaskTemplateRequest {

    @NotNull(message = "User ID is required")
    private Long userId;

    @NotBlank(message = "Title is required")
    private String title;

    private String description;

    private TaskPriority priority = TaskPriority.MEDIUM;

    @NotNull(message = "Recurrence type is required")
    private RecurrenceType recurrenceType;

    private String cronExpression;  // Optional: for advanced scheduling

    private LocalTime scheduleTime;  // Time of day to create task

    private Integer dayOfWeek;  // For WEEKLY: 1=Monday, 7=Sunday

    private Integer dayOfMonth;  // For MONTHLY: 1-31

    private Integer daysToComplete = 1;  // Days to complete the task

    private String category;
}
