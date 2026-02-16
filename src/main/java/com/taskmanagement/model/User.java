package com.taskmanagement.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Entity representing a user/employee
 * Demonstrates basic entity mapping
 */
@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    private String department;  // e.g., "Engineering", "Operations"

    private String designation;  // e.g., "Graduate Engineer Trainee"

    @Column(name = "employee_id")
    private String employeeId;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "email_digest_enabled")
    private Boolean emailDigestEnabled = true;  // Whether to receive daily digest emails
}
