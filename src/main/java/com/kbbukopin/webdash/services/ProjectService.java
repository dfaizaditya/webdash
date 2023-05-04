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
    
    ResponseEntity<Object> updateProject(Long id, String month, Project newProject);

    ResponseEntity<Object> deleteProject(Long id, String month);

    void importToDb(List<MultipartFile> multipleFiles);

    ResponseEntity<Object> getProjectStat();

    ResponseEntity<Object> getProjectsByFilter(String name, String unit, String category);

    ResponseEntity<Object> getProjectsByFilterMonth(String month);

    ResponseEntity<Object> getEvidenceKpi(String month);

    ByteArrayInputStream load();
}
