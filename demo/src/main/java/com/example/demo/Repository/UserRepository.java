package com.example.demo.Repository;

import com.example.demo.Enum.Role;
import com.example.demo.Entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    // 1. Find user by username (most common - used for authentication)
    Optional<User> findByUsername(String username);

    // 2. Check if username exists
    boolean existsByUsername(String username);

    // 3. Find all users by role
    List<User> findByRole(Role role);

    // 4. Find user by username with accounts eager loaded (optimized)
    @Query("SELECT u FROM User u JOIN FETCH u.accounts WHERE u.username = :username")
    Optional<User> findByUsernameWithAccounts(@Param("username") String username);

    // 5. Search users by username containing a keyword
    @Query("SELECT u FROM User u WHERE LOWER(u.username) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<User> searchByUsernameContaining(@Param("keyword") String keyword);

    // 6. Count users by role
    long countByRole(Role role);

    // 7. Find all users with their accounts (for reporting)
    @Query("SELECT u FROM User u JOIN FETCH u.accounts")
    List<User> findAllWithAccounts();

}