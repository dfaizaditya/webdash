package com.kbbukopin.webdash.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.kbbukopin.webdash.entity.Role;

public interface RoleRepository extends JpaRepository<Role, Long> {
    Role findByName(String name);
}
