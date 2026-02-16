package com.taskmanagement.repository;

import com.taskmanagement.model.RecurrenceType;
import com.taskmanagement.model.TaskTemplate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository for TaskTemplate entity
 */
@Repository
public interface TaskTemplateRepository extends JpaRepository<TaskTemplate, Long> {

    // Find templates by user
    List<TaskTemplate> findByAssignedToId(Long userId);

    // Find active templates
    List<TaskTemplate> findByIsActiveTrue();

    // Find templates by recurrence type
    List<TaskTemplate> findByRecurrenceType(RecurrenceType recurrenceType);

    // Find active templates by recurrence type
    List<TaskTemplate> findByIsActiveTrueAndRecurrenceType(RecurrenceType recurrenceType);
}
