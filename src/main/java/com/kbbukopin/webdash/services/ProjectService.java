package com.kbbukopin.webdash.services;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import com.kbbukopin.webdash.dto.PagedResponse;
import com.kbbukopin.webdash.entity.Project;
import java.io.ByteArrayInputStream;

public interface ProjectService {

    PagedResponse<Project> getAllProjects(int page, int size);
    
    ResponseEntity<Object> updateProject(Long id, Project newProject);

    ResponseEntity<Object> deleteProject(Long id);

    void importToDb(List<MultipartFile> multipleFiles);

    ResponseEntity<Object> getProjectStat();

    ByteArrayInputStream load();
}
