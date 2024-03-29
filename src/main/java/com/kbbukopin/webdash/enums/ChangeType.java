package com.kbbukopin.webdash.enums;

public enum ChangeType {
    NORMAL_MINOR("Normal Minor"),
    NORMAL_MAJOR("Normal Major"),
    NA("N/A");

    private final String name;

    private ChangeType(String name){
        this.name = name;
    }

    public String getName() {
        return name;
    }

}
