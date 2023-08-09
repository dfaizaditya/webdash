package com.kbbukopin.webdash.controllers;

import com.kbbukopin.webdash.entity.DayOff;
import com.kbbukopin.webdash.entity.Project;
import com.kbbukopin.webdash.response.ResponseMessage;
import com.kbbukopin.webdash.services.dayOff.DayOffService;
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
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/day_off")
public class DayOffController {

    @Autowired
    private DayOffService dayOffService;

    @GetMapping
    public ResponseEntity<Object> getAllDayOff() {
        return dayOffService.getAllDayOff();
    }

    @PostMapping("/create")
    public ResponseEntity<Object> createDayOff(@Valid @RequestBody DayOff newDayOff) {
        return dayOffService.createDayOff(newDayOff);
    }

    @PutMapping("/{year}/{month}/{day}")
    public ResponseEntity<Object> updateDayOff(@PathVariable(name = "year") int year,
                                               @PathVariable(name = "month") int month,
                                               @PathVariable(name = "day") int day,
                                               @Valid @RequestBody DayOff newDayOff) {
        LocalDate date = LocalDate.of(year, month, day);
        return dayOffService.updateDayOff(date, newDayOff);
    }

    @DeleteMapping("/deleteMultiple")
    public ResponseEntity<Object> deleteMultipleDayOff(@RequestBody List<DayOff> multipleDayOff){
        try{
            List<LocalDate> dates = multipleDayOff.stream()
                    .map(DayOff::getDate)
                    .collect(Collectors.toList());

            return dayOffService.deleteMultipleDayOff(dates);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to delete data");
        }
    }

    @PostMapping(path = "/upload")
    public ResponseEntity<?> importDataFromExcelToDb(@RequestPart(required = true) List<MultipartFile> files){

        return dayOffService.importToDb(files);

    }

    @GetMapping("/download")
    public ResponseEntity<Resource> getTemplateFile() {

        String filename = "Template Day Off.xlsx";

        InputStreamResource file = new InputStreamResource(dayOffService.getTemplateExcel());

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + filename)
                .contentType(MediaType.parseMediaType("application/vnd.ms-excel"))
                .body(file);
    }

}
