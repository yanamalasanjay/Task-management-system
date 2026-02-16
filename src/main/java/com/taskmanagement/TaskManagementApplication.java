package com.taskmanagement;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Main Application Class for Personal Task & Duty Management System
 *
 * This application provides:
 * - Task CRUD operations with priority management
 * - Recurring task engine (daily, weekly, monthly schedules)
 * - Cron-based automatic task generation
 * - Email digest system for task summaries
 * - Priority-based deadline alerts
 * - Daily reporting duty tracking
 *
 * Built for Reliance Industries Ltd - Material Data & Reporting Management
 *
 * @author Yanamala Sanjay
 */
@SpringBootApplication
@EnableScheduling  // Enable scheduled tasks (cron jobs)
@EnableAsync  // Enable asynchronous email sending
public class TaskManagementApplication {

    public static void main(String[] args) {
        SpringApplication.run(TaskManagementApplication.class, args);
        System.out.println("\n========================================");
        System.out.println("📋 Task Management System Started!");
        System.out.println("🔗 REST API: http://localhost:8081/api");
        System.out.println("⏰ Scheduled Jobs: ENABLED");
        System.out.println("========================================\n");
    }
}
