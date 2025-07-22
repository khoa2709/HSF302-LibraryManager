package com.team5.librarymanager.repository;

import com.team5.librarymanager.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {

    User findByUsername(String un);

    boolean existsByUsername(String username);
}