package com.kbbukopin.webdash.services.nonproject;

import com.kbbukopin.webdash.entity.NonProject;
import com.kbbukopin.webdash.entity.Project;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.util.List;

public interface NonProjectService {

    ResponseEntity<Object> getNonProjectsByFilter(Long year, String month, String name, String unit);

    ResponseEntity<Object> createNonProject(NonProject newNonProject);

    ResponseEntity<Object> getNonProjectByPrimaryKey(Long id);

    ResponseEntity<Object> updateNonProject(Long id, NonProject newNonProject);

    ResponseEntity<Object> deleteMultipleNonProjects(List<Long> ids);

    ResponseEntity<Object> importToDb(Long period_id, List<MultipartFile> multipleFiles);

    ByteArrayInputStream load(Long year, String month);

}
