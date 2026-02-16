package com.taskmanagement.service;

import com.taskmanagement.dto.TaskDigest;
import com.taskmanagement.model.Task;
import com.taskmanagement.model.TaskStatus;
import com.taskmanagement.model.User;
import com.taskmanagement.repository.TaskRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service for email notifications and digests
 * Demonstrates:
 * - Asynchronous email sending
 * - Email template generation
 * - Digest system implementation
 *
 * @author Yanamala Sanjay
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {

    private final JavaMailSender mailSender;
    private final TaskRepository taskRepository;
    private static final String FROM_EMAIL = "noreply@taskmanagement.com";

    /**
     * Send task creation notification
     */
    @Async
    public void sendTaskCreationNotification(Task task) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(FROM_EMAIL);
            message.setTo(task.getAssignedTo().getEmail());
            message.setSubject("New Task Assigned: " + task.getTitle());

            String body = buildTaskCreationEmail(task);
            message.setText(body);

            mailSender.send(message);
            log.info("Task creation email sent to: {}", task.getAssignedTo().getEmail());

        } catch (Exception e) {
            log.error("Failed to send task creation email", e);
        }
    }

    /**
     * Send task status update notification
     */
    @Async
    public void sendTaskStatusUpdate(Task task, TaskStatus newStatus) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(FROM_EMAIL);
            message.setTo(task.getAssignedTo().getEmail());
            message.setSubject("Task Status Updated: " + task.getTitle());

            String body = buildStatusUpdateEmail(task, newStatus);
            message.setText(body);

            mailSender.send(message);
            log.info("Status update email sent: {}", task.getTitle());

        } catch (Exception e) {
            log.error("Failed to send status update email", e);
        }
    }

    /**
     * Send task reminder
     * Priority-based alerts
     */
    @Async
    public void sendTaskReminder(Task task) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(FROM_EMAIL);
            message.setTo(task.getAssignedTo().getEmail());

            String priority = task.getPriority() == com.taskmanagement.model.TaskPriority.CRITICAL
                    ? "URGENT: " : "";

            message.setSubject(priority + "Task Reminder: " + task.getTitle());

            String body = buildReminderEmail(task);
            message.setText(body);

            mailSender.send(message);
            log.info("Reminder email sent for task: {}", task.getTitle());

        } catch (Exception e) {
            log.error("Failed to send reminder email", e);
        }
    }

    /**
     * Send daily task digest
     * Demonstrates email digest system
     */
    @Async
    public void sendDailyTaskDigest(User user) {
        try {
            // Gather task statistics
            List<Task> allTasks = taskRepository.findByAssignedToId(user.getId());
            List<Task> todaysTasks = taskRepository.findTasksDueToday().stream()
                    .filter(t -> t.getAssignedTo().getId().equals(user.getId()))
                    .collect(Collectors.toList());

            List<Task> upcomingTasks = taskRepository.findByAssignedToIdAndDueDateBetween(
                    user.getId(),
                    LocalDate.now().plusDays(1),
                    LocalDate.now().plusDays(7)
            );

            List<Task> overdueTasks = taskRepository.findOverdueTasks().stream()
                    .filter(t -> t.getAssignedTo().getId().equals(user.getId()))
                    .collect(Collectors.toList());

            long completedCount = allTasks.stream()
                    .filter(t -> t.getStatus() == TaskStatus.COMPLETED)
                    .count();

            long pendingCount = allTasks.stream()
                    .filter(t -> t.getStatus() != TaskStatus.COMPLETED)
                    .count();

            // Build digest
            TaskDigest digest = TaskDigest.builder()
                    .userName(user.getName())
                    .userEmail(user.getEmail())
                    .totalTasks((int) allTasks.size())
                    .completedTasks((int) completedCount)
                    .pendingTasks((int) pendingCount)
                    .overdueTasks(overdueTasks.size())
                    .todaysTasks(buildTaskSummaries(todaysTasks))
                    .upcomingTasks(buildTaskSummaries(upcomingTasks))
                    .overdueTasksList(buildTaskSummaries(overdueTasks))
                    .build();

            // Send email
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(FROM_EMAIL);
            message.setTo(user.getEmail());
            message.setSubject("Daily Task Digest - " + LocalDate.now());

            String body = buildDigestEmail(digest);
            message.setText(body);

            mailSender.send(message);
            log.info("Daily digest sent to: {}", user.getEmail());

        } catch (Exception e) {
            log.error("Failed to send daily digest", e);
        }
    }

    // Email body builders

    private String buildTaskCreationEmail(Task task) {
        return String.format("""
                Dear %s,

                A new task has been assigned to you:

                Title: %s
                Priority: %s
                Due Date: %s
                Description: %s

                Please log in to the system to view details.

                Best regards,
                Task Management System
                """,
                task.getAssignedTo().getName(),
                task.getTitle(),
                task.getPriority(),
                task.getDueDate(),
                task.getDescription() != null ? task.getDescription() : "N/A"
        );
    }

    private String buildStatusUpdateEmail(Task task, TaskStatus newStatus) {
        return String.format("""
                Dear %s,

                Task status has been updated:

                Task: %s
                New Status: %s
                %s

                Thank you!
                """,
                task.getAssignedTo().getName(),
                task.getTitle(),
                newStatus,
                newStatus == TaskStatus.COMPLETED ? "Great job completing this task!" : ""
        );
    }

    private String buildReminderEmail(Task task) {
        long daysUntil = task.getDaysUntilDeadline();
        String urgency = daysUntil == 0 ? "DUE TODAY!" :
                daysUntil == 1 ? "due tomorrow" :
                        String.format("due in %d days", daysUntil);

        return String.format("""
                Dear %s,

                REMINDER: Task %s

                Title: %s
                Priority: %s
                Due Date: %s

                Please complete this task on time.

                Best regards,
                Task Management System
                """,
                task.getAssignedTo().getName(),
                urgency,
                task.getTitle(),
                task.getPriority(),
                task.getDueDate()
        );
    }

    private String buildDigestEmail(TaskDigest digest) {
        StringBuilder sb = new StringBuilder();

        sb.append(String.format("""
                Dear %s,

                Here is your daily task summary for %s:

                ========================================
                TASK STATISTICS
                ========================================
                Total Tasks: %d
                Completed: %d
                Pending: %d
                Overdue: %d

                """,
                digest.getUserName(),
                LocalDate.now(),
                digest.getTotalTasks(),
                digest.getCompletedTasks(),
                digest.getPendingTasks(),
                digest.getOverdueTasks()
        ));

        // Today's tasks
        if (!digest.getTodaysTasks().isEmpty()) {
            sb.append("========================================\n");
            sb.append("TASKS DUE TODAY\n");
            sb.append("========================================\n");
            for (TaskDigest.TaskSummary task : digest.getTodaysTasks()) {
                sb.append(String.format("- [%s] %s\n", task.getPriority(), task.getTitle()));
            }
            sb.append("\n");
        }

        // Overdue tasks
        if (!digest.getOverdueTasksList().isEmpty()) {
            sb.append("========================================\n");
            sb.append("OVERDUE TASKS (ACTION REQUIRED!)\n");
            sb.append("========================================\n");
            for (TaskDigest.TaskSummary task : digest.getOverdueTasksList()) {
                sb.append(String.format("- [%s] %s (Due: %s)\n",
                        task.getPriority(), task.getTitle(), task.getDueDate()));
            }
            sb.append("\n");
        }

        // Upcoming tasks
        if (!digest.getUpcomingTasks().isEmpty()) {
            sb.append("========================================\n");
            sb.append("UPCOMING TASKS (Next 7 Days)\n");
            sb.append("========================================\n");
            for (TaskDigest.TaskSummary task : digest.getUpcomingTasks()) {
                sb.append(String.format("- %s (Due: %s - %d days)\n",
                        task.getTitle(), task.getDueDate(), task.getDaysUntilDeadline()));
            }
            sb.append("\n");
        }

        sb.append("""
                ========================================

                Log in to the system to manage your tasks.

                Best regards,
                Task Management System
                """);

        return sb.toString();
    }

    private List<TaskDigest.TaskSummary> buildTaskSummaries(List<Task> tasks) {
        return tasks.stream()
                .map(task -> TaskDigest.TaskSummary.builder()
                        .taskId(task.getId())
                        .title(task.getTitle())
                        .priority(task.getPriority().toString())
                        .dueDate(task.getDueDate() != null ? task.getDueDate().toString() : "N/A")
                        .daysUntilDeadline(task.getDaysUntilDeadline())
                        .build())
                .collect(Collectors.toList());
    }
}
