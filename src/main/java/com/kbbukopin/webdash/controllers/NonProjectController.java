package com.kbbukopin.webdash.controllers;

import com.kbbukopin.webdash.entity.NonProject;
import com.kbbukopin.webdash.entity.Project;
import com.kbbukopin.webdash.services.nonproject.NonProjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/non_projects")
public class NonProjectController {

    @Autowired
    private NonProjectService nonProjectService;


    @GetMapping("/search")
    public ResponseEntity<Object> getNonProjectsByFilter(
            @RequestParam(required = false) Long year,
            @RequestParam(required = false) String month,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String unit
    )
    {
        return nonProjectService.getNonProjectsByFilter(year, month, name, unit);
    }

    @PostMapping("/create")
    public ResponseEntity<Object> createNonProject(@Valid @RequestBody NonProject nonProject) {
        return nonProjectService.createNonProject(nonProject);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> getNonProjectByPrimaryKey(@PathVariable(name = "id") Long id) {
        return nonProjectService.getNonProjectByPrimaryKey(id);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Object> updateProject(@PathVariable(name = "id") Long id,
                                                @Valid @RequestBody NonProject nonProject) {
        return nonProjectService.updateNonProject(id, nonProject);
    }

    @DeleteMapping("/deleteMultiple")
    public ResponseEntity<Object> deleteMultipleNonProject(@RequestBody List<NonProject> nonProjects) {
        try {
            List<Long> ids = nonProjects.stream()
                    .map(NonProject::getId)
                    .collect(Collectors.toList());

            return nonProjectService.deleteMultipleNonProjects(ids);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to delete data");
        }
    }

    @GetMapping("/download")
    public ResponseEntity<Resource> getFile(@RequestParam(required = false) Long year,
                                            @RequestParam(required = false) String month) {

        String filename = "Non Projects ";

        if(month==null || month.equals("")){
            filename += year+".xlsx";
        } else {
            filename += month+"-"+year+".xlsx";
        }

        InputStreamResource file = new InputStreamResource(nonProjectService.load(year, month));

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + filename)
                .contentType(MediaType.parseMediaType("application/vnd.ms-excel"))
                .body(file);
    }

    @PostMapping(path = "/upload")
    public  ResponseEntity<?>  importDataFromExcelToDb(@RequestParam(required = true) Long period_id,
                                                       @RequestPart(required = true)List<MultipartFile> files){
        return nonProjectService.importToDb(period_id, files);
    }
}
