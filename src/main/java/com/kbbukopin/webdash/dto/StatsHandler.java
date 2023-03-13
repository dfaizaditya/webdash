package com.kbbukopin.webdash.dto;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.HashMap;
import java.util.Map;

public class StatsHandler {
    public static ResponseEntity<Object> generateResponse(String message, HttpStatus status, Object responseObj, Object responseObj2) {
        Map<String, Object> map = new HashMap<String, Object>();
            map.put("message", message);
            map.put("status", status.value());
            map.put("projectType", responseObj);
            map.put("projectCompletion", responseObj2);

            return new ResponseEntity<Object>(map,status);
    }
}
