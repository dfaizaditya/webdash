package com.kbbukopin.webdash.dto;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.HashMap;
import java.util.Map;

public class StatHandler {
    public static ResponseEntity<Object> generateResponse(String message, HttpStatus status, Integer total, Object responseObj) {
        Map<String, Object> map = new HashMap<String, Object>();
            map.put("message", message);
            map.put("status", status.value());
            map.put("total", total);
            map.put("data", responseObj);

            return new ResponseEntity<Object>(map,status);
    }
}
