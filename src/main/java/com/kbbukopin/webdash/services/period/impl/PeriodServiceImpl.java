package com.kbbukopin.webdash.services.period.impl;

import com.kbbukopin.webdash.dto.ResponseHandler;
import com.kbbukopin.webdash.entity.Period;
import com.kbbukopin.webdash.repository.PeriodRepository;
import com.kbbukopin.webdash.services.period.PeriodService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
public class PeriodServiceImpl implements PeriodService {
    @Autowired
    private PeriodRepository periodRepository;

    @Override
    public ResponseEntity<Object> getAllPeriods() {
        Date date = new Date();
        LocalDate localDate = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        long year = localDate.getYear();

        if(!periodRepository.existsByYear(year)) {
            addCurrentPeriod(year);
        }

        List<Period> periods = periodRepository.findAll();
        return ResponseHandler.generateResponse("Success", HttpStatus.OK, periods);
    }

    private void addCurrentPeriod(Long year){
        Period period = Period.builder()
                .year(year)
                .build();

        periodRepository.save(period);
    }

    public Period getPeriodByYear(Long year){

        Date date = new Date();
        LocalDate localDate = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        long currentYear = localDate.getYear();

        if(!periodRepository.existsByYear(currentYear)) {
            addCurrentPeriod(currentYear);
        }

        Period period = new Period();

        if(periodRepository.existsByYear(year)) {
            period = periodRepository.getPeriodByYear(year);
        }

        return period;
    }
}