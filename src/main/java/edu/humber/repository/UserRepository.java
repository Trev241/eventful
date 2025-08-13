package edu.humber.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import edu.humber.model.User;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String username);
    
    // Admin dashboard statistics
    long countByRole(String role);
}
