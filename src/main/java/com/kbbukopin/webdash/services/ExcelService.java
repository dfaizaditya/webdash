package com.kbbukopin.webdash.services;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.kbbukopin.webdash.entity.Project;
import com.kbbukopin.webdash.helper.ExcelHelper;
import com.kbbukopin.webdash.repository.ProjectRepository;

@Service
public class ExcelService {
  @Autowired
  ProjectRepository repository;

  public void save(MultipartFile file) {
    try {
      List<Project> projects = ExcelHelper.excelToProjects(file.getInputStream());
      repository.saveAll(projects);
    } catch (IOException e) {
      throw new RuntimeException("fail to store excel data: " + e.getMessage());
    }
  }

  public ByteArrayInputStream load() {
    List<Project> projects = repository.findAll();

    ByteArrayInputStream in = ExcelHelper.projectsToExcel(projects);
    return in;
  }

  public List<Project> getAllProjects() {
    return repository.findAll();
  }
}
