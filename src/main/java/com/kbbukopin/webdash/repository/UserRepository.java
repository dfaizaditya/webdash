package com.kbbukopin.webdash.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.kbbukopin.webdash.entity.User;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, String> {
    Optional<User> findByUsername(String username);
}
