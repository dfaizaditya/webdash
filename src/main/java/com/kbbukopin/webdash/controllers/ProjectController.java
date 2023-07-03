package com.kbbukopin.webdash.controllers;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import javax.validation.Valid;

import com.kbbukopin.webdash.enums.*;
import com.kbbukopin.webdash.services.appPlatform.AppPlatformService;
import com.kbbukopin.webdash.services.pic.PicService;
import com.kbbukopin.webdash.services.techPlatform.TechPlatformService;
import com.kbbukopin.webdash.services.userSponsor.UserSponsorService;
import org.apache.commons.collections4.map.LinkedMap;
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
import com.kbbukopin.webdash.services.project.ProjectService;
import com.kbbukopin.webdash.utils.AppConstants;

@RestController
@RequestMapping("/api/projects")
public class ProjectController {
	@Autowired
	private ProjectService projectService;

	@Autowired
	private UserSponsorService userSponsorService;

	@Autowired
	private AppPlatformService appPlatformService;

	@Autowired
	private TechPlatformService techPlatformService;

	@Autowired
	private PicService picService;

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

	@PostMapping("/create")
	public ResponseEntity<Object> createProject(@Valid @RequestBody  Project project) {
		return projectService.createProject(project);
	}

	@DeleteMapping("/{id}/{month}")
	public ResponseEntity<Object> deleteProject(@PathVariable(name = "id") Long id,
												@PathVariable(name = "month") String month) {
		return projectService.deleteProject(id, month);
	}

	@DeleteMapping("/deleteMultiple")
	public ResponseEntity<Object> deleteMultipleProject(@RequestBody List<Project> projects) {
		try {
			List<Long> ids = projects.stream()
					.map(Project::getId)
					.collect(Collectors.toList());
			List<String> months = projects.stream()
					.map(Project::getMonth)
					.collect(Collectors.toList());

			return projectService.deleteMultipleProjects(ids, months);
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to delete data");
		}
	}

	@GetMapping("/statistics")
	public ResponseEntity<Object> getProjectStat(@RequestParam(required = false) Long year,
												 @RequestParam(required = false) String month) {
		return projectService.getProjectStat(year, month);
	}

	@GetMapping("/evidence")
	public ResponseEntity<Object> getEvidenceKpi(@RequestParam(required = false) Long year,
												 @RequestParam(required = false) String month)
	{
		return projectService.getEvidenceKpi(year, month);
	}

	@GetMapping("/search")
	public ResponseEntity<Object> getProjectsByFilter(
		@RequestParam(required = false) Long year,
		@RequestParam(required = false) String month,
		@RequestParam(required = false) String name,
		@RequestParam(required = false) String info1,
        @RequestParam(required = false) String unit,
		@RequestParam(required = false) String type,
        @RequestParam(required = false) String category
    	// @RequestParam(required = false, defaultValue = "false") boolean availableOnly
		)
	{
		return projectService.getProjectsByFilter(year, month, name, info1, unit, type, category);
	}

	@GetMapping("/enums")
    public LinkedMap<String, Object> getAllEnums() {
		LinkedMap<String, Object> result = new LinkedMap<>();
        
        // Get all unit names from the Unit enum
        List<String> unitType = Arrays.stream(UnitType.values())
                .map(UnitType::getName)
                .collect(Collectors.toList());
        result.put("unitType", unitType);
        
        // Get all category names from the Category enum
        List<String> categoryType = Arrays.stream(CategoryType.values())
                .map(CategoryType::getName)
                .collect(Collectors.toList());
        result.put("categoryType", categoryType);

		// Get all project type names from the ProjectType enum
        List<String> projectType = Arrays.stream(ProjectType.values())
                .map(ProjectType::getName)
                .collect(Collectors.toList());
        result.put("projectType", projectType);

		// Get all status option 1 names from the StatusOptions1 enum
		List<String> statusOptions1 = Arrays.stream(StatusOptions1.values())
				.map(StatusOptions1::getName)
				.collect(Collectors.toList());
		result.put("statusOptions1", statusOptions1);

		// Get all status option 2 names from the StatusOptions2 enum
		List<String> statusOptions2 = Arrays.stream(StatusOptions2.values())
				.map(StatusOptions2::getName)
				.collect(Collectors.toList());
		result.put("statusOptions2", statusOptions2);

		// Get all change type names from the ChangeType enum
		List<String> changeType = Arrays.stream(ChangeType.values())
				.map(ChangeType::getName)
				.collect(Collectors.toList());
		result.put("changeType", changeType);

		// Get all documentation type names from the DocumentationType enum
		List<String> documentationType = Arrays.stream(DocumentationType.values())
				.map(DocumentationType::getName)
				.collect(Collectors.toList());
		result.put("documentationType", documentationType);

		result.put("userSponsor", userSponsorService.getAllUserSponsors());
		result.put("appPlatform", appPlatformService.getAllAppPlatforms());
		result.put("techPlatform", techPlatformService.getAllTechPlatforms());
		result.put("pic", picService.getAllPics());

		return result;
    }

	@GetMapping("/download")
  	public ResponseEntity<Resource> getFile(@RequestParam(required = false) Long year,
											@RequestParam(required = false) String month) {

	String filename = "Projects ";

	if(month==null || month.equals("")){
		filename += year+".xlsx";
	} else {
		filename += month+"-"+year+".xlsx";
	}

    InputStreamResource file = new InputStreamResource(projectService.load(year, month));

    return ResponseEntity.ok()
        .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + filename)
        .contentType(MediaType.parseMediaType("application/vnd.ms-excel"))
        .body(file);
  }

	@PostMapping(path = "/upload")
    public  ResponseEntity<?>  importDataFromExcelToDb(@RequestParam(required = true) Long period_id,
													   @RequestPart(required = true)List<MultipartFile> files){
        String message = "";
		projectService.importToDb(period_id, files);
        message = "Uploaded the file successfully";
        return ResponseEntity.status(HttpStatus.OK).body(new ResponseMessage(message));
    }
}
