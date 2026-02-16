package com.taskmanagement.service;

import com.taskmanagement.dto.TaskRequest;
import com.taskmanagement.dto.TaskResponse;
import com.taskmanagement.model.Task;
import com.taskmanagement.model.TaskStatus;
import com.taskmanagement.model.User;
import com.taskmanagement.repository.TaskRepository;
import com.taskmanagement.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service class for Task management
 * Demonstrates business logic separation and transaction management
 *
 * @author Yanamala Sanjay
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class TaskService {

    private final TaskRepository taskRepository;
    private final UserRepository userRepository;
    private final EmailService emailService;

    /**
     * Create a new task
     */
    @Transactional
    public TaskResponse createTask(TaskRequest request) {
        log.info("Creating new task: {} for user: {}", request.getTitle(), request.getUserId());

        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        Task task = new Task();
        task.setAssignedTo(user);
        task.setTitle(request.getTitle());
        task.setDescription(request.getDescription());
        task.setPriority(request.getPriority());
        task.setDueDate(request.getDueDate());
        task.setStatus(TaskStatus.TODO);
        task.setCreatedAt(LocalDateTime.now());
        task.setIsRecurring(request.getIsRecurring());
        task.setRecurrenceType(request.getRecurrenceType());
        task.setCategory(request.getCategory());

        Task savedTask = taskRepository.save(task);
        log.info("Task created successfully with ID: {}", savedTask.getId());

        // Send task creation notification
        emailService.sendTaskCreationNotification(savedTask);

        return buildTaskResponse(savedTask, "Task created successfully");
    }

    /**
     * Get task by ID
     */
    public TaskResponse getTask(Long taskId) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new RuntimeException("Task not found"));

        return buildTaskResponse(task, null);
    }

    /**
     * Get all tasks for a user
     */
    public List<TaskResponse> getUserTasks(Long userId) {
        List<Task> tasks = taskRepository.findByAssignedToId(userId);
        return tasks.stream()
                .map(task -> buildTaskResponse(task, null))
                .collect(Collectors.toList());
    }

    /**
     * Get pending tasks for a user (ordered by priority and due date)
     */
    public List<TaskResponse> getUserPendingTasks(Long userId) {
        List<Task> tasks = taskRepository.findUserPendingTasksOrdered(userId);
        return tasks.stream()
                .map(task -> buildTaskResponse(task, null))
                .collect(Collectors.toList());
    }

    /**
     * Get overdue tasks
     */
    public List<TaskResponse> getOverdueTasks() {
        List<Task> tasks = taskRepository.findOverdueTasks();
        return tasks.stream()
                .map(task -> buildTaskResponse(task, null))
                .collect(Collectors.toList());
    }

    /**
     * Get tasks due today
     */
    public List<TaskResponse> getTasksDueToday() {
        List<Task> tasks = taskRepository.findTasksDueToday();
        return tasks.stream()
                .map(task -> buildTaskResponse(task, null))
                .collect(Collectors.toList());
    }

    /**
     * Update task status
     */
    @Transactional
    public TaskResponse updateTaskStatus(Long taskId, TaskStatus newStatus) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new RuntimeException("Task not found"));

        TaskStatus oldStatus = task.getStatus();
        task.setStatus(newStatus);

        if (newStatus == TaskStatus.COMPLETED) {
            task.setCompletedAt(LocalDateTime.now());
        }

        taskRepository.save(task);
        log.info("Task {} status updated from {} to {}", task.getTitle(), oldStatus, newStatus);

        // Send status update notification
        emailService.sendTaskStatusUpdate(task, newStatus);

        return buildTaskResponse(task, "Task status updated");
    }

    /**
     * Update task
     */
    @Transactional
    public TaskResponse updateTask(Long taskId, TaskRequest request) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new RuntimeException("Task not found"));

        task.setTitle(request.getTitle());
        task.setDescription(request.getDescription());
        task.setPriority(request.getPriority());
        task.setDueDate(request.getDueDate());
        task.setCategory(request.getCategory());

        Task updated = taskRepository.save(task);
        log.info("Task updated: {}", task.getId());

        return buildTaskResponse(updated, "Task updated successfully");
    }

    /**
     * Delete task
     */
    @Transactional
    public void deleteTask(Long taskId) {
        taskRepository.deleteById(taskId);
        log.info("Task deleted: {}", taskId);
    }

    /**
     * Get tasks by category
     */
    public List<TaskResponse> getTasksByCategory(Long userId, String category) {
        List<Task> tasks = taskRepository.findByAssignedToIdAndCategory(userId, category);
        return tasks.stream()
                .map(task -> buildTaskResponse(task, null))
                .collect(Collectors.toList());
    }

    /**
     * Get task statistics for a user
     */
    public java.util.Map<String, Object> getUserTaskStats(Long userId) {
        long totalTasks = taskRepository.findByAssignedToId(userId).size();
        long completedTasks = taskRepository.countByUserAndStatus(userId, TaskStatus.COMPLETED);
        long pendingTasks = totalTasks - completedTasks;
        long overdueTasks = taskRepository.findOverdueTasks().stream()
                .filter(t -> t.getAssignedTo().getId().equals(userId))
                .count();

        return java.util.Map.of(
                "totalTasks", totalTasks,
                "completedTasks", completedTasks,
                "pendingTasks", pendingTasks,
                "overdueTasks", overdueTasks
        );
    }

    /**
     * Build TaskResponse DTO from Task entity
     */
    private TaskResponse buildTaskResponse(Task task, String message) {
        return TaskResponse.builder()
                .taskId(task.getId())
                .title(task.getTitle())
                .description(task.getDescription())
                .status(task.getStatus())
                .priority(task.getPriority())
                .dueDate(task.getDueDate())
                .daysUntilDeadline(task.getDaysUntilDeadline())
                .isOverdue(task.isOverdue())
                .assignedToName(task.getAssignedTo().getName())
                .assignedToEmail(task.getAssignedTo().getEmail())
                .createdAt(task.getCreatedAt())
                .completedAt(task.getCompletedAt())
                .isRecurring(task.getIsRecurring())
                .recurrenceType(task.getRecurrenceType())
                .category(task.getCategory())
                .message(message)
                .build();
    }
}
