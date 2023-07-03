package com.kbbukopin.webdash.services.project;

import java.util.List;

import com.kbbukopin.webdash.entity.Period;
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

    ResponseEntity<Object> deleteMultipleProjects(List<Long> ids, List<String> months);

    void importToDb(Long id_project, List<MultipartFile> multipleFiles);

    ResponseEntity<Object> getProjectStat(Long year, String month);

    ResponseEntity<Object> getProjectsByFilter(Long year, String month, String name, String info1, String unit, String type, String category);

    ResponseEntity<Object> getEvidenceKpi(Long year, String month);

    ByteArrayInputStream load(Long year, String month);
}
