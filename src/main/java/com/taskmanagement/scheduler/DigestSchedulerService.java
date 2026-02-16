package com.taskmanagement.scheduler;

import com.taskmanagement.model.User;
import com.taskmanagement.repository.UserRepository;
import com.taskmanagement.service.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Service for sending daily email digests
 * Demonstrates email digest system implementation
 *
 * @author Yanamala Sanjay
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class DigestSchedulerService {

    private final UserRepository userRepository;
    private final EmailService emailService;

    /**
     * Send daily task digest emails
     * Runs every day at 8:00 AM
     */
    @Scheduled(cron = "0 0 8 * * ?")
    public void sendDailyDigests() {
        log.info("Running daily digest job...");

        List<User> users = userRepository.findByEmailDigestEnabledTrue();

        int digestsSent = 0;

        for (User user : users) {
            try {
                emailService.sendDailyTaskDigest(user);
                digestsSent++;
            } catch (Exception e) {
                log.error("Failed to send digest to user: {}", user.getEmail(), e);
            }
        }

        log.info("Daily digest job completed. Sent {} digests", digestsSent);
    }
}
