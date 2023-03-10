package com.kbbukopin.webdash.controllers;

import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.kbbukopin.webdash.dto.PagedResponse;
import com.kbbukopin.webdash.entity.Project;
import com.kbbukopin.webdash.response.ResponseMessage;
import com.kbbukopin.webdash.services.ProjectService;
import com.kbbukopin.webdash.utils.AppConstants;

@RestController
@RequestMapping("/api/projects")
public class ProjectController {
	@Autowired
	private ProjectService projectService;

	@GetMapping
	public PagedResponse<Project> getAllProjects(
			@RequestParam(name = "page", required = false, defaultValue = AppConstants.DEFAULT_PAGE_NUMBER) Integer page,
			@RequestParam(name = "size", required = false, defaultValue = AppConstants.DEFAULT_PAGE_SIZE) Integer size) {
		return projectService.getAllProjects(page, size);
	}

	@PutMapping("/{id}")
	public ResponseEntity<Object> updateProject(@PathVariable(name = "id") Long id,
			@Valid @RequestBody Project project) {
		return projectService.updateProject(id, project);
	}

	@GetMapping("/{statistic}")
	public ResponseEntity<Object> getProjectStat() {
		return projectService.getProjectStat();
	}

	@PostMapping(path = "/upload")
    public  ResponseEntity<?>  importDataFromExcelToDb(@RequestPart(required = true)List<MultipartFile> files){
        String message = "";
		projectService.importToDb(files);
        message = "Uploaded the file successfully";
        return ResponseEntity.status(HttpStatus.OK).body(new ResponseMessage(message));
    }
}
