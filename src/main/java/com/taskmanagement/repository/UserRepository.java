package com.taskmanagement.repository;

import com.taskmanagement.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository for User entity
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    // Find user by email
    Optional<User> findByEmail(String email);

    // Check if email exists
    boolean existsByEmail(String email);

    // Find users with email digest enabled
    List<User> findByEmailDigestEnabledTrue();

    // Find users by department
    List<User> findByDepartment(String department);
}
