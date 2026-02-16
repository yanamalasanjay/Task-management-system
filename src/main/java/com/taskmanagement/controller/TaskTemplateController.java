package com.taskmanagement.controller;

import com.taskmanagement.dto.TaskTemplateRequest;
import com.taskmanagement.model.TaskTemplate;
import com.taskmanagement.service.TaskTemplateService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST Controller for Task Template operations
 * Manages recurring task templates
 *
 * @author Yanamala Sanjay
 */
@RestController
@RequestMapping("/api/templates")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class TaskTemplateController {

    private final TaskTemplateService templateService;

    /**
     * Create new task template
     * POST /api/templates
     */
    @PostMapping
    public ResponseEntity<TaskTemplate> createTemplate(@Valid @RequestBody TaskTemplateRequest request) {
        try {
            TaskTemplate template = templateService.createTemplate(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(template);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Get template by ID
     * GET /api/templates/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<TaskTemplate> getTemplate(@PathVariable Long id) {
        try {
            TaskTemplate template = templateService.getTemplate(id);
            return ResponseEntity.ok(template);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Get all templates for a user
     * GET /api/templates/user/{userId}
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<TaskTemplate>> getUserTemplates(@PathVariable Long userId) {
        List<TaskTemplate> templates = templateService.getUserTemplates(userId);
        return ResponseEntity.ok(templates);
    }

    /**
     * Get all active templates
     * GET /api/templates/active
     */
    @GetMapping("/active")
    public ResponseEntity<List<TaskTemplate>> getActiveTemplates() {
        List<TaskTemplate> templates = templateService.getActiveTemplates();
        return ResponseEntity.ok(templates);
    }

    /**
     * Update template
     * PUT /api/templates/{id}
     */
    @PutMapping("/{id}")
    public ResponseEntity<TaskTemplate> updateTemplate(
            @PathVariable Long id,
            @Valid @RequestBody TaskTemplateRequest request) {
        try {
            TaskTemplate template = templateService.updateTemplate(id, request);
            return ResponseEntity.ok(template);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Toggle template active status
     * POST /api/templates/{id}/toggle
     */
    @PostMapping("/{id}/toggle")
    public ResponseEntity<TaskTemplate> toggleTemplateStatus(@PathVariable Long id) {
        try {
            TaskTemplate template = templateService.toggleTemplateStatus(id);
            return ResponseEntity.ok(template);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Delete template
     * DELETE /api/templates/{id}
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTemplate(@PathVariable Long id) {
        templateService.deleteTemplate(id);
        return ResponseEntity.ok().build();
    }
}
