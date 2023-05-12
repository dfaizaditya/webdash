package com.kbbukopin.webdash.controllers;

import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
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

	@GetMapping("/{id}/{month}")
	public ResponseEntity<Object> getProjectByIdandMonth(
			@PathVariable(name = "id") Long id,
			@PathVariable(name = "month") String month) {
		return projectService.getProjectByIdAndMonth(id, month);
	}
	
	@PutMapping("/{id}/{month}")
	public ResponseEntity<Object> updateProject(@PathVariable(name = "id") Long id,
												@PathVariable(name = "month") String month,
												@Valid @RequestBody Project project) {
		return projectService.updateProject(id, month, project);
	}

	@PostMapping("/created")
	public ResponseEntity<Object> createProject(@Valid @ModelAttribute Project project) {

		return projectService.createProject(project);

	}

	@DeleteMapping("/{id}/{month}")
	public ResponseEntity<Object> deleteProject(@PathVariable(name = "id") Long id,
												@PathVariable(name = "month") String month) {
		return projectService.deleteProject(id, month);
	}

	@GetMapping("/statistics")
	public ResponseEntity<Object> getProjectStat(@RequestParam(required = false) String month) {
		return projectService.getProjectStat(month);
	}

	@GetMapping("/evidence")
	public ResponseEntity<Object> getEvidenceKpi(
			@RequestParam(required = false) String month)
	{
		return projectService.getEvidenceKpi(month);
	}

	@GetMapping("/search")
	public ResponseEntity<Object> getProjectsByFilter(
		@RequestParam(required = false) String month,
		@RequestParam(required = false) String name,
        @RequestParam(required = false) String unit, 
        @RequestParam(required = false) String category
    	// @RequestParam(required = false, defaultValue = "false") boolean availableOnly
		)
	{
		return projectService.getProjectsByFilter(month, name, unit, category);
	}

	@GetMapping("/download")
  	public ResponseEntity<Resource> getFile() {
    String filename = "projects.xlsx";
    InputStreamResource file = new InputStreamResource(projectService.load());

    return ResponseEntity.ok()
        .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + filename)
        .contentType(MediaType.parseMediaType("application/vnd.ms-excel"))
        .body(file);
  }

	@PostMapping(path = "/upload")
    public  ResponseEntity<?>  importDataFromExcelToDb(@RequestPart(required = true)List<MultipartFile> files){
        String message = "";
		projectService.importToDb(files);
        message = "Uploaded the file successfully";
        return ResponseEntity.status(HttpStatus.OK).body(new ResponseMessage(message));
    }
}
