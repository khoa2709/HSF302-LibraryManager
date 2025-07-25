package com.team5.librarymanager.repository;

import com.team5.librarymanager.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface UserRepository extends JpaRepository<User, Long> {

    User findByUsername(String un);

    boolean existsByUsername(String username);

    // Đếm số lượng user không phải admin
    @Query("SELECT COUNT(u) FROM User u WHERE u.role <> 'admin'")
    Long countNormalUsers();

    List<User> findAllByFullNameContainingIgnoreCase(String fullName);
}