package com.taskmanagement.scheduler;

import com.taskmanagement.model.Task;
import com.taskmanagement.repository.TaskRepository;
import com.taskmanagement.service.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Service for sending task reminders
 * Demonstrates scheduled reminder system
 *
 * @author Yanamala Sanjay
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ReminderSchedulerService {

    private final TaskRepository taskRepository;
    private final EmailService emailService;

    /**
     * Send task reminders
     * Runs every 2 hours to check for tasks needing reminders
     */
    @Scheduled(fixedRate = 7200000) // Every 2 hours
    @Transactional
    public void sendTaskReminders() {
        log.info("Running task reminder check...");

        List<Task> tasks = taskRepository.findTasksNeedingReminders();

        int remindersSent = 0;

        for (Task task : tasks) {
            if (task.shouldSendReminder()) {
                emailService.sendTaskReminder(task);
                task.setReminderSent(true);
                taskRepository.save(task);
                remindersSent++;
            }
        }

        log.info("Reminder check completed. Sent {} reminders", remindersSent);
    }
}
