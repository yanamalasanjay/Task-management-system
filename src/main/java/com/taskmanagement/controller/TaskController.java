package com.taskmanagement.controller;

import com.taskmanagement.dto.TaskRequest;
import com.taskmanagement.dto.TaskResponse;
import com.taskmanagement.model.TaskStatus;
import com.taskmanagement.service.TaskService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * REST Controller for Task operations
 * Demonstrates RESTful API design
 *
 * @author Yanamala Sanjay
 */
@RestController
@RequestMapping("/api/tasks")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class TaskController {

    private final TaskService taskService;

    /**
     * Create new task
     * POST /api/tasks
     */
    @PostMapping
    public ResponseEntity<TaskResponse> createTask(@Valid @RequestBody TaskRequest request) {
        try {
            TaskResponse response = taskService.createTask(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(TaskResponse.builder().message("Error: " + e.getMessage()).build());
        }
    }

    /**
     * Get task by ID
     * GET /api/tasks/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<TaskResponse> getTask(@PathVariable Long id) {
        try {
            TaskResponse response = taskService.getTask(id);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Get all tasks for a user
     * GET /api/tasks/user/{userId}
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<TaskResponse>> getUserTasks(@PathVariable Long userId) {
        List<TaskResponse> tasks = taskService.getUserTasks(userId);
        return ResponseEntity.ok(tasks);
    }

    /**
     * Get pending tasks for a user (sorted by priority and due date)
     * GET /api/tasks/user/{userId}/pending
     */
    @GetMapping("/user/{userId}/pending")
    public ResponseEntity<List<TaskResponse>> getUserPendingTasks(@PathVariable Long userId) {
        List<TaskResponse> tasks = taskService.getUserPendingTasks(userId);
        return ResponseEntity.ok(tasks);
    }

    /**
     * Get overdue tasks
     * GET /api/tasks/overdue
     */
    @GetMapping("/overdue")
    public ResponseEntity<List<TaskResponse>> getOverdueTasks() {
        List<TaskResponse> tasks = taskService.getOverdueTasks();
        return ResponseEntity.ok(tasks);
    }

    /**
     * Get tasks due today
     * GET /api/tasks/due-today
     */
    @GetMapping("/due-today")
    public ResponseEntity<List<TaskResponse>> getTasksDueToday() {
        List<TaskResponse> tasks = taskService.getTasksDueToday();
        return ResponseEntity.ok(tasks);
    }

    /**
     * Get tasks by category
     * GET /api/tasks/user/{userId}/category/{category}
     */
    @GetMapping("/user/{userId}/category/{category}")
    public ResponseEntity<List<TaskResponse>> getTasksByCategory(
            @PathVariable Long userId,
            @PathVariable String category) {
        List<TaskResponse> tasks = taskService.getTasksByCategory(userId, category);
        return ResponseEntity.ok(tasks);
    }

    /**
     * Update task status
     * PUT /api/tasks/{id}/status
     */
    @PutMapping("/{id}/status")
    public ResponseEntity<TaskResponse> updateTaskStatus(
            @PathVariable Long id,
            @RequestParam TaskStatus status) {
        try {
            TaskResponse response = taskService.updateTaskStatus(id, status);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(TaskResponse.builder().message("Error: " + e.getMessage()).build());
        }
    }

    /**
     * Update task
     * PUT /api/tasks/{id}
     */
    @PutMapping("/{id}")
    public ResponseEntity<TaskResponse> updateTask(
            @PathVariable Long id,
            @Valid @RequestBody TaskRequest request) {
        try {
            TaskResponse response = taskService.updateTask(id, request);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(TaskResponse.builder().message("Error: " + e.getMessage()).build());
        }
    }

    /**
     * Delete task
     * DELETE /api/tasks/{id}
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTask(@PathVariable Long id) {
        taskService.deleteTask(id);
        return ResponseEntity.ok().build();
    }

    /**
     * Get task statistics for a user
     * GET /api/tasks/user/{userId}/stats
     */
    @GetMapping("/user/{userId}/stats")
    public ResponseEntity<Map<String, Object>> getUserTaskStats(@PathVariable Long userId) {
        Map<String, Object> stats = taskService.getUserTaskStats(userId);
        return ResponseEntity.ok(stats);
    }
}
