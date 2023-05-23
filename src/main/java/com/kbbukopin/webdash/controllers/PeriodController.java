package com.kbbukopin.webdash.controllers;

import com.kbbukopin.webdash.services.period.PeriodService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/periods")
public class PeriodController {

    @Autowired
    private PeriodService periodService;

    @GetMapping
    public ResponseEntity<Object> getAllPeriods(){
        return periodService.getAllPeriods();
    }

}
