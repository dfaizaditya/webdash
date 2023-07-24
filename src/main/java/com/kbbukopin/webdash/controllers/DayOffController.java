package com.kbbukopin.webdash.controllers;

import com.kbbukopin.webdash.response.ResponseMessage;
import com.kbbukopin.webdash.services.dayOff.DayOffService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/day_offs")
public class DayOffController {

    @Autowired
    private DayOffService dayOffService;

    @PostMapping(path = "/upload")
    public ResponseEntity<?> importDataFromExcelToDb(@RequestPart(required = true) List<MultipartFile> files){

        return dayOffService.importToDb(files);

    }

}
