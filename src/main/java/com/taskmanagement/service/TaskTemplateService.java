package com.taskmanagement.service;

import com.taskmanagement.dto.TaskTemplateRequest;
import com.taskmanagement.model.RecurrenceType;
import com.taskmanagement.model.TaskTemplate;
import com.taskmanagement.model.User;
import com.taskmanagement.repository.TaskTemplateRepository;
import com.taskmanagement.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Service for managing recurring task templates
 * Demonstrates template pattern for task generation
 *
 * @author Yanamala Sanjay
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class TaskTemplateService {

    private final TaskTemplateRepository templateRepository;
    private final UserRepository userRepository;

    /**
     * Create a new task template
     */
    @Transactional
    public TaskTemplate createTemplate(TaskTemplateRequest request) {
        log.info("Creating task template: {} for user: {}", request.getTitle(), request.getUserId());

        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        TaskTemplate template = new TaskTemplate();
        template.setAssignedTo(user);
        template.setTitle(request.getTitle());
        template.setDescription(request.getDescription());
        template.setPriority(request.getPriority());
        template.setRecurrenceType(request.getRecurrenceType());
        template.setCronExpression(request.getCronExpression());
        template.setScheduleTime(request.getScheduleTime());
        template.setDayOfWeek(request.getDayOfWeek());
        template.setDayOfMonth(request.getDayOfMonth());
        template.setDaysToComplete(request.getDaysToComplete());
        template.setCategory(request.getCategory());
        template.setIsActive(true);

        TaskTemplate saved = templateRepository.save(template);
        log.info("Template created with ID: {}", saved.getId());

        return saved;
    }

    /**
     * Get template by ID
     */
    public TaskTemplate getTemplate(Long templateId) {
        return templateRepository.findById(templateId)
                .orElseThrow(() -> new RuntimeException("Template not found"));
    }

    /**
     * Get all templates for a user
     */
    public List<TaskTemplate> getUserTemplates(Long userId) {
        return templateRepository.findByAssignedToId(userId);
    }

    /**
     * Get all active templates
     */
    public List<TaskTemplate> getActiveTemplates() {
        return templateRepository.findByIsActiveTrue();
    }

    /**
     * Update template
     */
    @Transactional
    public TaskTemplate updateTemplate(Long templateId, TaskTemplateRequest request) {
        TaskTemplate template = templateRepository.findById(templateId)
                .orElseThrow(() -> new RuntimeException("Template not found"));

        template.setTitle(request.getTitle());
        template.setDescription(request.getDescription());
        template.setPriority(request.getPriority());
        template.setRecurrenceType(request.getRecurrenceType());
        template.setCronExpression(request.getCronExpression());
        template.setScheduleTime(request.getScheduleTime());
        template.setDayOfWeek(request.getDayOfWeek());
        template.setDayOfMonth(request.getDayOfMonth());
        template.setDaysToComplete(request.getDaysToComplete());
        template.setCategory(request.getCategory());

        return templateRepository.save(template);
    }

    /**
     * Activate/Deactivate template
     */
    @Transactional
    public TaskTemplate toggleTemplateStatus(Long templateId) {
        TaskTemplate template = templateRepository.findById(templateId)
                .orElseThrow(() -> new RuntimeException("Template not found"));

        template.setIsActive(!template.getIsActive());
        return templateRepository.save(template);
    }

    /**
     * Delete template
     */
    @Transactional
    public void deleteTemplate(Long templateId) {
        templateRepository.deleteById(templateId);
        log.info("Template deleted: {}", templateId);
    }
}
