package com.kbbukopin.webdash.services;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import com.kbbukopin.webdash.dto.PagedResponse;
import com.kbbukopin.webdash.entity.Project;
import java.io.ByteArrayInputStream;

public interface ProjectService {

    PagedResponse<Project> getAllProjects(int page, int size);

    ResponseEntity<Object> getProjectByIdAndMonth(Long id, String month);

    ResponseEntity<Object> createProject(Project newProject);
    
    ResponseEntity<Object> updateProject(Long id, String month, Project newProject);

    ResponseEntity<Object> deleteProject(Long id, String month);

    void importToDb(List<MultipartFile> multipleFiles);

    ResponseEntity<Object> getProjectStat(String month);

    ResponseEntity<Object> getProjectsByFilter(String month, String name, String unit, String category);

    ResponseEntity<Object> getEvidenceKpi(String month);

    ByteArrayInputStream load();
}
