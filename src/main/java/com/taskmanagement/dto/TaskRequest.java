package com.taskmanagement.dto;

import com.taskmanagement.model.RecurrenceType;
import com.taskmanagement.model.TaskPriority;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * DTO for creating/updating tasks
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TaskRequest {

    @NotNull(message = "User ID is required")
    private Long userId;

    @NotBlank(message = "Title is required")
    private String title;

    private String description;

    private TaskPriority priority = TaskPriority.MEDIUM;

    private LocalDate dueDate;

    private Boolean isRecurring = false;

    private RecurrenceType recurrenceType = RecurrenceType.NONE;

    private String category;
}
