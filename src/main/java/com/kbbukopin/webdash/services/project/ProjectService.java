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

    ResponseEntity<Object> getProjectByPrimaryKey(Long id, String month, String unit, Long year);

    ResponseEntity<Object> createProject(Project newProject);
    
    ResponseEntity<Object> updateProject(Long id, String month, String unit, Long year, Project newProject);

    ResponseEntity<Object> deleteProject(Long id, String month, String unit, Long year);

    ResponseEntity<Object> deleteMultipleProjects(List<Long> ids, List<String> months, List<String> units, List<Long> projectPeriodIds);

    ResponseEntity<Object> importToDb(Long period_id, List<MultipartFile> multipleFiles);

    ResponseEntity<Object> getProjectStat(Long year, String month);

    ResponseEntity<Object> getProjectStats(Long year, String month);

    ResponseEntity<Object> getProjectCompletionStat(Long year, String month);

    ResponseEntity<Object> getProjectUnitStat(Long year, String month);

    ResponseEntity<Object> getProjectTypeStat(Long year, String month);

    ResponseEntity<Object> getProjectCategoryStat(Long year, String month);

    ResponseEntity<Object> getProjectDocumentationStat(Long year, String month);

    ResponseEntity<Object> getProjectRolloutStatusStat(Long year, String month);

    ResponseEntity<Object> getProjectRolloutUnitStat(Long year, String month);

    ResponseEntity<Object> getProjectAppPlatformStat(Long year, String month);

    ResponseEntity<Object> getProjectTechPlatformStat(Long year, String month);

    ResponseEntity<Object> getDashboardCompletion(Long year, String month);
    
    ResponseEntity<Object> getProjectsByFilter(Long year, String month, String name, String info1, String unit, String type, String category);

    ResponseEntity<Object> getEvidenceKpi(Long year, String month);

    ByteArrayInputStream load(Long year, String month);

    ResponseEntity<Object> getPinnedProjects();

}
