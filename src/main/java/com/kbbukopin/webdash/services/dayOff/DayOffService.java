package com.kbbukopin.webdash.services.dayOff;

import com.kbbukopin.webdash.entity.DayOff;
import com.kbbukopin.webdash.entity.Project;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.time.LocalDate;
import java.util.List;

public interface DayOffService {

    ResponseEntity<Object> getAllDayOff();

    ResponseEntity<Object> createDayOff(DayOff newDayOff);

    ResponseEntity<Object> updateDayOff(LocalDate date, DayOff newDayOff);

    ResponseEntity<Object> importToDb(List<MultipartFile> multipleFiles);

    ResponseEntity<Object> deleteMultipleDayOff(List<LocalDate> dates);

    ByteArrayInputStream getTemplateExcel();
}
