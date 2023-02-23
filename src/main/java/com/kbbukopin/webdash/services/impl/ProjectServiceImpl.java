package com.kbbukopin.webdash.services.impl;

import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.kbbukopin.webdash.dto.PagedResponse;
import com.kbbukopin.webdash.dto.ResponseHandler;
import com.kbbukopin.webdash.entity.Project;
import com.kbbukopin.webdash.exeptions.ResourceNotFoundException;
import com.kbbukopin.webdash.repository.ProjectRepository;
import com.kbbukopin.webdash.services.ProjectService;
import com.kbbukopin.webdash.utils.AppUtils;

@Service
public class ProjectServiceImpl implements ProjectService {

	@Autowired
	private ProjectRepository projectRepository;

	@Override
	public PagedResponse<Project> getAllProjects(int page, int size) {
		AppUtils.validatePageNumberAndSize(page, size);

		Pageable pageable = PageRequest.of(page, size);

		Page<Project> projects = projectRepository.findAll(pageable);

		List<Project> data = projects.getNumberOfElements() == 0 ? Collections.emptyList() : projects.getContent();

		return new PagedResponse<>(data, projects.getNumber(), projects.getSize(), projects.getTotalElements(),
				projects.getTotalPages(), projects.isLast());
	}

	@Override
	public ResponseEntity<Object> updateProject(Long id, Project newProject) {
		Project project = projectRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Project", "id", id));
		project.setTitle(newProject.getTitle());
		project.setDescription(newProject.getDescription());
		Project updatedProject = projectRepository.save(project);
		
		return ResponseHandler.generateResponse("Success", HttpStatus.OK, updatedProject);
	}
}