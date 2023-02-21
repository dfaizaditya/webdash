package com.kbbukopin.webdash.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.kbbukopin.webdash.entity.Project;;

public interface ProjectRepository extends JpaRepository<Project, Long> {
}
