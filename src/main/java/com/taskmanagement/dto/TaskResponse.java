package com.taskmanagement.dto;

import com.taskmanagement.model.RecurrenceType;
import com.taskmanagement.model.TaskPriority;
import com.taskmanagement.model.TaskStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * DTO for task response
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TaskResponse {

    private Long taskId;
    private String title;
    private String description;
    private TaskStatus status;
    private TaskPriority priority;
    private LocalDate dueDate;
    private Long daysUntilDeadline;
    private Boolean isOverdue;
    private String assignedToName;
    private String assignedToEmail;
    private LocalDateTime createdAt;
    private LocalDateTime completedAt;
    private Boolean isRecurring;
    private RecurrenceType recurrenceType;
    private String category;
    private String message;
}
