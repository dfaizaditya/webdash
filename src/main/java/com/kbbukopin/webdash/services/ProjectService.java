package com.kbbukopin.webdash.services;

import org.springframework.http.ResponseEntity;

import com.kbbukopin.webdash.dto.PagedResponse;
import com.kbbukopin.webdash.entity.Project;

public interface ProjectService {

    PagedResponse<Project> getAllProjects(int page, int size);
    
    ResponseEntity<Object> updateProject(Long id, Project newProject);
}
