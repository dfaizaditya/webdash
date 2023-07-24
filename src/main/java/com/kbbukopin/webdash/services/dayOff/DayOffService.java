package com.kbbukopin.webdash.services.dayOff;

import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface DayOffService {

    ResponseEntity<Object> importToDb(List<MultipartFile> multipleFiles);
}
