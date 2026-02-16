package com.taskmanagement.scheduler;

import com.taskmanagement.model.*;
import com.taskmanagement.repository.TaskRepository;
import com.taskmanagement.repository.TaskTemplateRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.DayOfWeek;
import java.util.List;

/**
 * Service for scheduled task generation and management
 * Demonstrates:
 * - Cron-based task scheduling with @Scheduled
 * - Automated recurring task generation
 * - Template-based task creation
 * - Daily, Weekly, and Monthly recurrence patterns
 *
 * This is a KEY component demonstrating scheduled jobs knowledge
 *
 * @author Yanamala Sanjay
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class TaskSchedulerService {

    private final TaskTemplateRepository templateRepository;
    private final TaskRepository taskRepository;

    /**
     * Generate daily recurring tasks
     * Runs every day at 6:00 AM
     * Cron Expression: "0 0 6 * * ?" - Second, Minute, Hour, Day, Month, DayOfWeek
     */
    @Scheduled(cron = "0 0 6 * * ?")
    @Transactional
    public void generateDailyTasks() {
        log.info("Running daily task generation job...");

        List<TaskTemplate> dailyTemplates = templateRepository
                .findByIsActiveTrueAndRecurrenceType(RecurrenceType.DAILY);

        for (TaskTemplate template : dailyTemplates) {
            // Check if task already generated today
            if (wasGeneratedToday(template)) {
                log.debug("Task already generated today for template: {}", template.getId());
                continue;
            }

            generateTaskFromTemplate(template);
        }

        log.info("Daily task generation completed. Generated {} tasks", dailyTemplates.size());
    }

    /**
     * Generate weekly recurring tasks
     * Runs every day at 6:00 AM, but only creates tasks on the specified day of week
     */
    @Scheduled(cron = "0 0 6 * * ?")
    @Transactional
    public void generateWeeklyTasks() {
        log.info("Running weekly task generation job...");

        List<TaskTemplate> weeklyTemplates = templateRepository
                .findByIsActiveTrueAndRecurrenceType(RecurrenceType.WEEKLY);

        int todayDayOfWeek = LocalDate.now().getDayOfWeek().getValue(); // 1=Monday, 7=Sunday

        for (TaskTemplate template : weeklyTemplates) {
            // Check if today matches the scheduled day of week
            if (template.getDayOfWeek() != null && template.getDayOfWeek() == todayDayOfWeek) {
                if (!wasGeneratedToday(template)) {
                    generateTaskFromTemplate(template);
                }
            }
        }

        log.info("Weekly task generation completed.");
    }

    /**
     * Generate monthly recurring tasks
     * Runs every day at 6:00 AM, but only creates tasks on the specified day of month
     */
    @Scheduled(cron = "0 0 6 * * ?")
    @Transactional
    public void generateMonthlyTasks() {
        log.info("Running monthly task generation job...");

        List<TaskTemplate> monthlyTemplates = templateRepository
                .findByIsActiveTrueAndRecurrenceType(RecurrenceType.MONTHLY);

        int todayDayOfMonth = LocalDate.now().getDayOfMonth();

        for (TaskTemplate template : monthlyTemplates) {
            // Check if today matches the scheduled day of month
            if (template.getDayOfMonth() != null && template.getDayOfMonth() == todayDayOfMonth) {
                if (!wasGeneratedToday(template)) {
                    generateTaskFromTemplate(template);
                }
            }
        }

        log.info("Monthly task generation completed.");
    }

    /**
     * Update overdue tasks
     * Runs every hour to check and update task status
     */
    @Scheduled(fixedRate = 3600000) // Every hour in milliseconds
    @Transactional
    public void updateOverdueTasks() {
        log.info("Checking for overdue tasks...");

        List<Task> overdueTasks = taskRepository.findOverdueTasks();

        for (Task task : overdueTasks) {
            if (task.getStatus() != TaskStatus.OVERDUE && task.getStatus() != TaskStatus.COMPLETED) {
                task.setStatus(TaskStatus.OVERDUE);
                taskRepository.save(task);
                log.warn("Task marked as overdue: {} (Due: {})", task.getTitle(), task.getDueDate());
            }
        }

        log.info("Overdue check completed. Found {} overdue tasks", overdueTasks.size());
    }

    /**
     * Check if template was already used to generate a task today
     */
    private boolean wasGeneratedToday(TaskTemplate template) {
        if (template.getLastGenerated() == null) {
            return false;
        }

        LocalDate lastGenDate = template.getLastGenerated().toLocalDate();
        LocalDate today = LocalDate.now();

        return lastGenDate.equals(today);
    }

    /**
     * Generate a task from a template
     * Core logic for recurring task creation
     */
    private void generateTaskFromTemplate(TaskTemplate template) {
        log.info("Generating task from template: {} for user: {}",
                template.getTitle(), template.getAssignedTo().getEmail());

        Task task = new Task();
        task.setAssignedTo(template.getAssignedTo());
        task.setTitle(template.getTitle());
        task.setDescription(template.getDescription());
        task.setPriority(template.getPriority());
        task.setStatus(TaskStatus.TODO);
        task.setCreatedAt(LocalDateTime.now());
        task.setIsRecurring(true);
        task.setRecurrenceType(template.getRecurrenceType());
        task.setTemplate(template);
        task.setCategory(template.getCategory());

        // Calculate due date based on template's daysToComplete
        LocalDate dueDate = LocalDate.now().plusDays(template.getDaysToComplete());
        task.setDueDate(dueDate);

        taskRepository.save(task);

        // Update template's last generated timestamp
        template.setLastGenerated(LocalDateTime.now());
        templateRepository.save(template);

        log.info("Task generated successfully: {} (Due: {})", task.getTitle(), task.getDueDate());
    }
}

/**
 * Interview Discussion Points:
 *
 * Q: How does @Scheduled work?
 * A: Spring's @Scheduled annotation uses TaskScheduler to execute methods
 *    at fixed intervals or based on cron expressions. It runs on a separate
 *    thread pool configured in the application.
 *
 * Q: What is a cron expression?
 * A: Cron expression defines a schedule: "second minute hour day month dayOfWeek"
 *    Example: "0 0 6 * * ?" = 6:00 AM every day
 *    "0 0 9 * * MON" = 9:00 AM every Monday
 *    "0 0 9 1 * ?" = 9:00 AM on 1st of every month
 *
 * Q: Why separate methods for daily/weekly/monthly?
 * A: Could use one method, but separation provides:
 *    - Clearer logs for debugging
 *    - Independent scheduling control
 *    - Easier to modify individual patterns
 *
 * Q: What if server restarts?
 * A: Tasks missed during downtime won't be generated.
 *    For production: use persistent scheduler like Quartz
 *    or event-driven architecture with message queues.
 *
 * Q: How to handle timezone issues?
 * A: Spring's scheduler uses server timezone by default.
 *    For production: store user timezone, schedule in UTC,
 *    convert when generating tasks.
 *
 * Time Complexity:
 * - Template lookup: O(n) where n = number of templates
 * - Task generation: O(1) for each template
 * - Overall: O(n) per schedule run
 */
