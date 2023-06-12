package com.kbbukopin.webdash.enums;

public enum ChangeType {

    NA("N/A"),
    NORMAL_MINOR("Normal Minor"),
    NORMAL_MAJOR("Normal Major");

    private final String name;

    private ChangeType(String name){
        this.name = name;
    }

    public String getName() {
        return name;
    }

}
