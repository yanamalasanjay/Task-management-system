package com.taskmanagement.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * DTO for daily task digest email
 * Contains summary of tasks for a user
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TaskDigest {

    private String userName;
    private String userEmail;
    private int totalTasks;
    private int completedTasks;
    private int pendingTasks;
    private int overdueTasks;
    private List<TaskSummary> todaysTasks;
    private List<TaskSummary> upcomingTasks;
    private List<TaskSummary> overdueTasksList;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TaskSummary {
        private Long taskId;
        private String title;
        private String priority;
        private String dueDate;
        private Long daysUntilDeadline;
    }
}
