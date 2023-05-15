package com.kbbukopin.webdash.dto;

import org.apache.commons.collections4.map.LinkedMap;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class KpiHandler {

    private static LinkedMap<String, Object> dataResponse = new LinkedMap<String, Object>();

    public static ResponseEntity<Object> generateResponse(String message, HttpStatus status) {
        LinkedMap<String, Object> response = new LinkedMap<String, Object>();

        response = KpiHandler.dataResponse;
        KpiHandler.resetDataResponse();

        response.put("message", message);
        response.put("status", status.value());

        return new ResponseEntity<Object>(response,status);
    }

    private static void resetDataResponse() {
        KpiHandler.dataResponse = new LinkedMap<String, Object>();
    }

    public static void addDataResponse(String key, Object data) {
        KpiHandler.dataResponse.put(key, data);
    }
}
